package spacegame.tilegame.sprites;

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


    public void updateRotation(long elapsedTime){
		
		//System.out.println("Current: " + currentRotation + " Future: " + futureRotation);
		if(Math.abs(currentRotation - futureRotation) < 2){
			rotationSpeed = 0;
			return;
		}else{
			double rotationChange = futureRotation - currentRotation;
			System.out.println("Rot Change: " + futureRotation + " : " + currentRotation + " : " +rotationChange);
			if(rotationChange <= 0){
				setRotationSpeed(-SPEED_ROTATION);
			}else{
				setRotationSpeed(SPEED_ROTATION);
			}
			currentRotation = currentRotation + rotationSpeed * elapsedTime;
			
		}
		
	}
    
    public float getMaxSpeed() {
        return 0.5f;
    }

}
