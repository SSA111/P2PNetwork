 import java.io.Serializable;

 public class ArrangementMessage implements Serializable {

    // Variabels
    private Location location;
    private NodeIdentifier nodeIdentifier;

    public ArrangementMessage(Location location, NodeIdentifier nodeIdentifier) { 
        this.location = location; 
        this.nodeIdentifier = nodeIdentifier;
    }

    // Getters 

    public Location getLocation() {
        return location; 
    }

    public NodeIdentifier getNodeIdentifier() {
        return nodeIdentifier; 
    }

    // Setters 

    public void setLocation(Location location) {
        this.location = location; 
    }

    public void setNodeIdentifier(NodeIdentifier nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier; 
    }
}
