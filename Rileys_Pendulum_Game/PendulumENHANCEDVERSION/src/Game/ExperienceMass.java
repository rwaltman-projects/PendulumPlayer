package Game;

import java.awt.Color;

public class ExperienceMass extends Mass{
	

	public ExperienceMass(double x, double y, double r,double dx, double dy,double experience) {
		super(x, y, r,dx,dy);
		setValue(experience);
		setColor(Color.BLUE);
	}

}
