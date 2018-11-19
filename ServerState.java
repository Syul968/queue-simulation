/**
    ServerState
    This class represents a queue-system server
    by storing its state data: client it is serving
    and time before it is released.
    
    @author		A07104218	Salvador Orozco Villalever
    @author		A01328937	Luis Francisco Flores Romero
    @version	1.0
    @since		17.nov.2018
*/
public class ServerState{

    private int clientId;
    private int timeToReleaseServer;    // In seconds

    /**
        Constructor
        Initializes a ServerState with no client (ID -1)
        and time to be released of 0 (seconds).
    */
    public ServerState(){

        this.clientId = -1;
        this.timeToReleaseServer = 0;
    }
    
    /**
        Full constructor
        Initializes a ServerState attending specified client
        and set to be realeased after specified time (seconds).
        @param    clientId               The ID of the client being served.
        @param    timeToReleaseServer    Time before this server is released.
    */
    public ServerState(int clientId, int timeToReleaseServer){

        this.setClientId(clientId);
        this.setTimeToReleaseServer(timeToReleaseServer);
    }
    
    /**
        Set client ID
        Assign a client (by ID) to this server.
        @param    clientId    The ID of the client assigned to this server.
        @return   Nothing.
    */
    public void setClientId(int clientId){

        this.clientId = clientId;
    }

    /**
        Set time to release server
        Assign time this server will be attending current client.
        @param    timeToReleaseServer    Time before this server is released.
        @return   Nothing
    */
    public void setTimeToReleaseServer(int timeToReleaseServer){

        this.timeToReleaseServer = timeToReleaseServer;
    }
    
    /**
        Get client ID
        Get the ID of the client being attended by this server.
        @return    ID of current client.
    */
    public int getClientId(){

        return this.clientId;
    }

    /**
        Get time to release server
        Get the time remaining for this server to be released.
        @return    Time remaining to be released.
    */
    public int getTimeToReleaseServer(){

        return this.timeToReleaseServer;
    }

    /**
        Tick
        Go one time step (one second) forward.
        @return    Nothing.
    */
    public void tick(){

        this.timeToReleaseServer--;
    }
}