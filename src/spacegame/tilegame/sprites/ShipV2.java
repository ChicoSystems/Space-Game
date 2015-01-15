/**
 * 
 */
package spacegame.tilegame.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import spacegame.graphics.SpriteV2;
import spacegame.input.LocationManager;
import spacegame.input.PlayerLocManager;
import spacegame.tilegame.ResourceManager;
import spacegame.util.Vector2D;

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
		currentForce = locMan.calculate(elapsedTime); // Update the applied forces.
		currentForce.truncate(maxForce); // Reduce force if it's over max.
		currentAcceleration = currentForce.scalarDiv(mass); // calculate acceleration
		currentAcceleration = currentAcceleration.scalarMult(elapsedTime); //Reduce acceleration over time passed
		//velocity = velocity.scalarMult(elapsedTime/1000); //Reduce acceleration over time passed
    	velocity = velocity.plus(currentAcceleration);//update velocity
    	//apply friction get the tanget from the normal (perp vector)
    	Vector2D tangent = velocity.unitVector().perp().perp();
		// compute the tangential velocity, scale by friction
    	double tv = velocity.dotProduct(tangent)*.8*(elapsedTime/1000);
		// subtract that from the main velocity
    	velocity = velocity.minus(tangent.scalarMult(tv));
    	if(velocity.length() < .001)velocity = new Vector2D(0,0); //stop velocity if it's to slow
    	velocity.truncate(maxSpeed); //Limit velocity based on maxSpeed.
    	//update rotation
    	torque = locMan.calculateTorque(elapsedTime);	
    	angularAcceleration = torque / mass; 
    	angularVelocity += angularAcceleration * (elapsedTime/1000);
    	angularVelocity = angularVelocity - angularVelocity * 1*(elapsedTime/1000);
    	if(Math.abs(angularVelocity) < .005) angularVelocity = 0;
    	orientation += 0.5 * angularVelocity * (elapsedTime/1000);
    	
    	
        position = position.plus(velocity); //Update position based on velocity.
	}

	public void drawSprite(Graphics2D g, int offsetX, int offsetY) {
		// Save the current transform of the graphics contexts.
	    AffineTransform saveTransform = g.getTransform();
	    // Create a identity affine transform, and apply to the Graphics2D context
	    AffineTransform identity = new AffineTransform();
	    g.setTransform(identity);
	    
	    
	    Ellipse2D.Double saucer = new Ellipse2D.Double(this.position.x, this.position.y, 25, 25);
	    double middleX = (int)saucer.getX()+offsetX+(int)saucer.getWidth()/2;
	    double middleY = (int)saucer.getY()+offsetY+(int)saucer.getHeight()/2;
	    g.rotate(this.orientation, middleX, middleY);
	    g.setColor(Color.blue);
	    g.fillArc((int)saucer.getX()+offsetX, (int)saucer.getY()+offsetY, (int)saucer.getWidth(), (int)saucer.getHeight(), 0, 360);
	    g.drawLine((int)saucer.getX()+offsetX, (int)saucer.getY()+offsetY, (int)saucer.getX()+offsetX+25, (int)saucer.getY()+offsetY);
	    g.setColor(Color.white);
	    g.fillArc((int)middleX, (int)middleY, 4, 4, 0, 360);
	    g.setTransform(saveTransform); //unrotate
	    drawTestGui(g);
	}
	
	/** Clones this Sprite. Does not clone position or velocity info. */
	public Object clone(ResourceManager p, int type) {
	    return new ShipV2(p, type);
	}
	
	public void drawTestGui(Graphics2D g){
		g.drawString("input: "+ ((PlayerLocManager)locMan).inputVector, 5, 25);
		g.drawString("force: "+ currentForce, 5, 50	);
		g.drawString("accel: "+ currentAcceleration, 5, 75	);
		g.drawString("veloc: "+ velocity, 5, 100	);
		g.drawString("posit: "+ position, 5, 125	);
		g.drawString("torqu: "+ torque, 5, 150);
		g.drawString("angAC: "+ angularAcceleration, 5, 175);
		g.drawString("angVL: "+ angularVelocity, 5, 200);
		g.drawString("orien: "+ orientation*180/Math.PI, 5, 225);
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
	
	public void pressRotateRight(){
		locMan.pressRotateRight();
	}
	
	public void pressRotateLeft(){
		locMan.pressRotateLeft();
	}
}