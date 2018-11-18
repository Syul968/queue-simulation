public class ServerState{

    private int clientId;
    private int timeToReleaseServer;

    public ServerState(){

        this.clientId = -1;
        this.timeToReleaseServer = 0;
    }

    public ServerState(int clientId, int timeToReleaseServer){

        this.setClientId(clientId);
        this.setTimeToReleaseServer(timeToReleaseServer);
    }

    public void setClientId(int clientId){

        this.clientId = clientId;
    }

    public void setTimeToReleaseServer(int timeToReleaseServer){

        this.timeToReleaseServer = timeToReleaseServer;
    }

    public int getClientId(){

        return this.clientId;
    }

    public int getTimeToReleaseServer(){

        return this.timeToReleaseServer;
    }

    public void tick(){

        this.timeToReleaseServer--;
    }
}