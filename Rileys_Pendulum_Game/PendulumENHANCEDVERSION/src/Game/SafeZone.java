package Game;

import java.awt.Color;
import java.awt.Graphics;

public class SafeZone extends GameObject {
	private static final long serialVersionUID = 1L;
	private int x;
	private int y;
	private int width;
	private int height;
	
	public SafeZone(int x, int y, int width, int height){
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	
	public boolean inside(int x, int y){
		if(this.x<x && this.x+width>x && this.y<y && this.y+width>y){
			return true;
		}
		return false;
	}
	
	public void draw(Graphics g, int offsetX, int offsetY){
		g.setColor(new Color(0,0,255,127));
		g.fillRect(x-offsetX, y-offsetY, width, height);
	}
}
