package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public abstract class Mass extends GameObject{
	private double x;
	private double y;
	private double dx = 0;
	private double dy = 0;
	private double radius;
	private Color c;
	private double magneticSpeed = 4;
	private double value;
	private double deaccel = 0.0025;
	private double accel = 0.01;
	private double maxAccel = 1;
	private boolean attracted = false;
	
	public Mass(double x, double y, double r,double dx, double dy){
		this.x = x;
		this.y = y;
		this.radius = r;
		this.dx = dx;
		this.dy =dy;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public double getRadius(){
		return radius;
	}
	
	public void setColor(Color c){
		this.c = c;
	}
	
	public void update(){
		accelerate();
		deaccelerate();
		x+=dx;
		y+=dy;
	}
	
	public boolean closeTo(Ball b){
		double distance = Math.sqrt(Math.pow(b.getX()-x, 2) + Math.pow(b.getY()-y, 2));
		if(distance<=b.getRadius()+radius+b.getMagneticRange()){
			return true;
		}
		return false;
	}
	
	public boolean inside(Ball b){
		double distance = Math.sqrt(Math.pow(b.getX()-x, 2) + Math.pow(b.getY()-y, 2));
		if(distance<=b.getRadius()+radius){
			return true;
		}
		return false;
	}
	
	public void pullTowards(double x, double y){
		double dx = x-this.x;
		double dy = y-this.y;
		double c = Math.sqrt(Math.pow(dx, 2)+ Math.pow(dy, 2));
		double newDx = magneticSpeed*dx/c;
		double newDy = magneticSpeed*dy/c;
		this.dx=newDx;
		this.dy=newDy;
	}
	
	public void draw(Graphics g, int offsetX, int offsetY){
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(c);
		g.fillOval((int)(x-radius-offsetX), (int)(y-radius-offsetY), (int)radius*2, (int)radius*2);
		g.setColor(Color.BLACK);
		g.drawOval((int)(x-radius-offsetX), (int)(y-radius-offsetY), (int)radius*2, (int)radius*2);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	public void respawn(){
		double x = Math.random()* Driver.MAP_WIDTH;
		this.x = x;
		this.y = -400;
	}
	
	private void deaccelerate(){
		if(dx>0){
			dx-= deaccel;
		}
		if(dx<0){
			dx+= deaccel;
		}
	}
	
	public boolean hit(int x, int y,int r){
		double dist = Math.sqrt(Math.pow(this.x - x, 2) + Math.pow(this.y-y, 2));
		if(dist<r+this.radius){
			return true;
		}
		return false;
	}
	
	private void accelerate(){
		if(dy<maxAccel){
			dy += accel;
		}
		if(!attracted && maxAccel<dy){
			dy = maxAccel;
		}
	}
	
	public double getValue(){
		return value;
	}
	
	public void setValue(double value){
		this.value = value;
	}
	
	public void setDx(double dx){
		this.dx = dx;
	}
	
	public void setDy(double dy){
		this.dy = dy;
	}

	public void setAttracted(boolean b) {
		attracted = b;	
	}

	public double getDx() {
		return dx;
	}
	
	public double getDy(){
		return dy;
	}
	
	

}
