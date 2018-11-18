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
	private int[] timeToReleaseServer;	// Server-wise time to release the resource
	private Queue<Integer> clientQueue;
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
		this.timeToReleaseServer = new int[servers];

		this.clientQueue = new Queue<>();
		this.servedClientsCount = 0;
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
	 * 
	 */
	public void runSimulation(){

		// We assume that the first client always arrives at time 0 seconds.

		// The total time required to serve the first client.
		int firstClientServiceTime = (int) Math.ceil(this.serviceTime * (1.0 + this.normalizedNext()));

		int firstAvailableServerIndex = this.getFirstAvailableServerIndex();

		if(firstAvailableServerIndex >= 0){

			this.assignClientToServer(firstClientServiceTime, firstAvailableServerIndex);
		}
		else{

			this.placeClientInQueue(firstClientServiceTime);
		}

		servedClientsCount++;

		while(servedClientsCount < this.clients){
			
			/*
				Steps:

				1. Update the time to release each server.
				2. Check whether there is a new client arrival.
				3. Check whether a client can be served.
				4. Increment the system current time.
			*/

			this.updateTimeToReleaseServersTime();
			this.enqueueNewClient();
			this.serveAllPossibleClients();
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
			if(this.timeToReleaseServer[i] > 0){

				this.timeToReleaseServer[i]--;

				if(this.timeToReleaseServer[i] == 0)
					this.servedClientsCount++;
			}
		}
	}

	public void enqueueNewClient(){

		if(this.currSystemTime == this.nextClientArrivalTime){

			this.placeClientInQueue(this.nextRandomServiceTime());
			this.nextClientArrivalTime = this.nextRandomArrivalTime();
		}
	}

	/**
	 * Method that adds a client to the queue, because no servers
	 * were available at the client's arrival time.
	 * @param clientServiceTime the total time required to serve 
	 * the client.
	 */
	void placeClientInQueue(int clientServiceTime){

		this.clientQueue.add(clientServiceTime);
	}

	/**
	 * Method that assigns a client to a server
	 * @param clientServiceTime the total time required to serve the client.
	 * @param firstAvailableServerIndex the index of the server to which the 
	 * client will be assigned.
	 */
	void assignClientToServer(int clientServiceTime, int firstAvailableServerIndex){

		this.timeToReleaseServer[firstAvailableServerIndex] = clientServiceTime;
	}

	/**
	 * Method that returns the index of the first available server.
	 * 
	 * @return the index of the first available server. If no available server exists,
	 * it then returns -1.
	 */
	int getFirstAvailableServerIndex(){

		for(int i = 0; i < this.servers; i++)
			if(this.timeToReleaseServer[i] == 0)
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

		return Math.ceil(time * (1.0 + this.normalizedNext()));
	}
}