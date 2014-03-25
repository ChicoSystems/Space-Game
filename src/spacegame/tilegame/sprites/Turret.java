package spacegame.tilegame.sprites;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

import spacegame.graphics.Animation;
import spacegame.graphics.Sprite;
import spacegame.tilegame.TileMap;

public class Turret extends Creature{
	public static float LEVEL_TO_SIZE = 5;
	public static float TURRET_TO_LEVEL = 1000;
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
		power = level*10;
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
		if(targetTime == 0 || System.currentTimeMillis() - targetTime > 1000) aquireTarget();
		
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
				l = new Laser(this.getX(), this.getY(), 
						s.getX()+s.getWidth()/2, s.getY()+s.getHeight()/2, this);
			}else if(target instanceof Turret){
				//the target shouldn't ever be a turret. we may need to change targeting code.
				System.err.println("target is a turret");
			}else if(target instanceof Projectile){
				//we arent implementing this yet.
			}else{
				if(target instanceof Player || target instanceof Ship){
					bodyColor = Color.red;
				}else{
					bodyColor = Color.green;
				}
				//target must be a planet
				Sprite p = (Sprite)target;
				l = new Laser(this.getX(), this.getY(),
						p.getX()+p.getWidth()/2, p.getY()+p.getHeight()/2, this);
			}
			if(l != null) map.addLaser(l);
		}
	}
	
	private Object aquireTarget(){
		Object newTarget = null;
		Object o = map.getPlayer();
		LinkedList<Sprite>sprites = map.getSprites();
		double distanceBetween = distanceBetween(this, o);
		if(distanceBetween <= TURRET_REACH && o != parent){
			newTarget = o;
		}
		for(int i = 0; i < sprites.size(); i++){
			if(newTarget != null){
				double newDistance = distanceBetween(this, sprites.get(i));
				if( newDistance < distanceBetween){
					o = sprites.get(i);
					distanceBetween = newDistance;
				}
			}else{
				if(target instanceof Turret){
					//we don't want to target other turrets as of yet, maybe later
				}else if(target instanceof Projectile){
					//we don't want to target projectiles yet, maybe later
				}else{
					if(target instanceof Player){
						System.err.println("target is player");
					}
					o = sprites.get(i);
					distanceBetween = distanceBetween(this, o);
					if(distanceBetween <= TURRET_REACH){
						newTarget = o;
						System.out.println("hello");
					}
				}
			}
		}
		if(newTarget != null)targetTime = System.currentTimeMillis();
		return newTarget;
	}
	
	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}
	
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
}
