package edu.smith.cs.csc212.fishgrid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class FishFood extends WorldObject {
    
    Color color = new Color(102, 51, 0);

    public FishFood(World world){
        super(world);
    }

    @Override
	public void draw(Graphics2D g) {
        g.setColor(this.color);
        Shape food = new Ellipse2D.Double(.2, .2, .2, .2);
		g.fill(food);
	}

	@Override
	public void step() {
	}
}