package Game;

import java.awt.Color;

public class HealthMass extends Mass{

	public HealthMass(double x, double y, double r,double dx, double dy, double health) {
		super(x, y, r,dx,dy);
		setValue(health);
		setColor(Color.RED);
	}

}
