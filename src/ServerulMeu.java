import java.awt.EventQueue;
import java.net.InetAddress;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class ServerulMeu {

	private static JFrame frame;
	private static JTextField serverPortTextField;
	private static JLabel serverIPAddressLabel;
	private static Server mServer = null;
	public static JTextArea chatTextArea;
	private boolean serverOpened = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerulMeu window = new ServerulMeu();
					window.frame.setVisible(true);
					
					System.out.println("ip: " + String.valueOf(InetAddress.getLocalHost().getHostAddress()));

		    		//System.err.println(Server.readFile("Events/events.txt"));
		    		
					serverIPAddressLabel.setText(InetAddress.getLocalHost().getHostAddress());
					//serverPortTextField.setText(String.valueOf(mServer.serverPort));
					
					
//				      Server server = new Server();
//				      server.start();
				      
				      
				      
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ServerulMeu() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 676, 471);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblErrors = new JLabel("Errors:");
		lblErrors.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblErrors.setBounds(20, 407, 51, 14);
		frame.getContentPane().add(lblErrors);
		
		final JLabel errorsLabel = new JLabel("...");
		errorsLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		errorsLabel.setBounds(71, 407, 579, 14);
		frame.getContentPane().add(errorsLabel);
		
		JLabel lblNewLabel = new JLabel("Server IP:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel.setBounds(10, 11, 72, 14);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblServerPort = new JLabel("Server Port:");
		lblServerPort.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblServerPort.setBounds(10, 45, 87, 14);
		frame.getContentPane().add(lblServerPort);
		
		JLabel lblServerStatus = new JLabel("Server Status:");
		lblServerStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblServerStatus.setBounds(10, 83, 99, 14);
		frame.getContentPane().add(lblServerStatus);
		
		serverIPAddressLabel = new JLabel("....");
		serverIPAddressLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		serverIPAddressLabel.setBounds(92, 11, 135, 14);
		frame.getContentPane().add(serverIPAddressLabel);
		
		serverPortTextField = new JTextField();
		serverPortTextField.setText("2007");
		serverPortTextField.setFont(new Font("Tahoma", Font.PLAIN, 14));
		serverPortTextField.setBounds(102, 44, 125, 20);
		frame.getContentPane().add(serverPortTextField);
		serverPortTextField.setColumns(10);
		
		final JLabel serverStatusLabel = new JLabel("Offline");
		serverStatusLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		serverStatusLabel.setBounds(112, 83, 57, 14);
		frame.getContentPane().add(serverStatusLabel);
		
		JButton btnNewButton = new JButton("Start/Stop Server");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(serverOpened == false)
				{
					try
					{
						mServer = new Server(Integer.parseInt(serverPortTextField.getText()));
						//mServer.serverPort = Integer.parseInt(serverPortTextField.getText());
					}
					catch(Exception ex)
					{
						//mServer.serverPort = 0;
						errorsLabel.setText("Portul trebuie sa fie un int.");
						return;
					}
					
					try
					{
						mServer.start();
						serverOpened = true;
						serverStatusLabel.setText("Online");

						chatTextArea.setText("Serverul a fost pornit cu succes pe portul " + serverPortTextField.getText() + ".\n\n");
					}
					catch(Exception ex)
					{
						errorsLabel.setText("Eroare la rularea serverului.");
						return;
					}
				}
				else
				{
					try 
					{
						mServer.stop = true;
						mServer.sleep(100);
						mServer = null;
					} 
					catch (Exception e) 
					{
						errorsLabel.setText("Eroare la oprirea serverului");
					}
					serverStatusLabel.setText("Offline");
				}
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNewButton.setBounds(10, 119, 159, 23);
		frame.getContentPane().add(btnNewButton);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(20, 166, 630, 214);
		frame.getContentPane().add(scrollPane);
		
		chatTextArea = new JTextArea();
		chatTextArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
		scrollPane.setViewportView(chatTextArea);
	}
}
