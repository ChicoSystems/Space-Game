package spacegame.graphics;

import java.util.Random;

import maths.*;

import spacegame.util.Vector2D;

public class SteeringBehaviorsV2 {
	protected SpriteV2 parent;
	Random r;// = new Random(System.currentTimeMillis());
	
	public static enum Deceleration {
        SLOW(3), NORMAL(2), FAST(1);
        private int value;

        private Deceleration(int value) {
                this.value = value;
        }
};

	
	public SteeringBehaviorsV2(SpriteV2 s){
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
		double panicDist = 1000 * 1000;
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
	
	/** Wander behavior.*/
	public Vector2D wander(){
		
		double wanderRadius = 250;
		double wanderDistance = 1000;
		double wanderJitter = .02;
		
		double r1 = (r.nextDouble()*2)-1;
		double r2 = (r.nextDouble()*2)-1;
		Vector2D wanderTarget = new Vector2D(r1 * wanderJitter, r2 * wanderJitter);
		//Vector2D wanderTarget = new Vector2D(0, 0);
		wanderTarget = wanderTarget.unitVector();
		wanderTarget = wanderTarget.scalarMult(wanderRadius);
		parent.side = parent.heading.perp();
		Vector2D targetLocal = wanderTarget.plus(new Vector2D(wanderDistance, 0));
		
		//here is out translate to world space code
		maths.Vector2D targetLocal_maths = new maths.Vector2D(targetLocal.x, targetLocal.y);
		maths.Vector2D heading_maths = new maths.Vector2D(parent.heading.x, parent.heading.y);
		maths.Vector2D side_maths = new maths.Vector2D(parent.side.x, parent.side.y);
		maths.Vector2D pos_maths = new maths.Vector2D(parent.position.x, parent.position.y);
		maths.Vector2D pre_worldTarget = Transformations.pointToWorldSpace(targetLocal_maths, heading_maths, side_maths, pos_maths);
		Vector2D targetWorld = new Vector2D(pre_worldTarget.x, pre_worldTarget.y);
		//done translating to world space.
		Vector2D newVel = targetWorld.minus(parent.position);
		return newVel;
		
		/*
		AffineTransform translate= AffineTransform.getTranslateInstance(parent.position.x, parent.position.y);
		System.out.println("Translate:" + translate.toString());
		Vector2D newVector = new Vector2D(0, 0);
		System.out.println("newVector:" + translate.transform(newVector, null));
		Vector2D wanderTarget = parent.heading;
		double wanderRadius = 50;
		double wanderDistance = 700;
		double wanderJitter = .2;
		
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
       //System.out.println("parentpos: " + parent.position);
        //System.out.println("wanderpos: " + wanderPos);
        return seek(wanderPos);
        
*/

		
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
	
	//interpose behavior
	public Vector2D interpose(Sprite s1, Sprite s2){
		//figure out where they'll be in the future.
		Vector2D midPoint = s1.position.plus(s2.position).scalarDiv(2);
		double timeToReachMidPoint = Vector2D.distance(parent.position.x, parent.position.y, midPoint.x, midPoint.y) / parent.getMass();
		
		//we have t, assume a and be will continue on trajectory, and extrapolate.
		Vector2D s1Pos = s1.velocity.scalarMult(timeToReachMidPoint).plus(s1.position);
		Vector2D s2Pos = s2.velocity.scalarMult(timeToReachMidPoint).plus(s2.position);
		
		//calculate midpoint of predicted positions.
		midPoint = s1Pos.plus(s2Pos).scalarDiv(2);
		Vector2D newVel = arrive(midPoint, Deceleration.FAST);
		return newVel;
		
	}
	
	//offsetpursuit behavior
	public Vector2D offsetPursuit(Sprite leader, Vector2D offset){

		//here is out translate to world space code
		maths.Vector2D offset_maths = new maths.Vector2D(offset.x, offset.y);
		maths.Vector2D heading_maths = new maths.Vector2D(parent.heading.x, parent.heading.y);
		maths.Vector2D side_maths = new maths.Vector2D(parent.side.x, parent.side.y);
		maths.Vector2D pos_maths = new maths.Vector2D(parent.position.x, parent.position.y);
		maths.Vector2D pre_worldTarget = Transformations.pointToWorldSpace(offset_maths, heading_maths, side_maths, pos_maths);
		Vector2D worldOffsetPos = new Vector2D(pre_worldTarget.x, pre_worldTarget.y);
		
		Vector2D toOffset = worldOffsetPos.minus(parent.position);
		//look ahead time is proportional to the distance between leader and puruer.
		double lookAheadTime = toOffset.length() /(parent.getMaxSpeed() + leader.dMaxSpeed);
		Vector2D newVel = arrive(leader.velocity.scalarMult(lookAheadTime).plus(worldOffsetPos), Deceleration.FAST);
		//Vector2D newVel = arrive(worldOffsetPos.plus(leader.velocity).scalarMult(lookAheadTime), Deceleration.FAST);
		return newVel;
	}

	public Vector2D pressMoveRight() {
		//return new Vector2D(1, 0);
		return parent.heading.perp().unitVector(); // one perp makes a right
	}
	
	public Vector2D pressMoveLeft() {
		//return new Vector2D(-1, 0);
		return parent.heading.perp().perp().perp().unitVector(); // three perps makes a left
	}
	
	public Vector2D pressMoveUp() {
		return new Vector2D(0, -1);
	}
	
	public Vector2D pressMoveDown() {
		return new Vector2D(0, 1);
	}
	
	public Vector2D pressMoveForward() {
		return parent.heading.unitVector();
	}
	
	public Vector2D pressMoveBackward() {
		return parent.heading.perp().perp().unitVector(); // to perps makes a backwards;
	}
	
	public double pressRotateRight(){
		return 10;
	}
	
	public double pressRotateLeft(){
		return -10;
	}

	public Vector2D addFriction(double fricCoeff){
		//vector 180's different from velocity, scaled by the fricCoeff, which should be < 1
		Vector2D fricVector = parent.velocity.rotate(Math.PI).unitVector();
		return fricVector;
	}

	

}