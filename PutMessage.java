 import java.io.Serializable;

 public class PutMessage implements Serializable {

    // Variabels
    private int key;
    private String value;

    public PutMessage(int key, String value) {
        this.key = key;
        this.value = value;
    }

    // Getters 

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value; 
    }    
}
