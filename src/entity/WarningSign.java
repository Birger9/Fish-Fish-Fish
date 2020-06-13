package entity;

import util.Point2D;

/*
 * NOTE: currently unused.
 */

public class WarningSign extends Entity
{
    protected WarningSign(final Point2D position, final Point2D size) {
	super(position, size);
    }
    /*
    private static final int BLINK_FREQ = 4;

    public WarningSign(final Point2D position, final Point2D size, boolean blink) {
	super(position, size);

	if(blink) {
	    blink(BLINK_FREQ);
	}
	sprite = AppPanel.getImageManager().getSpriteHashMap().get("WARNING SIGN");
	AppPanel.getUniverse().addEntity(this);
	}
     */
    }

