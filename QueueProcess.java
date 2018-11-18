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
	
	private int time;	// In seconds
	private IntegerSequenceGenerator generator;
	private int[] timeToRelease;	// Server-wise time to release the resource
	
	/**
		Constructor
		This is the default constructor. Please note this receives an
		already instanced IntegerSequenceGenerator.
		@param	generator		Pseudo-random numbers generator.
		@param	clients			Number of clients that will arrive.
		@param	servers			Number of servers in the system.
		@param	arrivalsRateM	Arrivals per minute.
		@param	serviceRateM	Serviced clients per minute, whole system.
	*/
	public QueueProcess(IntegerSequenceGenerator generator, int clients, 
		int servers, int arrivalsRateM, int serviceRateM) {
		this.generator = generator;
		
		this.clients = clients;
		this.servers = servers;
		this.arrivalsInterval = arrivalsRateToTime(arrivalsRateM);
		this.serviceTime = serviceRateToTime(serviceRateM);
		
		this.time = 0;
		this.timeToRelease = new int[servers];
	}
	
	/**
		Full constructor
		This constructor instantiates a QueueProcess creating its own
		IntegerSequenceGenerator.
		@param	
		@param	clients			Number of clients that will arrive.
		@param	servers			Number of servers in the system.
		@param	arrivalsRate	Arrivals per minute.
		@param	serviceRate		Serviced clients per minute per server.
	*/
	public QueueProcess(int seed, int multiplier, int increment, int mod,
		int clients, int servers, int arrivalsRate, int serviceRate) {
		this.generator = new IntegerSequenceGenerator(seed, multiplier,
			increment, mod);
		
	}
}