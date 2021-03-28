package Game;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class Enemy extends GameObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	private double radius = 10;
	private double health = 100;
	
	private double[] turretAngles = new double[]{
			0,90,180,270
	};
	
	private double turretTurnSpeed = 0.5;
	private int turretRadius = 10;
	
	private double maxBulletDist = 400;
	private double bulletSpeed = 1;
	private boolean canShoot = true;
	
	private int amount = 0;
	private boolean colorFalse = false;
	
	private Color c = Color.RED;
	
	private Timer fireRate = new Timer(400, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			canShoot = true;
			fireRate.stop();
		}

	});
	
	private Timer hitAnimation = new Timer(100, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(colorFalse){
				c = Color.YELLOW;
			}else{
				c = Color.RED;
			}
			amount++;
			colorFalse = !colorFalse;
			if(amount>4){
				amount = 0;
				hitAnimation.stop();
				c = Color.RED;
			}
		}

	});
	
	public Enemy(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public void update(){
		shoot();
		updateAngleTurrets();
	}
	
	public void draw(Graphics g,int offsetX, int offsetY){
		//((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		for(int i=0;i<turretAngles.length;i++){
			int r = turretRadius;
			double dx = radius * Math.sin(Math.toRadians(turretAngles[i]));
			double dy =  radius * Math.cos(Math.toRadians(turretAngles[i]));
			g.setColor(c);
			g.fillOval((int)(x+dx-r/2)-offsetX, (int)(y+dy-r/2)-offsetY, r, r);
			g.setColor(Color.BLACK);
			g.drawOval((int)(x+dx-r/2)-offsetX, (int)(y+dy-r/2)-offsetY, r, r);
		}
		g.setColor(c);
		g.fillOval((int)(x-radius)-offsetX, (int)(y-radius)-offsetY,(int)(radius*2),(int)(radius*2));
		g.setColor(Color.BLACK);
		g.drawOval((int)(x-radius)-offsetX, (int)(y-radius)-offsetY,(int)(radius*2),(int)(radius*2));
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
	}
	
	
	private void shoot(){
		if(canShoot){
			for(int i=0; i<turretAngles.length;i++){
				double dx = radius * Math.sin(Math.toRadians(turretAngles[i]));
				double dy =  radius * Math.cos(Math.toRadians(turretAngles[i]));
				Driver.f.addBullet(new Bullet(x+dx,y+dy,turretAngles[i]-90,maxBulletDist,0,bulletSpeed,this, Color.RED,50));
			}
			canShoot = false;
			fireRate.start();
		}
	}
	
	public boolean checkHit(int x, int y, int r){
		double distance = Math.sqrt(Math.pow(x-this.x, 2) + Math.pow(y-this.y, 2));
		distance = Math.abs(distance);
		if(distance<r+this.radius){
			return true;
		}
		return false;
	}
	
	private boolean inBounds(double x, double y){
		if(x>0 && x<Driver.MAP_WIDTH && y>0 && y<Driver.MAP_HEIGHT){
			return true;
		}
		return false;
	}
	
	private void updateAngleTurrets(){
		for(int i=0; i<turretAngles.length;i++){
			turretAngles[i] += turretTurnSpeed;
		}
	}
	
	public void hit(double damage){
		health-=damage;
		if(health>100){
			health = 100;
		}
		if(health<0){
			health = 0;
		}
		hitAnimation.start();
	}
	
	public double getHealth(){
		return health;
	}
}
