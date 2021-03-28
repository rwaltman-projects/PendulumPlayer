package Game;

import java.awt.Color;
import java.awt.Graphics;

public class DiamondTile extends GameObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int x;
	private int y;
	private int height = 75;
	private int width = 75;
	
	public DiamondTile(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public int inside(int x, int y){ //returns -1 if not inside or the corresponding quadrant its in
		int inside = -1;
		int offsetX = x-this.x;
		if(x>this.x && y<this.y){ // quadrant 1
			double expectedY = this.y - height/2 + offsetX;
			if(y>expectedY){
				inside = 1;
			}
		}else if(x<this.x && y<this.y){ // quadrant 2
			double expectedY = this.y - height/2 - offsetX;
			if(y>expectedY){
				inside = 2;
			}
		}else if(x<this.x && y>this.y){ //quadrant 3
			double expectedY = this.y + height/2 + offsetX;
			if(y<expectedY){
				inside = 3;
			}
		}else if(x>this.x && y>this.y){ // quadrant 4
			double expectedY = this.y + height/2 - offsetX;
			if(y<expectedY){
				inside = 4;
			}
		}
		return inside;
	}
	
	public void draw(Graphics g,int offsetX, int offsetY){
		g.setColor(Color.RED);
		int xPoly[] = {x-offsetX,x-width/2-offsetX,x-offsetX,x+width/2-offsetX};
        int yPoly[] = {y-height/2-offsetY,y-offsetY,y+height/2-offsetY,y-offsetY};
        g.fillPolygon(xPoly, yPoly, xPoly.length);
        g.setColor(Color.BLACK);
        g.drawPolygon(xPoly, yPoly, xPoly.length);
	}
}
