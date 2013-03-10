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
    private static final int DIE_TIME = 1000;

    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;

    private Animation left;
    private Animation right;
    private Animation deadLeft;
    private Animation deadRight;
    private int state;
    private long stateTime;
    private double rotation;

    /**
        Creates a new Creature with the specified Animations.
    */
    public Creature(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(right);
        this.left = left;
        this.right = right;
        this.deadLeft = deadLeft;
        this.deadRight = deadRight;
        state = STATE_NORMAL;
    }


    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)left.clone(),
                (Animation)right.clone(),
                (Animation)deadLeft.clone(),
                (Animation)deadRight.clone()
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
        return 0;
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
    
    public double getRotation(){
    	return rotation;
    }
    
    public void setRotation(double rot){
    	rotation = rot;
    }
    
    /*
    public Animation rotateAnimation(Animation anim, double rot){
    	System.out.println("Rotating: " + rot);
    	Animation newAnim = (Animation) anim.clone();
    	ArrayList <AnimFrame> oldFrames = anim.getFrames();
    	ArrayList <AnimFrame> newFrames = new ArrayList<AnimFrame>();
    	
    	for(int i = 0; i < oldFrames.size(); i++){
    		AnimFrame oldAnimFrame = oldFrames.get(i);
	    		Image oldImage = oldFrames.get(i).image;
	    		Image newImage = rotateImage(oldImage, rot);
    		AnimFrame newAnimFrame = new AnimFrame(newImage, oldAnimFrame.endTime);
    		newFrames.add(newAnimFrame);
    	}
    	newAnim.setFrames(newFrames);
    	return newAnim;
    }
    
    public Image rotateImage(Image img, double rot) {
    	BufferedImage rotateImage = (BufferedImage)img;
    		
    	AffineTransformOp op = null;
    	 
    	try {
    	 
    	   AffineTransform tx = new AffineTransform();
    	 
    	   //Rotate 90º
    	   tx.rotate(Math.toRadians(rot), rotateImage.getWidth(null)
    	              / 2.0, rotateImage.getHeight(null) / 2.0);
    	 
    	   AffineTransform translationTransform;
    	   translationTransform = findTranslation(tx, rotateImage);
    	 
    	   tx.preConcatenate(translationTransform);
    	 
    	   op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
    	 
    	   } catch (Exception e) {
    	      //Do something here
    	   }
    		
    	
    	   return (Image)rotateImage;
    	}
    
    private AffineTransform findTranslation(AffineTransform at, BufferedImage bi) {
    	   Point2D p2din, p2dout;
    	 
    	   p2din = new Point2D.Double(0.0, 0.0);
    	   p2dout = at.transform(p2din, null);
    	   double ytrans = p2dout.getY();
    	 
    	   p2din = new Point2D.Double(0, bi.getHeight());
    	   p2dout = at.transform(p2din, null);
    	   double xtrans = p2dout.getX();
    	 
    	   AffineTransform tat = new AffineTransform();
    	   tat.translate(-xtrans, -ytrans);
    	 
    	   return tat;
    	}
    
    */
    


    /**
        Updates the animaton for this creature.
    */
    public void update(long elapsedTime) {
    	rotation = Math.atan2(this.getVelocityX(), this.getVelocityY());
    	
        // select the correct Animation
        Animation newAnim = anim;
        //newAnim = rotateAnimation((Animation)left, rotation);

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
