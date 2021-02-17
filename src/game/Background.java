package game;

import entity.Entity;
import util.Point2D;

/**
 * A background object automatically scales an image to fit the entire map.
 * This can later be expanded to support parallax scrolling.
 */

public class Background extends Entity
{
    /**
     * Constructor for a Background object, the super class for Background is the Entity class.
     * Adds the Background object to the universe and sets the sprite for the Background object
     */
    public Background(AppPanel appPanel) {
	super(new Point2D(-appPanel.getMapWidth() / 2, -appPanel.getMapHeight() / 2),
	      new Point2D(appPanel.getMapWidth() + appPanel.getScreenWidth(), appPanel.getMapHeight() + appPanel.getScreenHeight()),
	      appPanel.getImageManager().getSpriteHashMap().get("BACKGROUND"), appPanel);
	appPanel.getUniverse().addEntity(this);
    }
}
