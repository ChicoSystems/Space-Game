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
		return desiredVelocity.minus(parent.getVelocity());
	}

}
