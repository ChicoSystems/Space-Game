package spacegame.tilegame.sprites;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Vector;

import spacegame.graphics.Animation;
import spacegame.graphics.Sprite;

public class Ship{
	
	public class Nose{
		Line2D.Double noseLine1;
		Line2D.Double noseLine2;
		Ellipse2D.Double saucer;
		Ship parent;
		double xorigin;
		double yorigin;
		double engine1X;
		double engine2X;
		double engineTopY;
		double noseLength;
		double noseRadius;
		public double noseX;
		public double noseY;
		
		public Nose(Ship parent){
			this.parent = parent;
			xorigin = parent.getX();
			yorigin = parent.getY();
			engine1X = parent.engine1.engine.get(0).x;
			engine2X = parent.engine2.engine.get(0).x;
			engineTopY = parent.engine1.engine.get(0).y;
			
			int lengthPoints = parent.speed;
			int radiusPoints = (parent.speed/4)+parent.power;
			if(lengthPoints == 0){
				noseLength = 1; //(lengthPoints - 1) / (1000 - 1) * (6-1) + 1;
			}else{
				noseLength = parent.map(lengthPoints, 1, 1000, 2, 6);
			}
			
			if(radiusPoints == 0){
				noseRadius = 1;//(lengthPoints - 1) / (1000 - 1) * (6-1) + 1;
			}else{
				noseRadius = parent.map(lengthPoints, 1, 1000, 2, 12);
			}
			noseLength *= Ship.PIXEL_PER_UNIT;
			noseRadius *= Ship.PIXEL_PER_UNIT;
			
			noseX = xorigin;
			noseY = engineTopY - noseLength;
			noseLine1 = new Line2D.Double(engine1X, engineTopY, noseX, noseY);
			noseLine2 = new Line2D.Double(engine2X, engineTopY, noseX, noseY);
			saucer = new Ellipse2D.Double(noseX-(noseRadius/2), noseY-(noseRadius/2), noseRadius, noseRadius);
		}
		
		public void update(){
			xorigin = parent.getX();
			yorigin = parent.getY();
			engine1X = parent.engine1.engine.get(1).x;
			engine2X = parent.engine2.engine.get(1).x;
			engineTopY = parent.engine1.engine.get(0).y;
			int lengthPoints = parent.speed;
			int radiusPoints = (parent.speed/4)+parent.power;
			if(lengthPoints == 0){
				noseLength = 2; //(lengthPoints - 1) / (1000 - 1) * (6-1) + 1;
			}else{
				noseLength = parent.map(lengthPoints, 1, 1000, 2, 6);
			}
			
			if(radiusPoints == 0){
				noseRadius = 4;//(lengthPoints - 1) / (1000 - 1) * (6-1) + 1;
			}else{
				noseRadius = parent.map(lengthPoints, 1, 1000, 2, 12);
			}
			noseLength *= Ship.PIXEL_PER_UNIT;
			noseRadius *= Ship.PIXEL_PER_UNIT;
			noseX = xorigin;
			noseY = engineTopY - noseLength;
			noseLine1.setLine(engine1X, engineTopY, noseX, noseY);
			noseLine2.setLine(engine2X, engineTopY, noseX, noseY);
			saucer.x = noseX-noseRadius/2;
			saucer.y = noseY-noseRadius/2;
			saucer.width = noseRadius;
			saucer.height = noseRadius;
		}
	}
	
	private class Engine{
		int engineNum;
		Ship parent;
		double xorigin;
		double yorigin;
		
		Point2D.Double eAtt;
		double engineHeight;
		double engineWidth;
		ArrayList<Point2D.Double>engine;
		
		public Engine(Ship parent, int engineNum){
			this.parent = parent;
			this.engineNum = engineNum;
			xorigin = parent.getX();
			yorigin = parent.getY();
			int totalPoints = parent.speed+(parent.power/4);
			if(totalPoints == 0){
				engineHeight = 2;//(totalPoints - 1) / (1250 - 1) * (24-1) + 1;
				engineWidth = 1;//(totalPoints - 1) / (1250 - 1) * (6-1) + 1;
			}else{
				engineHeight = parent.map(totalPoints, 1, 1250, 2, 24);
				engineWidth = parent.map(totalPoints, 1, 1250, 2, 6);
			}
			engineHeight *= Ship.PIXEL_PER_UNIT;
			engineWidth *= Ship.PIXEL_PER_UNIT;
			
			
			if(engineNum == 0){
				eAtt=parent.body.e1Att;
			}else{
				eAtt=parent.body.e2Att;
			}
			
			engine = new ArrayList<Point2D.Double>();
			engine.add(new Point2D.Double(0, 0));
			engine.add(new Point2D.Double(0, 0));
			engine.add(new Point2D.Double(0, 0));
			engine.add(new Point2D.Double(0, 0));	
		}
		
		public void update(){
			int totalPoints = parent.speed+(parent.power/4);
			if(totalPoints == 0){
				engineHeight = 2;//(totalPoints - 1) / (1250 - 1) * (24-1) + 1;
				engineWidth = 1;//(totalPoints - 1) / (1250 - 1) * (6-1) + 1;
			}else{
				engineHeight = parent.map(totalPoints, 1, 1250, 2, 24);
				engineWidth = parent.map(totalPoints, 1, 1250, 2, 6);
			}
			engineHeight *= Ship.PIXEL_PER_UNIT;
			engineWidth *= Ship.PIXEL_PER_UNIT;
			
			if(engineNum == 0){
				eAtt=parent.body.e1Att;
				engine.set(0, new Point2D.Double(eAtt.x-engineWidth, eAtt.y-engineHeight/2));
				engine.set(1, new Point2D.Double(eAtt.x, eAtt.y-engineHeight/2));
				engine.set(2, new Point2D.Double(eAtt.x, eAtt.y+engineHeight/2));
				engine.set(3, new Point2D.Double(eAtt.x-engineWidth, eAtt.y+engineHeight/2));
			}else{
				eAtt=parent.body.e2Att;
				engine.set(0, new Point2D.Double(eAtt.x+engineWidth, eAtt.y-engineHeight/2));
				engine.set(1, new Point2D.Double(eAtt.x, eAtt.y-engineHeight/2));
				engine.set(2, new Point2D.Double(eAtt.x, eAtt.y+engineHeight/2));
				engine.set(3, new Point2D.Double(eAtt.x+engineWidth, eAtt.y+engineHeight/2));
			}
		}
	}
	
	private class ShipBody{
		Ship parent;
		double radius;
		double xorigin;
		double yorigin;
		ArrayList<Point2D.Double> body;
		Point2D.Double e1Att;
		Point2D.Double e2Att;
		
		public ShipBody(Ship parent){
			this.parent = parent;
			xorigin = parent.getX();
			yorigin = parent.getY();
			body = new ArrayList<Point2D.Double>();
			int totalPoints = parent.speed + parent.power;
			if(totalPoints == 0){
				radius = .5;
			}else{
				radius = parent.map(totalPoints, 1, 2000, 2, 6);
				//radius = (totalPoints - 1) / (2000 - 1) * (6-1) + 1;
			}
			radius = radius * Ship.PIXEL_PER_UNIT;
			e1Att = new Point2D.Double(xorigin-radius, yorigin+0);
			e2Att = new Point2D.Double(xorigin+radius, yorigin+0);
			
			body.add(new Point2D.Double(xorigin-radius, yorigin-radius));
			body.add(new Point2D.Double(xorigin+radius, yorigin-radius));
			body.add(new Point2D.Double(xorigin+radius, yorigin+radius));
			body.add(new Point2D.Double(xorigin-radius, yorigin+radius));
			//Y = (X-A)/(B-A) * (D-C) + C	
		}
		
		public void update(){
			int totalPoints = parent.speed + parent.power;
			if(totalPoints == 0){
				radius = .5;
			}else{
				radius = parent.map(totalPoints, 1, 2000, 2, 6);
			}
			radius = radius * Ship.PIXEL_PER_UNIT;
			xorigin = parent.getX();
			yorigin = parent.getY();
			e1Att = new Point2D.Double(xorigin-radius, yorigin+0);
			e2Att = new Point2D.Double(xorigin+radius, yorigin+0);
			body.set(0, new Point2D.Double(xorigin-radius, yorigin-radius));
			body.set(1, new Point2D.Double(xorigin+radius, yorigin-radius));
			body.set(2, new Point2D.Double(xorigin+radius, yorigin+radius));
			body.set(3, new Point2D.Double(xorigin-radius, yorigin+radius));
		}
	}
	protected float SPEED_ROTATION = .25f;
	public static final int PIXEL_PER_UNIT = 5;
    private static final int DIE_TIME = 1000;
    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    
    public static final int STATE_DEAD = 2;
    protected float maxSpeed = 0;
    protected float currentSpeed = 0;
    protected float boostSpeed = 0;
    public double id;
    private int state;
    
	private long stateTime;
    // position (pixels)
    private float x;
    private float y;
    // velocity (pixels per millisecond)
    protected float dx;
    
    protected float dy;
    protected float currentRotation = 0;
    protected float futureRotation = 0;
    
	protected float rotationSpeed = SPEED_ROTATION;
	int power;
	int speed;

	ShipBody body;
	Engine engine1;
	Engine engine2;
	public Nose nose;
	
	public Ship() {
		power = 120;
		speed = 444;
		id = Math.random();
        state = STATE_NORMAL;
		body = new ShipBody(this);
		engine1 = new Engine(this, 0);
		engine2 = new Engine(this, 1);
		nose = new Nose(this);
		this.setMaxSpeed(.2f);
    	this.setBoostSpeed(this.getMaxSpeed()*2);
    	this.setCurrentSpeed(this.getMaxSpeed());
	}
	
	private float calculateDifferenceBetweenAngles(float firstAngle, float secondAngle){
		   	float difference = secondAngle - firstAngle;
	        while (difference < -180) difference += 360;
	        while (difference > 180) difference -= 360;
	        return difference;
	   }
	
    public void collide(){
    	collideHorizontal();
    	collideVertical();
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        setVelocityY(0);
    }
	
	 /**	
		Clones this Sprite. Does not clone position or velocity
		info.
	*/
	public Object clone() {
		return new Ship();
	}
	
	public void drawShip(Graphics2D g, int offsetX, int offsetY){
		drawBody(g, offsetX, offsetY);
		drawEngines(g, offsetX, offsetY);
		drawNose(g, offsetX, offsetY);	
	}
	
	private void drawEngines(Graphics2D g, int offsetX, int offsetY){
		Point2D.Double p1 = engine1.engine.get(0);
		Point2D.Double p2 = engine1.engine.get(1);
		Point2D.Double p3 = engine1.engine.get(2);
		Point2D.Double p4 = engine1.engine.get(3);
		g.drawLine((int)p1.x+offsetX, (int)p1.y+offsetY, (int)p2.x+offsetX, (int)p2.y+offsetY);
		g.drawLine((int)p2.x+offsetX, (int)p2.y+offsetY, (int)p3.x+offsetX, (int)p3.y+offsetY);
		g.drawLine((int)p3.x+offsetX, (int)p3.y+offsetY, (int)p4.x+offsetX, (int)p4.y+offsetY);
		g.drawLine((int)p4.x+offsetX, (int)p4.y+offsetY, (int)p1.x+offsetX, (int)p1.y+offsetY);
		
		Point2D.Double p5 = engine2.engine.get(0);
		Point2D.Double p6 = engine2.engine.get(1);
		Point2D.Double p7 = engine2.engine.get(2);
		Point2D.Double p8 = engine2.engine.get(3);
		g.drawLine((int)p5.x+offsetX, (int)p5.y+offsetY, (int)p6.x+offsetX, (int)p6.y+offsetY);
		g.drawLine((int)p6.x+offsetX, (int)p6.y+offsetY, (int)p7.x+offsetX, (int)p7.y+offsetY);
		g.drawLine((int)p7.x+offsetX, (int)p7.y+offsetY, (int)p8.x+offsetX, (int)p8.y+offsetY);
		g.drawLine((int)p8.x+offsetX, (int)p8.y+offsetY, (int)p5.x+offsetX, (int)p5.y+offsetY);
		//System.out.println("current loc: " + this.getX() + " " + this.getY());
		//System.out.println("polygon loc: " + p1.x + " "	+ p1.y);
	}
	
	private void drawBody(Graphics2D g, int offsetX, int offsetY){
		Point2D.Double p1 = body.body.get(0);
		Point2D.Double p2 = body.body.get(1);
		Point2D.Double p3 = body.body.get(2);
		Point2D.Double p4 = body.body.get(3);
		g.drawLine((int)p1.x+offsetX, (int)p1.y+offsetY, (int)p2.x+offsetX, (int)p2.y+offsetY);
		g.drawLine((int)p2.x+offsetX, (int)p2.y+offsetY, (int)p3.x+offsetX, (int)p3.y+offsetY);
		g.drawLine((int)p3.x+offsetX, (int)p3.y+offsetY, (int)p4.x+offsetX, (int)p4.y+offsetY);
		g.drawLine((int)p4.x+offsetX, (int)p4.y+offsetY, (int)p1.x+offsetX, (int)p1.y+offsetY);
	}
	
	private void drawNose(Graphics2D g, int offsetX, int offsetY){
		Line2D noseLine1 = nose.noseLine1;
		Line2D noseLine2 = nose.noseLine2;
		Ellipse2D saucer = nose.saucer;
		g.drawLine((int)noseLine1.getX1()+offsetX, (int)noseLine1.getY1()+offsetY, (int)noseLine1.getX2()+offsetX, (int)noseLine1.getY2()+offsetY);
		g.drawLine((int)noseLine2.getX1()+offsetX, (int)noseLine2.getY1()+offsetY, (int)noseLine2.getX2()+offsetX, (int)noseLine2.getY2()+offsetY);
		g.fillArc((int)saucer.getX()+offsetX, (int)saucer.getY()+offsetY, (int)saucer.getWidth(), (int)saucer.getHeight(), 0, 360);
	}

	/**
	Gets the maximum speed of this Creature.
	*/
	public float getBoostSpeed() {
	   return boostSpeed;
	}
	
	/**
	Gets the maximum speed of this Creature.
	*/
	public float getCurrentSpeed() {
	   return currentSpeed;
	}

	public float getFutureRotation() {
		return futureRotation;
	}

	/**
	    Gets this Sprite's height, based on the size of the
	    current image.
	*/
	public float getHeight() {
	    return (float) (body.radius*2);
	}

	/**
		   Gets the maximum speed of this Creature.
		*/
		public float getMaxSpeed() {
		   return maxSpeed;
		}

	public float getRotation() {
		if(currentRotation < 0){
			currentRotation = 360 - currentRotation;
		}
		
		currentRotation %= 360;
		return currentRotation;
	}

	public double getRotationSpeed() {
		return rotationSpeed;
	}

	/**
	    Gets the state of this Creature. The state is either
	    STATE_NORMAL, STATE_DYING, or STATE_DEAD.
	*/
	public int getState() {
	    return state;
	}

	/**
	    Gets the horizontal velocity of this Sprite in pixels
	    per millisecond.
	*/
	public float getVelocityX() {
	    return dx;
	}
	
	/**
	    Gets the vertical velocity of this Sprite in pixels
	    per millisecond.
	*/
	public float getVelocityY() {
	    return dy;
	}

	/**
	    Gets this Sprite's width, based on the size of the
	    current image.
	*/
	public float getWidth() {
		return (float) (body.radius*2);
	}

	/**
	    Gets this Sprite's current x position.
	*/
	public float getX() {
	    return x;
	}

	/**
    	Gets this Sprite's current y position.
	*/
	public float getY() {
	    return y;
	}

	/**
	    Checks if this creature is alive.
	*/
	public boolean isAlive() {
	    return (state == STATE_NORMAL);
	}
	
	public long map(long x, long in_min, long in_max, long out_min, long out_max)
	{
	  return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	/**
	Gets the maximum speed of this Creature.
	*/
	public void setBoostSpeed(Float speed) {
	   boostSpeed = speed;
	}
	
	/**
	Gets the maximum speed of this Creature.
	*/
	public void setCurrentSpeed(Float speed) {
	   currentSpeed = speed;
	}
   
   public void setFutureRotation(float toRotation) {
		if(toRotation < 0){
			toRotation = 360 + toRotation;
		}
		//System.out.println("Set to Rotation: " + toRotation);
		this.futureRotation = toRotation;
   }
   
   /**
	Gets the maximum speed of this Creature.
	*/
	public void setMaxSpeed(Float speed) {
	   maxSpeed = speed;
	}
	
	public void setRotation(float rotation) {
		if(rotation < 0){
			rotation = 360 + rotation;
		}
		//System.out.println("Set Rot: " + rotation);
		this.currentRotation = rotation;
	}
	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}
	
	/**
	    Sets the state of this Creature to STATE_NORMAL,
	    STATE_DYING, or STATE_DEAD.
	*/
	public void setState(int state) {
	    if (this.state != state) {
	        this.state = state;
	        stateTime = 0;
	        if (state == STATE_DYING) {
	            setVelocityX(0);
	            setVelocityY(0);
	        }
	    }
	}
	
	/**
	    Sets the horizontal velocity of this Sprite in pixels
	    per millisecond.
	*/
	public void setVelocityX(float dx) {
	    this.dx = dx;
	}
	
	/**
	    Sets the vertical velocity of this Sprite in pixels
	    per millisecond.
	*/
	public void setVelocityY(float dy) {
	    this.dy = dy;
	}
	
	/**
	    Sets this Sprite's current x position.
	*/
	public void setX(float x) {
	    this.x = x;
	}
	
	/**
	    Sets this Sprite's current y position.
	*/
	public void setY(float y) {
	    this.y = y;
	}
	
	/**
	    Updates this Sprite's Animation and its position based
	    on the velocity.
	*/
	public void update(long elapsedTime) {
	    x += dx * elapsedTime;
	    y += dy * elapsedTime;
	    //anim.update(elapsedTime);
	    
	 // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
        
        body.update();
        engine1.update();
        engine2.update();
        nose.update();
	}
	
	public void updateRotation(long elapsedTime){
   	 float rotation = (float) Math.atan2(this.dy, this.dx);
        rotation = (float) Math.toDegrees(rotation);
        rotation = rotation + 90;
        
        if((this.dx == 0) && (this.dy == 0)){
        	rotation = this.getRotation(); // keeps from reseting rotation when velocity is 0
        }
        	this.setFutureRotation(rotation);
		
		//System.out.println("Current: " + currentRotation + " Future: " + futureRotation);
		if(Math.abs(getRotation() - getFutureRotation()) < 3){//don't rotate if change is less then 2 degrees
			rotationSpeed = 0;
			return;
		}else{
			float rotationChange = calculateDifferenceBetweenAngles(getRotation(), getFutureRotation());
			//System.out.println("Rot Change: " + getFutureRotation() + " : " + getRotation() + " : " +rotationChange + " : " + rotationSpeed);
			if(rotationChange <= 0){
				setRotationSpeed(-SPEED_ROTATION);
			}else{
				setRotationSpeed(SPEED_ROTATION);
			}
			//System.out.println("Set Rot: " + ((float)(getRotation() + getRotationSpeed() * elapsedTime)));
			setRotation((float)(getRotation() + getRotationSpeed() * elapsedTime));
		}	
	}
}
