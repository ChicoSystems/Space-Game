package spacegame.tilegame.sprites;

import java.awt.geom.Ellipse2D;
import java.util.Random;

import spacegame.graphics.Animation;

/**
    The Player.
*/
public class Planet extends Creature {
	
	String name;
	double totalPower;
	int planetType;
	long lastCollideTime;
	
	public Ellipse2D circle;
	
	public Planet(Animation[] anim){
    	super(anim);
    	this.planetType = 0;
    	totalPower = 0;
    	setLastCollideTime(0);
    	circle = new Ellipse2D.Double(this.getX(), this.getY(), this.getWidth(), this.getHeight());
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
		int randomNumber = random.nextInt(3000000 - 100000) + 100000;
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
		circle.setFrame(this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	

    public void wakeUp() {
        // do nothing
    }
}
