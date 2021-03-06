package spacegame.tilegame.sprites;

import spacegame.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Grub extends Creature {

    public Grub(Animation[] anim /*Animation left, Animation right,
        Animation deadLeft, Animation deadRight*/)
    {
       // super(left, right, deadLeft, deadRight);
    	super(anim);
    }


    public float getMaxSpeed() {
        return 0.05f;
    }

}
