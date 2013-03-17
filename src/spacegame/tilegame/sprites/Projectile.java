package spacegame.tilegame.sprites;

import java.lang.reflect.Constructor;

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
    
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
            		/*
                (Animation)left.clone(),
                (Animation)right.clone(),
                (Animation)deadLeft.clone(),
                (Animation)deadRight.clone()*/
            		creatureAnim.clone(),
            		parentId
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


}
