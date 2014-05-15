import java.awt.EventQueue;
import java.net.InetAddress;

import javax.swing.JFrame;


public class ServerulMeu {

	private JFrame frame;

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
					
				      Server server = new Server();
				      server.start();
				      
				      
				      
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
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
