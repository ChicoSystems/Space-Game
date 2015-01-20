package spacegame.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import spacegame.graphics.*;
import spacegame.input.*;
import spacegame.sound.*;
import spacegame.test.GameCore;
import spacegame.tilegame.sprites.*;
import spacegame.util.Vector2D;


/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {

    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int DRUM_TRACK = 1;

    public static final float GRAVITY = 0.002f;
    

    private Point pointCache = new Point();
    private TileMap map;
    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    public ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    public InputManager inputManager;
    public TileMapRenderer renderer;
    protected MainMenu menu;
    
    

    public GameAction moveUp;
    public GameAction moveDown;
    public GameAction moveLeft;
    public GameAction moveRight;
    public GameAction rotateLeft;
    public GameAction rotateRight;
    public GameAction moveUp2;
    public GameAction moveDown2;
    public GameAction moveLeft2;
    public GameAction moveRight2;
    public GameAction speedBoost;
    public GameAction fire;
    public GameAction laser;
    public GameAction jump;
    public GameAction configAction;
    public GameAction shipMenuAction;
    public GameAction menuAction;
    public GameAction exit;
    public GameAction sndPlayerTurret;
    


    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(this, screen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        renderer = new TileMapRenderer();
        renderer.setBackground(
            resourceManager.loadImage("background.png"));

        // load first map
        map = resourceManager.loadNextMap();

        // load sounds
        soundManager = new SoundManager(PLAYBACK_FORMAT);
        prizeSound = soundManager.getSound("sounds/prize.wav");
        boopSound = soundManager.getSound("sounds/boop2.wav");

        // start music
        midiPlayer = new MidiPlayer();
        Sequence sequence =
            midiPlayer.getSequence("sounds/music.midi");
        midiPlayer.play(sequence, true);
        toggleDrumPlayback();
        
        menu = new MainMenu((GameManager) this);
        menu.displayMainMenu = false;
        
    }


    /**
        Closes any resurces used by the GameManager.
    */
    public void stop() {
        super.stop();
        midiPlayer.close();
        soundManager.close();
    }


    private void initInput() {
    	moveUp = new GameAction("moveUp");
        moveDown = new GameAction("moveDown");
        moveLeft = new GameAction("moveLeft");
        moveRight = new GameAction("moveRight");
        rotateLeft = new GameAction("rotateLeft");
        rotateRight = new GameAction("rotateRight");
        moveUp2 = new GameAction("moveUp2");
        moveDown2 = new GameAction("moveDown2");
        moveLeft2 = new GameAction("moveLeft2");
        moveRight2 = new GameAction("moveRight2");
        speedBoost = new GameAction("speedBoost");
        fire = new GameAction("fire");
        laser = new GameAction("laser");
        jump = new GameAction("jump",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        menuAction = new GameAction("menuAction",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        shipMenuAction = new GameAction("shipMenuAction",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        configAction = new GameAction("configAction",
                GameAction.DETECT_INITAL_PRESS_ONLY);
        exit = new GameAction("exit",
            GameAction.DETECT_INITAL_PRESS_ONLY);
        sndPlayerTurret = new GameAction("sndPlayerTurret",
                GameAction.DETECT_INITAL_PRESS_ONLY);

        inputManager = new InputManager(
            screen.getFullScreenWindow());
       // inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveUp2, KeyEvent.VK_UP);
        inputManager.mapToKey(moveDown2, KeyEvent.VK_DOWN);
        inputManager.mapToKey(moveLeft2, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight2, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(moveUp, KeyEvent.VK_W);
        inputManager.mapToKey(moveDown, KeyEvent.VK_S);
        inputManager.mapToKey(moveLeft, KeyEvent.VK_A);
        inputManager.mapToKey(moveRight, KeyEvent.VK_D);
        inputManager.mapToKey(rotateLeft, KeyEvent.VK_Q);
        inputManager.mapToKey(rotateRight, KeyEvent.VK_E);
        inputManager.mapToKey(speedBoost, KeyEvent.VK_SHIFT);
        inputManager.mapToKey(fire, KeyEvent.VK_F);
        inputManager.mapToKey(menuAction, KeyEvent.VK_F1);
        inputManager.mapToMouse(shipMenuAction, InputManager.MOUSE_BUTTON_3);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        inputManager.mapToKey(sndPlayerTurret, KeyEvent.VK_SPACE);
       // inputManager.mapToKey(configAction, KeyEvent.VK_C);
        
        inputManager.mapToMouse(laser, InputManager.MOUSE_BUTTON_1);
    }


    private void checkInput(long elapsedTime) {
        if (exit.isPressed()) {
            stop();
        }
        
        if(sndPlayerTurret.isPressed()){
        	ShipV2 s = new ShipV2(this.resourceManager, 1);
        	s.setPosition(getMap().getPlayer().getPosition().plus(new Vector2D(50, 50)));
        	map.addSpriteV2(s);
        	/* old way to add an ai ship
        	if(!map.getAIShips().isEmpty()){
        		Ship player2 = map.getAIShips().get(0);
            	if(player2 != null){
            		Animation[] animation = new Animation[1];
                	Animation a = resourceManager.createPlanetAnim((Image)resourceManager.planetImages.get(0));
            		animation[0] = (Animation) a;
            		Turret t = new Turret(player2, player2.getX(), player2.getY(), 1, animation);
            		getMap().addSprite(t);
            	}
        	}
        	*/
        }
        
        if (configAction.isPressed()) {
            // hide or show the config dialog
            boolean show = !menu.tabbedMainMenu.isVisible();
            menu.tabbedMainMenu.setVisible(show);
        }
        
        if(menuAction.isPressed()){
        	menu.displayMainMenu = !menu.displayMainMenu;
        }

        //Ship player = (Ship)map.getPlayer(); //old reference to player
        ShipV2 player = (ShipV2)map.getPlayer();
        
        if (player.isAlive()) {
        	
        	//Vector2D steeringForce = new Vector2D(0, 0); //used by old way to update ship
        	
        	if(rotateLeft.isPressed()){
        		player.pressRotateLeft();
        	}else if(rotateRight.isPressed()){
        		player.pressRotateRight();
        	}
        	
        	if (moveLeft.isPressed()) {
                //velocityX-=player.getCurrentSpeed();
        		//steeringForce = new Vector2D(-1, 0);
        		//steeringForce = steeringForce.plus(new Vector2D(-1, 0)); //old way to steer ship
        		player.pressMoveLeft(); //New way to steer ship
            }else if (moveRight.isPressed()) {
        		//steeringForce = new Vector2D(1, 0);
            	//steeringForce = steeringForce.plus(new Vector2D(1, 0));//old way to steer ship
            	player.pressMoveRight(); // New way to steer ship
            }else{
            	//System.out.println("x:" + player.velocity.x + " y:" +player.velocity.y);
            	//System.out.println("vel len:"+player.velocity.length());
            	/* Old way to slow down ship
            	double newX = player.velocity.x / 1.5;
            	if(Math.abs(newX) < .003 || player.velocity.length() <.02){
            		newX = 0;
            	}
            	player.velocity = new Vector2D(newX, player.velocity.y);
            	*/
            	//shipV2.releaseMoveHorizontal();
            }
        	
            if (moveUp.isPressed()) {
            	//steeringForce = steeringForce.plus(new Vector2D(0, -1));//old way to steer ship
            	//shipV2.pressMoveUp();
            	player.pressMoveForward(); // New way to steer the ship
            }else if (moveDown.isPressed()) {
            	//steeringForce = steeringForce.plus(new Vector2D(0, 1));//old way to steer ship
            	//shipV2.pressMoveDown();
            	player.pressMoveBackward(); //New way to steer the ship
            }else{
            	/* Old way to add friction
            	double newY = player.velocity.y / 1.5;
            	if(Math.abs(newY) < .003 || player.velocity.length() <.02){
            		newY = 0;
            	}
            	player.velocity = new Vector2D(player.velocity.x, newY);
            	*/
            	//shipV2.releaseMoveVertical();
            }
            
        	//steeringForce.truncate(player.dMaxForce*5); //old way to limit steering force
        	
        	//Acceleration = Force / Mass
        	//Vector2D acceleration = steeringForce.scalarDiv(player.dMass); //Old way to update acceleration
        	
        	
        	//update velocity
        	//player.velocity = player.velocity.plus(acceleration.scalarMult(elapsedTime)); //old way to update velocity
        	
        	//make sure we do not exceed max speeds
        	//player.velocity.truncate(player.dMaxSpeed); // old way to limit velocity
        	//player.setVelocity(player.getVelocity().scalarDiv(1)); //old way to set velocity
        	
        	if (speedBoost.isPressed()) {
            	(player.getLocMan()).getSteering().currentImpulseScalar = (player.getLocMan()).getSteering().impulseScalar*3;
              
            }else if (!speedBoost.isPressed()) {
            	(player.getLocMan()).getSteering().currentImpulseScalar = (player.getLocMan()).getSteering().impulseScalar;
            }
            if (fire.isPressed()) {
            	//player.setX(100);
            	//player.setY(100);
            	player.setPosition(new Vector2D(100,100));
            	//remove all spritev2, except the first one, and place it at player coords
            	SpriteV2 s = map.getSpriteV2().get(0);
            	map.setSpriteV2s(new ArrayList<SpriteV2>());
            	s.setPosition(player.getPosition());
            	s.setVelocity(new Vector2D(0,0));
            	map.addSpriteV2(s);
               //createProjectile(player);
            }
            if(laser.isPressed()) {
                createLaser(player, inputManager.getMouseX(), inputManager.getMouseY());
            }else{
            	//destroyLaser(player); 
            }
            
            if(shipMenuAction.isPressed()){
            	/*
            	//menu.displayMainMenu = !menu.displayMainMenu;
            	int mousex = inputManager.getMouseX()-renderer.offX;
            	int mousey = inputManager.getMouseY()-renderer.offY;
            	Ellipse2D saucer = map.getPlayer().getNose().saucer;
            	if(map.getPlayer().getNose().saucer.contains(mousex, mousey)){
            		System.out.println("shipMenuActipon pressed");
                	menu.tabbedShipMenu.setVisible(!menu.tabbedShipMenu.isVisible());
                	menu.tabbedBuildMenu.setVisible(false);
                	menu.setMenuLocation(menu.tabbedShipMenu, mousex+renderer.offX, mousey+renderer.offY);
            	}else{
            		if(menu.tabbedShipMenu.isVisible())menu.tabbedShipMenu.setVisible(false);
            		menu.tabbedBuildMenu.setVisible(!menu.tabbedBuildMenu.isVisible());
                	menu.setMenuLocation(menu.tabbedBuildMenu, mousex+renderer.offX, mousey+renderer.offY);
            	}*/
            }
        	
        	/*
            float velocityX = 0;
            float velocityY = 0;
            if (moveLeft.isPressed()) {
                velocityX-=player.getCurrentSpeed();
            }
            if (moveRight.isPressed()) {
                velocityX+=player.getCurrentSpeed();
            }
            if (moveUp.isPressed()) {
                velocityY-=player.getCurrentSpeed();
            }
            if (moveDown.isPressed()) {
                velocityY+=player.getCurrentSpeed();
            }
            if (jump.isPressed()) {
               // player.jump(false);
            }
            
            double totalVel =  Math.sqrt((velocityX * velocityX)+(velocityY * velocityY));
            totalVel = (totalVel / .05);
            if(totalVel !=0){
            	player.setVelocityX((float) (velocityX/totalVel));
                player.setVelocityY((float) (velocityY/totalVel));
                //System.out.println("vx: " + velocityX/totalVel);
                //System.out.println("vy: " + velocityY/totalVel);
            }else{
            	player.setVelocityX(0);
                player.setVelocityY(0);
            }
            
            */
           
        }
        
       /* Ship player2 = (Ship)map.getPlayer2();
        if (player2 != null && player2.isAlive()) {
            float velocityX = 0;
            float velocityY = 0;
            player2.setCurrentSpeed(player2.getMaxSpeed());
            if (moveLeft2.isPressed()) {
                velocityX-=player2.getCurrentSpeed();
            }
            if (moveRight2.isPressed()) {
                velocityX+=player2.getCurrentSpeed();
            }
            if (moveUp2.isPressed()) {
                velocityY-=player2.getCurrentSpeed();
            }
            if (moveDown2.isPressed()) {
                velocityY+=player2.getCurrentSpeed();
            }
            player2.setVelocityX(velocityX);
            player2.setVelocityY(velocityY);
            */
       // }

    }
    
    private void createLaser(ShipV2 player, int xTarget, int yTarget) {
    	if(map.laserExists(player)){
    		//do nothing there is already a laser for this player.
    	}else{
    		//this player does not already have a laser, it is being added.
    		/*
    		map.addLaser((float)player.getNose().noseX,
    					(float)player.getNose().noseY, 
   				 		// (player.getX()+xTarget-(screen.getWidth()/2))+player.getWidth(), 
   				 		 //(player.getY()+yTarget-(screen.getHeight()/2))+player.getHeight(), 
    					(float)((xTarget)+player.getNose().noseX), 
    					(float)((yTarget)+player.getNose().noseY), 
   				player);*/
    	}
    	
    }
    
    private void createLaser(Turret t) {
    	Sprite target = (Sprite) t.getTarget();
    	if(map.laserExists(t)){
    		//do nothing there is already a laser for this player.
    	}else{
    		//this player does not already have a laser, it is being added.
    		map.addLaser((float)t.getX(),
    					(float)t.getY(), 
   				 		// (player.getX()+xTarget-(screen.getWidth()/2))+player.getWidth(), 
   				 		 //(player.getY()+yTarget-(screen.getHeight()/2))+player.getHeight(), 
    					(float)(target.getX()), 
    					(float)(target.getY()), 
   				t);
    	}
    }
    
    private void destroyLaser(Ship player){
    	map.removeLaser(player);
    }
    

    private void createProjectile(Ship player) {
		// TODO Auto-generated method stub
    	Projectile p = (Projectile) ((Projectile) resourceManager.rocketSprites.get((int)(Math.random()*resourceManager.rocketSprites.size()))).clone();
    	p.parentId = player.id;
        p.setRotation((float) player.heading.perp().getTheta());
        p.setVelocityX(player.getVelocityX()*1.5f);
        p.setVelocityY(player.getVelocityY()*1.5f);
        
        float dist = 50;
        
        if(p.getVelocityX()>0){
        	p.setX(player.getX()+dist);
        }else if(p.getVelocityX()<0){
        	p.setX(player.getX()-dist);
        }else if(p.getVelocityX() == 0){
        	p.setX(player.getX());
        }
        
        if(p.getVelocityY()>0){
        	p.setY(player.getY()+dist);
        }else if(p.getVelocityY()<0){
        	p.setY(player.getY()-dist);
        }else if(p.getVelocityY() == 0){
        	p.setY(player.getY());
        }
        map.addSprite(p);
		
	}


	public void draw(Graphics2D g) {
        renderer.draw(g, map, screen.getWidth(), screen.getHeight());
        drawMenu(g, map, screen.getWidth(), screen.getHeight());
    }
	
	public void drawMenu(Graphics2D g, TileMap theMap, 
						float screenWidth, float screenHeight){
		if(menu.displayMainMenu){
			JFrame frame = super.screen.getFullScreenWindow();
	        // the layered pane contains things like popups (tooltips,
	        // popup menus) and the content pane.
			//frame.getLayeredPane().setDoubleBuffered(true);
			//frame.getLayeredPane().setBackground(Color.red);
			frame.setBackground(Color.red);
			//frame.setOpacity(1);
			
	        frame.getLayeredPane().paintComponents(g);
	        //frame.getLayeredPane().repaint();
		}
	}


    /**
        Gets the current map.
    */
    public TileMap getMap() {
        return map;
    }


    /**
        Turns on/off drum playback in the midi music (track 1).
    */
    public void toggleDrumPlayback() {
        Sequencer sequencer = midiPlayer.getSequencer();
        if (sequencer != null) {
            sequencer.setTrackMute(DRUM_TRACK,
                !sequencer.getTrackMute(DRUM_TRACK));
        }
    }

    public Point getTileCollision(Ship ship,
            float newX, float newY)
        {
            float fromX = Math.min(ship.getX(), newX);
            float fromY = Math.min(ship.getY(), newY);
            float toX = Math.max(ship.getX(), newX);
            float toY = Math.max(ship.getY(), newY);

            // get the tile locations
            int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
            int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
            int toTileX = TileMapRenderer.pixelsToTiles(
                toX + ship.getWidth() - 1);
            int toTileY = TileMapRenderer.pixelsToTiles(
                toY + ship.getHeight() - 1);

            // check each tile for a collision
            for (int x=fromTileX; x<=toTileX; x++) {
                for (int y=fromTileY; y<=toTileY; y++) {
                    if (x < 0 || x >= map.getWidth() ||
                        map.getTile(x, y) != null)
                    {
                        // collision found, return the tile
                        pointCache.setLocation(x, y);
                        return pointCache;
                    }
                }
            }

            // no collision found
            return null;
        }

    /**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
    */
    public Point getTileCollision(Sprite sprite,
        float newX, float newY)
    {
        float fromX = Math.min(sprite.getX(), newX);
        float fromY = Math.min(sprite.getY(), newY);
        float toX = Math.max(sprite.getX(), newX);
        float toY = Math.max(sprite.getY(), newY);

        // get the tile locations
        int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
        int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
        int toTileX = TileMapRenderer.pixelsToTiles(
            toX + sprite.getWidth() - 1);
        int toTileY = TileMapRenderer.pixelsToTiles(
            toY + sprite.getHeight() - 1);

        // check each tile for a collision
        for (int x=fromTileX; x<=toTileX; x++) {
            for (int y=fromTileY; y<=toTileY; y++) {
                if (x < 0 || x >= map.getWidth() ||
                    map.getTile(x, y) != null)
                {
                    // collision found, return the tile
                    pointCache.setLocation(x, y);
                    return pointCache;
                }
            }
        }

        // no collision found
        return null;
    }


    /**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
    */
    public boolean isCollision(Sprite s1, Sprite s2) {
        // if the Sprites are the same, return false
        if (s1 == s2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }
        
        if(s1 instanceof Laser && s2 instanceof Planet){
        	Laser l = (Laser)s1;
        	Planet p = (Planet)s2;
        	if(l.getLine().intersects(p.circle.getBounds2D())){
        		//System.out.println("laser hit");
        		return true;
        	}else{
        		//System.out.println("laser fail");
        		return false;
        	}
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }
    
    /**
	    Checks if two Sprites collide with one another. Returns
	    false if the two Sprites are the same. Returns false if
	    one of the Sprites is a Creature that is not alive.
	*/
	public boolean isCollision(Laser s1, Ship s2) {
	    // if the Sprites are the same, return false
		boolean returnVal = false;
	    
	    if(s1 instanceof Laser && s2 instanceof Ship){
	    	Laser l = (Laser)s1;
	    	Ship s = (Ship)s2;
	    	if(l.getLine().intersects(s2.getX(), s2.getY(), s2.getX()+s2.getWidth(), s2.getY()+s2.getHeight())){
	    		//System.out.println("laser hit");
	    		return true;
	    	}else{
	    		//System.out.println("laser fail");
	    		return false;
	    	}
	    }
	    return returnVal;
	}
	
	/**
    Checks if two Sprites collide with one another. Returns
    false if the two Sprites are the same. Returns false if
    one of the Sprites is a Creature that is not alive.
*/
public boolean isCollision(Laser s1, Turret s2) {
    // if the Sprites are the same, return false
	boolean returnVal = false;
    
    if(s1 instanceof Laser && s2 instanceof Turret){
    	Laser l = (Laser)s1;
    	Turret t = (Turret)s2;
    	if(l.getLine().intersects(s2.getX(), s2.getY(), s2.getX()+s2.getWidth(), s2.getY()+s2.getHeight())){
    		//System.out.println("laser hit");
    		returnVal = true;
    	}else{
    		//System.out.println("laser fail");
    		returnVal = false;
    	}
    }
    if(s1.parent == s2)returnVal = false;
    return returnVal;
}
    
    public boolean isCollision(Ship s1, Sprite s2) {

        // if one of the Sprites is a dead Creature, return false
        if (s1 instanceof Ship && !((Ship)s1).isAlive()) {
            return false;
        }
        if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int s1x = Math.round(s1.getX());
        int s1y = Math.round(s1.getY());
        int s2x = Math.round(s2.getX());
        int s2y = Math.round(s2.getY());

        // check if the two sprites' boundaries intersect
        return (s1x < s2x + s2.getWidth() &&
            s2x < s1x + s1.getWidth() &&
            s1y < s2y + s2.getHeight() &&
            s2y < s1y + s1.getHeight());
    }


    /**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
    */
    public Sprite getSpriteCollision(Ship ship) {

        // run through the list of Sprites
        LinkedList <Sprite> sprites = map.getSprites();
        for (int i = 0; i < sprites.size(); i++) {
            Sprite otherSprite = (Sprite)sprites.get(i);
            if (isCollision(ship, otherSprite)) {
                // collision found, return the Sprite
                return otherSprite;
            }
        }

        // no collision found
        return null;
    }


    /**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
    */
    public void update(long elapsedTime) {
    	
        ShipV2 player = (ShipV2)map.getPlayer();
        JTabbedPane tabbelShipMenu = menu.tabbedShipMenu;
        //JPanel sliderMenu = ((JPanel)menu.tabbedShipMenu.getComponent(0));
        //menu.updateShipSliders(sliderMenu);

        /* don't plan on doing this now
        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = resourceManager.reloadMap();
            return;
        }*/

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
       // updateShip(player, elapsedTime);
        player.update(elapsedTime);
        map.updateSpriteV2(elapsedTime);
        
        // spritev2 test
        //this.getMap().getSpriteV2().get(0).update(elapsedTime);
        
        /* ai ships are going to be represented in the main sprite list
        for(int i = 0; i < map.getAIShips().size(); i++){
        	Ship player2 = null;
        	player2 = map.getAIShips().get(i);
        	//if(player2 != null)updateShip(player2, elapsedTime);
            if(player2 != null) player2.update(elapsedTime);
        }
        */
        
        
        //updateLasers(); // not worrying about lasers right now

        // update other sprites
        //ListIterator i = (ListIterator) map.getSprites();
        LinkedList<Sprite>sprites = map.getSprites();
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = (Sprite)sprites.get(i);
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    sprites.remove(i);
                }
                else {
                    updateCreature(creature, elapsedTime);
                }
            }
            // normal update
            sprite.update(elapsedTime);
        }
    }

    //updates one end of each laser too follow it's parent.
    private void updateLasers(){
    	Iterator l = map.getLasers();
    	Laser laser;
    	Object player;
    	double x1, y1, x2, y2;
    	while(l.hasNext()){
    		laser = (Laser)l.next();
    		player = laser.getParent();
    		if(player instanceof Ship){
    			//moves the players laser with reguards to his/her mouse.
    			x1 = (float)((Ship)player).getNose().noseX;
        		y1 = (float)((Ship)player).getNose().noseY;
        		double mouseX = inputManager.getMouseX();
        		double mouseY = inputManager.getMouseY();
        		x2 = ((Ship)player).getX()-(screen.getWidth()/2-mouseX)+(32);
    			y2 = ((Ship)player).getY()-(screen.getHeight()/2-mouseY)+(32);
    			
    			//remove x totalPower from parent ship for each second
    			//the laser has been going, where x is the current power of the laser.
    			double elapsedLaserTime = System.currentTimeMillis() - laser.getLastUpdate();
    			double powerDiff = Laser.getPowerDifference(laser, elapsedLaserTime);
    			
    			double newTotalPower = ((Ship)player).getTotalPower()-powerDiff;
    			if(newTotalPower <3 )newTotalPower = 3;
    			((Ship)player).setTotalPower(newTotalPower);
    					
    		}else{
    			Object target = ((Turret)player).getTarget();
    			if(target instanceof Ship){
    				Ship s = (Ship)target;
    				x2 = s.getX()+s.getWidth()/2;
    				y2 = s.getY()+s.getHeight()/2;
    			}else{
    				Sprite p = (Sprite)target;
    				x2 = p.getX()+p.getWidth()/2;
    				y2 = p.getY()+p.getHeight()/2;
    			}
    			x1 = (float)((Turret)player).getX();
    			y1 = (float)((Turret)player).getY();
    		}
    		laser.setLastUpdate(System.currentTimeMillis());
    		laser.getLine().setLine(x1, y1, x2, y2);
    		checkLaserCollisions(laser);
    		
    	}
    }
    
    private void checkLaserCollisions(Laser laser){
    	checkLaserCollisionsWithSprites(laser);
    }
    
    private void checkLaserCollisionsWithSprites(Laser laser){
    	 // update other sprites
        //Iterator i = map.getSprites();
    	LinkedList <Sprite> oldSprites = map.getSprites();
    	LinkedList <Sprite> sprites = (LinkedList<Sprite>) oldSprites.clone();
    	//sprites.add(map.getPlayer()); Not doing this right now
    	for(int i = 0; i < map.getAIShips().size(); i++){
    		sprites.add(map.getAIShips().get(i));
    	}
    	
        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = (Sprite)sprites.get(i);
            if (sprite instanceof Planet) {
            	Planet planet = (Planet)sprite;
            	if(isCollision(laser, planet)) collideLaserWithSprite(laser, planet);
            }else if(sprite instanceof Ship){
            	Ship ship = (Ship)sprite;
            	if(isCollision(laser, ship)) collideLaserWithSprite(laser, ship);
            }else if(sprite instanceof Turret){
            	Turret t = (Turret)sprite;
            	//if(isCollision(laser, t)) collideLaserWithSprite(laser, t);
            	//lasers shoujldn't collide with turrets should they? maybe later
            }
        }
    }
    
    private void collideLaserWithSprite(Laser laser, Sprite sprite){
    	//System.out.println("Laser Colliding with Planet");
    	long currentTime = System.currentTimeMillis();
    	long lastCollideTime = laser.getLastCollideTime(sprite);
    	laser.setLastCollideTime(sprite, lastCollideTime);
    	long elapsedCollideTime = laser.getElapsedCollideTime(sprite);//currentTime - lastCollideTime;
    	
    	if(elapsedCollideTime <= 100){
    		double powerDifference = 0;
    		
    		//laser collides with planet
    		if(sprite instanceof Planet){
    			Planet planet = (Planet)sprite;
    			//laser comes from a ship
    			if(laser.parent instanceof Ship){
        			//powerDifference = (laser.power/1000) *(elapsedCollideTime);
        			powerDifference = Laser.getPowerDifference(laser, elapsedCollideTime);
        			double newTotalPower = ((Ship)laser.parent).getTotalPower() + powerDifference*2;
        			if(newTotalPower < 3)newTotalPower = 3;
        			
        			((Ship)laser.parent).setTotalPower(newTotalPower);
        		//laser comes from a turret
    			}else if(laser.parent instanceof Turret){
        			//powerDifference = (laser.power/1000) *(elapsedCollideTime);
        			powerDifference = Laser.getPowerDifference(laser, elapsedCollideTime);
        			Ship s = ((Turret)laser.parent).getParent();
        			s.setTotalPower(s.getTotalPower()+powerDifference);
        		}
        		planet.totalPower(planet.totalPower()-powerDifference);
        	//laser collides with a ship
    		}else if(sprite instanceof Ship){
    			Ship ship = (Ship)sprite;
    			if(laser.parent == ship){
    				//do nothing this laser cannot damage the ship it is from
    				
    			}else if(laser.parent instanceof Turret){
    				Turret t = (Turret)laser.parent;
    				if(t.getParent() == ship){
    					/*do nothing if this lasers parent is a turret who's parent
        				is the ship being collided with. This will cause a turret 
        				to not do any damage to the ship that spawned it.*/
    				}else{
    					double totalPower = ((Ship)((Turret)laser.parent).getParent()).getTotalPower();
    					if(totalPower <= 3){
    						powerDifference = (double)elapsedCollideTime/1000;
        				}else{
        					powerDifference = Laser.getPowerDifference(laser, elapsedCollideTime);	
        				}
        				ship.setHitpoints(ship.getHitpoints()-powerDifference);
    				}
    			}else{
    				double totalPower = ((Ship)laser.parent).getTotalPower();
					if(totalPower <= 3){
						powerDifference = (double)elapsedCollideTime/1000;
    				}else{
    					powerDifference = Laser.getPowerDifference(laser, elapsedCollideTime);	
    				}
    				ship.setHitpoints(ship.getHitpoints()-powerDifference);
    			}
    		}else if(sprite instanceof Turret){
    			Turret turret = (Turret)sprite;
    			if(laser.parent == turret){
    				//do nothing this laser cannot damage it's own turret.
    			}else{
    				powerDifference = Laser.getPowerDifference(laser, elapsedCollideTime);
    				turret.setHitpoints(turret.getHitpoints()-powerDifference);
    			}
    		}
    		
    	}
    	laser.setLastCollideTime(sprite, currentTime);
    }

    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {
    	//creature.updateRotation(elapsedTime);

        // change x
        float dx = creature.getVelocityX();
        float oldX = creature.getX();
        float newX = oldX + dx * elapsedTime;
        Point tile =
            getTileCollision(creature, newX, creature.getY());
        if (tile == null) {
            creature.setX(newX);
        }
        else {
            // line up with the tile boundary
            if (dx > 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x) -
                    creature.getWidth());
            }
            else if (dx < 0) {
                creature.setX(
                    TileMapRenderer.tilesToPixels(tile.x + 1));
            }
            creature.collideHorizontal();
        }

        // change y
        float dy = creature.getVelocityY();
        float oldY = creature.getY();
        float newY = oldY + dy * elapsedTime;
        tile = getTileCollision(creature, creature.getX(), newY);
        if (tile == null) {
            creature.setY(newY);
        }
        else {
            // line up with the tile boundary
            if (dy > 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y) -
                    creature.getHeight());
            }
            else if (dy < 0) {
                creature.setY(
                    TileMapRenderer.tilesToPixels(tile.y + 1));
            }
            creature.collideVertical();
        }
    }
    
    /**
	    Updates the creature, applying gravity for creatures that
	    aren't flying, and checks collisions.
	*/
    /*
	private void updateShip(Ship creature,
	    long elapsedTime)
	{
		//if(creature.getVelocity().length() > .00001){
			//creature.updateHeading(elapsedTime, creature.getVelocity());
			//creature.updateRotation(elapsedTime);
		//}
		
	
	    // change x
	    float dx = creature.getVelocityX();
	    float oldX = creature.getX();
	    float newX = oldX + dx * elapsedTime;
	    Point tile =
	        getTileCollision(creature, newX, creature.getY());
	    if (tile == null) {
	        creature.setX(newX);
	    }
	    else {
	        // line up with the tile boundary
	        if (dx > 0) {
	            creature.setX(
	                TileMapRenderer.tilesToPixels(tile.x) -
	                creature.getWidth());
	        }
	        else if (dx < 0) {
	            creature.setX(
	                TileMapRenderer.tilesToPixels(tile.x + 1));
	        }
	        creature.collideHorizontal();
	    }
	    if (creature instanceof Ship) {
	        checkPlayerCollision((Ship)creature, false);
	    }
	
	    // change y
	    float dy = creature.getVelocityY();
	    float oldY = creature.getY();
	    float newY = oldY + dy * elapsedTime;
	    tile = getTileCollision(creature, creature.getX(), newY);
	    if (tile == null) {
	        creature.setY(newY);
	    }
	    else {
	        // line up with the tile boundary
	        if (dy > 0) {
	            creature.setY(
	                TileMapRenderer.tilesToPixels(tile.y) -
	                creature.getHeight());
	        }
	        else if (dy < 0) {
	            creature.setY(
	                TileMapRenderer.tilesToPixels(tile.y + 1));
	        }
	        creature.collideVertical();
	    }
	    if (creature instanceof Ship) {
	        boolean canKill = (oldY < creature.getY());
	        checkPlayerCollision((Ship)creature, canKill);
	    }
	    
	    
	   
	
	}

*/
    /**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
    */
    public void checkPlayerCollision(Ship player,
        boolean canKill)
    {
        if (!player.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite collisionSprite = getSpriteCollision(player);
        if (collisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)collisionSprite);
        }
        else if (collisionSprite instanceof Creature) {
        	if(collisionSprite instanceof Planet){
        		//player.setVelocityX(0);
        		//player.setVelocityY(0);
        	}else if(collisionSprite instanceof Projectile){
        		if(((Projectile)collisionSprite).parentId == player.id){
        			
        		}else{
        			//player.setState(Creature.STATE_DYING);
        			//((Creature) collisionSprite).setState(Creature.STATE_DYING);
        		}
        	}else{
	            Creature badguy = (Creature)collisionSprite;
	            if (canKill) {
	                // kill the badguy and make player bounce
	               // soundManager.play(boopSound);
	               // badguy.setState(Creature.STATE_DYING);
	                //player.setY(badguy.getY() - player.getHeight());
	            }
	            else {
	                // player dies!
	                //player.setState(Creature.STATE_DYING);
	            }
        	}
        }
    }


    /**
        Gives the player the speicifed power up and removes it
        from the map.
    */
    public void acquirePowerUp(PowerUp powerUp) {
        // remove it from the map
        map.removeSprite(powerUp);

        if (powerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            soundManager.play(prizeSound);
        }
        else if (powerUp instanceof PowerUp.Music) {
            // change the music
            soundManager.play(prizeSound);
            toggleDrumPlayback();
        }
        else if (powerUp instanceof PowerUp.Goal) {
            // advance to next map
            soundManager.play(prizeSound,
                new EchoFilter(2000, .7f), false);
            map = resourceManager.loadNextMap();
        }
    }

}
