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

/**
 * The Ship is the main player vehicle. It automatically grows and changes size 
 * based on hitpoints, power and speed.
 * @author admin Isaac Assegai
 *
 */
public class Ship extends Creature  {

	public static final int LEVEL_MIN = 1;
	public static final int LEVEL_MAX = 1000;
	/**
	 * The Minimum amount of hitpoints a ship can have.
	 */
	public static final int HITPOINT_MIN = 1;
	/**
	 * The Maximum amount of hitpoints a ship can have.
	 */
	public static final int HITPOINT_MAX = 100000;
	/**
	 * The initial number of hitpoints a new ship starts with.
	 */
	public static final int HITPOINT_INIT = HITPOINT_MIN;
	/**
	 * The Minimum amount of power a ship can have.
	 */
	public static final int POWER_MIN = 1;
	/**
	 * The Maximum amount of power a ship can have.
	 */
	public static final int POWER_MAX = 1000;
	/**
	 * The initial amount of power a new ship is started with.
	 */
	public static final int POWER_INIT = POWER_MIN;
	/**
	 * The Minimum amount of speed a ship can have.
	 */
	public static final int SPEED_MIN = 1;
	/**
	 * The Maximum amount of speed a ship can have.
	 */
	public static final int SPEED_MAX = 1000;
	/**
	 * The initial amount of speed that a new ship starts with.
	 */
	public static final int SPEED_INIT = SPEED_MIN;
	/**
	 * The number of pixels each ship unit is worth.
	 */
	public static final int PIXEL_PER_UNIT = 5;
	
	/**
	 * The ships level, decides the upgrade points of totalPower;
	 */
	private int level;
    
    

	/**
     * The Current power of the ship. This is passed down to the ships weapons
     * to decide exactly how much damage/mining a ship can do. Size of the ship
     * also depends on power.
     */
	private int power;
	/**
	 * The Current speed points that the ship has. The ships maximum velocity increases with larger
	 * number of speed points. Size of the ship also changes depending on speed.
	 */
	private int speed;
	/**
	 * The current number of hitpoints that this ship has.
	 */
	private double hitpoints;
	/**
	 * The animation array sprites use. The Ships animation array will me animations
	 * made of blank pictures, as ships are sprites with no animations.
	 */
	private Animation[] animArray;
	
	/**
	 * The upper limit of the totalPower field. Based on level.
	 */
	private int totalPowerLimit;
	/**
	 * The pool of total power the ship has saved up, movement, weapons, and hp will
	 * be regenerated from this pool.
	 */
	private double totalPower;
	/**
	 * The body of the ship.
	 */
	private ShipBody body;
	/**
	 * The left engine of the ship.
	 */
	private Engine engine1;
	/**
	 * The right engine of the ship.
	 */
	private Engine engine2;
	/**
	 * The Nose of the ship.
	 */
	private Nose nose;
	/**
	 * The parent of the ship. The games resource manager. Gives the ship access
	 * to the rest of the game.
	 */
	private ResourceManager parent;
	
	/**
	 * Construct a new ship.
	 * @param parent - The Parent of the ship. The games resource Manager. Gives
	 * the ship access to the rest of the game.
	 * @param animArray - An array of blank animations.
	 */
	public Ship(ResourceManager parent, Animation[] animArray) {
		super(animArray);
		this.animArray = animArray;
		this.parent = parent;
		level = 1;
		totalPowerLimit = level*3;
		totalPower = 3;
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
	
	 /**	
		In this case this creates a new ship with the same parent and animArray.
	 */
	public Object clone() {
		return new Ship(parent, animArray);
	}
	
	/**
	 * Draws the ship to the graphics context
	 * @param g - The graphics context the ship is drawn to.
	 * @param offsetX - The offset of the clients main player to the game world. X Axis.
	 * @param offsetY - The offset of the clients main player to the game world. Y Axis.
	 */
	public void drawShip(Graphics2D g, int offsetX, int offsetY){
		// Save the current transform of the graphics contexts.
	    AffineTransform saveTransform = g.getTransform();
	    // Create a identity affine transform, and apply to the Graphics2D context
	    AffineTransform identity = new AffineTransform();
	    g.setTransform(identity);
	    
	    //rotate ship at the middle of it's saucer
		g.rotate((Math.toRadians(this.getRotation())), this.x+offsetX, this.y+offsetY-engine1.engineHeight/2-nose.noseLength);
		drawBody(g, offsetX, offsetY);
		drawEngines(g, offsetX, offsetY);
		drawNose(g, offsetX, offsetY);
		int sx = Math.round(getX()) + offsetX;
    	int sy = Math.round(getY()) + offsetY;
		g.setTransform(saveTransform); //unrotate
		
		//draw ships hp above ship.
		Color saveColor = g.getColor();
        g.setColor(Color.red);
        DecimalFormat df = new DecimalFormat("#");
        String hp = df.format(this.hitpoints);
    	g.drawString(hp, sx, (float) (sy-this.getHeight()/2));
    	g.setColor(saveColor);
	}
	
	/**
	 * Draws the ships engines to the Graphics Context.
	 * @param g - The Graphics Context to draw the engine to.
	 * @param offsetX - The X offset of the main ship in the world.
	 * @param offsetY - The Y offset of the main ship in the world.
	 */
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
	}
	
	/**
	 * Draws the Body of the Ship to the Graphics Context.
	 * @param g - The Graphics Context we are drawing the ship body to.
	 * @param offsetX - The offset of the main ship in the x axis.
	 * @param offsetY - The offset of the main ship in the y axis.
	 */
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
	
	/**
	 * Draws the nose of the ship to the Graphics Context.
	 * @param g - The Graphics Context we are drawing the ship nose to.
	 * @param offsetX - The x offset of the main player in the game world.
	 * @param offsetY - The y offset of the main player in the game world.
	 */
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
	 * Returns the current number of hitpoints the ship has.
	 * @return - The Hitpoints
	 */
	public double getHitpoints() {
		return hitpoints;
	}

	/**
	 * Sets the current number of hitpoints for this ship.
	 * @param hitpoints - Hitpoints to set.
	 */
	public void setHitpoints(double hitpoints) {
		this.hitpoints = hitpoints;
	}

	/**
	    Gets this Sprite's width, based on the size of the
	    current image.
	*/
	public float getWidth() {
		return (float) ((body.width*2)+ (engine1.engineWidth*2));
	}
	
	/**
	 * Returns the current power of the ship.
	 * @return - Current power of the ship.
	 */
	public int getPower() {
		return power;
	}

	/**
	 * Sets the current power of the ship.
	 * @param power - The power to set.
	 */
	public void setPower(int power) {
		this.power = power;
	}

	/**
	 * Gets the current number of speed points the ship is operating with.
	 * This is not the actual velocity of the ship.
	 * @return
	 */
	public int getSpeed() {
		return speed;
	}

	/**
	 * Sets the current number of speed points fot the ship.
	 * This is not the velocity of the ship. But the points that decide the velocity.
	 * @param speed - The speed points to set.
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	/**
	 * Returns the Ships current level
	 * @return - The current Level of the ship
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * Sets the current level of the ship.
	 * @param level - The level to set.
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Returns the total power limit of the ship
	 * @return - The total power limit of the ship.
	 */
	public int getTotalPowerLimit() {
		return totalPowerLimit;
	}

	/**
	 * Sets the total power limit of the ship.
	 * @param totalPowerLimit - The power limit to set.
	 */
	public void setTotalPowerLimit(int totalPowerLimit) {
		this.totalPowerLimit = totalPowerLimit;
	}

	/**
	 * Returns the current total power. The pool of power the ship is powered by.
	 * @return - The current total power of the ship.
	 */
	public double getTotalPower() {
		return totalPower;
	}

	/**
	 * Sets the pool of total power.
	 * @param totalPower - The pool to set.
	 */
	public void setTotalPower(double totalPower) {
		this.totalPower = totalPower;
	}

	/**
	 * Maps points from the d-e range to the f-g range.
	 * @param points - The number we are mapping.
	 * @param d - The start of the initial range.
	 * @param e - The end of the initial range.
	 * @param f - The start of the return range.
	 * @param g - The end of the return range.
	 * @return - The points value mapped to the f-g range.
	 */
	public static double map(double points, double d, double e, double f, double g){
	  return (points - d) * (g - f) / (e - d) + f;
	}
	
	/**
	 * Returns the current animation array, should be an array of empty animations.
	 * @return - The animArray
	 */
	public Animation[] getAnimArray() {
		return animArray;
	}

	/**
	 * Sets the anim array for this ship. Should be an array of blank animations.
	 * @param animArray
	 */
	public void setAnimArray(Animation[] animArray) {
		this.animArray = animArray;
	}

	/**
	 * Returns the body of the ship
	 * @return - The ship body.
	 */
	public ShipBody getBody() {
		return body;
	}

	/**
	 * Sets the body of the ship
	 * @param body - The body to set.
	 */
	public void setBody(ShipBody body) {
		this.body = body;
	}

	/**
	 * Returns the left engine of the ship.
	 * @return - The left engine of the ship.
	 */
	public Engine getEngine1() {
		return engine1;
	}

	/**
	 * Sets the left engine of the ship.
	 * @param engine1 - The engine to set.
	 */
	public void setEngine1(Engine engine1) {
		this.engine1 = engine1;
	}

	/**
	 * Returns the right engine of the ship.
	 * @return - The right engine.
	 */
	public Engine getEngine2() {
		return engine2;
	}

	/**
	 * Sets the right engine of the ship.
	 * @param engine2
	 */
	public void setEngine2(Engine engine2) {
		this.engine2 = engine2;
	}

	/**
	 * Returns the nose of the ship.
	 * @return - The ship nose.
	 */
	public Nose getNose() {
		return nose;
	}

	/**
	 * Sets the nose of the ship.
	 * @param nose - The ship to set.
	 */
	public void setNose(Nose nose) {
		this.nose = nose;
	}

	/**
	 * Returns the parent of the ship. Should be the games main resourcemanager.
	 * @return - The resource manager of the game.
	 */
	public ResourceManager getParent() {
		return parent;
	}

	/**
	 * Sets the parent of the Ship. The games resourceManager
	 * @param parent - The resource manager to keep track of.
	 */
	public void setParent(ResourceManager parent) {
		this.parent = parent;
	}
	
	/**
	    Updates the ships state, body, engines, nose and max speed.
	*/
	public void update(long elapsedTime) {
		//make sure totalpower pool is under the power limit.
		totalPowerLimit = level*3;
		if(totalPower >= totalPowerLimit){
			totalPower = totalPowerLimit;
		}else if(totalPower <= 2){
			totalPower = 3;
		}
		
		if(state == STATE_DEAD){
			parent.parent.getMap().setPlayer2(null);
		}
	    x += dx * elapsedTime;
	    y += dy * elapsedTime;
	    
	    //check hp, if less than or = to 0 we will set state
        state = getStateFromHP(hitpoints);
	    
	    // update to "dead" state if dying.
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
           state = STATE_DEAD; //setState(STATE_DEAD);
        }
        
        body.update();
        engine1.update();
        engine2.update();
        nose.update();
        
        //update max speed based on speed points.
        float maxSpeed = (float) map(speed, 1, 1000, .05, .6);
        setMaxSpeed(maxSpeed);
	}
	
	private int getStateFromHP(double hp){
		int state = 0;
		if(hp <= 0){
			state=STATE_DYING;
		}else{
			state=STATE_NORMAL;
		}
		return state;
	}
	
	/**
	 * The Nose of the Ship.
	 * @author Isaac Assegai
	 *
	 */
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
