package spacegame.graphics;

import spacegame.util.Vector2D;

public class SteeringBehaviors {
	Sprite parent;
	
	public SteeringBehaviors(Sprite s){
		parent = s;
		
	}
	
	public Vector2D calculate(Vector2D velocity){
		return velocity.unitVector();
	}
	
	/** Seek Behavior */
	public Vector2D seek(Vector2D targetPos){
		Vector2D desiredVelocity = targetPos.minus(parent.getPosition()).scalarMult(parent.getMaxSpeed());
		desiredVelocity = desiredVelocity.minus(parent.getVelocity());
		desiredVelocity.truncate(parent.getMaxSpeed());
		return desiredVelocity.minus(parent.getVelocity());
	}
	
	/** Flee Behavior */
	public Vector2D flee(Vector2D targetPos){
		
		//Only flee if the target is within panic distance.
		double panicDist = 200 * 200;
		if(parent.getPosition().distanceSq(targetPos) > panicDist){
			return new Vector2D(0, 0);
		}else{
			Vector2D desiredVelocity = parent.position.minus(targetPos).unitVector().scalarMult(parent.getMaxSpeed());
			desiredVelocity = desiredVelocity.minus(parent.getVelocity());
			return desiredVelocity;
		}
	}

}
