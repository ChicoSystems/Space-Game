package spacegame.input;

import spacegame.graphics.SteeringBehaviorsV2;
import spacegame.util.Vector2D;

/**
 * @author Isaac Assegai
 *
 */
public interface LocationManager {
	Vector2D calculate(double elapsedTime);
	public SteeringBehaviorsV2 getSteering();
	public void setSteering(SteeringBehaviorsV2 steering);
	public double calculateTorque(double elapsedTime);
	void pressMoveRight();
	void pressMoveLeft();
	public void pressRotateLeft();
	public void pressRotateRight();
	void pressMoveForward();
	void pressMoveBackward();
	Vector2D calculateGravity(double elapsedTime);
}
