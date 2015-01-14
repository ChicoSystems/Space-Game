package spacegame.input;

import spacegame.graphics.SteeringBehaviorsV2;
import spacegame.util.Vector2D;

/**
 * @author Isaac Assegai
 *
 */
public interface LocationManager {
	Vector2D calculate();
	public SteeringBehaviorsV2 getSteering();
	public void setSteering(SteeringBehaviorsV2 steering);
	void pressMoveRight();
	void pressMoveLeft();
	void pressMoveDown();
	void pressMoveUp();
}
