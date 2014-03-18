package spacegame.tilegame;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.Iterator;

import spacegame.graphics.Sprite;
import spacegame.tilegame.sprites.Creature;
import spacegame.tilegame.sprites.Laser;
import spacegame.tilegame.sprites.Ship;


/**
    The TileMapRenderer class draws a TileMap on the screen.
    It draws all tiles, sprites, and an optional background image
    centered around the position of the player.

    <p>If the width of background image is smaller the width of
    the tile map, the background image will appear to move
    slowly, creating a parallax background effect.

    <p>Also, three static methods are provided to convert pixels
    to tile positions, and vice-versa.

    <p>This TileMapRender uses a tile size of 64.
*/
public class TileMapRenderer {

    private static final int TILE_SIZE = 32;
    // the size in bits of the tile
    // Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
    private static final int TILE_SIZE_BITS = 5;

    private Image background;

    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(float pixels) {
        return pixelsToTiles(Math.round(pixels));
    }


    /**
        Converts a pixel position to a tile position.
    */
    public static int pixelsToTiles(int pixels) {
        // use shifting to get correct values for negative pixels
        return pixels >> TILE_SIZE_BITS;

        // or, for tile sizes that aren't a power of two,
        // use the floor function:
        //return (int)Math.floor((float)pixels / TILE_SIZE);
    }


    /**
        Converts a tile position to a pixel position.
    */
    public static int tilesToPixels(int numTiles) {
        // no real reason to use shifting here.
        // it's slighty faster, but doesn't add up to much
        // on modern processors.
        return numTiles << TILE_SIZE_BITS;

        // use this if the tile size isn't a power of 2:
        //return numTiles * TILE_SIZE;
    }


    /**
        Sets the background to draw.
    */
    public void setBackground(Image background) {
        this.background = background;
    }


    /**
        Draws the specified TileMap.
    */
    public void draw(Graphics2D g, TileMap map,
        int screenWidth, int screenHeight)
    {
        Ship player = map.getPlayer();
        int mapWidth = tilesToPixels(map.getWidth());
        int mapHeight = tilesToPixels(map.getHeight());

        // get the scrolling position of the map
        // based on player's position
        int offsetX = screenWidth / 2 -
            Math.round(player.getX()) - TILE_SIZE;
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);

        // get the y offset to draw all sprites and tiles
        int offsetY = screenHeight / 2 -
                Math.round(player.getY()) - TILE_SIZE;
            offsetY = Math.min(offsetY, 0);
            offsetY = Math.max(offsetY, screenHeight - mapHeight);

        // draw black background, if needed
        if (background == null ||
            screenHeight > background.getHeight(null))
        {
            g.setColor(Color.black);
            g.fillRect(0, 0, screenWidth, screenHeight);
        }

        // draw parallax background image
        if (background != null) {
            int x = offsetX *
                (screenWidth - background.getWidth(null)) /
                (screenWidth - mapWidth);
            int y = offsetY *
                    (screenHeight - background.getHeight(null)) /
                    (screenHeight - mapHeight);

            g.drawImage(background, x, y, null);
        }

        // draw the visible tiles
        int firstTileX = pixelsToTiles(-offsetX);
        //System.out.println(firstTileX);
        int lastTileX = firstTileX +
            pixelsToTiles(screenWidth) + 1;
        for (int y=0; y<map.getHeight(); y++) {
            for (int x=firstTileX; x <= lastTileX; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }
        
        int firstTileY = pixelsToTiles(-offsetY);
        int lastTileY = firstTileY +
            pixelsToTiles(screenHeight) + 1;
        for (int y=0; y<map.getWidth(); y++) {
            for (int x=firstTileY; x <= lastTileY; x++) {
                Image image = map.getTile(x, y);
                if (image != null) {
                    g.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }

        // draw player
        //g.drawImage(player.getImage(),
         //   Math.round(player.getX()) + offsetX,
         //   Math.round(player.getY()) + offsetY,
         //   null);
        player.drawShip(g, offsetX, offsetY);
        
        //draw ships
        //Iterator s = map.getShips();
       // while(s.hasNext()){
        //	Ship ship = (Ship)s.next();
        //	ship.drawShip(g);
        //}
        
        // draw lasers
        Iterator l = map.getLasers();
        while(l.hasNext()){
        	Laser laser = (Laser)l.next();
        	Line2D line = laser.getLine();
        	int x1 = (int)Math.round(line.getX1())+offsetX;
        	int y1 = (int)Math.round(line.getY1())+offsetY;
        	int x2 = (int) line.getX2()+offsetX;
        	int y2 = (int) line.getY2()+offsetY;
        	System.out.println("width: " + screenWidth + " height: "+ screenHeight);
        	Color origColor = g.getColor();
        	g.setColor(Color.YELLOW);
        	g.drawLine(x1,  y1,  x2,  y2);
        	g.setColor(origColor);
        	
        	
        }
        

        // draw sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            g.drawImage(sprite.getImage(), x, y, null);

            // wake up the creature when it's on screen
            if (sprite instanceof Creature &&
                x >= 0 && x < screenWidth)
            {
                ((Creature)sprite).wakeUp();
            }
        }
    }

}
