import java.io.*;
import java.net.*;
import java.util.*;

public class Node implements Serializable {

    // Variabels 
    private NodeIdentifier aNodeIdentifier = new NodeIdentifier();
    private HashMap<Integer, String> aMessagesMap = new HashMap<Integer, String>(); 
    private HashMap<Location, NodeIdentifier> aNodeMap = new HashMap<Location, NodeIdentifier>(); 
    private HashMap<Location, NodeIdentifier> aNeighbourNodeMap = new HashMap<Location, NodeIdentifier>(); 

    public static void main(String[] args) {

        // Get Node IP From Arguments 
        String anInternalIP = args[0];   

        // Get Node Port From Arguments 
        int anInternalPort = Integer.parseInt(args[1]);   

        // Generate Unique Id 
        UUID anId = UUID.randomUUID(); 

        // Create A New Node 
        Node aNode = new Node(anInternalIP, anInternalPort, anId);

        // If Sufficient Parameters Are Given, Then Create New Node With Neighbour Node 
        if (args.length > 3) {      

            // Get Node IP From Arguments 
            String anExternalIP = args[2];   

            // Get Node Port From Arguments 
            int anExternalPort = Integer.parseInt(args[3]);

            // Generate Another Unique Id 
            UUID anotherId = UUID.randomUUID(); 

            // Create Node Identifier Of Joining Node 
            NodeIdentifier anExternalNodeIdentifier = new NodeIdentifier(anExternalIP, anExternalPort, anotherId); 

            // Join Nodes
            aNode.join(anExternalNodeIdentifier);   
        }        
    }

    //// NODE CONSTRUCTION //// 

    public Node(String anInternalIP, int anInternalPort, UUID anId) {

        // Set Port (This Nodes Own Port)
        aNodeIdentifier.setIP(anInternalIP); 
        aNodeIdentifier.setPort(anInternalPort); 
        aNodeIdentifier.setId(anId); 

        // Define Thread For Listening For Messages 
        Thread aMessageListeningThread = new Thread(() -> listenForMessages()); 
        aMessageListeningThread.start();

        // Start Pinging For Broader Awareness Of Other Nodes / Peers 
        Thread aPingThread = new Thread(() -> ping()); 
        aPingThread.start(); 
    }

    //// MESSAGE LISTENING //// 

    private void listenForMessages() {    

        try {  
        
            // Get Port 
            int aPort = aNodeIdentifier.getPort();  
                        
            // Print Out That We Are Listening
            System.out.println("*** " + aPort + " ***"); 
            System.out.println("Listening For Messages");

            // Declare ServerSocket 
            ServerSocket aListeningServerSocket = new ServerSocket(aPort);

            // Listen As Long As Node Is Alive 
            while (true) {

                // Accept Incoming Data From Socket 
                Socket aSocket = aListeningServerSocket.accept();        

                // Get Object Input Stream From Client 
                ObjectInputStream anObjectInputStream = new ObjectInputStream(aSocket.getInputStream()); 

                // Get Object From Object Input Stream 
                Object anObject = anObjectInputStream.readObject();

                // Handle Message 
                handleMessage(anObject);   

                // Debug Connections 
                // printNeighbours();
        
            }
         
        } catch (IOException anException) {
            // Print Out Exception
            System.out.println("IOException :" + anException.getMessage()); 
        } catch (Exception anException) {
            // Print Out Exception
            System.err.println("Exception : " + anException);    
                       
        }
    }

    //// PING ////

    private void ping() {

        // As Long As The Node Is Alive Ping Peers  
        while (true) {

            // Delay Ping 
            try {
                Thread.sleep(5000);
            } catch (InterruptedException anException) {
                // Print Out Exception 
                System.out.println("InterruptedException: " + anException.getMessage()); 
            }

            // Prevent Concurrency Issues 
            synchronized (aNodeMap) {

                // Loop Over Nodes 
               for (Map.Entry<Location, NodeIdentifier> anEntry : aNodeMap.entrySet()) {
                   
                    // Get Node Identifier 
                    NodeIdentifier anExternalNodeIdentifier = (NodeIdentifier) anEntry.getValue();

                    // Make Sure NodeIdentifier Is Not Null
                    if (anExternalNodeIdentifier != null) {

                        // Create Ping Message 
                        String aString = "Ping";

                        // Send Message 
                        sendMessage(anExternalNodeIdentifier, aString);
                    }
                }
            }
                  
        }
    }

    //// MESSAGE HANDLING ////

    private void handleMessage(Object anObject) {
         
        // Determine Message Type 
        if (anObject instanceof GetMessage) {

            // Retrieve Get Message From Object
            GetMessage aGetMessage = (GetMessage) anObject;

            // Get Key From Message 
            int aKey = aGetMessage.getKey(); 

            // Get Message From Message Map 
            String aValue = aMessagesMap.get(aKey); 

            // Print Out That Request Was Received 
            System.out.println("Get Received : " + aValue);

            // Create Put Message With Get Message Key And Found Value
            PutMessage aPutMessage = new PutMessage(aKey, aValue);

            // Create Node Identifier 
            NodeIdentifier anotherNodeIdentifier = new NodeIdentifier(aGetMessage.getIP(), aGetMessage.getPort(), null);

            // Send Response 
            sendMessage(anotherNodeIdentifier, aPutMessage);     

        }else if (anObject instanceof PutMessage) {

            // Retrieve Put Message From Object
            PutMessage aPutMessage = (PutMessage) anObject;

            // Get Key From Message 
            int aKey = aPutMessage.getKey(); 

            // Get Message From Message Map 
            String aValue = aPutMessage.getValue(); 

            // Only Insert If Not Already Inserted 
            if (!(aMessagesMap.containsKey(aKey) && aMessagesMap.get(aKey).equals(aValue))) {
                
                // Print Out That Message Was Received 
                System.out.println("Put Received : " + aKey + " " + aValue);

                // Insert Message Into Messages Map 
                aMessagesMap.put(aKey, aValue);

                // Send To All Other Neighbour Nodes 
                duplicateAtNeighbours(aKey, aValue);
            }
        }else if (anObject instanceof RetrieveAllMessage) {
            
            // Get Retrieve Message From Object
            RetrieveAllMessage aRetrieveMessage = (RetrieveAllMessage) anObject;

            // If Messages Map Is Null, Then We Are In The Process Of Retriveing The Messages Map
            if (aRetrieveMessage.getMessagesMap() != null) {

                // Set Message Map 
                aMessagesMap = aRetrieveMessage.getMessagesMap(); 

            }else {

                // Set Messages Map 
                aRetrieveMessage.setMessagesMap(aMessagesMap); 

                // Create NodeIdentifier Of Node Sendig This Retrieve Message To This Node 
                NodeIdentifier anInternalNodeIdentifier = new NodeIdentifier(aRetrieveMessage.getInternalIP(), aRetrieveMessage.getInternalPort(), null);

                // Respond With Messages Map 
                sendMessage(anInternalNodeIdentifier, aRetrieveMessage);   
            }             
        }else if (anObject instanceof JoinMessage) {

            // Get Join Message From Object
            JoinMessage aJoinMessage = (JoinMessage) anObject;

            // If Id Is Null, Then We Are In The Process Of Retrieving The Id Of This Node 
            if (aJoinMessage.getId() != null) {  

                // Get Node Identifier For Joining Node 
                NodeIdentifier aPreviousNodeIdentifier = new NodeIdentifier(aJoinMessage.getExternalIP(), aJoinMessage.getExternalPort(), aJoinMessage.getId());
                
                // Add As Neighbour 
                addNeighbour(Location.Previous, aPreviousNodeIdentifier);
                
                // Get Existing Neighbours 
                retrieveNeighbours(aPreviousNodeIdentifier, null);             

            }else {          

                // Set Id
                aJoinMessage.setId(aNodeIdentifier.getId()); 

                // Create NodeIdentifier Of Node Sendig This Join Message To This Node 
                NodeIdentifier anInternalNodeIdentifier = new NodeIdentifier(aJoinMessage.getInternalIP(), aJoinMessage.getInternalPort(), aJoinMessage.getId());

                // Send Message With Id 
                sendMessage(anInternalNodeIdentifier, aJoinMessage);   
            }

        }else if (anObject instanceof ArrangementMessage) {

            // Get Arrangement Message From Object
            ArrangementMessage anArrangementMessage = (ArrangementMessage) anObject;

            // Get Location 
            Location aLocation = anArrangementMessage.getLocation(); 

            // Create Node Identifier
            NodeIdentifier anotherNodeIdentifier = anArrangementMessage.getNodeIdentifier(); 

            // Rearrange Neighbours 
            rearrangeNeighbours(aLocation, anotherNodeIdentifier); 

        } else if (anObject instanceof NeighbourMessage) {

            // Get Neighbour Message From Object
            NeighbourMessage aNeighbourMessage = (NeighbourMessage) anObject;

            // If Node Map Is Null, Then We Are In The Process Of Retriveing The Node Map
            if (aNeighbourMessage.getNodeMap() != null) {

                // Set Node Map 
                aNeighbourNodeMap = aNeighbourMessage.getNodeMap(); 

                // If Crash Location Of Neighbour Message Is Null Then We Are In The Process Of Joining Nodes
                // Otherwise We Are In A Process Of Crash Recovery
                if (aNeighbourMessage.getCrashLocation() == null) {

                    // Determine Number Of Nodes Joining 
                    switch (numberOfNodes(aNeighbourNodeMap)) {
                        case 0: 

                            // Establish Neighbour Connections, If The First Another Node Is Joining 
                            addNeighbour(Location.Next, aNodeMap.get(Location.Previous));
                            tellNeighbour(aNodeMap.get(Location.Previous), Location.Next);
                            tellNeighbour(aNodeMap.get(Location.Next), Location.Previous);

                            break;
                        case 2:                  

                            // Establish Neighbour Connections, If The Second Another Node Is Joining 
                            addNeighbour(Location.PreviousPrevious, aNeighbourNodeMap.get(Location.Previous));
                            addNeighbour(Location.Next, aNeighbourNodeMap.get(Location.Previous));
                            addNeighbour(Location.NextNext, aNodeMap.get(Location.Previous));
                            tellAllNeighbours();

                            break;
                        default: 

                            // Establish Neighbour Connections, If More Than Second Node Is Joining 
                            addNeighbour(Location.Next, aNeighbourNodeMap.get(Location.Next));
                            addNeighbour(Location.NextNext, aNeighbourNodeMap.get(Location.NextNext));
                            addNeighbour(Location.PreviousPrevious, aNeighbourNodeMap.get(Location.Previous));
                            tellAllNeighbours();

                            break;
                            
                    }                   
    
                    // Get Messages From Neighbours 
                    retrieveMessagesFromNeighbour(Location.Previous); 

                }else {

                    // Get Crash Location 
                    Location aCrashLocation = aNeighbourMessage.getCrashLocation(); 

                    // Determine Which Location We Want To Recover. 
                    switch (aCrashLocation) {

                        case PreviousPrevious:  

                            // I Experienced A Case Where Previous Previous Was Not Reassigned Correctly After A Crash.
                            // It Occured When The Network Shrinked From An Even Number Of Nodes To An Uneven Number.
                            // Therefore We Now Check If The Number Of Nodes Is Even Or Uneven And Assign Either The Neighbours
                            // Previous Or The Neighbours Previous Previous
                            if ((numberOfNodes(aNeighbourNodeMap) % 2 ) == 0) {
                                
                                // Add New Neighbour      
                                addNeighbour(Location.PreviousPrevious, aNeighbourNodeMap.get(Location.Previous));
                            }else {
                                // Add New Neighbour      
                                addNeighbour(Location.PreviousPrevious, aNeighbourNodeMap.get(Location.PreviousPrevious));
                            }                                    

                            break;
                        case Previous:            

                            // Add New Neighbour      
                            addNeighbour(Location.PreviousPrevious, aNeighbourNodeMap.get(Location.Previous));                                   
                    
                            break; 
                        case Next:      

                            // Add New Neighbour 
                            addNeighbour(Location.NextNext, aNeighbourNodeMap.get(Location.Next));                                          
                 
                            break;
                        case NextNext:    

                            // I Experienced A Case Where Next Next Was Not Reassigned Correctly After A Crash.
                            // It Occured When The Network Shrinked From An Even Number Of Nodes To An Uneven Number.
                            // Therefore We Now Check If The Number Of Nodes Is Even Or Uneven And Assign Either The Neighbours
                            // Next Or The Neighbours Next Next
                            if ((numberOfNodes(aNeighbourNodeMap) % 2 ) == 0) {
                                
                                // Add New Neighbour      
                                addNeighbour(Location.NextNext, aNeighbourNodeMap.get(Location.Next));
                            }else {
                                // Add New Neighbour      
                                addNeighbour(Location.NextNext, aNeighbourNodeMap.get(Location.NextNext));
                            }                                                                    
                            
                            break;
                    }
                }

            }else {

                // Set Node Map 
                aNeighbourMessage.setNodeMap(aNodeMap); 

                // Create NodeIdentifier Of Node Sendig This Neighbour Message To This Node 
                NodeIdentifier anInternalNodeIdentifier = new NodeIdentifier(aNeighbourMessage.getInternalIP(), aNeighbourMessage.getInternalPort(), null);

                // Respond With Nodes Map 
                sendMessage(anInternalNodeIdentifier, aNeighbourMessage);   
            }  
        }else if (anObject instanceof RecoveryMessage) {

            // Get Recovery Message From Object
            RecoveryMessage aRecoveryMessage = (RecoveryMessage) anObject;

            // Get Node Identifier 
            NodeIdentifier anExternalNodeIdentifier = aRecoveryMessage.getNodeIdentifier();

            // Recover From Node Crash 
            recover(anExternalNodeIdentifier); 

        } 
    }

   public void retrieveMessagesFromNeighbour(Location aLocation) {

        // Get Node Identifier Based On Location 
        NodeIdentifier anExternalNodeIdentifier = aNodeMap.get(aLocation); 

        // Create Retrieve Message 
        RetrieveAllMessage aRetrieveMessage = new RetrieveAllMessage(aNodeIdentifier.getIP(), aNodeIdentifier.getPort(), anExternalNodeIdentifier.getIP(), anExternalNodeIdentifier.getPort());

        // Send Message 
        sendMessage(anExternalNodeIdentifier, aRetrieveMessage); 
    }
 
    //// MESSAGE SENDING ////

    private void sendMessage(NodeIdentifier anIdentifier, Object anObject) {
        try {
            // Define Socket
			Socket aSocket = new Socket(anIdentifier.getIP(), anIdentifier.getPort());

            // Get Object Output Stream
            ObjectOutputStream anObjectOutputStream = new ObjectOutputStream(aSocket.getOutputStream()); 

            // Send Object Via Object Output Stream 
            anObjectOutputStream.writeObject(anObject);

        } catch (ConnectException anException) {

            // Print Out That A Node Has Crashed 
            System.out.println("Node Crash : " + anIdentifier.getPort()); 

            // Handle Node Crash
            handleCrash(anIdentifier);           
          
        } catch (Exception anException) {
	        // Print Out Exception
			System.err.println("Exception : " + anException.getMessage());       
       }
    }

    //// NODE JOINING ////

   public void join(NodeIdentifier anExternalNodeIdentifier) {

        // Create Node Join Message 
        JoinMessage aJoinMessage = new JoinMessage(aNodeIdentifier.getIP(), aNodeIdentifier.getPort(), anExternalNodeIdentifier.getIP(), anExternalNodeIdentifier.getPort()); 

        // Send Message 
        sendMessage(anExternalNodeIdentifier, aJoinMessage); 
    } 

    //// NODE LINKING ////

    private void addNeighbour(Location aLocation, NodeIdentifier anotherNodeIdentifier) {

        // Prevent Concurrency Issues 
        synchronized (aNodeMap) {
            // Add Node To Location At Node Map
            if (anotherNodeIdentifier != null && anotherNodeIdentifier.equals(aNodeIdentifier)) {     

                // Just Add Null At Location 
                aNodeMap.put(aLocation, null);   

            }else {

                // Add Location For Node Identifier 
                aNodeMap.put(aLocation, anotherNodeIdentifier);
            } 
        }
    }

    //// NODE REARRANGING //// 

    public void rearrangeNeighbours(Location aLocation, NodeIdentifier anotherNodeIdentifier) {

        if (aLocation.equals(Location.Previous)) {

            // The Old Previous Is Now The New Previous Previous 
            addNeighbour(Location.PreviousPrevious, aNodeMap.get(Location.Previous)); 

            // The New Neighbour Is The New Previous 
            addNeighbour(Location.Previous, anotherNodeIdentifier);

        } else if (aLocation.equals(Location.Next)) {

            // The Old Next Is Now The New Next Next 
            addNeighbour(aLocation.NextNext, aNodeMap.get(aLocation.Next));

            // The New Neighbour Is The New Next 
            addNeighbour(aLocation.Next, anotherNodeIdentifier); 
        } else  {

            // Update Next Next And Previous Previous
            addNeighbour(aLocation, anotherNodeIdentifier); 
        }

    }

    private void tellNeighbour(NodeIdentifier anExternalNodeIdentifier, Location aLocation) {

        // Create Arrangement Message 
        ArrangementMessage anArrangementMessage = new ArrangementMessage(aLocation, aNodeIdentifier); 

        // Send Message 
        sendMessage(anExternalNodeIdentifier, anArrangementMessage);
    }

    //// NODE BACKUPING //// 

   private void duplicateAtNeighbours(int aKey, String aValue) {

        // Prevent Concurrency Issues 
        synchronized (aNodeMap) {

            // Get Nodes From Node Map
            Object[] someNodes = aNodeMap.values().toArray(); 

            // Loop Over Nodes 
            for (Object anObject : someNodes) {

                // Make Sure Object Is Not Null
                if (anObject != null) {

                    // Create Put Message 
                    PutMessage aPutMessage = new PutMessage(aKey, aValue);

                    // Get Node Identifier 
                    NodeIdentifier anExternalNodeIdentifier = (NodeIdentifier) anObject;

                    // Print Out That The Message Was Duplicated
                    System.out.println("Put Duplicated : " + aKey + " " + aValue); 

                    // Send Message 
                    sendMessage(anExternalNodeIdentifier, aPutMessage);
                }
            }
        }
    } 

    //// NODE CALCULATION //// 

    private int numberOfNodes(Map<Location, NodeIdentifier> aMap) {
        
        // Define Counter  
        int aCount = 0;

        // Loop Over Nodes In Map
        for (Map.Entry<Location, NodeIdentifier> aNode : aMap.entrySet()) {

            // Ensure That Node Is Not Null
            if (aNode.getValue() != null) {

                // Increment Counter
                aCount++;
            }
        }
        return aCount;
    }

    //// NODE RETRIEVAL //// 
    
    public void retrieveNeighbours(NodeIdentifier anExternalNodeIdentifier, Location aCrashLocation) {

        // Create Neighbour Message 
        NeighbourMessage aNeighbourMessage = new NeighbourMessage(aNodeIdentifier.getIP(), aNodeIdentifier.getPort(), anExternalNodeIdentifier.getIP(), anExternalNodeIdentifier.getPort());

        // Set Crash Location If We Are In A Process Of Crash Recovery 
        aNeighbourMessage.setCrashLocation(aCrashLocation);

        // Send Neighbour Message 
        sendMessage(anExternalNodeIdentifier, aNeighbourMessage); 
    } 

    //// NODE POSITION ////

    private List<Location> retrieveNode(NodeIdentifier anExternalNodeIdentifier) {

        // Define List Containing Found Nodes 
        List<Location> aLocationList = new ArrayList<>();

        // Prevent Concurrency Issues 
        synchronized (aNodeMap) {

            // Loop Over Locations In Node Map
            for (Location aLocation : aNodeMap.keySet()) {

                // Get Node Identifier At Location 
                NodeIdentifier anotherNodeIdentifier = aNodeMap.get(aLocation); 

                // Make Sure That The Node Retrived From Node Map Is Not Null
                if (anotherNodeIdentifier != null) {

                    // We Found The Node 
                    if (anotherNodeIdentifier.equals(anExternalNodeIdentifier)) {

                        // Add To List 
                        aLocationList.add(aLocation);
                    }
                }
            }
        }
        return aLocationList;
    }


    //// NODE NOTIFYING //// 

    private void tellAllNeighbours() {
        try {
            // Loop All Node Connections In Node Map
            for (Location aLocation : aNodeMap.keySet()) {

                // Get NodeIdentifier From Node Map
                NodeIdentifier anotherNodeIdentifier = aNodeMap.get(aLocation);

                // Tell Neighbours
                switch (aLocation) {
                    case Previous:
                    
                        // Tell Previous Neighbour Of Next  
                        tellNeighbour(anotherNodeIdentifier, Location.Next);

                        break;
                    case Next:

                        // Tell Next Neighbour Of Previous  
                        tellNeighbour(anotherNodeIdentifier, Location.Previous);

                        break;
                    case PreviousPrevious:

                        // Tell Previous Previous Neighbour Of Next Next  
                        tellNeighbour(anotherNodeIdentifier, Location.NextNext);

                        break;
                    case NextNext:
                        
                        // Tell Next Next Neighbour Of Previous Previous  
                        tellNeighbour(anotherNodeIdentifier, Location.PreviousPrevious);

                        break;
                }
            }
        } catch (Exception anException) {
            // Print Out Exception
            System.out.println("Exception:" + anException.getMessage());
        }        
    }

    //// NODE CRASH HANDLING - PART B /////

    private void handleCrash(NodeIdentifier anExternalNodeIdentifier) {

        // Print Out That We Are Recovering 
        System.out.println("Recovering : " + anExternalNodeIdentifier.getPort());
        
        // Recover Crash On This Node 
        recover(anExternalNodeIdentifier); 

        // Before The Pinging Functionality Was Implemented The Node Send Out A Recovery Message To All Neighbours If A Node Crashed. 
        // The Node Don't Need To Do This Anymore As Every Node Now Pings Its Neighbours And Handle Recovery From There. 
        // Prevent Concurrency Issues 
        /* synchronized (aNodeMap) {

            // Loop Over Entries In Node Map 
            for (Map.Entry<Location, NodeIdentifier> aNode : aNodeMap.entrySet()) {

                // Ensure That Node Is Not Null
                if (aNode.getValue() != null) {

                    // Create Crash Recovery Message 
                    RecoveryMessage aRecoveryMessage = new RecoveryMessage(anExternalNodeIdentifier); 

                    // Print Out That Recovery Messages Is Being Sent 
                    System.out.println("Recovery send : " + anExternalNodeIdentifier.getPort());

                    // Send Crash Recovery Message To Other Nodes  
                    sendMessage(aNode.getValue(), aRecoveryMessage);
                } 
            }
        }     */   
    }

    private void recover(NodeIdentifier anExternalNodeIdentifier) {

        // Get Crashed Node
        List<Location> someNodes = retrieveNode(anExternalNodeIdentifier); 

        // Loop Over Location For Crashed Node
        for (Location aLocation : someNodes) {
            switch (aLocation) {
                case PreviousPrevious: 

                    // Get Previous 
                    NodeIdentifier aPreviousNodeIdentifier = aNodeMap.get(Location.Previous); 

                    // Retrieve Neighbours Of Previous 
                    retrieveNeighbours(aPreviousNodeIdentifier, Location.PreviousPrevious);

                    break;
                case Previous:

                    // Get Previous Previous
                    NodeIdentifier aPreviousPreviousNodeIdentifier = aNodeMap.get(Location.PreviousPrevious); 

                    // Get Next 
                    NodeIdentifier aNextNodeIdentifier = aNodeMap.get(Location.Next); 
                    
                    // Check That NodeIdentifiers Is Not Null
                    if (aPreviousPreviousNodeIdentifier != null) {

                        // Add New Previous Neighbour
                        addNeighbour(Location.Previous, aNodeMap.get(Location.PreviousPrevious));
                        
                        // Retrieve Neighbours Of Previous Previous
                        retrieveNeighbours(aPreviousPreviousNodeIdentifier, Location.Previous);          

                    }else if (aNextNodeIdentifier != null) {

                        if (!aNextNodeIdentifier.equals(anExternalNodeIdentifier)) {

                            // Add New Previous Neighbour
                            addNeighbour(Location.Previous, aNextNodeIdentifier);

                        }else {
                            
                            // Add New Neighbour
                            addNeighbour(aLocation, null);
                        }
                    }else {

                        // Add New Neighbour
                        addNeighbour(aLocation, null);
                    }

                    break; 

                case Next:
                    
                    // Get Next Next
                    NodeIdentifier aNextNextNodeIdentifier = aNodeMap.get(Location.NextNext); 

                    // Get Previous 
                    NodeIdentifier anotherPreviousNodeIdentifier = aNodeMap.get(Location.Previous); 
                    
                    // Check That NodeIdentifiers Is Not Null
                    if (aNextNextNodeIdentifier != null) {

                        // Add New Next Neighbour
                        addNeighbour(Location.Next, aNodeMap.get(Location.NextNext));
                        
                        // Retrieve Neighbours Of Next Next
                        retrieveNeighbours(aNextNextNodeIdentifier, Location.Next); 

                    }else if (anotherPreviousNodeIdentifier != null) {

                        if (!anotherPreviousNodeIdentifier.equals(anExternalNodeIdentifier)) {

                            // Add New Next Neighbour
                            addNeighbour(Location.Next, anotherPreviousNodeIdentifier);

                        }else {
                            
                            // Add New Neighbour
                            addNeighbour(aLocation, null);
                        }
                    }else {
                        
                        // Add New Neighbour
                        addNeighbour(aLocation, null);
                    } 
    
                    break;
                case NextNext:

                    // Get Next 
                    NodeIdentifier anotherNextNodeIdentifier = aNodeMap.get(Location.Next); 

                    // Retrieve Neighbours Of Next 
                    retrieveNeighbours(anotherNextNodeIdentifier, Location.NextNext);

                    break;
                default:
                    break;
            }
        } 
    }

    //// DEBUGGING ////
    
    /* private void printNeighbours() {
        // Print Out Node Connections
        try {
            System.out.println("Next: " + aNodeMap.get(Location.Next).getPort());
            System.out.println("Previous: " + aNodeMap.get(Location.Previous).getPort());
            System.out.println("Next Next: " + aNodeMap.get(Location.NextNext).getPort());
            System.out.println("Previous Previous: " + aNodeMap.get(Location.PreviousPrevious).getPort());
        } catch (NullPointerException anException) {
            try {
                System.out.println("Next: " + aNodeMap.get(Location.Next).getPort());
                System.out.println("Previous: " + aNodeMap.get(Location.Previous).getPort());
            } catch (NullPointerException anotherException) {
                System.out.println("Exception :" + anotherException.getMessage());
            }
        }
    } */
    
    // Getters 
    public NodeIdentifier getNodeIdentifier() {
        return aNodeIdentifier; 
    }
}