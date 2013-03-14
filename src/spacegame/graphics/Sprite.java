package spacegame.graphics;
import spacegame.tilegame.sprites.*;

import java.awt.Image;

public class Sprite {

	protected float SPEED_ROTATION = .25f;
    protected Animation anim;
    // position (pixels)
    private float x;
    private float y;
    // velocity (pixels per millisecond)
    protected float dx;
    protected float dy;
    
    protected float currentRotation = 0;
    protected float futureRotation = 0;
    protected float rotationSpeed = SPEED_ROTATION;

    /**
        Creates a new Sprite object with the specified Animation.
    */
    public Sprite(Animation anim) {
        this.anim = anim;
    }

    /**
        Updates this Sprite's Animation and its position based
        on the velocity.
    */
    public void update(long elapsedTime) {
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        
        anim.update(elapsedTime);
    }

    /**
        Gets this Sprite's current x position.
    */
    public float getX() {
        return x;
    }

    /**
        Gets this Sprite's current y position.
    */
    public float getY() {
        return y;
    }

    /**
        Sets this Sprite's current x position.
    */
    public void setX(float x) {
        this.x = x;
    }

    /**
        Sets this Sprite's current y position.
    */
    public void setY(float y) {
        this.y = y;
    }

    /**
        Gets this Sprite's width, based on the size of the
        current image.
    */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
        Gets this Sprite's height, based on the size of the
        current image.
    */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
        Gets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityX() {
        return dx;
    }

    /**
        Gets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityY() {
        return dy;
    }

    /**
        Sets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
        Sets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
        Gets this Sprite's current image.
    */
    public Image getImage() {
        return anim.getImage();
    }

    /**
        Clones this Sprite. Does not clone position or velocity
        info.
    */
    public Object clone() {
        return new Sprite(anim);
    }

	public double getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public float getRotation() {
		if(currentRotation < 0){
			currentRotation = 360 - currentRotation;
		}
		
		currentRotation %= 360;
		return currentRotation;
	}

	public void setRotation(float rotation) {
		if(rotation < 0){
			rotation = 360 + rotation;
		}
		//System.out.println("Set Rot: " + rotation);
		this.currentRotation = rotation;
	}

	public float getFutureRotation() {
		return futureRotation;
	}

	public void setFutureRotation(float toRotation) {
		if(toRotation < 0){
			toRotation = 360 + toRotation;
		}
		//System.out.println("Set to Rotation: " + toRotation);
		this.futureRotation = toRotation;
	}
	
	
}
