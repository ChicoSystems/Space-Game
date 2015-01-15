package spacegame.graphics;
import java.awt.Graphics2D;

import spacegame.tilegame.ResourceManager;
import spacegame.util.Vector2D;

public abstract class SpriteV2 {
	protected ResourceManager parent;
	protected Vector2D currentForce;
	protected Vector2D currentAcceleration;
	protected Vector2D velocity;
    protected Vector2D position;
    protected Vector2D heading;
    protected Vector2D side;
    protected double mass;
    protected double maxForce;
    protected double maxSpeed;
    protected double maxTurnRate;
    
    /** Creates a new Sprite object with the specified Animation. */
	public SpriteV2(ResourceManager parent) {
		 this.parent = parent;
		 mass = 2;
		 maxForce = .01;
		 maxSpeed = 5;
		 maxTurnRate = 1;
		 currentForce = new Vector2D(0, 0);
		 currentAcceleration = new Vector2D(0, 0);
	     velocity = new Vector2D(0, 0);
	     position = new Vector2D(0, 0);
	     heading = new Vector2D(1, 0);
	     side = new Vector2D(0,1);
	}
	
	public ResourceManager getParent() {
		return parent;
	}

	public void setParent(ResourceManager parent) {
		this.parent = parent;
	}

	public Vector2D getCurrentForce() {
		return currentForce;
	}

	public void setCurrentForce(Vector2D currentForce) {
		this.currentForce = currentForce;
	}

	public Vector2D getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2D velocity) {
		this.velocity = velocity;
	}

	public Vector2D getPosition() {
		return position;
	}

	public void setPosition(Vector2D position) {
		this.position = position;
	}

	public Vector2D getHeading() {
		return heading;
	}

	public void setHeading(Vector2D heading) {
		this.heading = heading;
	}

	public Vector2D getSide() {
		return side;
	}

	public void setSide(Vector2D side) {
		this.side = side;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getMaxTurnRate() {
		return maxTurnRate;
	}

	public void setMaxTurnRate(double maxTurnRate) {
		this.maxTurnRate = maxTurnRate;
	}
	
	/** Clones this Sprite. Does not clone position or velocity info. */
	public abstract Object clone(ResourceManager p, int type);

	public void update(double elapsedTime){
		updateLocation(elapsedTime);
		//drawSprite(parent.parent.screen.getGraphics());
	}
	
	protected abstract void updateLocation(double elapsedTime);
	public abstract void drawSprite(Graphics2D g, int offsetX, int offsetY);
}