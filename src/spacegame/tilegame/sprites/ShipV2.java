/**
 * 
 */
package spacegame.tilegame.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import spacegame.graphics.GamePolygon;
import spacegame.graphics.SpriteV2;
import spacegame.input.AILocManager;
import spacegame.input.LocationManager;
import spacegame.input.PlayerLocManager;
import spacegame.tilegame.ResourceManager;
import spacegame.util.Vector2D;

/**
 * @author Isaac Assegai
 * ship type 0 is player controlled, ship type 1 is ai controlled.
 */
public class ShipV2 extends SpriteV2 {
	GamePolygon body;
	Ellipse2D.Double oldBody;
	protected LocationManager locMan;
	int type; //0 if player controlled

	public LocationManager getLocMan() {
		return locMan;
	}

	public void setLocMan(LocationManager locMan) {
		this.locMan = locMan;
	}

	public ShipV2(ResourceManager parent, int type) {
		super(parent);
		this.type = type;
		oldBody = new Ellipse2D.Double();
		
		body = new GamePolygon(position);
		//body.
		//body.translate((int)position.x, (int)position.x);
		buildBody();
		if(type == 0){
			locMan = new PlayerLocManager(this);
		}else{
			locMan = new AILocManager(this); // we'll need to change this line to an ai manager
		}	
	}
	
	private void buildBody(){
		body.addPoint(position.x+0, position.y+10);
		body.addPoint(position.x-4.5, position.y-9.5);
		body.addPoint(position.x-0, position.y-5);
		body.addPoint(position.x+4.5, position.y-9.5);
	}

	protected void updateLocation(double elapsedTime) {
		//oldPosition = position;
		currentForce = locMan.calculate(elapsedTime); // Update the applied forces.
		currentForce = currentForce.plus(locMan.calculateGravity(elapsedTime));
		double forceReduction = 1; // makes orbits more graceful
		this.currentForce = currentForce.scalarMult(forceReduction*(elapsedTime/1000)); //scale forces by time
		//currentForce.truncate(maxForce); // Reduce force if it's over max.
		currentAcceleration = currentForce.scalarDiv(mass); // calculate acceleration
		currentAcceleration = currentAcceleration.scalarMult(elapsedTime); //Reduce acceleration over time passed
		//velocity = velocity.scalarMult(elapsedTime/1000); //Reduce acceleration over time passed
    	velocity = velocity.plus(currentAcceleration);//update velocity
    	//apply friction get the tanget from the normal (perp vector)
    	Vector2D tangent = velocity.unitVector().perp().perp();
		// compute the tangential velocity, scale by friction
    	double frictionConstant = .5;
    	double tv = velocity.dotProduct(tangent)*frictionConstant*(elapsedTime/1000);
		// subtract that from the main velocity
    	velocity = velocity.minus(tangent.scalarMult(tv));
    	if(velocity.length() < .001)velocity = new Vector2D(0,0); //stop velocity if it's to slow
    	//velocity.truncate(maxSpeed); //Limit velocity based on maxSpeed.
    	//update rotation
    	torque = locMan.calculateTorque(elapsedTime);	
    	angularAcceleration = torque / (mass/2); 
    	angularVelocity += angularAcceleration * (elapsedTime/1000);
    	angularVelocity = angularVelocity - angularVelocity * 1*(elapsedTime/1000); //angular friction
    	if(Math.abs(angularVelocity) < .005) angularVelocity = 0; // stop when low enough
    	orientation += 0.6 * angularVelocity * (elapsedTime/1000);
    	orientation = orientation % (Math.PI*2); // keep orientation under 360 degrees
    	heading.setPolar(1, orientation-Math.PI/2);
    	
       // position = position.plus(velocity); //Update position based on velocity.
        position.setLocation(position.plus(velocity));
        body.updatePosition(position);
        //body.translate((int)(position.x - oldPosition.x), (int)(position.y - oldPosition.y));
        //body.
	}

	public void drawSprite(Graphics2D g, int offsetX, int offsetY) {
		// Save the current transform of the graphics contexts.
	    AffineTransform saveTransform = g.getTransform();
	    // Create a identity affine transform, and apply to the Graphics2D context
	    AffineTransform identity = new AffineTransform();
	    g.setTransform(identity);
	    g.rotate(orientation, offsetX, offsetY);
	   
	    g.setTransform(saveTransform); //unrotate
	    
	   
	    oldBody = new Ellipse2D.Double(this.position.x, this.position.y, 25, 25);
	    double middleX = (int)oldBody.getX()+offsetX+(int)oldBody.getWidth()/2;
	    double middleY = (int)oldBody.getY()+offsetY+(int)oldBody.getHeight()/2;
	    Vector2D endLine = new Vector2D(middleX, middleY + 50);
	    g.rotate(orientation, middleX, middleY);
	    
	    //g.setColor(Color.blue);
	    //g.fillArc((int)oldBody.getX()+offsetX, (int)oldBody.getY()+offsetY, (int)oldBody.getWidth(), (int)oldBody.getHeight(), 0, 360);
	    //g.drawLine((int)oldBody.getX()+offsetX, (int)oldBody.getY()+offsetY, (int)oldBody.getX()+offsetX+25, (int)oldBody.getY()+offsetY);
	    //g.setColor(Color.RED);
	    //g.drawLine((int)middleX, (int)middleY, (int)endLine.x, (int)endLine.y);
	    //g.setColor(Color.white);
	    //g.fillArc((int)middleX, (int)middleY, 4, 4, 0, 360);
	    body.drawGamePolygon(g, offsetX, offsetY);
	    g.setTransform(saveTransform); //unrotate
	    drawTestGui(g);
	    
	}
	
	

	/** Clones this Sprite. Does not clone position or velocity info. */
	public Object clone(ResourceManager p, int type) {
	    return new ShipV2(p, type);
	}
	
	public void drawTestGui(Graphics2D g){
		if(this.type != 1){
			//g.drawString("input: "+ ((PlayerLocManager)locMan).inputVector, 5, 25);
			/*
			g.drawString("force: "+ currentForce, 5, 50	);
			g.drawString("accel: "+ currentAcceleration, 5, 75	);
			g.drawString("veloc: "+ velocity, 5, 100	);
			g.drawString("posit: "+ position, 5, 125	);
			g.drawString("torqu: "+ torque, 5, 150);
			g.drawString("angAC: "+ angularAcceleration, 5, 175);
			g.drawString("angVL: "+ angularVelocity, 5, 200);
			g.drawString("orien: "+ orientation*180/Math.PI, 5, 225);
			g.drawString("headi: "+ heading, 5, 250);*/
			//g.drawString("Poly position " + body.xpoints[0] + ":"+ body.ypoints[0], 5, 50	);
			
		}
		
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

	public void pressMoveForward() {
		locMan.pressMoveForward();
	}
	
	public void pressMoveBackward() {
		locMan.pressMoveBackward();
	}
	
	public double getWidth(){
		return body.getWidth();
	}
	
	public double getHeight(){
		return body.getHeight();
	}

	
}