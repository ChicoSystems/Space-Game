package spacegame.tilegame.sprites;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import spacegame.graphics.Animation;
import spacegame.graphics.Sprite;
import spacegame.tilegame.ResourceManager;

public class Ship extends Creature  {
	public static final int HITPOINT_MIN = 1;
	public static final int HITPOINT_MAX = 100000;
	public static final int HITPOINT_INIT = HITPOINT_MIN;
	
	public static final int POWER_MIN = 1;
	public static final int POWER_MAX = 1000;
	public static final int POWER_INIT = POWER_MIN;
	
	public static final int SPEED_MIN = 1;
	public static final int SPEED_MAX = 1000;
	public static final int SPEED_INIT = SPEED_MIN;
	
	public static final int PIXEL_PER_UNIT = 5;
    // velocity (pixels per millisecond)
    
    
	public int power;
	public int speed;
	private double hitpoints;
	
	public double getHitpoints() {
		return hitpoints;
	}

	public void setHitpoints(double hitpoints) {
		this.hitpoints = hitpoints;
	}

	private Animation[] animArray;
	
	//the power pool drawn from to run the ship
	public double totalPower;

	public ShipBody body;
	Engine engine1;
	Engine engine2;
	public Nose nose;
	public ResourceManager parent;
	
	public Ship(ResourceManager parent, Animation[] animArray) {
		super(animArray);
		this.animArray = animArray;
		this.parent = parent;
		totalPower = 1;
		power = 1;
		speed = 1;
		hitpoints = HITPOINT_INIT;
		id = Math.random();
		body = new ShipBody(this);
		engine1 = new Engine(this, 0);
		engine2 = new Engine(this, 1);
		nose = new Nose(this);
		this.setMaxSpeed(.4f);
    	this.setBoostSpeed(this.getMaxSpeed()*2);
    	this.setCurrentSpeed(this.getMaxSpeed());
	}
	
	private float calculateDifferenceBetweenAngles(float firstAngle, float secondAngle){
		   	float difference = secondAngle - firstAngle;
	        while (difference < -180) difference += 360;
	        while (difference > 180) difference -= 360;
	        return difference;
	   }
	
	 /**	
		Clones this Sprite. Does not clone position or velocity
		info.
	*/
	public Object clone() {
		return new Ship(parent, animArray);
	}
	
	public void drawShip(Graphics2D g, int offsetX, int offsetY){
		 // Save the current transform of the graphics contexts.
	      AffineTransform saveTransform = g.getTransform();
	      // Create a identity affine transform, and apply to the Graphics2D context
	      AffineTransform identity = new AffineTransform();
	      g.setTransform(identity);
	      
	    //rotate ship at the middle of it's saucer
		g.rotate((Math.toRadians(this.getRotation())), this.x+offsetX, this.y+offsetY-engine1.engineHeight/2-nose.noseLength);
		//g.rotate((Math.toRadians(this.getRotation())));
		drawBody(g, offsetX, offsetY);
		drawEngines(g, offsetX, offsetY);
		drawNose(g, offsetX, offsetY);
		int sx = Math.round(getX()) + offsetX;
    	int sy = Math.round(getY()) + offsetY;
		DecimalFormat df = new DecimalFormat("#");
        String hp = df.format(this.hitpoints);
        
		//g.fillArc((int)this.x+offsetX, (int)this.y+offsetY, 5, 5, 0, 360);
		g.setTransform(saveTransform);
		Color saveColor = g.getColor();
        g.setColor(Color.red);
    	g.drawString(hp, sx, (float) (sy-this.getHeight()/2));
    	 g.setColor(saveColor);
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
	    Gets this Sprite's height, based on the size of the
	    current image.
	*/
	public float getHeight() {
	    return (float) (engine1.engineHeight+nose.noseLength+nose.noseRadius/2);
	}

	/**
		Gets the maximum speed of this Creature.
	 */
	public float getMaxSpeed() {
	   return maxSpeed;
	}

	/**
	    Gets this Sprite's width, based on the size of the
	    current image.
	*/
	public float getWidth() {
		return (float) ((body.width*2)+ (engine1.engineWidth*2));
	}

	
	public static double map(double heightPoints, double d, double e, double f, double g)
	{
	  return (heightPoints - d) * (g - f) / (e - d) + f;
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
        
        //update max speed based on speed points.
        float maxSpeed = (float) map(speed, 1, 1000, .05, .6);
        setMaxSpeed(maxSpeed);
       // setCurrentSpeed(maxSpeed);
	}
	
	public class Nose{
		Line2D.Double noseLine1;
		Line2D.Double noseLine2;
		public Ellipse2D.Double saucer;
		Ship parent;
		double xorigin;
		double yorigin;
		double engine1X;
		double engine2X;
		double engineTopY;
		public double noseLength;
		public double noseRadius;
		public double noseX;
		public double noseY;
		
		public Nose(Ship parent){
			this.parent = parent;
			xorigin = parent.getX();
			yorigin = parent.getY();
			engine1X = parent.engine1.engine.get(0).x;
			engine2X = parent.engine2.engine.get(0).x;
			engineTopY = parent.engine1.engine.get(0).y;
			
			double lengthPoints = parent.speed;
			double radiusPoints = (parent.speed/4)+parent.power;
			if(lengthPoints <=1 ){
				noseLength = 1; //(lengthPoints - 1) / (1000 - 1) * (6-1) + 1;
			}else{
				noseLength = parent.map(lengthPoints, 1, 1000, 2, 6);
			}
			
			if(radiusPoints <=2){
				noseRadius = 1;//(lengthPoints - 1) / (1000 - 1) * (6-1) + 1;
			}else{
				noseRadius = parent.map(radiusPoints, 1, 1000, 2, 12);
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
			int lengthPoints = (int) (parent.speed+(parent.power/3)+parent.hitpoints/300);
			int radiusPoints = (int) ((parent.speed/4)+parent.power+parent.hitpoints/400);
			if(lengthPoints <=3){
				noseLength = 2; //(lengthPoints - 1) / (1000 - 1) * (6-1) + 1;
			}else{
				noseLength = parent.map(lengthPoints, 1, 1667, 2, 10);
			}
			
			if(radiusPoints <=3){
				noseRadius = 4;//(lengthPoints - 1) / (1000 - 1) * (6-1) + 1;
			}else{
				noseRadius = parent.map(radiusPoints, 1, 1500, 2, 14);
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
			if(totalPoints <=2){
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
			//int totalPoints = parent.speed+(parent.power/4);
			double heightPoints = (parent.speed*1.5+(parent.power/4)+parent.hitpoints/200);
			double widthPoints = parent.speed/8+(parent.power/2) + parent.hitpoints/200;
			
			if(heightPoints <= 3){
				engineHeight = 2;
			}else{
				engineHeight = parent.map(heightPoints, 1, 2175, 2, 24);
			}
			
			if(widthPoints <= 3){
				engineWidth = 1;//(totalPoints - 1) / (1250 - 1) * (6-1) + 1;
			}else{
				engineWidth = parent.map(widthPoints, 1, 1250, 2, 6);
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
	
	public class ShipBody{
		Ship parent;
		public double width;
		public double height;
		double xorigin;
		double yorigin;
		ArrayList<Point2D.Double> body;
		//Rectangle body;// = new Rectangle(100, 100, 100, 100);
		Point2D.Double e1Att;
		Point2D.Double e2Att;
		
		public ShipBody(Ship parent){
			this.parent = parent;
			xorigin = parent.getX();
			yorigin = parent.getY();
			body = new ArrayList<Point2D.Double>();
			
			int widthPoints = (int) (parent.speed/4 + parent.power + parent.hitpoints/100);
			int heightPoints = (int) (parent.speed/2 + parent.power/4 + parent.hitpoints/100);
			//int totalPoints = 
			if(widthPoints <= 3){
				width = .5;
			}else{
				width = parent.map(widthPoints, 1, 2250, 2, 6);
			}
			
			if(heightPoints <=3){
				height = .5;
			}else{
				height = parent.map(heightPoints, 1, 1750, 2, 6);
			}
			
			width = width * Ship.PIXEL_PER_UNIT;
			height = height * Ship.PIXEL_PER_UNIT;
			
			e1Att = new Point2D.Double(xorigin-width, yorigin+0);
			e2Att = new Point2D.Double(xorigin+width, yorigin+0);
			
			//body = new Rectangle((int)(xorigin-width), (int)(yorigin-height), (int)width*2, (int)height*2);
			body.add(new Point2D.Double(xorigin-width, yorigin-height));
			body.add(new Point2D.Double(xorigin+width, yorigin-height));
			body.add(new Point2D.Double(xorigin+width, yorigin+height));
			body.add(new Point2D.Double(xorigin-width, yorigin+height));
			//Y = (X-A)/(B-A) * (D-C) + C	
		}
		
		public void update(){
			int widthPoints = (int) (parent.speed/10 + parent.power/2 + parent.hitpoints/100);
			int heightPoints = (int) (parent.speed/2 + parent.power/5 + parent.hitpoints/100);
			//int totalPoints = 
			if(widthPoints <=3){
				width = .5;
			}else{
				width = parent.map(widthPoints, 1, 2100, 2, 6);
			}
			
			if(heightPoints <=3){
				height = .5;
			}else{
				height = parent.map(heightPoints, 1, 1700, 2, 6);
			}
			
			width = width * Ship.PIXEL_PER_UNIT;
			height = height * Ship.PIXEL_PER_UNIT;
			
			xorigin = parent.getX();
			yorigin = parent.getY();
			e1Att = new Point2D.Double(xorigin-width, yorigin+0);
			e2Att = new Point2D.Double(xorigin+width, yorigin+0);
			
			//body.
			body.set(0, new Point2D.Double(xorigin-width, yorigin-height));
			body.set(1, new Point2D.Double(xorigin+width, yorigin-height));
			body.set(2, new Point2D.Double(xorigin+width, yorigin+height));
			body.set(3, new Point2D.Double(xorigin-width, yorigin+height));
		}
	}
}
