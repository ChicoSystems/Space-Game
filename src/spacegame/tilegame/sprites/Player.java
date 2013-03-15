package spacegame.tilegame.sprites;

import java.io.ObjectInputStream.GetField;

import spacegame.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;

    private boolean onGround;

    public Player(Animation[] anim /*Animation left, Animation right,
        Animation deadLeft, Animation deadRight*/)
    {
        //super(left, right, deadLeft, deadRight);
    	super(anim);
    	this.setMaxSpeed(.5f);
    	this.setBoostSpeed(this.getMaxSpeed()*2);
    	this.setCurrentSpeed(this.getMaxSpeed());
    }
    
    public void collide(){
    	collideHorizontal();
    	collideVertical();
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }


    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }


}
