 import java.io.Serializable;

 public class RecoveryMessage implements Serializable {

    // Variabels
    private NodeIdentifier nodeIdentifier;

    public RecoveryMessage(NodeIdentifier nodeIdentifier) { 
        this.nodeIdentifier = nodeIdentifier;
    }

    // Getters 

    public NodeIdentifier getNodeIdentifier() {
        return nodeIdentifier; 
    }

    // Setters 

    public void setNodeIdentifier(NodeIdentifier nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier; 
    }
}
