package Game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class Ball extends GameObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x;
	private double y;
	private double dx;
	private double dy;
	private double radius = 10; 
	private double aVel = 0;
	private double aAcc = 0;
	private double power = 0.020;

	private double oldX;
	private double oldY;
	private double fallingDx;
	private double fallingDy;

	private double armLength = 100;
	private double minArm = 50;
	private double maxArm = 200;
	private double armChangeSpeed = 2;
	private double angle = 0;

	private static double GRAV = -0.025;
	private static double FALLING_GRAV = 0.025;
	private static double MAX_SPEED = 2.5;
	private double fallingDeaccel = 0.002;

	private boolean increaseArm = false;
	private boolean decreaseArm = false;
	private boolean right = false;
	private boolean left = false;

	private boolean attached = true;

	private boolean attachWhenReady = false;
	private boolean shooting =false;
	private boolean shotUp = false;

	private double hookX;
	private double hookY;
	private double hookDx;
	private double hookDy;
	private double hookSpeed = 8;
	private double hookRadius = 5;

	private double turretAngle = 0;
	private double maxBulletDist = 400;
	private double bulletSpeed = 10;
	private double bulletDamage = 10;
	private int turretRadius = 5;
	
	
	private double maxHealth = 100;
	private double health = 100;
	private double maxEnergy = 100;
	private double energy = 100;

	private Color base;
	private Color c;
	private boolean colorfalse = false;
	private int amount = 0;

	private boolean canShoot = true;
	private boolean canTakeDamage = true;

	private int respawnX;
	private int respawnY;

	private int deaths = 0;
	private int kills = 0;
	private int level =0;
	private int experience = 0;
	
	private double magneticRange = 40;
	
	public static int[] experienceLevels = new int[]{
		0,100,300,600
	};

	public static int[] radiusLevels = new int[]{
		10,10,10,10//12,15,20	
	};

	public static int[] turretRadiusLevels = new int[]{
		5,5,5,5//6,7,10
	};

	public static int[] bulletDamageLevel = new int[]{
		10,15,30,50
	};

	private boolean dead = false;
	private boolean done = false;

	private int secondsDead = 0;

	public Ball(double x, double y, Color c){
		base = new Color(c.getRed(), c.getBlue(),c.getGreen());
		c = base;
		hookX = x;
		hookY = y;
		respawnX = (int)x;
		respawnY = (int)y;
	}


	private Timer hitAnimation = new Timer(100, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(colorfalse){
				c = base;
			}else{
				c = Color.WHITE;
			}
			amount++;
			colorfalse = !colorfalse;
			if(amount>1){
				amount = 0;
				hitAnimation.stop();
				c = base;
			}
		}

	});

	private Timer respawnTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			secondsDead++;
			if(secondsDead>=3){
				health = 100;
				energy = 100;
				respawnTimer.stop();
				secondsDead = 0;
				armLength = 100;
				dead = false;
				experience = 0;
			}
		}

	});

	public void update(){
		doOnce();
		checkLevelStats();
		checkDead();
		checkInSafeZone();
		changeArm();
		hitBoundries();
		checkHit();
		if(attached){
			move();
			swing();
		}else{
			regenEnergy();
			fall();
			updateHook();
		}
	}

	private void checkLevelStats(){
		int level = 0;
		for(int i=0;i<experienceLevels.length;i++){
			if(experience>=experienceLevels[i]){
				level = i;
			}
		}
		this.level = level;
		radius = radiusLevels[this.level];
		turretRadius = turretRadiusLevels[this.level];
		hookRadius = turretRadiusLevels[this.level];
		bulletDamage = bulletDamageLevel[this.level];
	}

	private void doOnce(){
		if(!done){
			c = base;
			done = true;
		}
	}

	private void checkDead(){
		if(health<=0 && !respawnTimer.isRunning()){
			respawn();
		}
	}

	private void checkInSafeZone(){
		if(Driver.f.inSafeZone((int)this.x, (int)this.y)){
			canShoot = false;
			canTakeDamage = false;
		}else{
			canShoot = true;
			canTakeDamage = true;
		}
	}


	private void checkHit(){
		if(attached){
			if(Driver.f.hitDiamond((int)x, (int)y)>0){
				aVel = -aVel;
			}
		}else{
			if(Driver.f.hitDiamond((int)x, (int)y)>0){
				int a = Driver.f.hitDiamond((int)x, (int)y);
				if(a==1){
					double temp = fallingDx;
					fallingDx = fallingDy;
					fallingDy = temp;
				}else if(a==2){
					double temp = fallingDx;
					fallingDx = -fallingDy;
					fallingDy = -temp;
				}else if(a==3){
					double temp = fallingDx;
					fallingDx = fallingDy;
					fallingDy = temp;
				}else{
					double temp = fallingDx;
					fallingDx = -fallingDy;
					fallingDy = -temp;
				}

			}
		}
	}


	private boolean inBounds(double x, double y){
		if(x>0 && x<Driver.MAP_WIDTH && y>0 && y<Driver.MAP_HEIGHT){
			return true;
		}
		return false;
	}

	private void move(){
		int a = 0;
		if(right){
			a++;
		}
		if(left){
			a--;
		}
		if(a == 0){
			regenEnergy();
		}
		if(energy>0){
			aVel += a*power;
			energy -= Math.abs(a)*0.2;
		}
	}

	private void regenEnergy(){
		if(energy<100){
			energy += 0.025;
		}
	}


	public void hitBoundries(){
		if(y>Driver.MAP_HEIGHT-radius || y<radius){
			if(!attached){
				fallingDy = -fallingDy;
			}else{
				aVel = -aVel;
			}
		}
		if(x>Driver.MAP_WIDTH-radius || x<radius){
			if(!attached){
				fallingDx = -fallingDx;
			}else{
				aVel = -aVel;
			}
		}
	}

	private void fall(){
		x+=fallingDx;
		y+=fallingDy;
		if(fallingDy<MAX_SPEED){
			fallingDy+=FALLING_GRAV;
		}
//		if(fallingDx>0){
//			fallingDx-=fallingDeaccel;
//		}
//		if(fallingDx<0){
//			fallingDx+=fallingDeaccel;
//		}
	}

	private void swing(){
		aAcc = GRAV*Math.sin(Math.toRadians(angle));
		angle+=aVel;
		aVel+=aAcc;
		aVel *= 0.99;
		fallingDx = x-oldX;
		fallingDy = y-oldY;
		dx = armLength*Math.cos(Math.toRadians(angle));
		dy = armLength*Math.sin(Math.toRadians(angle));
		oldX = x;
		oldY = y;
		x = hookX + dy;
		y = hookY + dx;
	}

	public boolean checkHit(int x, int y, int r){
		double distance = Math.sqrt(Math.pow(x-this.x, 2) + Math.pow(y-this.y, 2));
		distance = Math.abs(distance);
		if(distance<r+this.radius){
			return true;
		}
		return false;
	}


	private void changeArm(){
		int a = 0;
		if(armLength>minArm && increaseArm){
			a--;
		}
		if(armLength<maxArm && decreaseArm){
			a++;
		}
		if(armLength>maxArm){
			a = -1;
		}else if(armLength<minArm){
			a = 1;
		}
		if((armLength>minArm || a>0)){
			armLength += a*armChangeSpeed;
		}
	}

	private void updateHook(){
		if(shooting){
			armLength = Math.sqrt(Math.pow(hookX-x, 2) + Math.pow(hookY-y, 2));
			if(inBounds(hookX,hookY) && armLength < maxArm){
				if(Driver.f.hitDiamond((int)hookX, (int)hookY)>0){
					shooting = false;
					attached = true;
					double dx = hookX - x;
					double dy = hookY - y;
					angle = Math.toDegrees(Math.atan(dx/dy));
					if(hookY>y){
						angle += 180;
						if(this.x>hookX){
							aVel = -getSpeed();
						}else{
							aVel = getSpeed();
						}
					}
				}else{
					hookX += hookDx;
					hookY += hookDy;
				}
			}else{
				shooting = false;
			}
		}else if(!attached){
			hookX = x;
			hookY = y;
		}
	}

	public void draw(Graphics g,int offsetX, int offsetY){
		if(!dead){
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			if(shooting||attached){
				g.setColor(Color.BLACK);
				g.drawLine((int)x-offsetX, (int)y-offsetY, (int)hookX-offsetX, (int)hookY-offsetY);
				g.setColor(c);
				g.fillOval((int)(hookX-hookRadius-offsetX), (int)(hookY-hookRadius-offsetY), (int)(hookRadius*2), (int)(hookRadius*2));
				g.setColor(Color.BLACK);
				g.drawOval((int)(hookX-hookRadius-offsetX), (int)(hookY-hookRadius-offsetY), (int)(hookRadius*2), (int)(hookRadius*2));
			}
			if(attached){
				g.setColor(c);
				if(angle<-90 || angle>90){
					double y1 = hookY - y;
					double x1 = hookX - x;
					double angle = Math.toDegrees(Math.atan(x1/y1));
					double dx = radius * Math.sin(Math.toRadians(angle));
					double dy =  radius * Math.cos(Math.toRadians(angle));
					int r = (int) hookRadius;
					g.setColor(c);
					g.fillOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
					g.setColor(Color.BLACK);
					g.drawOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
				}else{
					double y1 = hookY - y;
					double x1 = hookX - x;
					double angle = Math.toDegrees(Math.atan(x1/y1));
					double dx = radius * Math.sin(Math.toRadians(angle+180));
					double dy =  radius * Math.cos(Math.toRadians(angle+180));
					int r = (int)hookRadius;
					g.setColor(c);
					g.fillOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
					g.setColor(Color.BLACK);
					g.drawOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
				}
			}else if(shooting){
				if(shotUp){
					double y1 = hookY - y;
					double x1 = hookX - x;
					double angle = Math.toDegrees(Math.atan(x1/y1));
					double dx = radius * Math.sin(Math.toRadians(angle+180));
					double dy =  radius * Math.cos(Math.toRadians(angle+180));
					int r = 5;
					g.setColor(c);
					g.fillOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
					g.setColor(Color.BLACK);
					g.drawOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
				}else if(!shotUp){
					double y1 = hookY - y;
					double x1 = hookX - x;
					double angle = Math.toDegrees(Math.atan(x1/y1));
					double dx = radius * Math.sin(Math.toRadians(angle));
					double dy =  radius * Math.cos(Math.toRadians(angle));
					int r = 5;
					g.setColor(c);
					g.fillOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
					g.setColor(Color.BLACK);
					g.drawOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
				}
			}
//			if(right && attached){
//				g.setColor(new Color(255,127,0,127));
//				double dx = (radius-5) * Math.sin(Math.toRadians(angle-90));
//				double dy =  (radius-5) * Math.cos(Math.toRadians(angle-90));
//				int r = 10;
//				g.fillOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
//			}
//			if(left && attached){
//				g.setColor(new Color(255,127,0,127));
//				double dx = (radius-5) * Math.sin(Math.toRadians(angle+90));
//				double dy =  (radius-5) * Math.cos(Math.toRadians(angle+90));
//				int r = 10;
//				g.fillOval((int)(x+dx-r)-offsetX, (int)(y+dy-r)-offsetY, r*2, r*2);
//			}
			g.setColor(c);
			double dx = radius * Math.sin(Math.toRadians(turretAngle+90));
			double dy =  radius * Math.cos(Math.toRadians(turretAngle+90));
			g.fillOval((int)(x+dx-turretRadius)-offsetX, (int)(y+dy-turretRadius)-offsetY, turretRadius*2, turretRadius*2);
			g.setColor(Color.BLACK);
			g.drawOval((int)(x+dx-turretRadius)-offsetX, (int)(y+dy-turretRadius)-offsetY, turretRadius*2, turretRadius*2);
			g.setColor(c);
			g.fillOval((int)(x-radius)-offsetX, (int)(y-radius)-offsetY,(int)(radius*2),(int)(radius*2));
			g.setColor(Color.BLACK);
			g.drawOval((int)(x-radius)-offsetX, (int)(y-radius)-offsetY,(int)(radius*2),(int)(radius*2));
//			g.setColor(new Color(255,0,127,60));
//			g.fillOval((int)(x-magneticRange-offsetX), (int)(y-magneticRange-offsetY), (int)(magneticRange*2), (int)(magneticRange*2));
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
		}

	}



	public void setIncreaseArm(boolean b){
		increaseArm = b;
	}

	public void setDecreaseArm(boolean b){
		decreaseArm = b;
	}

	public void setArmLength(double len){
		armLength = len;
	}

	public void setRight(boolean b){
		right = b;
	}

	public void setLeft(boolean b){
		left = b;
	}

	public void setAngle(double angle){
		this.angle = angle;
	}

	public double getX(){
		return x;
	}

	public double getY(){
		return y;
	}

	public void setAttached(boolean b){
		attached = b;
	}

	public boolean getAttached(){
		return attached;
	}

	public void setAVel(double v){
		aVel = v;
	}

	public double getSpeed(){
		double speed =  Math.sqrt(Math.pow(fallingDx, 2) + Math.pow(fallingDy, 2));
		return speed;
	}

	public boolean getAttachedWhenReady(){
		return attachWhenReady;
	}

	public double getFallingDx(){
		return fallingDx;
	}

	public void setShotUp(boolean b){
		shotUp = b;
	}

	public void setTurretAngle(double a){
		turretAngle = a;
	}

	private void shootBullet(){
		Driver.f.addBullet(new Bullet(x,y,turretAngle,maxBulletDist,getSpeed(),bulletSpeed,this,base,bulletDamage));
	}

	public void hit(double amount){
		health-=amount;
		if(!hitAnimation.isRunning()){
			hitAnimation.start();
		}else{
			amount = 1;
		}
		if(health<0){
			health = 0;
		}
		if(health>100){
			health = 100;
		}
	}

	public void giveCommand(String s){
		if(!dead){
			if(s.substring(0,5).equals("space")){
				attached = false;
			}else{
				String p = s.substring(0, 8);
				String r = s.substring(0, 9);
				if(p.equals("pressedw")){
					increaseArm = true;
				}
				if(r.equals("releasedw")){
					increaseArm = false;
				}
				if(p.equals("presseds")){
					decreaseArm = true;
				}
				if(r.equals("releaseds")){
					decreaseArm = false;
				}
				if(p.equals("pressedd")){
					right = true;
				}
				if(r.equals("releasedd")){
					right = false;
				}
				if(p.equals("presseda")){
					left = true;
				}
				if(r.equals("releaseda")){
					left = false;
				}

				if(s.substring(0, 4).equals("dir:")){
					String digits = "0123456789.";
					int ind = s.indexOf(":");
					s = (s.substring(ind+1));
					double dir;
					for(int i=0;i<s.length();i++){
						String c = s.substring(i,i+1);
						if(digits.indexOf(c)<0){
							return;
						}
					}
					if(s.substring(0,1).equals("0")){
						return;
					}else{
						dir = Double.parseDouble(s);
					}
					turretAngle = dir;
				}
				if(s.equals("shootbullet")){
					if(canShoot){
						shootBullet();
					}
				}
				if(s.length()>10 && s.substring(0,10).equals("shoothook:")){
					int comma = s.indexOf(",");
					int x = Integer.parseInt(s.substring(10,comma));
					int y = Integer.parseInt(s.substring(comma+1));
					if(!attached){
						hookDy = -hookSpeed*Math.cos(Math.toRadians(turretAngle-90));
						hookDx = -hookSpeed*Math.sin(Math.toRadians(turretAngle-90));
						shooting = true;
					}
				}
			}
		}
	}

	public boolean canTakeDamage(){
		return canTakeDamage;
	}


	public int getHealth() {
		return (int) health;
	}

	public int getEnergy(){
		return (int) energy;
	}

	public void respawn(){
		int oldX = (int) x;
		int oldY = (int) y;
		hookX = respawnX;
		hookY = respawnY;
		x = respawnX;
		y = respawnY + 50;
		Driver.f.makeMassExplosion(oldX, oldY, (int)maxHealth/10, (int)energy/10, experience);
		dx = 0;
		dy = 0;
		fallingDx = 0;
		fallingDy = 0;
		increaseArm = false;
		decreaseArm = false;
		left = false;
		right = false;
		deaths++;
		respawnTimer.start();
		dead = true;
	}

	public void addKill(){
		kills++;
		experience += 50;
	}


	public int getDeaths() {
		return deaths;
	}


	public int getKills() {
		return kills;
	}

	public Color getColor(){
		return base;
	}


	public boolean getDead() {
		return dead;
	}

	public int getExperience(){
		return experience;
	}

	public int getLevel(){
		return level;
	}

	public void addExperience(double i) {
		experience+=i;
	}

	public double getRadius() {
		return radius;
	}
	
	public double getMagneticRange(){
		return magneticRange;
	}

	public void addMass(Mass mass) {
		if(mass instanceof HealthMass){
			addHealth(mass.getValue());
		}else if(mass instanceof ExperienceMass){
			addExperience(mass.getValue());
		}else{
			addEnergy(energy);
		}
			
	}

	private void addHealth(double value) {
		health += value;
		if(health>maxHealth){
			health = maxHealth;
		}
		if(health<0){
			health = 0;
		}
	}
	
	private void addEnergy(double value) {
		energy += value;
		if(energy>maxEnergy){
			energy = maxEnergy;
		}
		if(energy<0){
			energy = 0;
		}
	}

}