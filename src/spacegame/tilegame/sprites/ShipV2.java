/**
 * 
 */
package spacegame.tilegame.sprites;

import spacegame.graphics.SpriteV2;
import spacegame.input.LocationManager;
import spacegame.tilegame.ResourceManager;

/**
 * @author Isaac Assegai
 *
 */
public class ShipV2 extends SpriteV2 {
	LocationManager locMan;

	public ShipV2(ResourceManager parent, LocationManager locMan) {
		super(parent);
		locMan = this.locMan;
	}

	protected void updateLocation(double elapsedTime) {
		
	}

	protected void drawSprite(double elapsedTime) {
		
	}


}
