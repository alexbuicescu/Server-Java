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
	            
	            try {
	
					String read = input.readLine();
					System.out.println("am citit: " + read);
					
					ServerulMeu.chatTextArea.setText(ServerulMeu.chatTextArea.getText() + 
											"Client #" + clientNumber + ": " + read + '\n');
	
				} catch (IOException e) {
					e.printStackTrace();
				}
	            
	            sendMessage("Connection successful");
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
    }
}