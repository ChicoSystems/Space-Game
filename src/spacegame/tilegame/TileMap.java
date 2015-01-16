package spacegame.tilegame;

import java.awt.Image;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;

import spacegame.graphics.Sprite;
import spacegame.graphics.SpriteV2;
import spacegame.tilegame.sprites.Laser;
import spacegame.tilegame.sprites.Planet;
import spacegame.tilegame.sprites.Player;
import spacegame.tilegame.sprites.Ship;
import spacegame.tilegame.sprites.Turret;


/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Of course, Images are used multiple times in the tile
    map.
*/
public class TileMap {

	ResourceManager parent;
    private Image[][] tiles;
    private LinkedList sprites;
    private ArrayList <Laser> lasers;
    private SpriteV2 player;
    private ArrayList <Ship> aiShips;
    private ArrayList <SpriteV2> spritev2;

    public ArrayList<SpriteV2> getSpriteV2() {
		return spritev2;
	}
	
	public void addSpriteV2(SpriteV2 s){
		spritev2.add(s);
	}


	/**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(ResourceManager p, int width, int height) {
    	parent = p;
        tiles = new Image[width][height];
        sprites = new LinkedList();
        lasers = new ArrayList<Laser>();
        aiShips = new ArrayList<Ship>();
        spritev2 = new ArrayList<SpriteV2>();
        
        //createRandomPlanets(sprites, 1000);
    }


    /**
        Gets the width of this TileMap (number of tiles across).
    */
    public int getWidth() {
        return tiles.length;
    }


    /**
        Gets the height of this TileMap (number of tiles down).
    */
    public int getHeight() {
        return tiles[0].length;
    }


    /**
        Gets the tile at the specified location. Returns null if
        no tile is at the location or if the location is out of
        bounds.
    */
    public Image getTile(int x, int y) {
        if (x < 0 || x >= getWidth() ||
            y < 0 || y >= getHeight())
        {
            return null;
        }
        else {
            return tiles[x][y];
        }
    }


    /**
        Sets the tile at the specified location.
    */
    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }


    /**
        Gets the player Sprite.
    */
    public SpriteV2 getPlayer() {
        return player;
    }


    /**
        Sets the player Sprite.
    */
    public void setPlayer(SpriteV2 player) {
        this.player = player;
    }
    
    /**
	    Gets the player2 Sprite.
	*/
	public ArrayList<Ship> getAIShips() {
	    return aiShips;
	}
	
	
	/**
	    Sets the player Sprite.
	*/
	public void setAIShips(ArrayList<Ship>newList) {
	    aiShips = newList;
	}


    /**
        Adds a Sprite object to this map.
    */
    public synchronized void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }


    /**
        Removes a Sprite object from this map.
    */
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }


    /**
        Gets an Iterator of all the Sprites in this map,
        excluding the player Sprite.
    */
    public synchronized LinkedList<Sprite> getSprites() {
        return sprites;
    }
    
    public synchronized Iterator getSpritesIterator(){
    	return sprites.iterator();
    }
    
    /**
    Gets an Iterator of all the Sprites in this map,
    excluding the player Sprite.
	*/
	public Iterator getLasers() {
	    return lasers.iterator();
	}
	
	/**
	 * Gets a laser from the list, referenced by lasers parent.
	 * @param laserParent The parent object. (Turret or Ship probably).
	 * @return The laser we are looking for.
	 */
	public Laser getLaser(Sprite laserParent){
		for(int i = 0; i < lasers.size(); i++){
			Sprite p = (Sprite) lasers.get(i).parent;
			if(p == laserParent){
				return (Laser)lasers.get(i);
			}
		}
		return null;
	}
    
    public void addLaser(float f, float g, float h, float i, Object player){
    	//Line2D laser = new Line2D.Double(f, g, h, i);
    	Laser laser = new Laser(f, g, h, i, player);
    	lasers.add(laser);
    }
    
    public void addLaser(Laser l){
    	lasers.add(l);
    }
    
    public void removeLaser(Object player){
    	for(int i = 0; i < lasers.size(); i++){
    		if(lasers.get(i).getParent() == player){
    			lasers.remove(i);
    		}
    	}
    } 
    
    public boolean laserExists(Object player){
    	boolean result = false;
    	for(int i = 0; i < lasers.size(); i++){
    		if(lasers.get(i).getParent() == player){
    			result = true;
    		}
    	}
    	return result;
    }
    
    public static void createRandomPlanets(ResourceManager rm, TileMap t, int numPlanets){
    	int mapWidth = t.getWidth();
    	int mapHeight = t.getWidth();
    	Random r = new Random();
    	int tileFromEdge = 5;
  
    	for(int n = 0; n < numPlanets; n++){
    		int newX = r.nextInt((mapWidth-tileFromEdge)-tileFromEdge)+tileFromEdge;
    		int newY = r.nextInt((mapHeight-tileFromEdge)-tileFromEdge)+tileFromEdge;
    		 // center the sprite
    		
    		Planet newP = (Planet)rm.planetSprites.get(r.nextInt(rm.planetImages.size()-1)+1).clone();
    		
            newP.setX(
                TileMapRenderer.tilesToPixels(newX) +
                (TileMapRenderer.tilesToPixels(1) -
                		newP.getWidth()) / 2);
            
            newP.setY(
                    TileMapRenderer.tilesToPixels(newY) +
                    (TileMapRenderer.tilesToPixels(1) -
                    		newP.getHeight()) / 2);
            
            newP.setRandomTotalPower();
            newP.circle.setFrame(newP.circle.getX(), newP.circle.getY(), newP.totalPower()/Planet.POWER_TO_SIZE, newP.totalPower()/Planet.POWER_TO_SIZE);
            
    		if(canPlacePlanet(newP, t.getSprites())){
    			t.addSprite(newP);
    		}else{
    			createRandomPlanets(rm, t, 1);
    			//can't add this sprite at this location, so try again with only one sprite.
    		}
    	}
    	//System.out.println("here");
    }
    
    public static boolean canPlacePlanet(Planet p, LinkedList<Sprite>sprites){
    	boolean returnVal = true;
    	if(sprites.isEmpty()){
    		returnVal = true;
    	}else{
    		for(int i = 0; i < sprites.size(); i++){
        		if(distanceBetween(p, sprites.get(i)) < 1000){
        			returnVal = false;
        		}
        	}
    	}
    	
    	return returnVal;
    }
    
    /**
	 * Finds the distance between a turret and another object.
	 * May need to refactor this now that all object are sprites.
	 * @param t
	 * @param o2
	 * @return
	 */
	public static float distanceBetween(Sprite s1, Sprite o2){
		float x1, x2, y1, y2;
		x1 = s1.getX();
		y1 = s1.getY();
		
		if(o2 instanceof Ship){
			Ship s = (Ship)o2;
			x2 = s.getX();
			y2 = s.getY();
		}else{
			Sprite s = (Sprite)o2;
			x2 = s.getX();
			y2 = s.getY();
		}
		return distanceBetween(x1, y1, x2, y2);
	}
	
	public static float distanceBetween(float x1, float y1, float x2, float y2){
		
				float distance = (float) Math.sqrt(((x2-x1)*(x2-x1)) + ((y2-y1)*(y2-y1)));
				return distance;
	}


	

}
