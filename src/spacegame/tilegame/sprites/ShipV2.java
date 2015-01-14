/**
 * 
 */
package spacegame.tilegame.sprites;

import java.awt.geom.Ellipse2D;

import spacegame.graphics.SpriteV2;
import spacegame.input.LocationManager;
import spacegame.tilegame.ResourceManager;
import spacegame.util.Vector2D;

/**
 * @author Isaac Assegai
 *
 */
public class ShipV2 extends SpriteV2 {
	protected Ellipse2D.Double body;
	protected LocationManager locMan;

	public ShipV2(ResourceManager parent, LocationManager locMan) {
		super(parent);
		locMan = this.locMan;
	}

	protected void updateLocation(double elapsedTime) {
		currentForce = locMan.calculate(); // Update the applied forces.
		currentForce.truncate(maxForce); // Reduce force if it's over max.
		currentAcceleration = currentForce.scalarDiv(mass); // calculate acceleration
		currentAcceleration = currentAcceleration.scalarMult(elapsedTime); //Reduce acceleration over time passed
    	velocity = velocity.plus(currentAcceleration);//update velocity
    	velocity.truncate(maxSpeed); //Limit velocity based on maxSpeed.
    	velocity = velocity.scalarMult(elapsedTime); //Reduce velocity based on time passed.
        position = position.plus(velocity); //Update position based on velocity.
		
	}

	protected void drawSprite(double elapsedTime) {
		
	}
	
	/** Clones this Sprite. Does not clone position or velocity info. */
	public Object clone(ResourceManager p, LocationManager l) {
	    return new ShipV2(p, l);
	}



}
