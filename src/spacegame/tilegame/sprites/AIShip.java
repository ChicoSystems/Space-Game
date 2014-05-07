package spacegame.tilegame.sprites;

import spacegame.graphics.Animation;
import spacegame.input.AIManager;
import spacegame.tilegame.ResourceManager;

public class AIShip extends Ship {

	AIManager aiManager;
	
	
	public AIShip(ResourceManager parent, AIManager aiManager, Animation[] animArray) {
		super(parent, animArray);
		this.aiManager = aiManager;
	}
	
	public AIShip(ResourceManager parent, Animation[] animArray) {
		super(parent, animArray);
		AIManager aiManager = new AIManager(this);
		this.aiManager = aiManager;
	}

}
