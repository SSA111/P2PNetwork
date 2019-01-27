import java.io.Serializable;
 
public class GetMessage implements Serializable {

    // Variabels
    private String ip;
    private int key;
    private int port;
    private int from; 
    private boolean isForwarded; 

    public GetMessage(String ip, int port, int key) { 
        this.ip = ip; 
        this.key = key;
        this.port = port;
        this.from = from; 
    }
    
    // Setters 

    public void setIP(String ip) {
        this.ip = ip;
    }

    public void setKey(int key) {
        this.key = key;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public void setFrom(int from) {
        this.from = from;
    }

    public void setForwarded(boolean isForwarded) {
        this.isForwarded = isForwarded;
    }

    // Getters 

    public String getIP() {
        return ip;
    }

    public int getKey() {
        return key;
    }

    public int getPort() {
        return port;
    }

    public int getFrom() {
        return from; 
    }

    public boolean getForwarded() {
        return isForwarded; 
    }
   
}
