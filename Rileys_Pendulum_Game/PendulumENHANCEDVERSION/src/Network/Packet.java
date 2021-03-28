package Network;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;

import Game.GameObject;

public class Packet implements Serializable,Cloneable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<GameObject> objects;
	
	public Packet(ArrayList<GameObject> a){
		objects = a;
	}
	
	public ArrayList<GameObject> getArrayList(){
		return objects;
	}
	 
}
