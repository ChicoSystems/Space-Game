package spacegame.tilegame.sprites;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import spacegame.graphics.Animation;
import spacegame.graphics.Sprite;
import spacegame.tilegame.TileMap;

public class Turret extends Creature  {
	public static float LEVEL_TO_SIZE = 5;
	public static float TURRET_TO_LEVEL = 500;
	private double TURRET_REACH;
	private Ship parent;
	private Ellipse2D circle;
	private Rectangle2D rect;
	private double hp;
	private int level;
	public int power;
	TileMap map;
	Object target;
	double targetTime = 0;
	public Color bodyColor;
	
	public Turret(Ship p, float x, float y, int level, Animation[] anim) {
		super(anim);
		parent = p;
		map = parent.parent.parent.getMap();
		this.level = level;
		setHp(level * 100);
		power = level;
		TURRET_REACH = level * TURRET_TO_LEVEL;
		int width = (int)(getHp()/LEVEL_TO_SIZE);
		int height = (int)(getHp()/LEVEL_TO_SIZE);
		this.setX(x);
		this.setY(y);
		circle = new Ellipse2D.Double(this.getX()-width/2, (double)(this.getY()-height/2), (double)(width), (double)(height));
		target = aquireTarget();
		bodyColor = Color.white;
	}
	
	public void update(long elapsedTime){
		super.update(elapsedTime);
		if(this.getHp() <= 0)this.setState(Creature.STATE_DEAD);
		TURRET_REACH = level * TURRET_TO_LEVEL;
		double newWidth = getHp()/LEVEL_TO_SIZE;
		double newHeight = getHp()/LEVEL_TO_SIZE;
		circle.setFrame(new Rectangle((int)(this.getX()-newWidth/2), (int)(this.getY()-newHeight/2), (int)newWidth, (int)(newHeight)));
		if(targetTime == 0 || System.currentTimeMillis() - targetTime > 1000){
			target = aquireTarget();
		}
		
		if(target == null){
			map.removeLaser(this);
		}else if(map.laserExists(this)){
			//don't do anything laser will track target with code in manager.updateLaser()
		}else{
			Laser l = null;
			//a laser doesn't exist, add one.
			if(target instanceof Ship){
				bodyColor = Color.red;
				Ship s = (Ship)target;
				float xTarget = s.getX()-s.getWidth()/2;
				float yTarget = s.getY()-s.getHeight()/2;
				l = new Laser(this.getX(), this.getY(), 
						xTarget, yTarget, this);
			}else if(target instanceof Turret){
				//the target shouldn't ever be a turret. we may need to change targeting code.
				System.err.println("target is a turret");
			}else if(target instanceof Projectile){
				//we arent implementing this yet.
			}else{
				if(target instanceof Player){
					bodyColor = Color.red;
				}else{
					bodyColor = Color.green;
				}
				//target must be a planet
				Sprite p = (Sprite)target;
				l = new Laser(this.getX(), this.getY(),
						p.getX()+p.getWidth()/2, p.getY()+p.getHeight()/2, this);
			}
			if(l != null){
				l.setPower(this.power);
				map.addLaser(l);
			}
		}
	}
	
	/**
	 * Finds the closest valid target to this turret. Sets target time so we don't end
	 * up targeting too often.
	 * @return The newly aquired target, null if no target available.
	 */
	private Sprite aquireTarget(){
		//add player to a new list of sprites so we can check for targets.
		Sprite newTarget = null;
		Sprite o = map.getPlayer();
		Sprite o2 = map.getPlayer2();
		LinkedList<Sprite>possibleTargets = (LinkedList<Sprite>) map.getSprites().clone();
		possibleTargets.add(o);
		possibleTargets.add(o2);
		
		newTarget = getClosestValidTarget(possibleTargets);
		
		if(newTarget != null){
			targetTime = System.currentTimeMillis();
			//set color based on target
			if(newTarget instanceof Planet){
				bodyColor = Color.green;
				Laser l = map.getLaser(this);
				if(l != null) l.color = Color.green;
			}else if(newTarget instanceof Ship){
				bodyColor = Color.red;
				Laser l = map.getLaser(this);
				if(l != null) l.color = Color.red;
			}else{
				bodyColor = Color.white;
			}
		}else if(newTarget == null){
			bodyColor = Color.white;
		}
		return newTarget;
	}
	
	/**
	 * Returns the Closes valid target for this turret.
	 * @param possibleTargets The list of possible targets, 
	 * right now this is all sprites except lasers
	 * @return The closes valid target for this turret, Null if no target is found.
	 */
	private Sprite getClosestValidTarget(LinkedList<Sprite>possibleTargets){
		Sprite target = null;
		possibleTargets = getValidTargets(possibleTargets);
		target = getClosestTarget(possibleTargets);
		return target;
	}
	
	/**
	 * Gets the closest target to this turret from a list of targets.
	 * @param targets The list to evaluate
	 * @return The closest target in the list, 
	 * NULL if the list was null and there is no target.
	 */
	private Sprite getClosestTarget(LinkedList<Sprite>targets){
		Sprite closestTarget = null;
		if(targets == null){
			closestTarget = null;
		}else{
			closestTarget = targets.get(0);
			for(int i = 0; i < targets.size(); i++){
				if(distanceBetween(this, closestTarget) > distanceBetween(this, targets.get(i))){
					closestTarget = targets.get(i);	
				}
			}
		}
		return closestTarget;
	}
	
	/**
	 * Searches input LinkedList for a Sprite that can work as a valid target
	 * for this Turret.
	 * @param sprites The list to search for a valid target in
	 * @return A list of valid targets
	 */
	private LinkedList<Sprite> getValidTargets(LinkedList<Sprite>sprites){
		LinkedList<Sprite>possibleTargets = new LinkedList<Sprite>();
		for(int i = 0; i < sprites.size(); i++){
			Sprite s = sprites.get(i);
			if(validTarget(s))possibleTargets.add(s);
		}
		//no targets were found make the list a null val
		if(possibleTargets.size() == 0) possibleTargets = null;
		return possibleTargets;
	}
	
	
	/**
	 * Evaluates a sprite to see if it is a valid target for this turret
	 *valid targets are closer then turret reach and are not
	 *related to this turret, ie, isn't another turret owned by same player
     *returns null if there is no valid target
	 * @param s The sprite under evaluation
	 * @return True if Valid, False if not.
	 */
	private boolean validTarget(Sprite s){
		boolean returnVal = false;
		if(validTargetDistance(s) && validTargetNotParent(s)) returnVal = true;
		return returnVal;
	}
	
	/**
	 * Check if sprite is related to this turret. The sprite could be it's parent,
	 * or it could be another turret that shares parents with ours.
	 * @param s The Sprite under evaluation.
	 * @return True if NOT Related, False if IS related
	 */
	private boolean validTargetNotParent(Sprite s){
		boolean returnVal = true;
		if(s instanceof Ship){
			Ship ship = (Ship)s;
			if(parent == ship) returnVal = false;
		}else if(s instanceof Turret){
			Turret turret = (Turret)s;
			if(parent == turret.parent) returnVal = false;
		}else{
			//expand validtarget here
		}
		return returnVal;
	}
	
	/**
	 * Evaluates if a Sprite and this turret are within TURRET_REACH value
	 * @param s The sprite under evaluation
	 * @return True if distance is within TURRET_REACH, False if not.
	 */
	private boolean validTargetDistance(Sprite s){
		boolean returnVal = false;
		if(s != null && distanceBetween(this, s) < TURRET_REACH) returnVal = true;
		return returnVal;
	}
	
	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}
	
	/**
	 * Finds the distance between a turret and another object.
	 * May need to refactor this now that all object are sprites.
	 * @param t
	 * @param o2
	 * @return
	 */
	public static float distanceBetween(Turret t, Object o2){
		float x1, x2, y1, y2;
		x1 = t.getX();
		y1 = t.getY();
		
		if(o2 instanceof Ship){
			Ship s = (Ship)o2;
			x2 = s.getX();
			y2 = s.getY();
		}else{
			Sprite s = (Sprite)o2;
			x2 = s.getX();
			y2 = s.getY();
		}
		return distanceBetween(x1, y1, x2, y2);
	}
	
	public static float distanceBetween(float x1, float y1, float x2, float y2){
		return (float) Math.sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
	}
	
	public static double map(double heightPoints, double d, double e, double f, double g){
		return (heightPoints - d) * (g - f) / (e - d) + f;
	}

	public Ship getParent() {
		return parent;
	}

	public void setParent(Ship parent) {
		this.parent = parent;
	}

	public Ellipse2D getCircle() {
		return circle;
	}

	public void setCircle(Ellipse2D circle) {
		this.circle = circle;
	}

	public Rectangle2D getRect() {
		return rect;
	}

	public void setRect(Rectangle2D rect) {
		this.rect = rect;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getHp() {
		return hp;
	}

	public void setHp(double hp) {
		this.hp = hp;
	}
	
	public float getWidth(){
		return (float) circle.getWidth();
	}
	
	public float getHeight(){
		return (float) circle.getHeight();
	}
}
