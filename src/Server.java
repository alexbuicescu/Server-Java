import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class Server extends Thread
{
	private int clientNumber = 0;
	String message;
	boolean initServer = false;
	boolean stop = false;
	private int serverPort;
	ServerSocket providerSocket;
	private ArrayList<ClientConnection> client = new ArrayList<ClientConnection>();
	private HashMap clientIDS = new HashMap();

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

	private void sendCardToAnoterClient(String clientID, String myCard)
	{
		if (clientIDS.get(clientID) != null)
		{
			int id = Integer.parseInt(String.valueOf(clientIDS.get(clientID)));
			client.get(id).sendMessage(myCard);
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
					cc.start();

					client.add(cc);
					// Thread th = new Thread(cc);

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

	class ClientConnection extends Thread // implements Runnable
	{
		private BufferedReader input = null;
		private Socket mConnection;
		private ServerSocket mServerSocket;
		private int mCurrentClientNumber;
		private String mCurrentClientID = "";

		public ClientConnection(ServerSocket providerSocket, Socket connection, int clientNumber)
		{
			mConnection = connection;
			mServerSocket = providerSocket;
			this.mCurrentClientNumber = clientNumber;
		}

		public void run()
		{
			// try
			// {
			try
			{
				// this.input = new BufferedReader(new
				// InputStreamReader(mConnection.getInputStream()));
				while ((this.input = new BufferedReader(new InputStreamReader(mConnection.getInputStream()))) != null)
				{
					try
					{
						String read = "";
						// try
						// {
						read = input.readLine();
						// }
						// catch(Exception ex)
						// {
						// continue;
						// }

						if (read != null && read.length() > 0 && read.equals("null") == false)
						{
							String sendMessageString = "nu am gasit nimic";

							System.out.println("am citit: " + read);
							ServerulMeu.chatTextArea.setText(ServerulMeu.chatTextArea.getText() + "Client #" + mCurrentClientNumber + ": " + read + '\n');
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
							else if (read.charAt(0) == 'c' && elements.length > 1)
							{
								sendMessageString = "trebuie sa scriu cartea de vizita de la: " + elements[1];
								sendCardToAnoterClient(mCurrentClientID, read);
								setConection(mCurrentClientID, elements[1]);
							}
							else if (read.charAt(0) == '8' && elements.length > 1)
							{
								sendMessageString = "trebuie sa trimit cartea de vizita catre: " + elements[1];
								sendCardToAnoterClient(elements[1], "card " + getMyCredentials(mCurrentClientID));
								setConection(elements[1], mCurrentClientID);
							}
							else if (read.charAt(0) == '9' && elements.length > 1)
							{
								mCurrentClientID = elements[1];
								clientIDS.put(mCurrentClientID, new Integer(mCurrentClientNumber));

								sendMessageString = "am primit id-ul";
							}
							sendMessage(sendMessageString);
						}

						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

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

			// }
			// finally
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

		private void setConection(String id1, String id2)
		{
			String con = readFile("persoane-conexiuni.txt");

			String[] conections = con.split("\n");
			String newFileString = "";

			boolean found = false;
			for (int i = 0; i < conections.length; i++)
			{
				String[] items = conections[i].split(" ");
				if (items.length > 2)
				{
					// daca 2 are pe 1, atunci pun si pe 1 ca are pe 2
					if (items[2].equals("0") == true)
					{
						if (items[1].equals(id1) == true && items[0].equals(id2) == true)
						{
							items[2] = "1";
							found = true;
							// break;
						}
					}
					newFileString += items[0] + " " + items[1] + " " + items[2] + "\n";
				}
			}

			if (found == false)
			{
				newFileString += id1 + " " + id2 + " " + "0" + "\n";
			}
			writeFile("persoane-conexiuni.txt", newFileString);
		}

		public String getClientID()
		{
			return mCurrentClientID;
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

			String serverName = "Server to Client #" + mCurrentClientNumber + ": " + msg + '\n';
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
						// friends += "2 " + getMyFriend(userProperties[1]) +
						// '\n';
						friends += getMyFriend(userProperties[1]) + " 2" + '\n';
					}
					else if (userProperties[1].equals(myID) == true)
					{
						// friends += "2 " + getMyFriend(userProperties[0]) +
						// '\n';
						friends += getMyFriend(userProperties[0]) + " 2" + '\n';
					}
				}
				else
				{
					// daca eu il am pe el
					if (userProperties[0].equals(myID) == true)
					{
						// friends += "0 " + getMyFriend(userProperties[1]) +
						// '\n';
						friends += getMyFriend(userProperties[1]) + " 0" + '\n';
					}
					else
					// daca el ma are pe mine
					if (userProperties[1].equals(myID) == true)
					{
						// friends += "1 " + getMyFriend(userProperties[0]) +
						// '\n';
						friends += getMyFriend(userProperties[0]) + " 1" + '\n';
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
					persoane[i] += " " + extractBytes(friendID + ".jpg");
					return persoane[i];
				}
			}
			return null;
		}

		String getListOfPeopleGoingToEvent(String eventID)
		{
			String peopleGoingToEvent = readFile("Events/event" + eventID + "/event-persoane.txt");

			String[] people = peopleGoingToEvent.split("\n");

			String allPeople = "";

			for (int i = 0; i < people.length; i++)
			{
				String[] properties = people[i].split(" ");
				people[i] += " " + extractBytes(properties[0] + ".jpg");

				allPeople += people[i] + "\n";
			}
			return allPeople;
			// return readFile("Events/event" + eventID +
			// "/event-persoane.txt");
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
					users[i] += " " + extractBytes(myID + ".jpg");
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

	public static void writeFile(String fileName, String text)
	{
		if (text.length() > 0 && text.charAt(text.length() - 1) == '\n')
		{
			StringBuilder sb = new StringBuilder(text);
			sb.deleteCharAt(text.length() - 1);
			text = sb.toString();
		}

		PrintWriter writer = null;
		try
		{
			writer = new PrintWriter(fileName, "UTF-8");

			writer.print(text);
			writer.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String extractBytes(String ImageName)
	{
		return "1110100";
//		try
//		{
//			// open image
//			File imgPath = new File(ImageName);
//			BufferedImage bufferedImage;
//			bufferedImage = ImageIO.read(imgPath);
//
//			// get DataBufferBytes from Raster
//			WritableRaster raster = bufferedImage.getRaster();
//			DataBufferByte data = (DataBufferByte) raster.getDataBuffer();
//
//			byte[] bytes = data.getData();
//			
//
//			String text = Base64.getEncoder().encodeToString(bytes);;//new String(bytes, "utf-8");
//
////			for (int i = 0; i < bytes.length; i++)
////			{
////				text += bytes[i];
////			}
//			
//			System.out.println(text);
//
//			return text;//new String(bytes, "utf-8");//text;//(data.getData());
//		}
//		catch (IOException e)
//		{
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		return null;
	}
	
//	BufferedImage buf_image; // this is BufferedImage reference you got after converting it from Image
//	byte[] imageByteArray = bufferedImageToByteArray(buf_image,"jpg");

	public static byte[] bufferedImageToByteArray()//BufferedImage image, String format)
	{
//		Image image = new Image("asd");
	    try
		{
	    	String format = "jpg";
	    	String path = "0.jpg";
	        File file = new File(path);
	        BufferedImage image2 = ImageIO.read(file);
        
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image2, format, baos);
			
			String text = String.valueOf(baos.toByteArray());
			
//			for(int i = 0 ; i < baos.toByteArray().; i++)
			{
//				System.out.println(baos.toByteArray()[i]);
			}
			String str = new String(baos.toByteArray(), "UTF-8"); // for UTF-8 encoding
			
//			System.out.println(baos.toByteArray().length);
//			System.out.println(str);
			
		    return baos.toByteArray();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return null;
	}

}