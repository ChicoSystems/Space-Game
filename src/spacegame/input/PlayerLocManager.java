package spacegame.input;

import spacegame.graphics.SpriteV2;
import spacegame.graphics.SteeringBehaviorsV2;
import spacegame.util.Vector2D;

/**
 * @author Isaac Assegai
 * Decides the players location based on the players input.
 * Uses steering to calculate the desired forces to add to
 * the players ship based on input
 */
public class PlayerLocManager implements LocationManager {
	SpriteV2 parent;
	SteeringBehaviorsV2 steering;
	Vector2D inputVector; // Used to find players input
	
	public PlayerLocManager(SpriteV2 parent){
		this.parent = parent;
		steering = new SteeringBehaviorsV2(this.parent);
		inputVector = new Vector2D(0 ,0);
	}

	public SpriteV2 getParent() {
		return parent;
	}

	public void setParent(SpriteV2 parent) {
		this.parent = parent;
	}

	public SteeringBehaviorsV2 getSteering() {
		return steering;
	}

	public void setSteering(SteeringBehaviorsV2 steering) {
		this.steering = steering;
	}

	public Vector2D calculate(double elapsedTime) {
		// Calculate the forces on the ship containing this locationManager
		//
		//inputVector = inputVector.plus(steering.addFriction(.01)); // Add friction
		Vector2D returnVector = new Vector2D(inputVector.x, inputVector.y); 
		//inputVector = inputVector.scalarMult(3*(elapsedTime/1000));
		//if(inputVector.length() < .00000008 && inputVector.length() != 0) inputVector = new Vector2D(0, 0); // reduce input vector over time
	
		System.out.println(inputVector.length());
		
		return returnVector;
	}

	
	public void pressMoveRight() {
		inputVector = inputVector.plus(steering.pressMoveRight());
	}

	@Override
	public void pressMoveLeft() {
		inputVector = inputVector.plus(steering.pressMoveLeft());
		System.out.println("playerLocMan: pressMoveLeft() " + inputVector);
	}

	@Override
	public void pressMoveDown() {
		inputVector = inputVector.plus(steering.pressMoveDown());
	}

	@Override
	public void pressMoveUp() {
		inputVector = inputVector.plus(steering.pressMoveUp());
	}

}
