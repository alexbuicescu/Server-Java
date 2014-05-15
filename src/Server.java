import java.io.*;
import java.net.*;
public class Server extends Thread
{
    String message;
    boolean initServer = false;
    int serverPort = 2006;
    ServerSocket providerSocket;
    Server()
    {
    	try {
			providerSocket = new ServerSocket(serverPort);
			initServer = true;
		} catch (IOException e) {
			System.err.println("Eroare la initializare server socket");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}///, 10);
    }
	
    public void run()
    {
    	while(true)
    	{
    		if(initServer == true)
	        {
	            System.out.println("Waiting for connection");
	            try {
					//providerSocket = new ServerSocket(serverPort);
					Socket connection = providerSocket.accept();
		            System.out.println("Am gasit conexiune");
					ClientConnection cc = new ClientConnection(providerSocket, connection);
					Thread th = new Thread(cc);
					th.start();
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
    	
    	public ClientConnection(ServerSocket providerSocket, Socket connection)
    	{
    		mConnection = connection;
    		mServerSocket = providerSocket;
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