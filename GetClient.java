import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.net.*;
import java.io.*;

public class GetClient {

    public static void main(String[] args) {
        
        try {

            // Get Internal IP From Arguments
            String anInternalIP = args[0];

            // Get External IP From Arguments
            String anExternalIP = args[1];

            // Get Port From Arguments 
            int anExternalPort = Integer.parseInt(args[2]);
            
            // Get Key From Arguments 
            int aKey = Integer.parseInt(args[3]);
     
            // Set Internal Retrieval Port
            int anInternalPort = 5010; // Integer.parseInt(args[3]);

            // Make Sure "anAddress", "aPort" and "aKey" Is Valid 
            if (!(anInternalIP == null)  && !(anInternalPort == -1) && !(anExternalIP == null) && !(anExternalPort == -1) && !(aKey == -1)) {

                // Create New Socket
                ServerSocket aSocket = new ServerSocket(anInternalPort);
                aSocket.setSoTimeout(10000);
                
                // Create New Thread To Listen For Replies 
                Thread aThread = new Thread(() -> retrieveResponse(aSocket)); 
    	        aThread.start();

                // Get Message  
                getMessage(anInternalIP, anInternalPort, anExternalIP, anExternalPort, aKey);
            }
            
        } catch (Exception anException) {

            // Print Out Exception 
            System.out.println("Exception : " + anException.getMessage()); 
        }
	}

    public static void getMessage(String anInternalIP, int anInternalPort, String anExternalIP, int anExternalPort, int aKey) {
        
		try {

            // Create GetMessage With Data
            GetMessage aGetMessage = new GetMessage(anInternalIP, anInternalPort, aKey);

            // Define Socket
			Socket aSocket = new Socket(anExternalIP, anExternalPort);
            
            // Get Object Output Stream
            ObjectOutputStream anObjectOutputStream = new ObjectOutputStream(aSocket.getOutputStream()); 

            // Send Object Via Object Output Stream 
            anObjectOutputStream.writeObject(aGetMessage);

		} catch (IOException anException) {
			// Print Out Exception
			System.err.println("IOException : " + anException.getMessage());
		} catch (Exception anException) {
			// Print Out Exception
			System.err.println("Exception : " + anException.getMessage());
		}
	}

    public static void retrieveResponse(ServerSocket socket) {
    	try {
	    	
            // Retrieve Reply From Socket
	    	Socket aReplySocket = socket.accept();    	
            
            // Get Object Input Stream From Client 
            ObjectInputStream anObjectInputStream = new ObjectInputStream(aReplySocket.getInputStream()); 

            // Get Object From Object Input Stream 
            PutMessage aPutMessage = (PutMessage) anObjectInputStream.readObject();

            // Check If Result Was Recieved 
            if (aPutMessage != null) {

                // Print Out Response 
                System.out.println("Put : " + aPutMessage.getKey() + " " + aPutMessage.getValue());
            }else {

                // Print Out Response 
                System.out.println("Data didn't exist");
            }

            // Close Socket 
            aReplySocket.close();

            // Exit  
            System.exit(0);

    	} catch(Exception anException) {
            // Print Out Exception
    		System.out.println("Exception : " + anException.getMessage());
    	}
    }    
}