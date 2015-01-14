package spacegame.graphics;
import spacegame.tilegame.ResourceManager;
import spacegame.util.Vector2D;
import java.awt.Image;

public class Sprite {
    protected Animation anim;
   
    public Vector2D velocity;
    protected Vector2D position;
    
    // Normalized Vector pointing in the direction of the heading.
    public Vector2D heading;
    public Vector2D oldheading;
    protected ResourceManager parent;
    
    //A vector perpendicular to the heading vector.
    public Vector2D side;
    
    //A class to keep track of steering behaviours.
    public SteeringBehaviors steering;
 
    public double dMass = .5;
    public double dMaxSpeed = 2;
    public double dMaxForce = .0001;
    public double dMaxTurnRate = .05;
    
    protected float currentRotation = 0;
    protected float futureRotation = 0;
    public float rotationSpeed = (float) dMaxTurnRate;

    /**
        Creates a new Sprite object with the specified Animation.
    */
    public Sprite(Animation anim, ResourceManager parent) {
        this.anim = anim;
        this.parent = parent;
        velocity = new Vector2D(0, 0);
        position = new Vector2D(0, 0);
        heading = new Vector2D(1, 0);
        oldheading = new Vector2D(0,0);
        side = new Vector2D(0,0);
        steering = new SteeringBehaviors(this);
    }

    /**
        Updates this Sprite's Animation and its position based
        on the velocity.
    */
    public void update(long elapsedTime) {
    	/*
        //x += dx * elapsedTime;
        //y += dy * elapsedTime;
    	
    	//calculate the current steering force
    	Vector2D steeringForce = steering.calculate(velocity);//steering.calculate();
    	
    	//Acceleration = Force / Mass
    	Vector2D acceleration = steeringForce.scalarDiv(dMass);
    	
    	//update velocity
    	velocity = velocity.plus(acceleration.scalarMult(elapsedTime));
    	
    	//make sure we do not exceed max speeds
        velocity.truncate(dMaxSpeed);
        
        //update the position
        position = position.plus(velocity.scalarMult(elapsedTime));
        
        // update the heading if the vehicle has a small velocity, but not too small
        //if(velocity.length() > 0.00001){
        //	heading = velocity.unitVector();
        //	side = heading.perp();
        //}
    	*/
        anim.update(elapsedTime);
    }
    
    public Vector2D getPosition(){
    	return position;
    }
    
    public Vector2D getVelocity(){
    	return velocity;
    }
    
    public void setVelocity(Vector2D vel){
    	velocity = vel;
    }
    
	/**
	Gets the maximum speed of this Creature.
	*/
	public float getMaxSpeed() {
	   //return maxSpeed;
		return (float) this.dMaxSpeed;
	}

    /**
        Gets this Sprite's current x position.
    */
    public float getX() {
        //return x;
    	return (float) position.x;
    }

    /**
        Gets this Sprite's current y position.
    */
    public float getY() {
    	return (float) position.y;
    }

    /**
        Sets this Sprite's current x position.
    */
    public void setX(float x) {
        //this.x = x;
        position.x = x;
    }

    /**
        Sets this Sprite's current y position.
    */
    public void setY(float y) {
        //this.y = y;
    	position.y = y;
    }

    /**
        Gets this Sprite's width, based on the size of the
        current image.
    */
    public float getWidth() {
        return anim.getImage().getWidth(null);
    }
    
    public void setWidth(int w){
    	if(w == 0) w = 1;
    	//System.out.println("Setting Width: " + w);
    	for(int i = 0; i < anim.getFrames().size(); i ++){
    		Image image = ((AnimFrame)anim.getFrames().get(i)).image;
    		image = image.getScaledInstance(w, image.getHeight(null), 0);
    		((AnimFrame)anim.getFrames().get(i)).image = image;
    	}
    	//anim.setImage(anim.getImage().getScaledInstance(w, anim.getImage().getHeight(null), 0));
    }
    
    public void setHeight(int h){
    	for(int i = 0; i < anim.getFrames().size(); i ++){
    		Image image = ((AnimFrame)anim.getFrames().get(i)).image;
    		image = image.getScaledInstance(image.getWidth(null), h, 0);
    		((AnimFrame)anim.getFrames().get(i)).image = image;
    	}
    }

    /**
        Gets this Sprite's height, based on the size of the
        current image.
    */
    public float getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
        Gets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityX() {
        //return dx;
    	return (float) velocity.x;
    }

    /**
        Gets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityY() {
       // return dy;
    	return (float) velocity.y;
    }

    /**
        Sets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityX(float dx) {
        //this.dx = dx;
    	velocity.x = dx;
    }

    /**
        Sets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityY(float dy) {
        //this.dy = dy;
    	velocity.y = dy;
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
    public Object clone(ResourceManager p) {
        return new Sprite((Animation)anim.clone(), p);
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
	
	/*
	public void updateHeading(long elapsedTime, Vector2D B){
		double targetAngle = Math.atan2(B.y - position.y, B.x - position.x);
		double currentAngle = heading.getTheta();

		//Calculate angular deviation
		double deviation = targetAngle - currentAngle;
		double absDeviation = (deviation < 0 ? -deviation : deviation);

		//double maxRotation = 5 * elapsedTime;

		//If the absolute deviation is less than max rotation, rotate by.
		//Else rotate the allowed radians in B's direction.
		if(absDeviation <= dMaxTurnRate)
		    heading = heading.rotate(deviation);
		else
		    heading = heading.rotate(Math.signum(deviation) * dMaxTurnRate);
	}
	
	*/
	
	/*
	public void updateRotation(long elapsedTime){
    	
   	 //float rotation = (float) Math.atan2(velocity.y, velocity.x);
		float rotation = (float) heading.getTheta();
        rotation = (float) Math.toDegrees(rotation);
        rotation = rotation + 90;
        
        
        //if((velocity.x == 0) && (velocity.y == 0)){
        //	rotation = this.getRotation(); // keeps from reseting rotation when velocity is 0
        //}
        
        //if(velocity.length() < .0000001){
        //	rotation = this.getRotation(); // keeps from reseting rotation when velocity is 0
        //}
        	this.setFutureRotation(rotation);
        	
		
		//System.out.println("Current: " + currentRotation + " Future: " + futureRotation);
		if(Math.abs(getRotation() - getFutureRotation()) < 2){//don't rotate if change is less then 2 degrees
			rotationSpeed = 0;
			return;
		}else{
			float rotationChange = calculateDifferenceBetweenAngles(getRotation(), getFutureRotation());
			//System.out.println("Rot Change: " + getFutureRotation() + " : " + getRotation() + " : " +rotationChange + " : " + rotationSpeed);
			if(rotationChange <= 0){
				setRotationSpeed((float) -dMaxTurnRate);
			}else{
				setRotationSpeed((float) dMaxTurnRate);
			}
			//System.out.println("Set Rot: " + ((float)(getRotation() + getRotationSpeed() * elapsedTime)));
			setRotation((float)(getRotation() + getRotationSpeed() * elapsedTime));
			//setRotation((float) heading.perp().getTheta());
			//p.setRotation((float) player.heading.perp().getTheta());
			
		}
		
	}
	
	*/
   
   private float calculateDifferenceBetweenAngles(float firstAngle, float secondAngle)
   {
   	float difference = secondAngle - firstAngle;
         while (difference < -180) difference += 360;
         while (difference > 180) difference -= 360;
         return difference;
  }
	
	
}
