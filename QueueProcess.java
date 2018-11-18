import java.util.*;
import javafx.util.Pair;
import java.lang.*;

/**
	Queue Process
	This class implements a representation for a queue system with
	one queue and multiple servers. This class will simulate behavior
	of such a system by running queue-relevant events at each point in
	time (seconds).
	
	@author		A07104218	Salvador Orozco Villalever
	@author		A01328937	Luis Francisco Flores Romero
	@version	1.0
	@since		17.nov.2018
*/
public class QueueProcess {

	private int servers;
	private int clients;
	private int arrivalsInterval;	// in seconds
	private int serviceTime;	// in seconds, per server
	
	private int currSystemTime;	// In seconds
	private int nextClientArrivalTime; // In seconds
	private IntegerSequenceGenerator generator;
	private ServerState[] serverStateArr; // Server-wise state <clientID, timeToReleaseServer>
	private Queue<Integer> clientQueue;
	private int arrivedClientsCount; // The count of clients who have arrived.
	private int servedClientsCount; // The count of clients that have already been served.
	
	/**
	 *	Constructor
	 *	This is the default constructor. Please note this receives an
	 *	already instanced IntegerSequenceGenerator.
	 *	@param	generator		Pseudo-random numbers generator.
	 *	@param	clients			Number of clients that will arrive.
	 *	@param	servers			Number of servers in the system.
	 *	@param	arrivalsRateM	Arrivals per minute.
	 *	@param	serviceRateM	Serviced clients per minute per server.
	 */
	public QueueProcess(IntegerSequenceGenerator generator, int clients, 
		int servers, int arrivalsRateM, int serviceRateM) {
		
		this.generator = generator;
		this.setQueueProcessProperties(clients, servers, arrivalsRateM, serviceRateM);
	}

	/**
	 *	Full constructor
	 *	This constructor instantiates a QueueProcess creating its own
	 *	IntegerSequenceGenerator.
	 *	@param	
	 *	@param	clients			Number of clients that will arrive.
	 *	@param	servers			Number of servers in the system.
	 *	@param	arrivalsRateM	Arrivals per minute.
	 *	@param	serviceRateM	Serviced clients per minute, whole system.
	 */
	public QueueProcess(int seed, int multiplier, int increment, int mod,
		int clients, int servers, int arrivalsRateM, int serviceRateM) {
		
		this.generator = new IntegerSequenceGenerator(seed, multiplier,
			increment, mod);

		this.setQueueProcessProperties(clients, servers, arrivalsRateM, serviceRateM);
	}

	/**
	 * Method that sets the other queue process properties.
 	 *	@param	clients			Number of clients that will arrive.
	 *	@param	servers			Number of servers in the system.
	 *	@param	arrivalsRateM	Arrivals per minute.
	 *	@param	serviceRateM	Serviced clients per minute per server.
	 */
	void setQueueProcessProperties(int clients, int servers, int arrivalsRateM, int serviceRateM){

		this.clients = clients;
		this.servers = servers;
		this.arrivalsInterval = arrivalsRateToTime(arrivalsRateM);
		this.serviceTime = serviceRateToTime(serviceRateM);

		if(!this.isValidQueueProcess(arrivalsRateM, serviceRateM))
			this.handleInvalidQueueProcess();

		this.nextClientArrivalTime = 0;
		this.currSystemTime = 0;
		this.serverStateArr = new ServerState[this.servers];

		for(int i = 0; i < this.servers; i++)
			this.serverStateArr[i] = new ServerState();

		this.clientQueue = new LinkedList<>();
		this.servedClientsCount = 0;
		this.arrivedClientsCount = 0;
	}

	/**
	 * Method that determines whether the queue process is valid, i.e. it does not fall
	 * into an infinite wait.
	 * @param arrivalsRateM	Arrivals per minute.
	 * @param serviceRateM	Serviced clients per minute, whole system.
	 * @return True if the process is valid. Else, false.
	 */
	public boolean isValidQueueProcess(int arrivalsRateM, int serviceRateM){

		return arrivalsRateM < serviceRateM * this.servers;
	}

	/**
	 * Method that handles an invalid queue process case.
	 */
	public void handleInvalidQueueProcess(){

		System.out.println("The process is invalid because the system will fall into an infinite wait.");
		System.exit(1);
	}

	/**
	 * Method that converts from arrival rate to arrivals interval
	 * @param arrivalsRateM the arrival rate in minutes
	 * @return the arrivals interval in minutes, rounded up.
	 */
	public int arrivalsRateToTime(int arrivalsRateM){

		return (int) Math.ceil(60.0/arrivalsRateM);
	}

	/**
	 * Method that converts from system service rate to service time per server
	 * @param serviceRateM the service rate in minutes per server
	 * @return the 
	 */
	public int serviceRateToTime(int serviceRateM){

		return (int) Math.ceil(60.0/serviceRateM);
	}

	/**
	 * Main method for running the simulation
	 */
	public void runSimulation(){

		while(servedClientsCount < this.clients){
			
			/*
				Steps:

				1. Update the time to release each server.
				2. Check whether there is a new client arrival.
				3. Check whether a client can be served.
				4. Increment the system current time.
			*/

			this.updateTimeToReleaseServersTime();

			if(this.arrivedClientsCount < this.clients)
				this.enqueueNewClient();

			this.serveAllPossibleClients();

			try{

				// Sleep for 1 second to make the simulation realistic
				Thread.sleep(10);
			}
			catch(InterruptedException ie){

				// Do nothing!
			}

			this.currSystemTime++;
		}
	}

	/**
	 * Method that updates the time to release each server.
	 */
	public void updateTimeToReleaseServersTime(){

		for(int i = 0; i < this.servers; i++){
			
			// Update the time only if the server is not 
			// currently available.
			if(this.serverStateArr[i].getTimeToReleaseServer() > 0){

				this.serverStateArr[i].tick();

				if(this.serverStateArr[i].getTimeToReleaseServer() == 0){

					System.out.print(this.getSystemTimeStamp() + " : ");
					System.out.println("Server #" + (i + 1) + " finished serving client #" + this.serverStateArr[i].getClientId() + ".");
					this.servedClientsCount++;
				}
			}
		}
	}

	/**
	 * Method that adds a new client to the client (waiting) queue. 
	 */
	public void enqueueNewClient(){

		// If a client just arrived
		if(this.currSystemTime == this.nextClientArrivalTime){

			this.arrivedClientsCount++;
			System.out.print(this.getSystemTimeStamp() + " : ");
			System.out.println("Client #" + this.arrivedClientsCount + " arrived.");

			if(this.computeAvailableServers() <= this.clientQueue.size()){

				System.out.print(this.getSystemTimeStamp() + " : ");
				System.out.println("Client #" + this.arrivedClientsCount + " is waiting for a server to become available.");
			}

			this.clientQueue.add(this.arrivedClientsCount);
			
			// Compute the next arrival time
			this.nextClientArrivalTime = this.nextRandomArrivalTime();
		}
	}

	/**
	 * Method to return a string corresponding to the current system
	 * timestamp
	 * @return a string corresponding to the current system
	 * timestamp
	 */
	public String getSystemTimeStamp(){

		int currSystemSeconds = this.currSystemTime % 60;
		int currSystemMinutes = (this.currSystemTime % 3600)/60;
		int currSystemHours = this.currSystemTime / 3600;

		return String.format("%02d:%02d:%02d", currSystemHours, currSystemMinutes, currSystemSeconds);
	}

	/**
	 * Method that computes the amount of available servers.
	 * @return availableServersCount the amount of available servers.
	 */
	public int computeAvailableServers(){

		int availableServersCount = 0;

		for(int i = 0; i < this.servers; i++)
			if(this.serverStateArr[i].getTimeToReleaseServer() == 0)
				availableServersCount++;

		return availableServersCount;
	}

	/**
	 * Method that assigns a client to a given server with a random
	 * service time.
	 * @param serverIndex the index of the server that will 
	 * service the client
	 */
	public void serveClient(int serverIndex){

		int nextClientId = this.clientQueue.remove();
		this.serverStateArr[serverIndex].setClientId(nextClientId);
		this.serverStateArr[serverIndex].setTimeToReleaseServer(this.nextRandomServiceTime());
		System.out.print(this.getSystemTimeStamp() + " : ");
		System.out.println("Client #" + nextClientId + " is being served by server #" + (serverIndex + 1) + ".");
	}

	/**
	 * Method that serves all possible clients
	 */
	public void serveAllPossibleClients(){

		int serverIndex = -1;

		while((serverIndex = this.getFirstAvailableServerIndex()) >= 0 && this.clientQueue.size() > 0){

			this.serveClient(serverIndex);
		}
	}

	/**
	 * Method that returns the index of the first available server.
	 * 
	 * @return the index of the first available server. If no available server exists,
	 * it then returns -1.
	 */
	int getFirstAvailableServerIndex(){

		for(int i = 0; i < this.servers; i++)
			if(this.serverStateArr[i].getTimeToReleaseServer() == 0)
				return i;

		return -1;
	}

	/**
	 * Method that returns a normalized pseudo random double.
	 * The normalization gives floating-point numbers in the range
	 * [-0.5, 0.5].
	 * @return the normalized floating-point number.
	 */
	public double normalizedNext(){

		double next = (double) this.generator.next();
		next = next/(this.generator.getMod() - 1.0);
		next -= 0.5F;

		return next;
	}
		
	/**
	 * Method that returns the next random arrival time for a new client.
	 * @return the next random arrival time.
	 */
	public int nextRandomArrivalTime(){

		// Consider the current system time for computing the next arrival time
		return this.currSystemTime + nextRandomTime(this.arrivalsInterval);
	}

	/**
	 * Method that returns the next random service time for a new client.
	 * @return the next random service time.
	 */
	public int nextRandomServiceTime(){

		return nextRandomTime(this.serviceTime);
	}
	
	/**
	 * Method that computes a random time considering a window of +-50% 
	 * of a given time. 
	 * @param time
	 * @return the next random time
	 */
	public int nextRandomTime(int time){

		return (int) Math.ceil(time * (1.0 + this.normalizedNext()));
	}
}