package spacegame.input;

import java.util.LinkedList;

import spacegame.graphics.Sprite;
import spacegame.graphics.SpriteV2;
import spacegame.graphics.SteeringBehaviorsV2;
import spacegame.tilegame.sprites.Planet;
import spacegame.util.Vector2D;

/**
 * @author Isaac Assegai
 * Decides the players location based on the players input.
 * Uses steering to calculate the desired forces to add to
 * the players ship based on input
 */
public class PlayerLocManager implements LocationManager {
	SpriteV2 parent;
	SteeringBehaviorsV2 steering;
	public Vector2D inputVector; // Used to find players input
	public double inputTorque;
	
	public PlayerLocManager(SpriteV2 parent){
		this.parent = parent;
		steering = new SteeringBehaviorsV2(this.parent);
		inputVector = new Vector2D(0 ,0);
		inputTorque = 0;
	}

	public SpriteV2 getParent() {
		return parent;
	}

	public void setParent(SpriteV2 parent) {
		this.parent = parent;
	}

	public SteeringBehaviorsV2 getSteering() {
		return steering;
	}

	public void setSteering(SteeringBehaviorsV2 steering) {
		this.steering = steering;
	}

	public Vector2D calculate(double elapsedTime) {
		Vector2D returnVector = new Vector2D(inputVector.x, inputVector.y); 
		inputVector = new Vector2D(0,0);
		//System.out.println(inputVector.length());
		
		return returnVector;
	}
	
	public double calculateTorque(double elapsedTime){
		double returnTorque = inputTorque;
		inputTorque = 0;
		return returnTorque;
	}

	
	public void pressMoveRight() {
		//inputVector = new Vector2D(0, inputVector.y).plus(steering.pressMoveRight());
		inputVector = inputVector.plus(steering.pressMoveRight());
	}

	@Override
	public void pressMoveLeft() {
		//inputVector = new Vector2D(0, inputVector.y).plus(steering.pressMoveLeft());
		inputVector = inputVector.plus(steering.pressMoveLeft());
		//System.out.println("playerLocMan: pressMoveLeft() " + inputVector);
	}
	
	public void pressRotateRight(){
		inputTorque = inputTorque + steering.pressRotateRight();
	}
	
	public void pressRotateLeft(){
		inputTorque = inputTorque + steering.pressRotateLeft();
	}

	public void pressMoveForward() {
		inputVector = inputVector.minus(steering.pressMoveForward());
	}

	public void pressMoveBackward() {
		inputVector = inputVector.minus(steering.pressMoveBackward());
	}

	@Override
	public Vector2D calculateGravity(double elapsedTime) {
		LinkedList<Sprite>sprites = parent.getParent().parent.getMap().getSprites();
		Vector2D force = new Vector2D(0,0);
		for(int i = 0; i < sprites.size(); i++){ // Add all sprites that are planets
			if(sprites.get(i) instanceof Planet){
				Planet p = (Planet)sprites.get(i);
				double g = .08;
				double pMass = p.dMass;
				double sMass = parent.getMass();
				Vector2D pCenter = p.getPosition().plus(new Vector2D(p.getWidth()/2, p.getHeight()/2));
				Vector2D sCenter = parent.getPosition().plus(new Vector2D(parent.getWidth()/2, parent.getHeight()/2));
				double distanceSQ = sCenter.distanceSq(pCenter); //the new vector offsets the position so we orbit around the center of the planet
				double distance = sCenter.distance(pCenter);
				double pRadius = p.circle.getBounds().height/2;
				
				//this part allow the ship to settle into the planet and stop
				if(distance > 35 && distance < pRadius){ // don't calculate if too close, or too far
					Vector2D preForce = new Vector2D(0,0);
					preForce = (pCenter.minus(sCenter));
					preForce = preForce.scalarMult(g * pMass * sMass);
					preForce = preForce.scalarDiv(distanceSQ);
					force = force.plus(preForce);
				}else if(distance <= 9){
					parent.setVelocity(parent.getVelocity().scalarMult(-.5));
				}
			}
		}
		force = force.scalarMult(elapsedTime/1000);
		return force;
	}
}
