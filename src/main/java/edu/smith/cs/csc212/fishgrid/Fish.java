package edu.smith.cs.csc212.fishgrid;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * Most Fish behavior lives up in WorldObject (a Fish just looks special!).
 * Or it is in Main, where the missing/found and player fish all act different!
 * 
 * @author jfoley
 */
public class Fish extends WorldObject {
	/**
	 * A fish is only special because of its color now!
	 */
	static Color[] COLORS = {
			Color.red,
			Color.green,
			Color.yellow,
			Color.blue,
			Color.cyan,
			Color.pink,
			Color.gray,
			Color.orange,
			Color.white,
			Color.magenta,
			Color.white
			// Maybe make a special fish that is more points?
			// WHITE WORTH 100!!
	};
	/**
	 * This is an index into the {@link #COLORS} array.
	 */
	int colorIndex;
	/**
	 * Whether or not this is the player;
	 */
	boolean player = false;
	Random rand = new Random();

	boolean fastScared;
	
	/**
	 * Called only on the Fish that is the player!
	 */
	public void markAsPlayer() {
		this.player = true;
	}

	public Color color(){
		return COLORS[this.colorIndex];
	}

	/**
	 * A Fish knows what World it belongs to, because all WorldObjects do.
	 * @param color Color by number.
	 * @param world The world itself.
	 */
	public Fish(int colorI, World world) {
		super(world);
		this.colorIndex = colorI;
		this.fastScared = this.randBool();
		
	}
	
	public boolean randBool(){
		if (rand.nextInt(10) < 4){
			return true;
		}
		return false;
	}
	/**
	 * What actual color is this fish? We store an index, so get it here.
	 * @return the Color object from our array.
	 */
	public Color getColor() {
		return COLORS[this.colorIndex];
	}
	
	/**
	 * Animate our fish by facing left and then right over time.
	 */
	private int dt = 0;
	
	/**
	 * Go ahead and ignore this method if you're not into graphics.
	 * We use "dt" as a trick to make the fish change directions every second or so; this makes them feel a little more alive.
	 */
	@Override
	public void draw(Graphics2D g) {
		dt += 1;
		if (dt > 100) {
			dt = 0;
		}
		Shape circle = new Ellipse2D.Double(-0.6, -0.6, 1.2, 1.2);
		Shape body = new Ellipse2D.Double(-.40, -.2, .8, .4);
		Shape tail = new Ellipse2D.Double(+.2, -.3, .2, .6);
		Shape eye = new Ellipse2D.Double(-.25, -.1, .1, .1);
		
		Color color = getColor();
		Color tailColor = color.darker();

		
		Graphics2D flipped = (Graphics2D) g.create();
		if (dt < 50) {
			flipped.scale(-1, 1);
		}
		
		if (this.player) {
			flipped.setColor(new Color(1f,1f,1f,0.5f));
			flipped.fill(circle);
		}

		// Draw the fish of size (1x1, roughly, at 0,0).
		flipped.setColor(color);
		flipped.fill(body);

		flipped.setColor(Color.black);
		flipped.fill(eye);

		// draw tail:
		flipped.setColor(tailColor);
		flipped.fill(tail);
		
		flipped.dispose();
	}
	
	@Override
	public void step() {
		// Fish are controlled at a higher level; see FishGame.
	}
}
