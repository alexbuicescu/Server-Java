import java.io.*;
import java.net.*;
public class Server extends Thread
{
	private int clientNumber = 0;
    String message;
    boolean initServer = false;
    boolean stop = false;
    private int serverPort;
    ServerSocket providerSocket;
    Server(int _serverPort)
    {
    	try {
    		serverPort = _serverPort;
			providerSocket = new ServerSocket(_serverPort);
			initServer = true;
		} catch (IOException e) {
			System.err.println("Eroare la initializare server socket");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}///, 10);
    }
	
    public void run()
    {
    	while(!stop)
    	{
    		if(initServer == true)
	        {
	            System.out.println("Waiting for connection");
	            try {
					//providerSocket = new ServerSocket(serverPort);
					Socket connection = providerSocket.accept();
		            System.out.println("Am gasit conexiune");
					ClientConnection cc = new ClientConnection(providerSocket, connection, clientNumber);
					Thread th = new Thread(cc);
					th.start();
					clientNumber++;
					//new Thread(new ClientConnection(connection)).start();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
    	}
    }
    
    class ClientConnection implements Runnable
    {
    	private BufferedReader input;
    	private Socket mConnection;
    	private ServerSocket mServerSocket;
    	private int clientNumber;
    	
    	public ClientConnection(ServerSocket providerSocket, Socket connection, int clientNumber)
    	{
    		
    		mConnection = connection;
    		mServerSocket = providerSocket;
    		this.clientNumber = clientNumber;
    	}
    	
    	public void run()
    	{
    		try{
	            //1. creating a server socket
	            //providerSocket = new ServerSocket(2004);///, 10);
	            //2. Wait for connection
	            System.out.println("Connection received from " + mConnection.getInetAddress().getHostName());
	            //3. get Input and Output streams
	            try {
	
					this.input = new BufferedReader(new InputStreamReader(mConnection.getInputStream()));
	
				} catch (IOException e) {
					e.printStackTrace();
				}
	            
	            String sendMessageString = "";
	            try {
	
					String read = input.readLine();
					System.out.println("am citit: " + read);
					
					if(read.length() > 0)
					{
						String[] elements = read.split(" ");
						
						if(read.charAt(0) == '0')
						{
							sendMessageString = getListOfEvents(elements[1]);
						}
						else
							if(read.charAt(0) == '1')
							{
								sendMessageString = getListOfMyFriends();
							}
							else
								if(read.charAt(0) == '2')
								{
									String eventID = elements[1];
									
									sendMessageString = getListOfPeopleGoingToEvent(eventID);
								}
								else
									if(read.charAt(0) == '3')
									{
										String eventID = elements[1];
										
										sendMessageString = getListOfCompaniesGoingToEvent(eventID);
									}

									else
										if(read.charAt(0) == '4')
										{
											String myEmail = elements[1];
											
											sendMessageString = getMyCredentials(myEmail);
										}
										else
										{
											sendMessageString = "nu am gasit nimic";
										}
					}
					
					ServerulMeu.chatTextArea.setText(ServerulMeu.chatTextArea.getText() + 
											"Client #" + clientNumber + ": " + read + '\n');
	
				} catch (IOException e) {
					e.printStackTrace();
				}
	            
	            sendMessage(sendMessageString);
	        }
	        finally{
	        	try {
					mConnection.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            //4: Closing connection
//	            try{
//	            	mServerSocket.close();
//	            }
//	            catch(IOException ioException){
//	                ioException.printStackTrace();
//	            }
	        }    		
    	}
    	
    	void sendMessage(String msg)
        {
    		String serverName = "Server to Client #" + clientNumber + ": " + msg + '\n';
    		ServerulMeu.chatTextArea.setText(ServerulMeu.chatTextArea.getText() + serverName);
    		PrintWriter out = null;
    		try {
    			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mConnection.getOutputStream())), true);
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		out.println(msg);
            
        }
    	
    	String getListOfEvents(String myID)
    	{
    		return null;
    	}

    	String getListOfMyFriends()
    	{
    		return readFile("Events/events.txt");
    	}

    	String getListOfPeopleGoingToEvent(String eventID)
    	{
    		return null;
    	}

    	String getListOfCompaniesGoingToEvent(String eventID)
    	{
    		return null;
    	}

    	String getMyCredentials(String myEmail)
    	{
    		return null;
    	}
    }
    
    public static String readFile(String fileName)
    {
    	BufferedReader reader = null;
		try 
		{
			reader = new BufferedReader(new FileReader(fileName));
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
    	String line = null;
    	String fullText = "";
    	try 
    	{
			while ((line = reader.readLine()) != null) 
			{
				fullText += line + '\n';
			}
		} 
    	catch (IOException e) {
			e.printStackTrace();
		}
    	return fullText;
    }
}