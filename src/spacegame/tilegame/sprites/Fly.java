package spacegame.tilegame.sprites;

import spacegame.graphics.Animation;

/**
    A Fly is a Creature that fly slowly in the air.
*/
public class Fly extends Creature {

    public Fly(Animation[] anim/*Animation left, Animation right,
        Animation deadLeft, Animation deadRight*/)
    {
        //super(left, right, deadLeft, deadRight);
    	super(anim);
    	this.setCurrentSpeed(getMaxSpeed());
    }


    public float getMaxSpeed() {
        return 0.2f;
    }


    public boolean isFlying() {
        return isAlive();
    }

}
