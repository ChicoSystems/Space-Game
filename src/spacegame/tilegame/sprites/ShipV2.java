/**
 * 
 */
package spacegame.tilegame.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import spacegame.graphics.SpriteV2;
import spacegame.input.LocationManager;
import spacegame.input.PlayerLocManager;
import spacegame.tilegame.ResourceManager;

/**
 * @author Isaac Assegai
 * ship type 0 is player controlled, ship type 1 is ai controlled.
 */
public class ShipV2 extends SpriteV2 {
	protected Ellipse2D.Double body;
	protected LocationManager locMan;

	public ShipV2(ResourceManager parent, int type) {
		super(parent);
		if(type == 0){
			locMan = new PlayerLocManager(this);
		}else{
			locMan = new PlayerLocManager(this); // we'll need to change this line to an ai manager
		}	
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

	public void drawSprite(Graphics2D g, int offsetX, int offsetY) {
		// Save the current transform of the graphics contexts.
	    AffineTransform saveTransform = g.getTransform();
	    // Create a identity affine transform, and apply to the Graphics2D context
	    AffineTransform identity = new AffineTransform();
	    g.setTransform(identity);
	    Ellipse2D.Double saucer = new Ellipse2D.Double(this.position.x, this.position.y, 25, 25);
	    g.setColor(Color.blue);
	    g.fillArc((int)saucer.getX()+offsetX, (int)saucer.getY()+offsetY, (int)saucer.getWidth(), (int)saucer.getHeight(), 0, 360);
	    g.setColor(Color.white);
	    g.setTransform(saveTransform); //unrotate
	}
	
	/** Clones this Sprite. Does not clone position or velocity info. */
	public Object clone(ResourceManager p, int type) {
	    return new ShipV2(p, type);
	}
	
	public void pressMoveUp(){
		locMan.pressMoveUp();
	}
	
	public void pressMoveDown(){
		locMan.pressMoveDown();
	}
	
	public void pressMoveLeft(){
		locMan.pressMoveLeft();
	}
	
	public void pressMoveRight(){
		locMan.pressMoveRight();
	}
}