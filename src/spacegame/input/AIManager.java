package spacegame.input;

import spacegame.tilegame.sprites.Creature;

public class AIManager {
	static final int STATE_DISCOVER = 0;
	static final int STATE_ATTACK = 1;
	static final int STATE_DEFEND = 2;
	static final int STATE_FOLLOW = 3;
	static final int STATE_FLEE = 4;
	static final int STATE_GATHER = 5;
	
	private Creature parent;
	private Creature target;
	private int currentState;
	
	public AIManager(Creature p){
		parent = p;
		currentState = STATE_DISCOVER;
	}
	
	public void updateAI(long timeElapsed){
		
	}

}
