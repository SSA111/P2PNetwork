import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.*;
import java.io.*;

public class PutClient {

    public static void main(String[] args) {
        
        try {

            // Get Address From Arguments
            String anAddress = args[0];

            // Get Port From Arguments 
            int aPort = Integer.parseInt(args[1]);
            
            // Get Key From Arguments 
            int aKey = Integer.parseInt(args[2]);

            // Get Value From Arguments 
            String aValue = args[3];

            // Make Sure "anAddress", "aPort", "aKey" and "aValue" Is Valid 
            if (!(anAddress == null) && !(aPort == -1) && !(aKey == -1) && !(aValue == null)) {

                // Put Message  
                putMessage(anAddress, aPort, aKey, aValue);
            }
            
        } catch (Exception anException) {

            // Print Out Exception 
            System.out.println("Exception : " + anException.getMessage()); 
        }
	}

    public static void putMessage(String anAddress, int aPort, int aKey, String aValue) {

		try {

            // Create PutMessage With Data
            PutMessage aPutMessage = new PutMessage(aKey, aValue);

            // Define Socket
			Socket aSocket = new Socket(anAddress, aPort);
            
            // Get Object Output Stream
            ObjectOutputStream anObjectOutputStream = new ObjectOutputStream(aSocket.getOutputStream()); 

            // Send Object Via Object Output Stream 
            anObjectOutputStream.writeObject(aPutMessage);

            // Close Socket
			aSocket.close();

		} catch (IOException anException) {
			// Print Out Exception
			System.err.println("IOException : " + anException.getMessage());
		} catch (Exception anException) {
			// Print Out Exception
			System.err.println("Exception : " + anException.getMessage());
		}
	}
}