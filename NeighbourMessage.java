 import java.io.Serializable;
 import java.util.*;

public class NeighbourMessage implements Serializable {

    // Variabels
    private String anInternalIP;
    private int anInternalPort;
    private String anExternalIP;
    private int anExternalPort;
    private HashMap<Location, NodeIdentifier> aNodeMap;
    private Location aCrashLocation; 

    public NeighbourMessage(String anInternalIP, int anInternalPort, String anExternalIP, int anExternalPort) { 
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
    
    public void setNodeMap(HashMap<Location, NodeIdentifier> aNodeMap) {
        this.aNodeMap = aNodeMap;
    }

    public void setCrashLocation(Location aCrashLocation) {
        this.aCrashLocation = aCrashLocation;
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

    public HashMap<Location, NodeIdentifier> getNodeMap() {
        return aNodeMap;
    }

    public Location getCrashLocation() {
        return aCrashLocation; 
    }
    

}
