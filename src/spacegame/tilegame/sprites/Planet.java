package spacegame.tilegame.sprites;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.Random;

import spacegame.graphics.Animation;

/**
    The Player.
*/
public class Planet extends Creature {
	
	
	public static final int POWER_TO_SIZE = 1000;
	String name;
	double totalPower;
	int planetType;
	long lastCollideTime;
	public Color color;
	
	public Ellipse2D circle;
	
	public Planet(Animation[] anim){
    	super(anim);
    	this.planetType = 0;
    	totalPower = 0;
    	setLastCollideTime(0);
    	circle = new Ellipse2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
    	
    	Random random = new Random();
		int randomNumber1 = random.nextInt(255);
		int randomNumber2 = random.nextInt(255);
		int randomNumber3 = random.nextInt(255);
		int randomNumber4 = random.nextInt(128-64)+64;
		
    	color = new Color(randomNumber1, randomNumber2, randomNumber3, randomNumber4);
	}
	
	public long getLastCollideTime() {
		return lastCollideTime;
	}


	public void setLastCollideTime(long lastCollideTime) {
		this.lastCollideTime = lastCollideTime;
	}


    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setRandomTotalPower(){
		Random random = new Random();
		int randomNumber = random.nextInt(1000000 - 10000) + 10000;
		totalPower(randomNumber);
	}

	public double totalPower() {
		return totalPower;
	}

	public void totalPower(double totalPower) {
		this.totalPower = totalPower;
	}

	public int getPlanetType() {
		return planetType;
	}

	public void setPlanetType(int planetType) {
		this.planetType = planetType;
	}
	
	public void update(long elapsedTime){
		super.update(elapsedTime);
		if(this.totalPower <= 0)this.setState(STATE_DEAD);
		
		
		double newWidth = totalPower()/Planet.POWER_TO_SIZE+50;
		double newHeight = totalPower()/Planet.POWER_TO_SIZE+50;
		
		//circle.setFrame(this.getX()-(newWidth/2), this.getY()-(newHeight/2), newWidth, newHeight);
		circle.setFrame(new Rectangle((int)((getX()-newWidth/2)+getWidth()/2), 
									  (int)((getY()-newHeight/2)+getHeight()/2), 
									  (int)newWidth, 
									  (int)newHeight));
		
	}

    public void wakeUp() {
        // do nothing
    }
}
