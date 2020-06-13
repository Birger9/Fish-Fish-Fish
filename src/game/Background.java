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
    public Background() {
	super(new Point2D(-AppPanel.getMapWidth() / 2, -AppPanel.getMapHeight() / 2),
	      new Point2D(AppPanel.getMapWidth() + AppPanel.getScreenWidth(), AppPanel.getMapHeight() + AppPanel.getScreenHeight()));
	AppPanel.getUniverse().addEntity(this);
	sprite = AppPanel.getImageManager().getSpriteHashMap().get("BACKGROUND");
    }
}
