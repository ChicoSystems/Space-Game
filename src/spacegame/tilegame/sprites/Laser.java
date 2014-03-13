package spacegame.tilegame.sprites;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Laser{
	Player parent;
	Line2D line;
	
	public Laser(double x1, double y1, double x2, double y2, Player player){
		line = new Line2D.Double(x1, y1, x2, y2);
		parent = player;
	}
	
	public Line2D getLine(){
		return line;
	}


	

	

}
