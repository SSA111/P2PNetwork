import java.io.Serializable;
import java.util.*;

public class RetrieveAllMessage implements Serializable {

    // Variabels
    private String anInternalIP;
    private int anInternalPort;
    private String anExternalIP;
    private int anExternalPort;
    private HashMap<Integer, String> aMessagesMap;  

    public RetrieveAllMessage(String anInternalIP, int anInternalPort, String anExternalIP, int anExternalPort) { 
        this.anInternalIP = anInternalIP; 
        this.anInternalPort = anInternalPort;
        this.anExternalIP = anExternalIP;
        this.anExternalPort = anExternalPort;
    }
    
    // Setters 

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

    public void setMessagesMap(HashMap<Integer, String> aMessagesMap) {
        this.aMessagesMap = aMessagesMap;
    }

    // Getters 

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
 
    public HashMap<Integer, String> getMessagesMap() {
        return aMessagesMap;
    }
 
}
