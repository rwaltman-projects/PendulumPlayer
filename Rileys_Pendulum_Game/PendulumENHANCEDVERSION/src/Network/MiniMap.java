package Network;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import Game.Ball;
import Game.Driver;

public class MiniMap {
	private double x;
	private double y;
	private int width;
	private int height;
	ArrayList<Ball> players = new ArrayList<Ball>();
	private int pointsRadius = 5;

	MiniMap(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setPlayers(ArrayList<Ball> players){
		this.players = players;
	}

	public int[] getSmallPos(double x, double y){
		int[] pos = new int[2];
		double newX = x/Driver.MAP_WIDTH*width;
		double newY = y/Driver.MAP_HEIGHT*height;
		pos[0] = (int)(newX+this.x);
		pos[1] = (int)(newY+this.y);
		return pos;
	}

	public void draw(Graphics g){
		g.setColor(new Color(255,255,255,200));
		g.fillRect((int)x, (int)y, width, height);
		//Graphics2D g.setStrok()
		g.setColor(Color.BLACK);
		g.drawRect((int)x, (int)y, width, height);
		g.setColor(Color.BLACK);
		g.drawRect((int)x, (int)y, width, height);
		for(int i=0;i<width;i+=20){
			for(int j=0;j<height;j+=20){
				g.drawRect((int)(i+x), (int)(j+y), 20, 20);
			}
		}
		for(int i=0;i<players.size();i++){
			g.setColor(players.get(i).getColor());
			int[] pos = getSmallPos((int)players.get(i).getX(),(int)players.get(i).getY());
			g.fillOval(pos[0]-pointsRadius, pos[1]-pointsRadius, pointsRadius*2, pointsRadius*2);
		}
	}

}
