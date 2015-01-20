package spacegame.input;

import spacegame.graphics.SpriteV2;
import spacegame.graphics.SteeringBehaviorsV2;
import spacegame.util.Vector2D;

public class AILocManager implements LocationManager {
	SpriteV2 parent;
	SteeringBehaviorsV2 steering;
	Vector2D inputVector;
	
	
	public AILocManager(SpriteV2 parent){
		this.parent = parent;
		steering = new SteeringBehaviorsV2(this.parent);
		inputVector = new Vector2D(0, 0);
	}

	@Override
	public Vector2D calculate(double elapsedTime) {
		inputVector = inputVector.plus(steering.evade(parent.getParent().parent.getMap().getPlayer()));
		//inputVector = inputVector.plus(steering.wander());
		//System.out.println("inputVecot.length "+inputVector.length());
		Vector2D returnVector = new Vector2D(inputVector.x, inputVector.y); 
		inputVector = new Vector2D(0,0);
		
		
		return returnVector;
		//return steering.seek(parent.getParent().parent.getMap().getPlayer().getPosition());
	}

	@Override
	public SteeringBehaviorsV2 getSteering() {
		return steering;
	}

	@Override
	public void setSteering(SteeringBehaviorsV2 steering) {
		this.steering = steering;
	}

	@Override
	public double calculateTorque(double elapsedTime) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void pressMoveRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressMoveLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressRotateLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressRotateRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressMoveForward() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pressMoveBackward() {
		// TODO Auto-generated method stub

	}

	@Override
	public Vector2D calculateGravity(double elapsedTime) {
		// TODO Auto-generated method stub
		return new Vector2D(0, 0);
	}

}
