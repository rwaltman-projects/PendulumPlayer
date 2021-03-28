package Game;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class Cursur {
	private double x;
	private double y;
	private double size = 6;
	private double length = 5;
	
	public Cursur(){
		
	}
	
	public void draw(Graphics g){
		g.setColor(new Color(255,0,0,100));
		((Graphics2D) g).setStroke(new BasicStroke(4));
		g.drawLine((int)(x+size),(int)(y),(int)(x+size+length), (int)(y));
		g.drawLine((int)(x-size),(int)(y),(int)(x-size-length), (int)(y));
		g.drawLine((int)(x),(int)(y-size),(int)(x), (int)(y-size-length));
		g.drawLine((int)(x),(int)(y+size),(int)(x), (int)(y+size+length));
		((Graphics2D) g).setStroke(new BasicStroke(1));
	}
	
	public void set(double x, double y){
		this.x = x;
		this.y = y;
	}
}
