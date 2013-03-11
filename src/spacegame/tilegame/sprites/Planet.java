package spacegame.tilegame.sprites;

import spacegame.graphics.Animation;

/**
    The Player.
*/
public class Planet extends Creature {


    public Planet(Animation[] anim /*Animation left, Animation right,
        Animation deadLeft, Animation deadRight*/)
    {
        //super(left, right, deadLeft, deadRight);
    	super(anim);
    }

    public void wakeUp() {
        // do nothing
    }
}
