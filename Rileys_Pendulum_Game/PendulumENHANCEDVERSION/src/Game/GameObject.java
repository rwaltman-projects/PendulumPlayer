package Game;

import java.awt.Graphics;
import java.io.Serializable;

public abstract class GameObject implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void draw(Graphics g, int offsetX, int offsetY){
		
	}
	
	public void update(){
		
	}
	
	public double getX(){
		return 1000;
	}
	
	public double getY(){
		return 1000;
	}
}
