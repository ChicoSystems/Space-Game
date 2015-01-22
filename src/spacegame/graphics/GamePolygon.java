package spacegame.graphics;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;

import spacegame.util.Vector2D;

public class GamePolygon{
	Vector2D position;
	ArrayList<Vector2D>poly;
	
	public GamePolygon(double x, double y){
		new GamePolygon(new Vector2D(x, y));
		
	}
	
	public GamePolygon(Vector2D pos) {
		position = pos;
		poly = new ArrayList<Vector2D>();
	}
	
	public void updatePosition(Vector2D newPosition){
		position = newPosition;
	}
	
	public void addPoint(double x, double y){
		poly.add(new Vector2D(x, y));
	}
	
	public void addPoint(Vector2D point){
		poly.add(point);
	}
	
	public void drawGamePolygon(Graphics2D g, int offsetX, int offsetY) {
		// TODO Auto-generated method stub
		if(poly.size() > 1){ // draw a polygon if there is more than one point
			for(int i = 0; i < poly.size(); i++){
				if(i == 0){
					// do nothing
				}else{
					int x1 = (int) (position.x+offsetX+poly.get(i-1).x+getWidth()/2);
					int y1 = (int) (position.y+offsetY+poly.get(i-1).y+getHeight()/2);
					int x2 = (int) (position.x+offsetX+poly.get(i).x+getWidth()/2);
					int y2 = (int) (position.y+offsetY+poly.get(i).y+getHeight()/2);
					g.drawLine(x1, y1, x2, y2);
				}
				
				if(i == poly.size()-1){ // draw the closing line
					int x1 = (int) (position.x+offsetX+poly.get(i).x+getWidth()/2);
					int y1 = (int) (position.y+offsetY+poly.get(i).y+getHeight()/2);
					int x2 = (int) (position.x+offsetX+poly.get(0).x+getWidth()/2);
					int y2 = (int) (position.y+offsetY+poly.get(0).y+getHeight()/2);
					g.drawLine(x1, y1, x2, y2);
				}
			}
		}else{//draw a point otherwise
			
		}
		
		
	}
	
	public double getWidth(){
		return 20;
	}
	
	public double getHeight(){
		return 20;
	}
	
	

}
