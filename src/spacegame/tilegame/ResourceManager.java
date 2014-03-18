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
    
    //Planet Images
    private ArrayList planetImages;
    public ArrayList <Sprite> planetSprites;
    
    private ArrayList <ArrayList> rocketImages;
    public ArrayList rocketSprites;
    
    
    private int currentMap;
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
    
    //Planet Images
    Image[] planets = new Image[76];
    
 // create creature animations
    Animation[] playerAnim = new Animation[360];
    Animation[] flyAnim = new Animation[360];
    Animation[] grubAnim = new Animation[360];
    Animation[] planetAnim = new Animation[360];

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc) {
        this.gc = gc;
        loadSprites();
        loadTileImages();
        
        
    }
    
    public void loadSprites(){
    	
    	loadRocketImages();
    	loadRocketSprites();
        loadPlanetImages();
        loadPlanetSprites();
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

    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }

    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }
    
    public Image getSmallerImage(Image image , float scale){
    	
    	float f_invScale = 1/scale;
    	int invScale = (int) f_invScale;
    	AffineTransform transform = new AffineTransform();
    	transform.scale(scale, scale);
    	//transform.translate(.5, .5);
    	
    	 // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null)/invScale,
            image.getHeight(null)/invScale,
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();
        return newImage;
    }
    
    public Animation rotateAnimation(Animation anim, double rot){
    	//System.out.println("Rotating: " + rot);
    	Animation newAnim = (Animation) anim.clone();
    	ArrayList <AnimFrame> oldFrames = (ArrayList<AnimFrame>) newAnim.getFrames().clone();
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
    
    
    public Image rotateImage(Image im, double angle) {
    	BufferedImage image = (BufferedImage)im;
    	
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int)Math.floor(h*cos+w*sin);
       // GraphicsConfiguration gc = gc.getDefaultConfiguration();
        
        BufferedImage result = gc.createCompatibleImage(neww, newh, Transparency.BITMASK);
        Graphics2D g = result.createGraphics();
        g.translate((neww-w)/2, (newh-h)/2);
        g.rotate(angle, w/2, h/2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }
    
    /*
    public Image rotateImage(Image image, double rot) {
    	// set up the transform
        AffineTransform transform = new AffineTransform();
        
        transform.rotate(rot, image.getWidth(null)/2, image.getWidth(null)/2);
       
        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.OPAQUE);
        
        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    	}*/
    
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
                else if (ch == 'p') {
                    addSprite(newMap, planetSprites.get(22), x, y);
                }
            }
        }

        // add the player to the map
        //Sprite player = (Sprite)playerSprite.clone();
        Ship player = new Ship();
        player.setX(TileMapRenderer.tilesToPixels(250));
        player.setY(TileMapRenderer.tilesToPixels(200));
        newMap.setPlayer(player);
        System.out.println("Add Player: " + player.toString() + " " + player.getWidth() + " " + player.getHeight());

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
           
            tiles.add(getSmallerImage(loadImage(name), .5f));
            ch++;
        }
    }
    
    public void loadRocketImages(){// loads rocket images into a 2d arraylist 
    	rocketImages = new ArrayList();
    	
    	int i = 1;
    	int j = 1;
    	
    	while(true){ //cycle through i
    		ArrayList r_img = new ArrayList();
    		while(true){ //cycle through j
	    		String name = "rockets/" + i + "r" + j + ".png";
	    		File file = new File("images/" + name);
	    		if(!file.exists()){
	    			j = 1;
	    			break;
	    		}
	    		//rocketImages.add(getSmallerImage(loadImage(name), .5f));
	    		r_img.add(getSmallerImage(loadImage(name), 1f));
	    		j++;
	    	}// end j loop
    		i++;
    		rocketImages.add(r_img); // add arraylist of images to arraylist of rockets
    		String name = "rockets/" + i + "r" + j + ".png";
    		File file = new File("images/" + name);
    		if(!file.exists()){
    			break;
    		}
    		
    	}// end i loop
	    	
    }
    
    public void loadPlanetImages(){
    	planetImages = new ArrayList();
    	int i = 1;
    	while(true){
    		String name = "planets/p_" + i + ".png";
    		File file = new File("images/" + name);
    		if(!file.exists()){
    			break;
    		}
    		//System.out.println(i + " : " + file);
    		planetImages.add(getSmallerImage(loadImage(name), .5f));
    		i++;
    	}
    }
    
    public void loadRocketSprites(){
    	rocketSprites = new ArrayList();
    	ArrayList anims = new ArrayList();
    	for(int i = 0; i < rocketImages.size(); i++){
    		Animation a = new Animation();
    		for(int j = 0; j < rocketImages.get(i).size(); j++){
    			a.addFrame((Image) rocketImages.get(i).get(j), 200);
    		}
    		Animation[] a_array = new Animation[360];
    		 for(int j = 0; j < 360; j++){
    	        	a_array[j] = rotateAnimation(a, Math.toRadians(j+1));
    	        }
    		Sprite s = new Projectile(a_array, 0);
    		rocketSprites.add(s);
    	}
    }
    
    public void loadPlanetSprites(){
    	planetSprites = new ArrayList();
    	ArrayList anims = new ArrayList();
    	for(int i = 0; i < planetImages.size(); i++){
    		Animation a = createPlanetAnim((Image)planetImages.get(i));
    		anims.add(a);
    	}
    	
    	for(int i = 0; i < planetImages.size(); i++){
    		Animation[] animation = new Animation[1];
    		animation[0] = (Animation) anims.get(i);
    		Sprite s = new Planet(animation);
    		System.out.println(i);
    		planetSprites.add(s);
    	}
    }


    public void loadCreatureSprites() {

        Image[][] images = new Image[360][];

        // load left-facing images
        images[0] = new Image[] {
        		getSmallerImage(loadImage("2ship1.png"), .5f),
        		getSmallerImage(loadImage("2ship2.png"), .5f),
        		getSmallerImage(loadImage("2ship3.png"), .5f),
        		getSmallerImage(loadImage("2ship4.png"), .5f),
        		getSmallerImage(loadImage("fly1.png"), .5f),
        		getSmallerImage(loadImage("fly2.png"), .5f),
        		getSmallerImage(loadImage("fly3.png"), .5f),
        		getSmallerImage(loadImage("grub1.png"), .5f),
        		getSmallerImage(loadImage("grub2.png"), .5f),
        		getSmallerImage(loadImage("star1.png"), .5f),
        };

        for(int i = 1; i < 360; i++){
        	images[i] = new Image[images[0].length];
        }
        
        for(int j = 0; j < 360; j++){
        	for(int i = 0; i<images[j].length; i++){
        		//System.out.println("j: " + j + " i: " + i);
        		
        		images[j][i] = rotateImage(images[0][i], Math.toRadians(j+1));
        	}
        }

        
        for (int j=0; j<360; j++) {
            playerAnim[j] = createPlayerAnim(
                images[j][0], images[j][1], images[j][2], images[j][3]);
            
            flyAnim[j] = createFlyAnim(
                images[j][4], images[j][5], images[j][6]);
            
            grubAnim[j] = createGrubAnim(
                images[j][7], images[j][8]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim);
        
        flySprite = new Fly(flyAnim);
        
        grubSprite = new Grub(grubAnim);
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
    
    private Animation createPlanetAnim(Image planet)
        {
            Animation anim = new Animation();
            anim.addFrame(planet, 250);
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
        anim.addFrame(getSmallerImage(loadImage("heart1.png"), .5f), 150);
        anim.addFrame(getSmallerImage(loadImage("heart2.png"), .5f), 150);
        anim.addFrame(getSmallerImage(loadImage("heart3.png"), .5f), 150);
        anim.addFrame(getSmallerImage(loadImage("heart2.png"), .5f), 150);
        goalSprite = new PowerUp.Goal(anim);

        // create "star" sprite
        anim = new Animation();
        anim.addFrame(getSmallerImage(loadImage("star1.png"), .5f), 100);
        anim.addFrame(getSmallerImage(loadImage("star2.png"), .5f), 100);
        anim.addFrame(getSmallerImage(loadImage("star3.png"), .5f), 100);
        anim.addFrame(getSmallerImage(loadImage("star4.png"), .5f), 100);
        coinSprite = new PowerUp.Star(anim);

        // create "music" sprite
        anim = new Animation();
        anim.addFrame(getSmallerImage(loadImage("music1.png"), .5f), 150);
        anim.addFrame(getSmallerImage(loadImage("music2.png"), .5f), 150);
        anim.addFrame(getSmallerImage(loadImage("music3.png"), .5f), 150);
        anim.addFrame(getSmallerImage(loadImage("music2.png"), .5f), 150);
        musicSprite = new PowerUp.Music(anim);
    }

}
