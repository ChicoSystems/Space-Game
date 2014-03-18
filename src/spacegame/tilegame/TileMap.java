package spacegame.tilegame;

import java.awt.Image;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;

import spacegame.graphics.Sprite;
import spacegame.tilegame.sprites.Laser;
import spacegame.tilegame.sprites.Player;
import spacegame.tilegame.sprites.Ship;


/**
    The TileMap class contains the data for a tile-based
    map, including Sprites. Each tile is a reference to an
    Image. Of course, Images are used multiple times in the tile
    map.
*/
public class TileMap {

    private Image[][] tiles;
    private LinkedList sprites;
    private ArrayList <Laser> lasers;
    private Ship player;

    /**
        Creates a new TileMap with the specified width and
        height (in number of tiles) of the map.
    */
    public TileMap(int width, int height) {
        tiles = new Image[width][height];
        sprites = new LinkedList();
        lasers = new ArrayList<Laser>();
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
    public void addSprite(Sprite sprite) {
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
    public Iterator getSprites() {
        return sprites.iterator();
    }
    
    /**
    Gets an Iterator of all the Sprites in this map,
    excluding the player Sprite.
	*/
	public Iterator getLasers() {
	    return lasers.iterator();
	}
    
    public void addLaser(float f, float g, float h, float i, Ship player){
    	//Line2D laser = new Line2D.Double(f, g, h, i);
    	Laser laser = new Laser(f, g, h, i, player);
    	lasers.add(laser);
    }
    
    public void removeLaser(Ship player){
    	for(int i = 0; i < lasers.size(); i++){
    		if(lasers.get(i).getParent() == player){
    			lasers.remove(i);
    		}
    	}
    } 
    
    public boolean laserExists(Ship player){
    	boolean result = false;
    	for(int i = 0; i < lasers.size(); i++){
    		if(lasers.get(i).getParent() == player){
    			result = true;
    		}
    	}
    	return result;
    }

}
