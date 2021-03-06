package spacegame.tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import spacegame.graphics.Sprite;
import spacegame.graphics.SpriteV2;
import spacegame.tilegame.sprites.Creature;
import spacegame.tilegame.sprites.Laser;
import spacegame.tilegame.sprites.Planet;
import spacegame.tilegame.sprites.Ship;
import spacegame.tilegame.sprites.ShipV2;
import spacegame.tilegame.sprites.Turret;


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
    
   public int offX = 0, offY = 0;

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
        //Ship player = map.getPlayer(); //former way to get player
       // Ship player2 = map.getPlayer2();
        
        //test drawing spritev2
        //SpriteV2 spriteV2 = map.getSpriteV2().get(0);
    	SpriteV2 player = map.getPlayer();
    	
    	
        
        int mapWidth = tilesToPixels(map.getWidth());
        int mapHeight = tilesToPixels(map.getHeight());

        // get the scrolling position of the map
        // based on player's position
        int offsetX = (int) (screenWidth / 2 -
            Math.round(player.getPosition().x) - TILE_SIZE);
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidth);

        // get the y offset to draw all sprites and tiles
        int offsetY = (int) (screenHeight / 2 -
                Math.round(player.getPosition().y) - TILE_SIZE);
            offsetY = Math.min(offsetY, 0);
            offsetY = Math.max(offsetY, screenHeight - mapHeight);

            offX = offsetX;
            offY = offsetY;
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
        
     // Save the current transform of the graphics contexts.
	    AffineTransform saveTransform = g.getTransform();
	   // AffineTransform tx = new AffineTransform();

	    //tx.translate(10,10); //10, 10 is height and width of img divide by 2
	   // tx.translate((int)player.getPosition().x+offsetX, (int)player.getPosition().y+offsetY);
	    
	   // tx.rotate(player.getHeading().getTheta());
	   // tx.translate(player.getPosition().x, -player.getPosition().y);
	   // g.setTransform(tx);
	    //tx.translate(-10,-10); 

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
        
        drawLasers(g, map, offsetX, offsetY);
        // player.drawShip(g, offsetX, offsetY); // old way to draw the ship
        
        map.drawSprites(g, offsetX, offsetY);
        
        //Ship player2 = map.getAIShips().get(map.getAIShips().size()-1);
        //player2.drawShip(g, offsetX, offsetY);
        for(int i = 0; i < map.getAIShips().size(); i++){
        	Ship player2 = null;
        	player2 = map.getAIShips().get(i);
        	if(player2 != null){
            	player2.drawShip(g, offsetX, offsetY);
            }
        }
        
        
        
        // draw sprites
        LinkedList<Sprite>sprites = map.getSprites();
        for(int i = 0; i < sprites.size(); i++){
        	
            Sprite sprite = (Sprite)sprites.get(i);
            int x = Math.round(sprite.getX()) + offsetX;
            int y = Math.round(sprite.getY()) + offsetY;
            
          //if sprite is a planet, draw a string with the total power;
            if(sprite instanceof Planet){
            	Planet p = (Planet)sprite;
            	int sx = Math.round(sprite.getX()) + offsetX;
            	int sy = Math.round(sprite.getY()) + offsetY;
            	double newWidth = p.totalPower()/Planet.POWER_TO_SIZE+50;
        		double newHeight = p.totalPower()/Planet.POWER_TO_SIZE+50;
        		double centerX = p.circle.getBounds().x + offsetX;
        		double centerY = p.circle.getBounds().y + offsetY;
        		
        		DecimalFormat df = new DecimalFormat("#");
                String tPower = df.format(((Planet)sprite).totalPower());
            	g.drawString(tPower, sx-sprite.getWidth()/2, sy);
            	//g.drawArc((int)(centerX), (int)(centerY), (int)newWidth, (int)newHeight, 0, 360);
            	
            	
            	Color saveColor = g.getColor();
            	Color centerColor = new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), 130);
            	Color edgeColor = new Color(p.color.getRed(), p.color.getGreen(), p.color.getBlue(), 1);
            	double radius = newWidth;
               // double xP = //(p.getWidth() - radius) / 2;
               // double yP = //(p.getHeight() - radius) / 2;
                RadialGradientPaint rgp = new RadialGradientPaint(
                                new Point((int)(centerX+radius/2), (int)(centerY+radius/2)),
                                (float) radius,
                                new float[]{.01f, .5f},
                                new Color[]{centerColor, edgeColor}
                                );
                g.setPaint(rgp);
                g.fill(new Arc2D.Float((int)(centerX), (int)(centerY), (int)radius, (int)radius, 0, 360, Arc2D.PIE));
            	
            	
            	
            	g.setColor(p.color);
            	//g.fillArc((int)(centerX), (int)(centerY), (int)newWidth, (int)newHeight, 0, 360);
            	g.setColor(saveColor);
            }
            
            if(sprite instanceof Turret){
            	Turret t = (Turret)sprite;
            	int sx = Math.round(sprite.getX()) + offsetX;
            	int sy = Math.round(sprite.getY()) + offsetY;
            	double newWidth = t.getHitpoints()/t.LEVEL_TO_SIZE;
        		double newHeight = t.getHitpoints()/t.LEVEL_TO_SIZE;
        		double centerX = t.getCircle().getBounds().x + offsetX;
        		double centerY = t.getCircle().getBounds().y + offsetY;
        		Color saveColor = g.getColor();
            	g.setColor(t.bodyColor);
            	g.fillArc((int)(centerX), (int)(centerY), (int)newWidth, (int)newHeight, 0, 360);
            	g.setColor(saveColor);
            }
            
            g.drawImage(sprite.getImage(), x, y, null);

            // wake up the creature when it's on screen
            if (sprite instanceof Creature && x >= 0 && x < screenWidth){
                ((Creature)sprite).wakeUp();
            } 
        }
        g.setTransform(saveTransform);
        g.drawString("test", (int)player.getPosition().x+offsetX, (int)player.getPosition().y+offsetY);
        player.drawSprite(g, offsetX, offsetY);
    }
    
    private void drawLasers(Graphics2D g, TileMap map, int offsetX, int offsetY){
    	 // draw lasers
        Iterator l = map.getLasers();
        while(l.hasNext()){
        	Laser laser = (Laser)l.next();
        	Line2D line = laser.getLine();
        	
        	int x1 = (int)Math.round(line.getX1())+offsetX;
        	int y1 = (int)Math.round(line.getY1())+offsetY;
        	int x2 = (int) Math.round(line.getX2())+offsetX;
        	int y2 = (int) Math.round(line.getY2())+offsetY;
        	if(laser.parent instanceof Turret){
        		Turret turret = (Turret)laser.parent;
        		Sprite target = (Sprite) turret.getTarget();
        		if(target instanceof Ship){
        			Ship s = (Ship)target;
        			x2 = (int)Math.round(s.getX()-s.getWidth()/2)+offsetX;
        	        y2 = (int)Math.round(s.getY()-s.getHeight()/2)+offsetY;
        		}
        	}
        	//System.out.println("width: " + screenWidth + " height: "+ screenHeight);
        	Color origColor = g.getColor();
        	g.setColor(laser.color);
        	Stroke stroke = g.getStroke();
        	g.setStroke(new BasicStroke((float) laser.width));
        	g.drawLine(x1,  y1,  x2,  y2);
        	g.setStroke(stroke);
        	g.setColor(origColor);
        	
        }
    }

}
