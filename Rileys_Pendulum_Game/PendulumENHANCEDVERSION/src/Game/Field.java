package Game;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.Timer;

public class Field extends GameObject{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int width;
	private int height;
	private  ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private SafeZone[] safeZone = new SafeZone[4];
	private ArrayList<Mass> mass = new ArrayList<Mass>();
	private ArrayList<DiamondTile> diamonds= new ArrayList<DiamondTile>();

	private Timer massMakerTimer = new Timer(1000, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			double x = Math.random()*width;
			mass.add(new ExperienceMass(x,-400,5,0,0,5));
			x = Math.random()*width;
			mass.add(new HealthMass(x,-400,5,0,0,5));
			x = Math.random()*width;
			mass.add(new EnergyMass(x,-400,5,0,0,0.25));
		}

	});

	public Field(int width, int height){
		this.width = width;
		this.height = height;
		makeSafeZones();
		makeDiamonds();
		makeMass();
		//massMakerTimer.start();
		//makeEnemies();
	}

	private void makeMass(){
		for(int i=0;i<10;i++){
			boolean notValid = true;
			while(notValid){
				double x = Math.random()*width;
				double y = Math.random()*height;
				int posX = (int)x / Tile.WIDTH;
				int posY = (int)y/ Tile.WIDTH;
				if(!(hitDiamond(posX, posY)>0)){
					notValid = false;
					mass.add(new HealthMass(x,y,6,0,0,5));
				}
			}
		}
		for(int i=0;i<10;i++){
			boolean notValid = true;
			while(notValid){
				double x = Math.random()*width;
				double y = Math.random()*height;
				int posX = (int)x / Tile.WIDTH;
				int posY = (int)y/ Tile.WIDTH;
				if(!(hitDiamond(posX, posY)>0)){
					notValid = false;
					mass.add(new ExperienceMass(x,y,6,0,0,2));
				}
			}
		}
		for(int i=0;i<10;i++){
			boolean notValid = true;
			while(notValid){
				double x = Math.random()*width;
				double y = Math.random()*height;
				int posX = (int)x / Tile.WIDTH;
				int posY = (int)y/ Tile.WIDTH;
				if(!(hitDiamond(posX, posY)>0)){
					notValid = false;
					mass.add(new EnergyMass(x,y,6,0,0,0.01));
				}
			}
		}
	}

	private void makeSafeZones(){
		int[] offsetsX = new int[]{
				0,0,0,0
		};
		int[] offsetsY = new int[]{
				0,-25,27,27
		};
		int safeZoneWidth = 250-Tile.WIDTH;
		for(int i =0;i<Driver.spawnPointsX.length;i++){
			safeZone[i] = new SafeZone(Driver.spawnPointsX[i]-safeZoneWidth/2+2+offsetsX[i],Driver.spawnPointsY[i]-Tile.WIDTH/2+offsetsY[i],safeZoneWidth,safeZoneWidth);
		}
	}

	public void update(){
		//updateEnemies();
		updateBullets();
		updateMass();
	}

	public boolean inSafeZone(int x, int y){
		for(int i=0;i<safeZone.length;i++){
			if(safeZone[i].inside(x, y)){
				return true;
			}
		}
		return false;
	}

	private void updateMass(){
		for(int i=0;i<Driver.p.size();i++){
			for(int j =0;j<mass.size();j++){
				if(mass.get(j).closeTo(Driver.p.get(i))){
					if(mass.get(j).inside(Driver.p.get(i))){
						Driver.p.get(i).addMass(mass.get(j));
						mass.remove(j);
						break;
					}else{
						mass.get(j).pullTowards(Driver.p.get(i).getX(),Driver.p.get(i).getY());
					}
					mass.get(j).setAttracted(true);
				}else{
					mass.get(j).setAttracted(false);
				}
				mass.get(j).update();
				if(mass.get(j).getY()>height+200){
					mass.get(j).respawn();
				}
			}
		}
		for(int i =0;i<mass.size();i++){
			if(hitDiamond((int)mass.get(i).getX(), (int)mass.get(i).getY())>0){
				int a = hitDiamond((int)mass.get(i).getX(), (int)mass.get(i).getY());
				if(a==1){
					double temp = mass.get(i).getDx();
					mass.get(i).setDx(mass.get(i).getDy());
					mass.get(i).setDy(temp);
				}else if(a==2){
					double temp = mass.get(i).getDx();
					mass.get(i).setDx(-mass.get(i).getDy());
					mass.get(i).setDy(-temp);
				}else if(a==3){
					double temp = mass.get(i).getDx();
					mass.get(i).setDx(mass.get(i).getDy());
					mass.get(i).setDy(temp);
				}else{
					double temp = mass.get(i).getDx();
					mass.get(i).setDx(-mass.get(i).getDy());
					mass.get(i).setDy(-temp);
				}
			}
			for(int j=0;j<mass.size();j++){
				if(j!=i){
					if(mass.get(i).hit((int)mass.get(j).getX(), (int)mass.get(j).getY(), (int)mass.get(j).getRadius())){
						double temp = mass.get(i).getDx();
						mass.get(i).setDx(mass.get(j).getDx());
						mass.get(j).setDx(temp);
					}
				}
			}
		}
	}

	
	public void makeDiamonds(){
		boolean odd = false;
		for(int i=200;i<height-100;i+=200){
			if(odd){
				for(int j=200;j<width-200;j+=400){
					diamonds.add(new DiamondTile(j, i));
				}
			}else{
				for(int j=400;j<width-200;j+=400){
					diamonds.add(new DiamondTile(j, i));
				}
			}
			odd = !odd;
		}
	}
	
	public void updateEnemies(){
		for(int i=0; i<enemies.size();i++){
			if(enemies.get(i).getHealth()<=0){
				enemies.remove(i);
			}else{
				enemies.get(i).update();
			}
		}
	}


	private void makeEnemies(){
		enemies.add(new Enemy(width/2+Tile.WIDTH/2,height/2 + Tile.WIDTH/2));
	}



	private void drawMass(Graphics g, int offsetX, int offsetY){
		for(int i=0;i<mass.size();i++){
			mass.get(i).draw(g, offsetX, offsetY);
		}
	}
	
	private void drawDiamonds(Graphics g, int offsetX, int offsetY){
		for(int i=0;i<diamonds.size();i++){
			diamonds.get(i).draw(g, offsetX, offsetY);
		}
	}

	public void makeMassExplosion(int x, int y,int amountHealth, int amountEnergy, int amountExperience){
		for(int i = 0;i<amountHealth;i++){
			double dir = Math.random()*360;
			double speedX = Math.random()*2;
			double speedY = speedX;
			double dx = speedX*Math.cos(Math.toRadians(dir));
			double dy = speedY*Math.sin(Math.toRadians(dir%180 + 180));
			mass.add(new HealthMass(x,y,5,dx,dy,5));
		}
		for(int i = 0;i<amountEnergy;i++){
			double dir = Math.random()*360;
			double speedX = Math.random()*2;
			double speedY = speedX;
			double dx = speedX*Math.cos(Math.toRadians(dir));
			double dy = speedY*Math.sin(Math.toRadians(dir%180 +180));
			mass.add(new EnergyMass(x,y,5,dx,dy,0.0025));
		}
		int times = amountExperience/100;
		for(int i = 0;i<times;i++){
			double speedX = Math.random()*2;
			double speedY = speedX;
			double dir = Math.random()*360;
			double dx = speedX*Math.cos(Math.toRadians(dir));
			double dy = speedY*Math.sin(Math.toRadians(dir%180 + 180));
			mass.add(new ExperienceMass(x,y,5,dx,dy,100));
		}
		System.out.println(mass.size());
	}

	
	public int hitDiamond(int x, int y){
		for(int i=0;i<diamonds.size();i++){
			int point  = diamonds.get(i).inside(x, y);
			if(point>0){
				return point;
			}
		}
		return -1;
	}




	private void drawEnemies(Graphics g, int offsetX, int offsetY){
		for(int i=0; i<enemies.size();i++){
			enemies.get(i).draw(g,offsetX,offsetY);
		}
	}

	public void draw(Graphics g,int offsetX, int offsetY){
//		g.setColor(Color.WHITE);
//		g.fillRect(0-offsetX, 0-offsetY, width, height);
		drawDiamonds(g, offsetX, offsetY);
		drawMass(g,offsetX,offsetY);
		drawBullets(g,offsetX,offsetY);
		drawEnemies(g,offsetX,offsetY);
		for(int i=0;i<safeZone.length;i++){
			safeZone[i].draw(g,offsetX, offsetY);	
		}
	}



	private void updateBullets(){
		for(int i=0; i<bullets.size();i++){
			bullets.get(i).update();
			if(hitDiamond((int)bullets.get(i).getX(), (int)bullets.get(i).getY())>0){
				int a = hitDiamond((int)bullets.get(i).getX(), (int)bullets.get(i).getY());
				if(a==1){
					double temp = bullets.get(i).getDx();
					bullets.get(i).setDx(bullets.get(i).getDy());
					bullets.get(i).setDy(temp);
				}else if(a==2){
					double temp = bullets.get(i).getDx();
					bullets.get(i).setDx(-bullets.get(i).getDy());
					bullets.get(i).setDy(-temp);
				}else if(a==3){
					double temp = bullets.get(i).getDx();
					bullets.get(i).setDx(bullets.get(i).getDy());
					bullets.get(i).setDy(temp);
				}else{
					double temp = bullets.get(i).getDx();
					bullets.get(i).setDx(-bullets.get(i).getDy());
					bullets.get(i).setDy(-temp);
				}

			}
			for(int j=0;j<Driver.p.size();j++){
				if(checkPlayerHit(Driver.p.get(j), bullets.get(i))){
					if(Driver.p.get(j).canTakeDamage()){
						if(!Driver.p.get(j).getDead() && Driver.p.get(j).getHealth()<=bullets.get(i).getDamage()){
							if(bullets.get(i).getOwner() instanceof Ball){
								Ball b = (Ball) (bullets.get(i).getOwner());
								b.addKill();
							}
						}
						Driver.p.get(j).hit(bullets.get(i).getDamage());
					}
					bullets.remove(i);
					i--;
					break;
				}else if(bullets.get(i).checkMax()){
					bullets.remove(i);
					i--;
					break;
				}else if(checkEnemiesHit(bullets.get(i))>=0){
					int a = checkEnemiesHit(bullets.get(i));
					if(enemies.get(a).getHealth()<=bullets.get(i).getDamage()){
						Ball b = (Ball)bullets.get(i).getOwner();
						b.addExperience(200);
					}
					enemies.get(a).hit(bullets.get(i).getDamage());
					bullets.remove(i);
					i--;
					break;
				}
			}
		}
	}




	private boolean checkPlayerHit(Ball p,Bullet b){
		if(!b.getOwner().equals(p)){
			if(p.checkHit((int)b.getX(), (int)b.getY(), b.getRadius())){
				return true;
			}
		}
		return false;
	}


	private int checkEnemiesHit(Bullet b){
		for(int i=0; i<enemies.size();i++){
			if(!b.getOwner().equals(enemies.get(i))){
				if(enemies.get(i).checkHit((int)b.getX(), (int)b.getY(), b.getRadius())){
					return i;
				}
			}
		}
		return -1;
	}

	public void drawBullets(Graphics g, int offsetX, int offsetY){
		for(int i=0; i<bullets.size(); i++){
			bullets.get(i).draw(g,offsetX, offsetY);
		}
	}

	public void addBullet(Bullet b){
		bullets.add(b);
	}


	private boolean inBounds(double x, double y){
		if(x>0 && x<Driver.MAP_WIDTH && y>0 && y<Driver.MAP_HEIGHT){
			return true;
		}
		return false;
	}
	
	public ArrayList<Bullet> getBullets(){
		return bullets;
	}
	
	public ArrayList<Mass> getMass(){
		return mass;
	}
	
	public ArrayList<DiamondTile> getDiamonds(){
		 return diamonds;
	}
}
