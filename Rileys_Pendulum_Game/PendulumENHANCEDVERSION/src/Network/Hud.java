package Network;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import Game.Ball;
import Game.Driver;

public class Hud {
    private int health = 100;
    private int energy = 100;
    private int deaths = 0;
    private int kills = 0;
    private int experience = 100;
    private int level;
	private MiniMap map;
	
	private int experienceLevel;
	
	
	public void draw(Graphics g){
		double barScale = 2;
		int space = 35;
		int offset = 30;
		int thickness = 32;
		g.setColor(new Color(0,0,0,127));
		g.fillRect(Driver.WIDTH-315, Driver.HEIGHT - 110 - offset, (int)(100*barScale), thickness);
		g.fillRect(Driver.WIDTH-315, Driver.HEIGHT + space - 110-offset, (int)(100*barScale), thickness);
		g.fillRect(Driver.WIDTH-315, Driver.HEIGHT + space + space - 110-offset, (int)(100*barScale), thickness);
		g.setColor(new Color(237, 118, 118));
		g.fillRect(Driver.WIDTH-315, Driver.HEIGHT - 110-offset, (int)(health*barScale), thickness);
		g.setColor(new Color(119, 239, 171));
		g.fillRect(Driver.WIDTH-315, Driver.HEIGHT + space - 110-offset, (int)(energy*barScale), thickness);
		g.setColor(new Color(119, 187, 239));
		g.fillRect(Driver.WIDTH-315, Driver.HEIGHT + space + space - 110-offset, (int)(experienceLevel*barScale), thickness);
		g.setColor(Color.BLACK);
		g.drawRect(Driver.WIDTH-115 - (int)(100*barScale), Driver.HEIGHT - 110-offset, (int)(100*barScale), thickness);
		g.drawRect(Driver.WIDTH-115 - (int)(100*barScale), Driver.HEIGHT + space - 110-offset, (int)(100*barScale), thickness);
		g.drawRect(Driver.WIDTH-115 - (int)(100*barScale), Driver.HEIGHT + space + space - 110-offset, (int)(100*barScale), thickness);

		
		Font f = new Font(Font.MONOSPACED, Font.BOLD, 20);
		g.setFont(f);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 125, 50);
		g.setColor(Color.WHITE);
		g.drawString("Kills: " + kills, 10, 20);
		g.drawString("Deaths: " + deaths, 10, 40);
		map.draw(g);
	}
	
	public void updateInfo(int health, int energy, int deaths, int kills,int level, int experience, MiniMap map){
		this.health = health;
		this.energy = energy;
		this.deaths = deaths;
		this.kills = kills;
		this.map = map;
		this.level = level;
		this.experience = experience;
		if(level == 0){
			experienceLevel = this.experience;
		}else{
			experienceLevel = (this.experience - Ball.experienceLevels[level]) / (Ball.experienceLevels[level]-Ball.experienceLevels[level-1])*100;
		}
	}
}
