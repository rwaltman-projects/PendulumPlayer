package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

public class Players extends GameObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ArrayList<Ball> players = new ArrayList<Ball>();
	
	
	public void add(Ball b){
		players.add(b);
	}
	
	public void draw(Graphics g, int offsetX, int offsetY){
		for(int i=0; i<players.size();i++){
			players.get(i).draw(g, offsetX, offsetY);
		}
	}
	
	public Ball get(int i){
		return players.get(i);
	}
	
	public void update(){
		for(int i=0; i<players.size();i++){
			players.get(i).update();
		}
	}
	
	public void makeNewPlayer(Color c, int x, int y){
		players.add(new Ball(x,y,c));
	}

	public int size() {
		// TODO Auto-generated method stub
		return players.size();
	}

	public ArrayList<Ball> getPlayers() {
		// TODO Auto-generated method stub
		return players;
	}

	
}
