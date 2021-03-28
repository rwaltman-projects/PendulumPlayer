package GUIRunner;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import Network.Client;
import Network.Server;

public class ClientLauncher extends JFrame implements ActionListener {
	private JTextField input, inputPort;
	private JButton makeServer;
	private int buttonWidth = 200;
	private int buttonHeight = 40;
	
	public ClientLauncher(){
		int width = 220;
		int height = 140;
		JPanel panel = new JPanel(new BorderLayout());
		setLayout(null);
		input = new JTextField(5);
		inputPort = new JTextField(5);
		input.setBounds(10,10,100,20);
		inputPort.setBounds(10,30,100,20);
		input.setText("IP Address");
		inputPort.setText("Port Num");
		add(input);
		add(inputPort);
		makeServer = new JButton("join Game");
		makeServer.setBounds(10,60,buttonWidth,buttonHeight);
		makeServer.addActionListener(this);
		add(makeServer);
		add(panel);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setLocation(550, 500);
		this.setSize(width, height);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == makeServer) {
			String ip = input.getText();
			String portString = inputPort.getText();
			int port = Integer.parseInt(portString);
			System.out.println(ip);
			Thread clientThread = new Thread(){
			    public void run(){
			    	Client s = new Client(ip,port);
			    	s.start();
			    }
			  };
			clientThread.start();
		}
	}
	
	public static void main(String args[]) throws Exception{
		ClientLauncher client = new ClientLauncher();
	}
}
