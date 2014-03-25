package spacegame.tilegame;

import java.awt.Image;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;

import spacegame.graphics.Sprite;
import spacegame.tilegame.sprites.Laser;
import spacegame.tilegame.sprites.Planet;
import spacegame.tilegame.sprites.Player;
import spacegame.tilegame.sprites.Ship;


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
    private Ship player;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(ResourceManager p, int width, int height) {
    	parent = p;
        tiles = new Image[width][height];
        sprites = new LinkedList();
        lasers = new ArrayList<Laser>();
        
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
    public Ship getPlayer() {
        return player;
    }


    /**
        Sets the player Sprite.
    */
    public void setPlayer(Ship player) {
        this.player = player;
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
    public synchronized LinkedList getSprites() {
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
    
    public static void createRandomPlanets(Planet p, TileMap t, int numPlanets){
    	int mapWidth = t.getWidth();
    	int mapHeight = t.getWidth();
    	Random r = new Random();
    	int tileFromEdge = 25;
  
    	for(int n = 0; n < numPlanets; n++){
    		int newX = r.nextInt((mapWidth-tileFromEdge)-tileFromEdge)+tileFromEdge;
    		int newY = r.nextInt((mapHeight-tileFromEdge)-tileFromEdge)+tileFromEdge;
    		 // center the sprite
    		
    		Planet newP = (Planet)p.clone();
    		
            newP.setX(
                TileMapRenderer.tilesToPixels(newX) +
                (TileMapRenderer.tilesToPixels(1) -
                		newP.getWidth()) / 2);
            
            newP.setY(
                    TileMapRenderer.tilesToPixels(newY) +
                    (TileMapRenderer.tilesToPixels(1) -
                    		newP.getHeight()) / 2);
            
            newP.setRandomTotalPower();
            newP.circle.setFrame(newP.circle.getX(), newP.circle.getY(), newP.totalPower()/Planet.POWER_TO_SIZE, p.totalPower()/Planet.POWER_TO_SIZE);
            
    		
    		Iterator i = t.getSpritesIterator();
    		
    		if(canPlacePlanet(newP, i)){
    			t.addSprite(newP);
    		}else{
    			createRandomPlanets(p, t, 1);
    			//can't add this sprite at this location, so try again with only one sprite.
    		}
    	}
    	//System.out.println("here");
    }
    
    public static boolean canPlacePlanet(Planet p, Iterator i){
    	
    	return true;
    }

}
