package spacegame.tilegame.sprites;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import spacegame.graphics.*;

/**
    A Creature is a Sprite that is affected by gravity and can
    die. It has four Animations: moving left, moving right,
    dying on the left, and dying on the right.
*/
public abstract class Creature extends Sprite {

    /**
        Amount of time to go from STATE_DYING to STATE_DEAD.
    */
    protected static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    protected float currentSpeed = 0;
    protected float boostSpeed = 0;
    public double id;

    /*
    private Animation left;
    private Animation right;
    private Animation deadLeft;
    private Animation deadRight;
    */
    Animation[] creatureAnim = new Animation[360];
     int state;
    protected long stateTime;

    /**
        Creates a new Creature with the specified Animations.
    */
    public Creature(Animation[] anim/*Animation left, Animation right,
        Animation deadLeft, Animation deadRight*/)
    {
        super(anim[0], null);
        this.creatureAnim = anim;
        /*
         * super(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;*/
        id = Math.random();
        state = STATE_NORMAL;
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
            		creatureAnim.clone()
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }


    /**
        Gets the maximum speed of this Creature.
    */
    public float getMaxSpeed() {
       // return maxSpeed;
       return (float) this.dMaxSpeed;
    }
    
    /**
    Gets the maximum speed of this Creature.
	*/
	public void setMaxSpeed(Float speed) {
	   // maxSpeed = speed;
		this.dMaxSpeed = speed;
	}
    /**
    Gets the maximum speed of this Creature.
	*/
	public float getCurrentSpeed() {
	    return currentSpeed;
	}
	
	/**
	Gets the maximum speed of this Creature.
	*/
	public void setCurrentSpeed(Float speed) {
	    currentSpeed = speed;
	}

	
	/**
    Gets the maximum speed of this Creature.
	*/
	public float getBoostSpeed() {
	    return boostSpeed;
	}
	
	/**
	Gets the maximum speed of this Creature.
	*/
	public void setBoostSpeed(Float speed) {
	    boostSpeed = speed;
	}


    /**
        Wakes up the creature when the Creature first appears
        on screen. Normally, the creature starts moving left.
    */
    public void wakeUp() {
        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
            setVelocityX(-getMaxSpeed());
        }
    }


    /**
        Gets the state of this Creature. The state is either
        STATE_NORMAL, STATE_DYING, or STATE_DEAD.
    */
    public int getState() {
        return state;
    }


    /**
        Sets the state of this Creature to STATE_NORMAL,
        STATE_DYING, or STATE_DEAD.
    */
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }


    /**
        Checks if this creature is alive.
    */
    public boolean isAlive() {
        return (state == STATE_NORMAL);
    }


    /**
        Checks if this creature is flying.
    */
    public boolean isFlying() {
        return false;
    }


    /**
        Called before update() if the creature collided with a
        tile horizontally.
    */
    public void collideHorizontal() {
        setVelocityX(-getVelocityX());
    }


    /**
        Called before update() if the creature collided with a
        tile vertically.
    */
    public void collideVertical() {
        setVelocityY(0);
    }
    
   
    /**
        Updates the animaton for this creature.
    */
    public void update(long elapsedTime) {
    	//rotation = Math.atan2(this.getVelocityX(), this.getVelocityY());
    	
        // select the correct Animation
        Animation newAnim = anim;
        newAnim = creatureAnim[(int)getRotation()];
        // update the Animation
        if (anim != newAnim) {
           anim = newAnim;
            anim.start();
        }
        else {
            anim.update(elapsedTime);
        }

        // update to "dead" state
        stateTime += elapsedTime;
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
    }

}
