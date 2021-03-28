package Game;

import java.awt.Color;

public class EnergyMass extends Mass{

	public EnergyMass(double x, double y, double r,double dx, double dy,double energy) {
		super(x, y, r,dx,dy);
		setValue(energy);
		setColor(Color.GREEN);
	}

}
