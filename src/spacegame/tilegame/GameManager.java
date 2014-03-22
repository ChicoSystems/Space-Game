package spacegame.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.swing.JFrame;

import spacegame.graphics.*;
import spacegame.input.*;
import spacegame.sound.*;
import spacegame.test.GameCore;
import spacegame.tilegame.sprites.*;


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
    private ResourceManager resourceManager;
    private Sound prizeSound;
    private Sound boopSound;
    public InputManager inputManager;
    private TileMapRenderer renderer;
    protected MainMenu menu;
    
    

    public GameAction moveUp;
    public GameAction moveDown;
    public GameAction moveLeft;
    public GameAction moveRight;
    public GameAction speedBoost;
    public GameAction fire;
    public GameAction laser;
    public GameAction jump;
    public GameAction configAction;
    public GameAction shipMenuAction;
    public GameAction menuAction;
    public GameAction exit;
    


    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        resourceManager = new ResourceManager(
        screen.getFullScreenWindow().getGraphicsConfiguration());

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

        inputManager = new InputManager(
            screen.getFullScreenWindow());
       // inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

        inputManager.mapToKey(moveUp, KeyEvent.VK_UP);
        inputManager.mapToKey(moveDown, KeyEvent.VK_DOWN);
        inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
        inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
        inputManager.mapToKey(moveUp, KeyEvent.VK_W);
        inputManager.mapToKey(moveDown, KeyEvent.VK_S);
        inputManager.mapToKey(moveLeft, KeyEvent.VK_A);
        inputManager.mapToKey(moveRight, KeyEvent.VK_D);
        inputManager.mapToKey(speedBoost, KeyEvent.VK_SHIFT);
        inputManager.mapToKey(fire, KeyEvent.VK_SPACE);
        inputManager.mapToKey(menuAction, KeyEvent.VK_F1);
        inputManager.mapToMouse(shipMenuAction, InputManager.MOUSE_BUTTON_3);
        inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
        
        inputManager.mapToMouse(laser, InputManager.MOUSE_BUTTON_1);
    }


    private void checkInput(long elapsedTime) {
        if (exit.isPressed()) {
            stop();
        }
        
        if (configAction.isPressed()) {
            // hide or show the config dialog
            boolean show = !menu.tabbedMainMenu.isVisible();
            menu.tabbedMainMenu.setVisible(show);
        }
        
        if(menuAction.isPressed()){
        	menu.displayMainMenu = !menu.displayMainMenu;
        }

        Ship player = (Ship)map.getPlayer();
        if (player.isAlive()) {
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
            if (speedBoost.isPressed()) {
                player.setCurrentSpeed(player.getBoostSpeed()*4);
            }else if (!speedBoost.isPressed()) {
                player.setCurrentSpeed(player.getMaxSpeed());
            }
            if (fire.isPressed()) {
            	player.setX(100);
            	player.setY(100);
               createProjectile(player);
            }
            if(laser.isPressed()) {
                createLaser(player, inputManager.getMouseX(), inputManager.getMouseY());
            }else{
            	destroyLaser(player); 
            }
            
            if(shipMenuAction.isPressed()){
            	System.out.println("shipMenuActipon pressed");
            	menu.tabbedShipMenu.setVisible(!menu.tabbedShipMenu.isVisible());
            	menu.setMenuLocation(menu.tabbedShipMenu, inputManager.getMouseX(), inputManager.getMouseY());
            }
             
            player.setVelocityX(velocityX);
            player.setVelocityY(velocityY);
        }

    }
    
    private void createLaser(Ship player, int xTarget, int yTarget) {
    	if(map.laserExists(player)){
    		//do nothing there is already a laser for this player.
    	}else{
    		//this player does not already have a laser, it is being added.
    		map.addLaser((float)player.nose.noseX,
    					(float)player.nose.noseY, 
   				 		// (player.getX()+xTarget-(screen.getWidth()/2))+player.getWidth(), 
   				 		 //(player.getY()+yTarget-(screen.getHeight()/2))+player.getHeight(), 
    					(float)((xTarget)+player.nose.noseX), 
    					(float)((yTarget)+player.nose.noseY), 
   				player);
    	}
    }
    
    private void destroyLaser(Ship player){
    	map.removeLaser(player);
    }
    
    

    private void createProjectile(Ship player) {
		// TODO Auto-generated method stub
    	Projectile p = (Projectile) ((Projectile) resourceManager.rocketSprites.get((int)(Math.random()*resourceManager.rocketSprites.size()))).clone();
    	p.parentId = player.id;
        p.setRotation(player.getRotation());
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
	        frame.getLayeredPane().paintComponents(g);
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
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite otherSprite = (Sprite)i.next();
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
        Ship player = (Ship)map.getPlayer();


        // player is dead! start map over
        if (player.getState() == Creature.STATE_DEAD) {
            map = resourceManager.reloadMap();
            return;
        }

        // get keyboard/mouse input
        checkInput(elapsedTime);

        // update player
        updateShip(player, elapsedTime);
        
        player.update(elapsedTime);
        
        updateLasers();

        // update other sprites
        Iterator i = map.getSprites();
        while (i.hasNext()) {
            Sprite sprite = (Sprite)i.next();
            if (sprite instanceof Creature) {
                Creature creature = (Creature)sprite;
                if (creature.getState() == Creature.STATE_DEAD) {
                    i.remove();
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
    	Ship player;
    	while(l.hasNext()){
    		laser = (Laser)l.next();
    		player = laser.getParent();
    		double x1 = (float)player.nose.noseX;
    		double y1 = (float)player.nose.noseY;
    		double mouseX = inputManager.getMouseX();
    		double mouseY = inputManager.getMouseY();
    		double x2 = player.getX()-(screen.getWidth()/2-mouseX)+(32);
			double y2 = player.getY()-(screen.getHeight()/2-mouseY)+(32);
			
			
    		//double x2 = (player.getX()+mouseX-screen.getWidth()/2)+player.getWidth();
    		//double y2 = (player.getY()+mouseY-screen.getHeight()/2)+player.getHeight();
    		laser.getLine().setLine(x1, y1, x2, y2);
    	}
    }

    /**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
    */
    private void updateCreature(Creature creature,
        long elapsedTime)
    {
    	creature.updateRotation(elapsedTime);

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
private void updateShip(Ship creature,
    long elapsedTime)
{
	creature.updateRotation(elapsedTime);

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
        		player.setVelocityX(0);
        		//player.setVelocityY(0);
        	}else if(collisionSprite instanceof Projectile){
        		if(((Projectile)collisionSprite).parentId == player.id){
        			
        		}else{
        			player.setState(Creature.STATE_DYING);
        			((Creature) collisionSprite).setState(Creature.STATE_DYING);
        		}
        	}else{
	            Creature badguy = (Creature)collisionSprite;
	            if (canKill) {
	                // kill the badguy and make player bounce
	                soundManager.play(boopSound);
	                badguy.setState(Creature.STATE_DYING);
	                player.setY(badguy.getY() - player.getHeight());
	            }
	            else {
	                // player dies!
	                player.setState(Creature.STATE_DYING);
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
