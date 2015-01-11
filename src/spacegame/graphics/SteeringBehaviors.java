package spacegame.graphics;

import java.util.Random;

import spacegame.util.Vector2D;

public class SteeringBehaviors {
	protected Sprite parent;
	Random r;// = new Random(System.currentTimeMillis());
	
	public static enum Deceleration {
        SLOW(3), NORMAL(2), FAST(1);
        private int value;

        private Deceleration(int value) {
                this.value = value;
        }
};

	
	public SteeringBehaviors(Sprite s){
		parent = s;
		r = new Random(System.currentTimeMillis());
		
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
	
	/** Arrive Behavior */
	public Vector2D arrive(Vector2D targetPos, Deceleration deceleration){
		Vector2D toTarget = targetPos.minus(parent.position);
		
		//distance to the target position
		double dist = toTarget.length();
		
		if(dist > 0){
			//used to fine tweak deceleration.
			double decelerationTweaker = 0.3;
			
			//the speed needed to reach the target with the desired deceleration
			double speed = dist / ((double)deceleration.value * decelerationTweaker);
			
			//velocity shouldn't exceed the max speed
			speed = Math.min(speed, parent.getMaxSpeed());
			
			//proceed similar to the seek algorithm
			Vector2D desiredVelocity = toTarget.scalarMult(speed/dist);
			desiredVelocity = desiredVelocity.minus(parent.getVelocity());
			return desiredVelocity;
		}
		return new Vector2D(0,0);
	}
	
	/** Pursuit behavior */
	public Vector2D pursuit(Sprite target){
		Vector2D toTarget = target.getPosition().minus(parent.getPosition());
		double relativeHeading = parent.heading.dotProduct(target.heading);
		
		if( (toTarget.dotProduct(parent.heading) > 0) && (relativeHeading < -0.95)	){
			return seek(target.position);
		}else{
			//look ahead time is proportional to the distance from parent to target
			//it's inversly proportional to the sum of the velocities.
			double lookAheadTime = toTarget.length() / (parent.getMaxSpeed() + target.getMaxSpeed());
			
			//seek to the future position
			return seek(target.velocity.scalarMult(lookAheadTime).plus(target.position));
		}
	}
	
	/** Evade behavior */
	public Vector2D evade(Sprite pursuer){
		Vector2D toPursuer = pursuer.position.minus(parent.position);
		double lookAheadTime = toPursuer.length() / (parent.getMaxSpeed() + pursuer.getMaxSpeed());
		return flee(pursuer.velocity.scalarMult(lookAheadTime).plus(pursuer.position));
		
	}
	
	/** Wander behavior. */
	public Vector2D wander(){
		Vector2D wanderTarget = parent.heading;
		double wanderRadius = 100;
		double wanderDistance = 500;
		double wanderJitter = .5;
		
		//this behavior is dependent on the update rate, so this line must
        //be included when using time independent framerate.
        double JitterThisTimeSlice = wanderJitter;
        
        double r1 = (r.nextDouble()*2)-1;
		double r2 = (r.nextDouble()*2)-1;

        //first, add a small random vector to the target's position
        wanderTarget.x += (r1) * JitterThisTimeSlice;
        wanderTarget.y += (r2) * JitterThisTimeSlice;

        //reproject this new vec2tor back on to a unit circle
        wanderTarget = wanderTarget.unitVector();
        wanderTarget = wanderTarget.scalarMult(wanderRadius);
        //wanderTarget = wanderTarget.plus(parent.position);

        //move the target into a position WanderDist in front of the agent
        Vector2D wanderPos = new Vector2D(parent.velocity.unitVector());
        //wanderPos.unitVectorVoid();
        wanderPos = wanderPos.scalarMult(wanderDistance);
        wanderPos = wanderPos.plus(parent.position);
        wanderPos = wanderPos.plus(wanderTarget);
       // System.out.println("wandertar: " + wanderTarget);
        System.out.println("parentpos: " + parent.position);
        System.out.println("wanderpos: " + wanderPos);
        return seek(wanderPos);


		
		/*
		//Add a small random vector to the targets position
		Random r = new Random(System.currentTimeMillis());
		double r1 = (r.nextDouble()*2)-1;
		double r2 = (r.nextDouble()*2)-1;
		wanderTarget = wanderTarget.plus(new Vector2D(r1*wanderJitter, r2*wanderJitter));
		wanderTarget.unitVectorVoid();
		
		//increase the length of the vector to the same radius as the wander circle
		wanderTarget.scalarMultVoid(wanderRadius);
		
		//move target into a position wanderDist in front of the parent
		Vector2D targetLocal = wanderTarget.plus(new Vector2D(wanderDistance, 0));
		
		//project the target back into world space
		//Vector2D targetWorld = pointToWorldSpace(targetLocal, parent.heading, parent.side, parent.position);
		
		
		return targetLocal.minus(parent.position);
		*/
		
	}

	

}
