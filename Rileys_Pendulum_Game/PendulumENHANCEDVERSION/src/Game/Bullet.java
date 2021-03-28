package Game;
import java.awt.Color;
import java.awt.Graphics;

public class Bullet extends GameObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	private double dx;
	private double dy;
	private int radius = 5;
	private double speed;
	private double dist = 0;
	private double maxDist;
	private double alpha = 255;
	private double rate;
	private Object owner;
	private double damage;
	private Color c;
	
	public Bullet(double x, double y, double dir,double maxBulletDist,double intialSpeed, double speed, Object owner, Color c,double damage){
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.speed += intialSpeed;
		dy = this.speed * Math.cos(Math.toRadians(dir+90));
		dx = this.speed * Math.sin(Math.toRadians(dir+90));
		maxDist = maxBulletDist;
		this.rate = 255/maxBulletDist;
		this.owner = owner;
		this.c = c;
		this.damage =damage;
	}
	
	public void draw(Graphics g,int offsetX, int offsetY){
		g.setColor(new Color(c.getRed(),c.getGreen(),c.getBlue(),(int)alpha));
		g.fillOval((int)x-offsetX - radius,(int)y-offsetY - radius, radius*2, radius*2);
		g.setColor(Color.BLACK);
		g.drawOval((int)x-offsetX - radius,(int)y-offsetY - radius, radius*2, radius*2);
	}
	
	public void update(){
		x+=dx;
		y+=dy;
		dist += speed;
		alpha-=rate;
		if(alpha>255){
			alpha = 255;
		}
		if(alpha<0){
			alpha =0;
		}
	}
	
	public boolean checkMax(){
		if(dist>maxDist){
			return true;
		}else{
			return false;
		}
	}
	
	public double getMaxDist(){
		return maxDist;
	}
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getDx(){
		return dx;
	}
	
	public double getDy(){
		return dy;
	}
	
	public void setDx(double dx){
		this.dx = dx;
	}
	
	public void setDy(double dy){
		this.dy = dy;
	}
	
	public int getRadius(){
		return radius;
	}
	
	public void setSize(int amount){
		radius = amount;
	}
	
	public Object getOwner(){
		return owner;
	}
	
	public double getDamage(){
		return damage;
	}
}
