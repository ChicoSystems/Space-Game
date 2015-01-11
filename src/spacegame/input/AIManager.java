package spacegame.input;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import spacegame.graphics.Sprite;
import spacegame.tilegame.TileMap;
import spacegame.tilegame.TileMapRenderer;
import spacegame.tilegame.sprites.AIShip;
import spacegame.tilegame.sprites.Creature;
import spacegame.tilegame.sprites.Ship;
import spacegame.tilegame.sprites.Turret;
import spacegame.util.Vector2D;

public class AIManager {
	static final int STATE_DISCOVER = 0;
	static final int STATE_ATTACK = 1;
	static final int STATE_DEFEND = 2;
	static final int STATE_FOLLOW = 3;
	static final int STATE_FLEE = 4;
	static final int STATE_GATHER = 5;
	static final int STATE_SEARCH = 6;
	static final int STATE_MOVETO = 7;
	
	
	public Creature parent;
	private Creature target;
	private Vector targetLocation;
	private int currentState;
	private int aggression;
	private int areaOfInterest = 2000;
	
	public AIManager(Creature p){
		parent = p;
		aggression = 1;
		currentState = STATE_DISCOVER;
	}
	
	public void updateAI(long timeElapsed){
		if(parent instanceof AIShip){
			if(currentState == STATE_DISCOVER){
				discover();
			}else if(currentState == STATE_ATTACK){
				attack(target);
			}else if(currentState == STATE_DEFEND){
				defend(target);
			}else if(currentState == STATE_FOLLOW){
				Vector v = new Vector();
				v.add(target.getX());
				v.add(target.getY());
				follow(v);
			}else if(currentState == STATE_FLEE){
				flee(target);
			}else if(currentState == STATE_GATHER){
				gather(target);
			}else if(currentState == STATE_SEARCH){
				search(target);
			}else if(currentState == STATE_MOVETO){
				follow(targetLocation);
			}
		}
	}
	
	/**
	 * Search the map for a target.
	 * @param target2
	 */
	private void search(Creature target2) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Gather Resources from a target.
	 * @param target2
	 */
	private void gather(Creature target2) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Flee from a target.
	 * @param target2
	 */
	private void flee(Creature target2) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Follow a target.
	 * @param target2
	 */
	private void follow(Vector followLoc) {
		// TODO Auto-generated method stub
		float vectorDiffX = (Float) followLoc.get(0) - parent.getX();
		float vectorDiffY = (Float) followLoc.get(1) - parent.getY();
		float totalDiff = (float) Math.sqrt((vectorDiffX * vectorDiffX)+(vectorDiffY * vectorDiffY));
		totalDiff = (float) (totalDiff /.05);
		if(totalDiff > 50){
			//System.out.println("totaldiff: " + totalDiff);
			//parent.setVelocityX(vectorDiffX/totalDiff);
			//parent.setVelocityY(vectorDiffY/totalDiff);
			//System.out.println("velx: " + parent.getVelocityX() + "vely: " + parent.getVelocityY());
		parent.setVelocity(parent.steering.seek(target.getPosition())) ;
		}else{
			parent.setVelocityX(0);
			parent.setVelocityY(0);
			currentState = STATE_DISCOVER;
		}
	}
	
	private void moveTo(Vector followLoc){
		
	}

	/**
	 * Defend a target from all attackers.
	 * @param target2
	 */
	private void defend(Creature target2) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Attack a target
	 * @param target2
	 */
	private void attack(Creature target2) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Discover what we should be doing.
	 */
	private void discover(){
		if(currentState == STATE_DISCOVER 
				|| currentState == STATE_DEFEND
				|| currentState == STATE_FOLLOW
				|| currentState == STATE_GATHER
				|| currentState == STATE_SEARCH){
			//We are not doing anything else to superscede a normal discover operation.
			normalDiscover();
		}else if(currentState == STATE_ATTACK || currentState == STATE_FLEE){
			discoverFromEngagement(target);
		}
	}
	
	/**
	 * AI does not have to worry about distancing itself from an
	 * enemy that it was engaged with. It can immediately change to
	 * discover.
	 */
	private void normalDiscover(){
		ArrayList possibleTargets = getPossibleTargets();
		if(possibleTargets.isEmpty()){//move to a random location in map
			Random r = new Random(System.currentTimeMillis());
			int xLim = TileMapRenderer.tilesToPixels(((Ship)(this.parent)).getParent().parent.getMap().getWidth());
			int yLim = TileMapRenderer.tilesToPixels(((Ship)(this.parent)).getParent().parent.getMap().getHeight());
			int randX = r.nextInt(xLim - 10)+10;
			int randY = r.nextInt(yLim -10)+10;
			targetLocation = new Vector(randX, randY);
			currentState = STATE_MOVETO;
		}else{
			target = getClosestTarget(possibleTargets);
			targetLocation = new Vector((int)target.getX(), (int)target.getY());
			currentState = STATE_FOLLOW;
		}
	}
	
	private ArrayList<Creature>getPossibleTargets(){
		ArrayList<Creature>possibleTargets = new ArrayList<Creature>();
		TileMap map = ((Ship)parent).getParent().parent.getMap();
		LinkedList sprites = map.getSprites();
		for(int i = 0; i < sprites.size(); i++){
			if(distanceBetween((Creature) sprites.get(i), this.parent) < areaOfInterest){
				possibleTargets.add((Creature) sprites.get(i));
			}
		}
		
		ArrayList<Ship> aiShips = map.getAIShips();
		for(int i = 0; i < aiShips.size(); i++){
			if(aiShips.get(i)!= this.parent && distanceBetween(aiShips.get(i), this.parent) < areaOfInterest){
				possibleTargets.add(aiShips.get(i));
			}
		}
		if(distanceBetween((Creature) map.getPlayer(), this.parent) < areaOfInterest){
			possibleTargets.add(map.getPlayer());
		}
		
		//possibleTargets.add(map.getPlayer2());
		return possibleTargets;
	}
	
	
	/**
	 * Gets the closest target to this turret from a list of targets.
	 * @param possibleTargets The list to evaluate
	 * @return The closest target in the list, 
	 * NULL if the list was null and there is no target.
	 */
	private Creature getClosestTarget(ArrayList possibleTargets){
		Creature closestTarget = null;
		if(possibleTargets == null){
			closestTarget = null;
		}else{
			closestTarget = (Creature) possibleTargets.get(0);
			for(int i = 0; i < possibleTargets.size(); i++){
				if(distanceBetween(this.parent, closestTarget) > distanceBetween(this.parent, (Creature)possibleTargets.get(i))){
					closestTarget = (Creature) possibleTargets.get(i);	
				}
			}
		}
		return (Creature)closestTarget;
	}
	
	/**
	 * Finds the distance between a turret and another object.
	 * May need to refactor this now that all object are sprites.
	 * @param t
	 * @param o2
	 * @return
	 */
	public static float distanceBetween(Creature t, Creature o2){
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
	
	/**
	 * We are changing to a discover mode from engageing with an enemy.
	 * Before we discover we want to seperate ourselves from the enemy.
	 * @param target2
	 */
	private void discoverFromEngagement(Creature target2){
		
	}
	
	

}
