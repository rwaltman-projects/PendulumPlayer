package GUIRunner;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Network.Client;
import Network.Server;

public class ServerLauncher{// extends JFrame{// implements ActionListener{
	private JFrame frame = new JFrame();
	private JTextField input;
	private JButton makeServer;
	private JLabel label = new JLabel();
	private int buttonWidth = 200;
	private int buttonHeight = 40;
	
	public ServerLauncher(){
		int width = 220;
		int height = 140;
		JPanel panel = new JPanel(new BorderLayout());
		panel.setLayout(null);
		input = new JTextField(5);
		input.setBounds(10,10,100,20);
		input.setText("Port Number");
		panel.add(input);
		makeServer = new JButton("Make Server");
		makeServer.setBounds(10,50,buttonWidth,buttonHeight);
		makeServer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String port = input.getText();
				int portNum = Integer.parseInt(port);
				Server s = null;
				try {
					Thread serverThread = new Thread(){
					    public void run(){
					    	Server s = new Server(portNum);
					    	s.start();
					    }
					  };
					serverThread.start();
					frame.dispose();
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
				System.out.println("finished making server");
			}
			
		});
		panel.add(makeServer);
		frame.add(panel);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.setLocation(550, 500);
		frame.setSize(width, height);
	}

	
	
	public static void main(String args[]) throws Exception{
		ServerLauncher server = new ServerLauncher();
	}
}
