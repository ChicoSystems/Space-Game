package spacegame.tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import spacegame.graphics.*;
import spacegame.tilegame.sprites.*;



/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class ResourceManager {

    private ArrayList tiles;
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
    }


    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
        String filename = "images/" + name;
        ImageIcon icon = new ImageIcon(filename);
        return icon.getImage();
       // return new ImageIcon(filename).getImage();
    }
    
    /*private Image resizeImage(Image originalImage, int img_width, int img_height)
		{
    		BufferedImage bi = new BufferedImage(originalImage.getWidth(null), originalImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
    		Graphics g = bi.createGraphics();
    		g.drawImage(originalImage, 0, 0, img_width, img_height, null);
    		ImageIcon newIcon = new ImageIcon(bi);
			g.dispose();
			
			return newIcon.getImage();
		}*/


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }
    
    public Image getHalfSizedImage(Image image){
    	AffineTransform transform = new AffineTransform();
    	transform.scale(.5, .5);
    	transform.translate(.5, .5);
    	
    	
    	 // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null)/2,
            image.getHeight(null)/2,
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();
        
        System.out.println("old: " + image.getWidth(null) + " : " + image.getHeight(null));
        System.out.println("new: " + newImage.getWidth(null) + " : " + newImage.getHeight(null));
        newImage = rotateImage(newImage, 95);
        return newImage;
    }
    
    
    public Animation rotateAnimation(Animation anim, double rot){
    	System.out.println("Rotating: " + rot);
    	Animation newAnim = (Animation) anim.clone();
    	ArrayList <AnimFrame> oldFrames = anim.getFrames();
    	ArrayList <AnimFrame> newFrames = new ArrayList<AnimFrame>();
    	
    	for(int i = 0; i < oldFrames.size(); i++){
    		AnimFrame oldAnimFrame = oldFrames.get(i);
	    		Image oldImage = oldFrames.get(i).image;
	    		Image newImage = rotateImage(oldImage, rot);
    		AnimFrame newAnimFrame = new AnimFrame(newImage, oldAnimFrame.endTime);
    		newFrames.add(newAnimFrame);
    	}
    	newAnim.setFrames(newFrames);
    	return newAnim;
    }
    
    public Image rotateImage(Image image, double rot) {
    	// set up the transform
        AffineTransform transform = new AffineTransform();
        transform.rotate(rot, image.getWidth(null)/2, image.getWidth(null)/2);
       

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    	}
    
    private AffineTransform findTranslation(AffineTransform at, BufferedImage bi) {
    	   Point2D p2din, p2dout;
    	 
    	   p2din = new Point2D.Double(0.0, 0.0);
    	   p2dout = at.transform(p2din, null);
    	   double ytrans = p2dout.getY();
    	 
    	   p2din = new Point2D.Double(0, bi.getHeight());
    	   p2dout = at.transform(p2din, null);
    	   double xtrans = p2dout.getX();
    	 
    	   AffineTransform tat = new AffineTransform();
    	   tat.translate(-xtrans, -ytrans);
    	 
    	   return tat;
    	}
    
    


    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public TileMap loadNextMap() {
        TileMap map = null;
        while (map == null) {
            currentMap++;
            try {
                map = loadMap(
                    "maps/map" + currentMap + ".txt");
            }
            catch (IOException ex) {
                if (currentMap == 1) {
                    // no maps to load!
                    return null;
                }
                currentMap = 0;
                map = null;
            }
        }

        return map;
    }


    public TileMap reloadMap() {
        try {
            return loadMap(
                "maps/map" + currentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


    private TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList lines = new ArrayList();
        int width = 0;
        int height = 0;

        // read every line in the text file into the list
        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                width = Math.max(width, line.length());
            }
        }

        // parse the lines to create a TileEngine
        height = lines.size();
        TileMap newMap = new TileMap(width, height);
        for (int y=0; y<height; y++) {
            String line = (String)lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // check if the char represents tile A, B, C etc.
                int tile = ch - 'A';
                if (tile >= 0 && tile < tiles.size()) {
                    newMap.setTile(x, y, (Image)tiles.get(tile));
                }

                // check if the char represents a sprite
                else if (ch == 'o') {
                    addSprite(newMap, coinSprite, x, y);
                }
                else if (ch == '!') {
                    addSprite(newMap, musicSprite, x, y);
                }
                else if (ch == '*') {
                    addSprite(newMap, goalSprite, x, y);
                }
                else if (ch == '1') {
                    addSprite(newMap, grubSprite, x, y);
                }
                else if (ch == '2') {
                    addSprite(newMap, flySprite, x, y);
                }
            }
        }

        // add the player to the map
        Sprite player = (Sprite)playerSprite.clone();
        player.setX(TileMapRenderer.tilesToPixels(3));
        player.setY(2);
        newMap.setPlayer(player);
        System.out.println(player.toString() + " " + player.getWidth() + " " + player.getHeight());

        return newMap;
    }


    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        tiles = new ArrayList();
        char ch = 'A';
        while (true) {
            String name = "tile_" + ch + ".png";
            File file = new File("images/" + name);
            if (!file.exists()) {
                break;
            }
           
            tiles.add(getHalfSizedImage(loadImage(name)));
            ch++;
        }
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];

        // load left-facing images
        images[0] = new Image[] {
        	getHalfSizedImage(loadImage("1ship1.png")),
        	getHalfSizedImage(loadImage("1ship2.png")),
        	getHalfSizedImage(loadImage("1ship3.png")),
        	getHalfSizedImage(loadImage("1ship4.png")),
        	getHalfSizedImage(loadImage("fly1.png")),
        	getHalfSizedImage(loadImage("fly2.png")),
        	getHalfSizedImage(loadImage("fly3.png")),
        	getHalfSizedImage(loadImage("grub1.png")),
        	getHalfSizedImage(loadImage("grub2.png")),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] flyAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2], images[i][3]);
            flyAnim[i] = createFlyAnim(
                images[i][4], images[i][5], images[i][6]);
            grubAnim[i] = createGrubAnim(
                images[i][7], images[i][8]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
            flyAnim[2], flyAnim[3]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3]);
    }


    private Animation createPlayerAnim(Image player1,
        Image player2, Image player3, Image player4)
    {
        Animation anim = new Animation();
        anim.addFrame(player1, 250);
        anim.addFrame(player2, 150);
        anim.addFrame(player1, 150);
        anim.addFrame(player2, 150);
        anim.addFrame(player3, 200);
        anim.addFrame(player2, 150);
        anim.addFrame(player4, 150);
        return anim;
    }


    private Animation createFlyAnim(Image img1, Image img2,
        Image img3)
    {
        Animation anim = new Animation();
        anim.addFrame(img1, 50);
        anim.addFrame(img2, 50);
        anim.addFrame(img3, 50);
        anim.addFrame(img2, 50);
        return anim;
    }


    private Animation createGrubAnim(Image img1, Image img2) {
        Animation anim = new Animation();
        anim.addFrame(img1, 250);
        anim.addFrame(img2, 250);
        return anim;
    }


    private void loadPowerUpSprites() {
        // create "goal" sprite
        Animation anim = new Animation();
        anim.addFrame(getHalfSizedImage(loadImage("heart1.png")), 150);
        anim.addFrame(getHalfSizedImage(loadImage("heart2.png")), 150);
        anim.addFrame(getHalfSizedImage(loadImage("heart3.png")), 150);
        anim.addFrame(getHalfSizedImage(loadImage("heart2.png")), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "star" sprite
        anim = new Animation();
        anim.addFrame(getHalfSizedImage(loadImage("star1.png")), 100);
        anim.addFrame(getHalfSizedImage(loadImage("star2.png")), 100);
        anim.addFrame(getHalfSizedImage(loadImage("star3.png")), 100);
        anim.addFrame(getHalfSizedImage(loadImage("star4.png")), 100);
        coinSprite = new PowerUp.Star(anim);

        // create "music" sprite
        anim = new Animation();
        anim.addFrame(getHalfSizedImage(loadImage("music1.png")), 150);
        anim.addFrame(getHalfSizedImage(loadImage("music2.png")), 150);
        anim.addFrame(getHalfSizedImage(loadImage("music3.png")), 150);
        anim.addFrame(getHalfSizedImage(loadImage("music2.png")), 150);
        musicSprite = new PowerUp.Music(anim);
    }

}
