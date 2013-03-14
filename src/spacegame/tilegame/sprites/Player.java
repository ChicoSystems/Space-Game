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
    	
    	 float rotation = (float) Math.atan2(this.dy, this.dx);
         rotation = (float) Math.toDegrees(rotation);
         rotation = rotation + 90;
         
         
         if((this.dx == 0) && (this.dy == 0)){
         	rotation = this.getRotation(); // keeps from reseting rotation when velocity is 0
         }
         	this.setFutureRotation(rotation);
         	
		
		//System.out.println("Current: " + currentRotation + " Future: " + futureRotation);
		if(Math.abs(getRotation() - getFutureRotation()) < 3){//don't rotate if change is less then 2 degrees
			rotationSpeed = 0;
			return;
		}else{
			float rotationChange = getFutureRotation() - getRotation();
			if(Math.abs(rotationChange) > 180){
				rotationChange = Math.abs(rotationChange);
				rotationChange = 360 - rotationChange;
				rotationChange = -rotationChange;
			}
			System.out.println("Rot Change: " + getFutureRotation() + " : " + getRotation() + " : " +rotationChange + " : " + rotationSpeed);
			if(rotationChange <= 0){
				setRotationSpeed(-SPEED_ROTATION);
			}else{
				setRotationSpeed(SPEED_ROTATION);
			}
			//System.out.println("Set Rot: " + ((float)(getRotation() + getRotationSpeed() * elapsedTime)));
			setRotation((float)(getRotation() + getRotationSpeed() * elapsedTime));
			
		}
		
	}
    
    public float getMaxSpeed() {
        return 0.5f;
    }

}
