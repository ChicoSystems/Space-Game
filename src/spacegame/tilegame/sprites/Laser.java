package spacegame.tilegame.sprites;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import spacegame.graphics.Sprite;

/**
 * A laser used by turrets and ships. The main weapon of the game.
 * @author Isaac Assegai
 *
 */
public class Laser extends Sprite{
	/**
	 * The object that spawned this laser. Usually a turret or ship.
	 */
	public Object parent;
	/**
	 * The Line that represents the laser itself. Also used for collision detection.
	 */
	Line2D line;
	/**
	 * The power of the laser. Decides damage the laser does.
	 */
	public double power;
	/**
	 * The width of the laser.
	 */
	public double width;
	/**
	 * The color of the laser, decided by what sprites the laser is colliding with.
	 */
	public Color color;
	/**
	 * A record of each sprite this laser has collided with, as well as the last time that
	 * collided.
	 */
	private HashMap<Sprite, Long>collisionSpriteTimes;
	
	/**
	 * Construct the Laser
	 * @param x1 - X1 Location of Laser in World Space
	 * @param y1 - Y1 Location of Laser in World Space
	 * @param x2 - X2 Location of Laser in World Space
	 * @param y2 - Y2 Location of Laser in World Space
	 * @param p The object that spawn this laser.
	 */
	public Laser(double x1, double y1, double x2, double y2, Object p){
		super(null);
		collisionSpriteTimes = new HashMap<Sprite, Long>();
		line = new Line2D.Double(x1, y1, x2, y2);
		parent = p;
		if(parent instanceof Ship){
			power = ((Ship) parent).getPower();
		}else{
			power = ((Turret) parent).power;
		}
		width = mapPowerToWidth(power);
		float red = (float) Ship.map(power, 0, 1000, 0, 1);
		float blue = (float) Ship.map(power, 0, 1000, 1, 0);
		float green = (float) Ship.map(power, 0, 1000, 1, 0);
		System.out.println("Color: " + red + ":" + blue);
		color = new Color(red,blue,green);
	}
	
	/**
	 * Decides lasers width based on the power.
	 * @param pow - The Power of the Laser
	 * @return - The Width of the Laser
	 */
	private double mapPowerToWidth(double pow){
		return Ship.map(pow, 0, 1000, 1, 8);
	}
	
	/**
	 * Returns the current power of the laser.
	 * @return - Current laser power.
	 */
	public double getPower() {
		return power;
	}

	/**
	 * Sets the current power of the laser.
	 * @param power - Power to set
	 */
	public void setPower(double power) {
		this.power = power;
	}

	/**
	 * Sets the parent object of this laser.
	 * @param parent - The object that spawned this laser.
	 */
	public void setParent(Object parent) {
		this.parent = parent;
	}

	/**
	 * Sets the line used as the laser itself.
	 * @param line - The laser line itself.
	 */
	public void setLine(Line2D line) {
		this.line = line;
	}

	/**
	 * Returns the current line representing the laser itself.
	 * @return - The current line of the laser.
	 */
	public Line2D getLine(){
		return line;
	}
	
	/**
	 * Returns the parent object.
	 * @return - The object that spawned this laser.
	 */
	public Object getParent(){
		return parent;
	}

	/**
	 * Records that this laser collided with a certain sprite at a certain time.
	 * @param s - The sprite we've collided with.
	 * @param lastCollideTime - The time of the collision. (System.getcurrentMillas())
	 */
	public void setLastCollideTime(Sprite s, long lastCollideTime) {
		collisionSpriteTimes.put(s, lastCollideTime);
	}

	/**
	 * The amount of time since we last collided with this sprite.
	 * @param s - The sprite under review.
	 * @return - The amount of time since last collision.
	 */
	public long getElapsedCollideTime(Sprite s) {
		return (long) (System.currentTimeMillis() - getLastCollideTime(s));
	}

	/**
	 * Returns the last time that a specific sprite was collided with.
	 * @param s - The sprite under review.
	 * @return - The time of last collision, 0 if there is no last collision.
	 */
	public long getLastCollideTime(Sprite s) {
		long lastCollideTime = 0;
		if(collisionSpriteTimes.containsKey(s)){
			lastCollideTime = collisionSpriteTimes.get(s);
		}
		return lastCollideTime;
	}

}
