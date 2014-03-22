package spacegame.tilegame.sprites;

import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import spacegame.graphics.Sprite;

public class Laser extends Sprite{
	public Ship parent;
	Line2D line;
	//Rect2D	rect;
	public double power;
	public double width;
	public Color color;
	
	public Laser(double x1, double y1, double x2, double y2, Ship player){
		super(null);
		line = new Line2D.Double(x1, y1, x2, y2);
		parent = player;
		power = parent.power;
		width = mapPowerToWidth(power);
		float red = (float) Ship.map(power, 0, 1000, 0, 1);
		float blue = (float) Ship.map(power, 0, 1000, 1, 0);
		float green = (float) Ship.map(power, 0, 1000, 1, 0);
		System.out.println("Color: " + red + ":" + blue);
		color = new Color(red,blue,green);
	}
	
	private double mapPowerToWidth(double pow){
		return Ship.map(pow, 0, 1000, 1, 8);
		//return pow;
		
	}
	
	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public void setParent(Ship parent) {
		this.parent = parent;
	}

	public void setLine(Line2D line) {
		this.line = line;
	}

	public Line2D getLine(){
		return line;
	}
	
	public Ship getParent(){
		return parent;
	}

}
