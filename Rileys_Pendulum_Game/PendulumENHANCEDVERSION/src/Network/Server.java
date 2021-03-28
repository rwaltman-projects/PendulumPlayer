package Network;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.synth.SynthScrollBarUI;


import Game.Driver;
import Game.Field;
import Game.GameObject;

public class Server{
	public int PORT_NUM = 22229;
	private DatagramSocket serverSocket;
	private byte[] receiveData = new byte[512];
	private byte[] sendData = new byte[32767];
	private ArrayList<Player> clients = new ArrayList<Player>();
	private Driver game;

	private static ByteArrayOutputStream out;
	private static ObjectOutputStream os;
	private static ByteArrayInputStream in;
	private static ObjectInputStream is;

	private ServerFrame frame;

	private boolean stop = false;
	private sendingClientData t1 = new sendingClientData();

	private int updateTime = 10;

	private int maxPlayers = 5;
	private int amountPlayers = 0;



	public Server(int i){
		try {
			PORT_NUM = i;
			serverSocket = new DatagramSocket(PORT_NUM);
			System.out.println("Server Created");
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	private void setupFrame(){
		frame = new ServerFrame();
		JButton b = new JButton();
		
		b.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//String action = e.getActionCommand();
				//if (action.equals("closeServer")) {
					sendDataToClients("close");
					System.out.println("Server Closed");
					closeServer();
					System.exit(0);
				//}

			}
		});
		//b.setActionCommand("closeServer");
		JPanel pan = new JPanel();
		pan.setBackground(Color.WHITE);
		frame.setSize(150,100);
		pan.add(b);
		pan.setLayout(null);
		b.setSize(150,100);
		b.setText("CLOSE SERVER");
		b.setVisible(true);
		frame.add(pan);
		frame.setVisible(true);
		System.out.println("Frame is set up!!");
	}

	public void start(){
		setupFrame();
		game = new Driver(1000,800);
		game.start();
		t1.start(); 
		System.out.println("started game and thread");
		while(!stop){
			recieveData();
		}
		
	}

	private void recieveData(){
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			serverSocket.receive(receivePacket);
			String message = new String( receivePacket.getData());
			message = message.trim().toLowerCase();
			if(message.substring(0,4).equals("join") && amountPlayers<maxPlayers){
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				System.out.println("new Player Joined");
				clients.add(new Player(IPAddress,port));
				amountPlayers++;
				game.addPlayer();
			}else if(message.trim().toLowerCase().substring(0, 8).equals("command:")){
				InetAddress IPAddress = receivePacket.getAddress();
				int number = -1;
				for(int i=0;i<clients.size();i++){
					if(clients.get(i).getAddress().equals(IPAddress)){
						number = i;
					}
				}
				int ind = message.indexOf(":");
				message = message.substring(ind+1);
				game.giveCommandPlayer(number,message);
			}else if(message.trim().toLowerCase().substring(0, 4).equals("dir:")){
				InetAddress IPAddress = receivePacket.getAddress();
				int number = -1;
				for(int i=0;i<clients.size();i++){
					if(clients.get(i).getAddress().equals(IPAddress)){
						number = i;
					}
				}
				game.giveCommandPlayer(number,message);
			}else if(message.trim().toLowerCase().substring(0, 11).equals("shootbullet")){
				InetAddress IPAddress = receivePacket.getAddress();
				int number = -1;
				for(int i=0;i<clients.size();i++){
					if(clients.get(i).getAddress().equals(IPAddress)){
						number = i;
					}
				}
				game.giveCommandPlayer(number, "shootbullet");
			}else if(message.trim().toLowerCase().substring(0, 10).equals("shoothook:")){
				message = message.trim().toLowerCase();
				InetAddress IPAddress = receivePacket.getAddress();
				int number = -1;
				for(int i=0;i<clients.size();i++){
					if(clients.get(i).getAddress().equals(IPAddress)){
						number = i;
					}
				}
				int ind  = message.indexOf(".");
				message = message.substring(0,ind);
				game.giveCommandPlayer(number, message);
			}else{
				System.out.println("didn't join");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void sendDataToClients(){
		for(int i=0;i<clients.size();i++){
			try {
				if(clients.get(i).getJustJoined()){
					clients.get(i).setJustJoined(false);
					int message = new Integer(amountPlayers-1);
					sendData = serialize(message);
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clients.get(i).getAddress(), clients.get(i).getPort());
					serverSocket.send(sendPacket);
					clients.get(i).setJustJoined(false);
				}else{
					ArrayList<GameObject> objs = (ArrayList<GameObject>) game.getObjects();
					Packet p  = new Packet(objs);
					sendData = serialize(p);
					if(sendData!=null){
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clients.get(i).getAddress(), clients.get(i).getPort());
						serverSocket.send(sendPacket);
					}
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void sendDataToClients(String s){
		for(int i=0;i<clients.size();i++){
			try {
				sendData = new String(s).getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clients.get(i).getAddress(), clients.get(i).getPort());
				serverSocket.send(sendPacket);

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	private void closeServer(){
		stop = true;
		serverSocket.close();
	}


	public static void main(String args[]) throws Exception{
//		Server s = null;
//		if(args != null) {
//			System.out.println("Opening sever with port "+Integer.parseInt(args[0]));
//			s = new Server(Integer.parseInt(args[0]));
//			System.out.println("Done creating the server ");
//		}
//		else {
//			s=new Server(22229);
//		}
		Server s =  new Server(22229);
		s.start();
	}

	public static byte[] serialize(Object obj) throws IOException {
		out = new ByteArrayOutputStream();
		os = new ObjectOutputStream(out);
		try{
			os.writeObject(obj);
		}catch(Throwable e){
			System.out.println("couldnt serialize");
			return null;
		}
		os.reset();
		return out.toByteArray();
	}
	

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		in = new ByteArrayInputStream(data);
		is = new ObjectInputStream(in);
		return is.readObject();
	}

	class sendingClientData extends Thread{
		private boolean stop = false;
		public void run(){
			while(!stop){
				try {
					this.sleep(updateTime);
					sendDataToClients();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public void stopSending(){
			stop = true;
		}
	}

}

