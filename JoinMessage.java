 import java.io.Serializable;
 import java.util.*;

 public class JoinMessage implements Serializable {

    // Variabels
    private UUID anId;
    private String anInternalIP;
    private int anInternalPort;
    private String anExternalIP;
    private int anExternalPort;

    public JoinMessage(String anInternalIP, int anInternalPort, String anExternalIP, int anExternalPort) { 
        this.anInternalIP = anInternalIP; 
        this.anInternalPort = anInternalPort;
        this.anExternalIP = anExternalIP;
        this.anExternalPort = anExternalPort;
    }
    
    // Setters 
    
    public void setId(UUID anId) {
        this.anId = anId;
    }

    public void setInternalIP(String anInternalIP) {
        this.anInternalIP = anInternalIP;
    }
    
    public void setInternalPort(int anInternalPort) {
        this.anInternalPort = anInternalPort;
    }

    public void setExternalIP(String anExternalIP) {
        this.anExternalIP = anExternalIP;
    }
    
    public void setExternalPort(int anExternalPort) {
        this.anExternalPort = anExternalPort;
    }

    // Getters 

    public UUID getId() {
        return anId;
    }

    public String getInternalIP() {
        return anInternalIP;
    }

    public int getInternalPort() {
        return anInternalPort;
    }
    
    public String getExternalIP() {
        return anExternalIP;
    }

    public int getExternalPort() {
        return anExternalPort;
    }
}
