package spacegame.tilegame.sprites;

import spacegame.graphics.Animation;

/**
    A Grub is a Creature that moves slowly on the ground.
*/
public class Projectile extends Creature {
	public double parentId; // the projectile has an id, and a parent id.

    public Projectile(Animation[] anim, double pid)
    {
    	super(anim);
    	parentId = pid;
    }


}
