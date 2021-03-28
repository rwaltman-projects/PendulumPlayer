package Game;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;


public class Driver {

	
	public static int WIDTH = 1000;
	public static int HEIGHT = 800;
	
	public static final int MAP_WIDTH = 2100;
	public static final int MAP_HEIGHT = 2000;
	
	public int amountPlayers = 0;
	
	
	private ArrayList<GameObject> objects = new ArrayList<GameObject>();

	
	private Color[] colors = new Color[]{
			Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA,Color.ORANGE
	};
	
	public static int[] spawnPointsX = new int[]{
			Driver.MAP_WIDTH/2,Driver.MAP_WIDTH/2,100,Driver.MAP_WIDTH-100
	};
	
	public static int[] spawnPointsY = new int[]{
			Tile.WIDTH/2,Driver.MAP_HEIGHT-150,Driver.MAP_HEIGHT/2-100,Driver.MAP_HEIGHT/2-100
	};
	
	public static Field f = new Field(MAP_WIDTH, MAP_HEIGHT);
	public static Players p = new Players();
	
	private Timer t = new Timer(10, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			update();
		}

	});

	
	public Driver(int width, int height){
		WIDTH = width;
		HEIGHT = height;
	}

	public void start() {
		setupObjects();
		t.start();
	}

	
	private void setupObjects(){
		objects.add(f);
		objects.add(p);

	}

	public void update(){
		f.update();
		p.update();
	}

	
	public void addPlayer(){
		if(amountPlayers<4){
			p.makeNewPlayer(colors[amountPlayers],spawnPointsX[amountPlayers],spawnPointsY[amountPlayers]);
			amountPlayers++;
		}
	}
	
	
	public void giveCommandPlayer(int x, String command){
		//System.out.println(command);
		p.get(x).giveCommand(command);
	}

	public ArrayList<GameObject> getObjects() {
		return objects;
	}


}
