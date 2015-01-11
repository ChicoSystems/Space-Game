package spacegame.tilegame.sprites;

import spacegame.graphics.Animation;
import spacegame.input.AIManager;
import spacegame.tilegame.ResourceManager;

public class AIShip extends Ship {

	AIManager aiManager;
	
	
	public AIShip(ResourceManager parent, AIManager aiManager, Animation[] animArray) {
		super(parent, animArray);
		this.aiManager = aiManager;
		this.setMaxSpeed(this.getMaxSpeed()/2);
	}
	
	public AIShip(ResourceManager parent, Animation[] animArray) {
		super(parent, animArray);
		AIManager aiManager = new AIManager(this);
		this.aiManager = aiManager;
	}
	
	public void update(long elapsedTime){
		super.update(elapsedTime);
		aiManager.updateAI(elapsedTime);
		if(state == STATE_DEAD){
			getParent().parent.getMap().getAIShips().remove(this);
		}
	}

}
