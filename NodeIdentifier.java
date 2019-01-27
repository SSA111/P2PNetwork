import java.io.*; 
import java.util.*;

public class NodeIdentifier implements Serializable {

    // Variabels 
    private String ip;
    private int port;
    private UUID id;

    public NodeIdentifier() {

    }
        	
    public NodeIdentifier(String ip, int port, UUID id) {
        this.ip = ip; 
        this.port = port;
        this.id = id;
    }

    // Setters 

    public void setIP(String ip) {
        this.ip = ip;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    // Getters 

    public String getIP() {
        return ip;
    }

    public UUID getId() {
        return id;
    }

    public int getPort() {
        return port;
    }
}