package Game;
import java.awt.Color;
import java.awt.Graphics;

public class Tile extends GameObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	private boolean solid = false;
	public static int WIDTH = 50;
	public static int offset = 4;

	public Tile(double x, double y, boolean solid){
		this.x = x;
		this.y = y;
		this.solid = solid;
	}
	
	public boolean checkHit(int x, int y){
		if(x > this.x && x<=this.x+WIDTH && y > this.y && y<=this.y+WIDTH){
			return true;
		}
		return false;
	}

	public void draw(Graphics g, int offsetX, int offsetY){
		if(solid){
			g.setColor(new Color(255,0,0,127));
			g.fillRect((int)(x-offsetX+offset), (int)(y-offsetY+offset), WIDTH-offset, WIDTH-offset);

		}
		g.setColor(Color.BLACK);
		g.drawRect((int)(x-offsetX+offset), (int)(y-offsetY+offset), WIDTH-offset, WIDTH-offset);
	}

	public void setSolid(boolean b){
		solid = b;
	}
	
	public boolean isSolid(){
		return solid;
	}
	
	public int hitLoc(double x, double y){
		double targetY = (this.y) + (x%WIDTH);
		double targetX = (this.y+WIDTH) - (x%WIDTH);
		if(y>targetY){
			if(y>targetX){
				return 1;
			}
			return 2;
		}else{
			if(y>targetX){
				return 3;
			}
			return 4;
		}	
	}
}
