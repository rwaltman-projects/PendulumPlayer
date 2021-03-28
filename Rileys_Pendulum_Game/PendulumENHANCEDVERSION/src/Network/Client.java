package Network;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import Game.Driver;
import Game.Field;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


import Game.GameObject;
import Game.Players;


public class Client{
	private InetAddress IPAddress; //2601:644:8100:d400:4048:5ce6:c5d1:6b28  fe80::4fad:882:4023:8e93
	private DatagramSocket clientSocket;
	private int PORT_NUM = 22229;
	private byte[] sendData = new byte[1024];
	private byte[] receiveData = new byte[64000];
	private JFrame frame = new JFrame();
	private ClientPanel panel;
	private ArrayList<GameObject> objects = new ArrayList<GameObject>();


	private int offsetX = 0;
	private int offsetY = 0;
	private int health = 100;
	private int energy = 100;
	private int deaths = 0;
	private int kills = 0;
	private int experience;
	private int experienceLevel;
	private int level;

	private int width = Driver.WIDTH;
	private int height = Driver.HEIGHT;

	private int miniMapSize = 100;

	private Hud hud = new Hud();
	private MiniMap map = new MiniMap(width-miniMapSize-10,height-miniMapSize-10-25,miniMapSize,miniMapSize);

	private static ByteArrayInputStream in;
	private static ObjectInputStream is;

	private int updateTime = 10;
	private int playerNumber = -1;

	private boolean dead = false;


	private boolean connected = false;

	public Client(String address, int port){
		try {
			PORT_NUM = port;
			clientSocket = new DatagramSocket();
			IPAddress = InetAddress.getByName(address);
		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
		setupFrame();
		sendMessage("joined");
		start();
	}

	private void setupFrame(){
		panel = new ClientPanel(){
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				drawMapDesign(g);
				if(objects!=null && objects.size()>0){
					Players p = (Players)objects.get(1);
					map.setPlayers(p.getPlayers());
					offsetX = (int) p.get(playerNumber).getX()-width/2;
					offsetY = (int) p.get(playerNumber).getY()-height/2;
					health = (int) p.get(playerNumber).getHealth();
					energy = (int) p.get(playerNumber).getEnergy();
					deaths = p.get(playerNumber).getDeaths();
					kills = p.get(playerNumber).getKills();
					dead = p.get(playerNumber).getDead();
					experience = p.get(playerNumber).getExperience();
					level = p.get(playerNumber).getLevel();
					hud.updateInfo(health, energy, deaths, kills,level, experience, map);
					for(int i=0;i<objects.size();i++){
						if(objects.get(i)!=null){
							objects.get(i).draw(g,offsetX,offsetY);
						}
					}
					drawInfo(g);

				}else{
					g.drawString("NOT RECEIVING DATA", 500, 400);
				}
			}

			public void mouseMoved(MouseEvent e) {
				double dx = e.getX()-width/2;
				double dy = e.getY()-height/2;
				double dir = 0.0;
				if(dx>0&&dy<0){
					dir = (Math.toDegrees(Math.atan(dy/dx)*-1));
				}
				if(dx<0&&dy<0){
					dir = (180-Math.toDegrees(Math.atan(dy/dx)));
				}
				if(dx>0&&dy>0){
					dir = (360 - Math.toDegrees(Math.atan(dy/dx)));
				}
				if(dx<0&&dy>0){
					dir = (180 +(-1*Math.toDegrees(Math.atan(dy/dx))));
				}
				sendMessage("dir:" + dir);
			}

			public void mouseReleased(MouseEvent e){
				if(SwingUtilities.isLeftMouseButton(e)){
					sendMessage("shootbullet");
				}
				if (SwingUtilities.isRightMouseButton(e)){
					sendMessage("shoothook:"+e.getX() + "," + e.getY() + ".");
				}
			}

		};
		panel.addMouseMotionListener(panel);
		panel.addMouseListener(panel);
		panel.getInputMap().put(KeyStroke.getKeyStroke("pressed W"),"pressedW");
		panel.getInputMap().put(KeyStroke.getKeyStroke("released W"),"releasedW");
		panel.getInputMap().put(KeyStroke.getKeyStroke("pressed A"),"pressedA");
		panel.getInputMap().put(KeyStroke.getKeyStroke("released A"),"releasedA");
		panel.getInputMap().put(KeyStroke.getKeyStroke("pressed D"),"pressedD");
		panel.getInputMap().put(KeyStroke.getKeyStroke("released D"),"releasedD");
		panel.getInputMap().put(KeyStroke.getKeyStroke("pressed S"),"pressedS");
		panel.getInputMap().put(KeyStroke.getKeyStroke("released S"),"releasedS");
		panel.getInputMap().put(KeyStroke.getKeyStroke("pressed SPACE"),"pressedSpace");

		panel.getActionMap().put("pressedW",new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				if(connected)
					sendMessage("command:pressedw"+ playerNumber);
			}
		});
		panel.getActionMap().put("releasedW",new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				if(connected)
					sendMessage("command:releasedw"+ playerNumber);
			}
		});

		panel.getActionMap().put("pressedA",new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				if(connected)
					sendMessage("command:presseda"+ playerNumber);
			}
		});
		panel.getActionMap().put("releasedA",new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				if(connected)
					sendMessage("command:releaseda"+ playerNumber);
			}
		});

		panel.getActionMap().put("pressedD",new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				if(connected)
					sendMessage("command:pressedd"+ playerNumber);
			}
		});
		panel.getActionMap().put("releasedD",new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				if(connected)
					sendMessage("command:releasedd"+ playerNumber);
			}
		});

		panel.getActionMap().put("pressedS",new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				if(connected)
					sendMessage("command:presseds"+ playerNumber);
			}
		});
		panel.getActionMap().put("releasedS",new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				if(connected)
					sendMessage("command:releaseds"+ playerNumber);
			}
		});
		panel.getActionMap().put("pressedSpace",new AbstractAction(){

			public void actionPerformed(ActionEvent arg0) {
				if(connected)
					sendMessage("command:space"+ playerNumber);
			}
		});

		frame.setBackground(new Color(127, 127, 127));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(width,height));
		panel.setPreferredSize(new Dimension(width,height));
		frame.add(panel);
		frame.pack();
		//frame.setResizable(false);
		panel.requestFocusInWindow();
	}

	public void start(){
		while(true){
			receiveData();
		}
	}

	private void sendMessage(String sentence){
		sentence = sentence.trim().toLowerCase();
		sendData = sentence.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT_NUM);
		try {
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void receiveData(){
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		try {
			clientSocket.receive(receivePacket);
			if(deserialize(receivePacket.getData()) instanceof Integer){
				playerNumber = (int)deserialize(receivePacket.getData());
				connected = true;
			}
			else if(deserialize(receivePacket.getData()) instanceof Packet){
				System.out.println("recieved packet");
				Packet p = (Packet) deserialize(receivePacket.getData());
				objects = p.getArrayList();
				panel.repaint();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void closeClient(){
		clientSocket.close();
		System.exit(0);
	}

	public static void main(String args[]){
		Client c = new Client("fe80::4b5f:da84:599b:17bb",22229);
		c.start();
	}

	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		in = new ByteArrayInputStream(data);
		is = new ObjectInputStream(in);
		try{
			return is.readObject();
		}catch(Throwable e){
			return null;
		}
	}

	private void drawInfo(Graphics g){
		hud.draw(g);

		Font f = new Font(Font.MONOSPACED, Font.BOLD, 20);
		g.setFont(f);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 125, 50);
		g.setColor(Color.WHITE);
		g.drawString("Kills: " + kills, 10, 20);
		g.drawString("Deaths: " + deaths, 10, 40);
		if(dead){
			g.setColor(Color.BLACK);
			g.setColor(new Color(0,0,0,127));
			g.fillRect(0, 0, Driver.WIDTH, Driver.HEIGHT);
		}
	}

	public void drawMapDesign(Graphics g){
		g.setColor(Color.BLACK);
		int width = 100;
		int height = 100;
		for(int x=0;x<Driver.MAP_WIDTH;x+=width) {
			for(int y = 0;y<Driver.MAP_HEIGHT;y+=height) {
				g.drawRect(x+2 -offsetX, y+2-offsetY, width-4, height-4);
			}
		}
	}

}