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
		try
		{
			serverPort = _serverPort;
			providerSocket = new ServerSocket(_serverPort, 1000);
			initServer = true;
		}
		catch (IOException e)
		{
			System.err.println("Eroare la initializare server socket");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void stopServer()
	{
		try
		{
			providerSocket.close();
		}
		catch (IOException ioException)
		{
			ioException.printStackTrace();
		}
	}

	public void run()
	{
		while (!stop)
		{
			if (initServer == true)
			{
				System.out.println("Waiting for connection");
				try
				{
					// providerSocket = new ServerSocket(serverPort);
					Socket connection = providerSocket.accept();
					System.out.println("Am gasit conexiune");
					ClientConnection cc = new ClientConnection(providerSocket, connection, clientNumber);
					Thread th = new Thread(cc);
					th.start();
					clientNumber++;
					// new Thread(new ClientConnection(connection)).start();

				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	class ClientConnection implements Runnable
	{
		private BufferedReader input = null;
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
			try
			{
				try
				{
					// this.input = new BufferedReader(new
					// InputStreamReader(mConnection.getInputStream()));
					while ((this.input = new BufferedReader(new InputStreamReader(mConnection.getInputStream()))) != null)
					{
						String sendMessageString = "nu am gasit nimic";
						try
						{

							String read = input.readLine();
							System.out.println("am citit: " + read);
							ServerulMeu.chatTextArea.setText(ServerulMeu.chatTextArea.getText() + "Client #" + clientNumber + ": " + read + '\n');

							if (read.length() > 0)
							{
								if (read.equals("stop") == true)
								{
									try
									{
										mConnection.close();
									}
									catch (IOException e)
									{
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									return;
								}
								String[] elements = read.split(" ");

								if (read.charAt(0) == '0')
								{
									sendMessageString = getListOfEvents(elements[1]);
								}
								else if (read.charAt(0) == '1')
								{
									String myID = elements[1];

									sendMessageString = getListOfMyFriends(myID);
								}
								else if (read.charAt(0) == '2')
								{
									String eventID = elements[1];

									sendMessageString = getListOfPeopleGoingToEvent(eventID);
								}
								else if (read.charAt(0) == '3')
								{
									String eventID = elements[1];

									sendMessageString = getListOfCompaniesGoingToEvent(eventID);
								}
								else if (read.charAt(0) == '4')
								{
									String myEmail = elements[1];

									sendMessageString = getMyCredentials(myEmail);
								}
								else if (read.charAt(0) == '5')
								{
									if (elements.length > 2)
									{
										String myEmail = elements[1];
										String myPassword = elements[2];

										sendMessageString = logMeIn(myEmail, myPassword);
									}
								}
							}

						}
						catch (IOException e)
						{
							e.printStackTrace();
						}

						sendMessage(sendMessageString);

						// try {
						//
						// this.input = new BufferedReader(new
						// InputStreamReader(mConnection.getInputStream()));
						//
						// } catch (IOException e) {
						// e.printStackTrace();
						// }
					}
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			finally
			{
				try
				{
					mConnection.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		void sendMessage(String msg)
		{
			msg += "\nstop";
			PrintWriter out = null;
			try
			{
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mConnection.getOutputStream())), true);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.println(msg);

			String serverName = "Server to Client #" + clientNumber + ": " + msg + '\n';
			System.err.println("am trimis: " + serverName);
			ServerulMeu.chatTextArea.setText(ServerulMeu.chatTextArea.getText() + serverName);
		}

		String getListOfEvents(String myID)
		{
			return readFile("Events/events.txt");
		}

		String getListOfMyFriends(String myID)
		{
			String dataBase = readFile("persoane-conexiuni.txt");

			String[] users = dataBase.split("\n");

			String friends = "";

			for (int i = 0; i < users.length; i++)
			{
				// 0 - doar eu il am
				// 1 - doar el ma are
				// 2 - ne avem amandoi
				String[] userProperties = users[i].split(" ");

				// daca ne avem reciproc
				if (userProperties[2].equals("1") == true)
				{
					// daca e cumva id-ul meu pe aici
					if (userProperties[0].equals(myID) == true)
					{
						friends += "2 " + getMyFriend(userProperties[1]) + '\n';
					}
					else if (userProperties[1].equals(myID) == true)
					{
						friends += "2 " + getMyFriend(userProperties[0]) + '\n';
					}
				}
				else
				{
					// daca eu il am pe el
					if (userProperties[0].equals(myID) == true)
					{
						friends += "0 " + getMyFriend(userProperties[1]) + '\n';
					}
					else
					// daca el ma are pe mine
					if (userProperties[1].equals(myID) == true)
					{
						friends += "1 " + getMyFriend(userProperties[0]) + '\n';
					}
				}
			}
			return friends;
		}

		String toatePersoanele = readFile("persoane.txt");

		private String getMyFriend(String friendID)
		{
			String[] persoane = toatePersoanele.split("\n");
			for (int i = 0; i < persoane.length; i++)
			{
				String[] properties = persoane[i].split(" ");
				if (properties[0].equals(friendID) == true)
				{
					return persoane[i];
				}
			}
			return null;
		}

		String getListOfPeopleGoingToEvent(String eventID)
		{
			return readFile("Events/event" + eventID + "/event-persoane.txt");
		}

		String getListOfCompaniesGoingToEvent(String eventID)
		{
			return readFile("Events/event" + eventID + "/event-companies.txt");
		}

		String getMyCredentials(String myID)
		{
			String dataBase = toatePersoanele;// readFile("persoane.txt");

			String[] users = dataBase.split("\n");

			for (int i = 0; i < users.length; i++)
			{
				String[] userProperties = users[i].split(" ");

				if (userProperties[0].equals(myID) == true)
				{
					return users[i];
				}
			}

			return null;
		}

		String logMeIn(String myEmail, String myPassword)
		{
			String dataBase = toatePersoanele;// readFile("persoane.txt");

			String[] users = dataBase.split("\n");

			String answer = "n";

			for (int i = 0; i < users.length; i++)
			{
				String[] userProperties = users[i].split(" ");

				if (userProperties.length > 3)
				{
					if ((userProperties[1].equals(myEmail) == true) && (userProperties[5].equals(myPassword) == true))
					{
						answer = "y " + userProperties[0];
						return answer;
					}
				}
			}

			return answer;
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
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return fullText;
	}
}