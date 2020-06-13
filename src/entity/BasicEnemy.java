package entity;

import game.AppPanel;
import util.Point2D;

/**
 * The BasicEnemy class contains information about basic enemies that the player will
 * encounter in the universe. This is the most common type of fish.
 */
public class BasicEnemy extends Fish {

    // Small fish variables
    private static final int SMALL_WIDTH = 50;
    private static final int SMALL_HEIGHT = 16;

    // Medium fish variables
    private static final int MEDIUM_WIDTH = 64;
    private static final int MEDIUM_HEIGHT = 32;
    private static final int MED_MOUTH_SIZE = 20;

    // Large fish variables
    private static final int LARGE_WIDTH = 128;
    private static final int LARGE_HEIGHT = 128;
    private static final int LAR_MOUTH_SIZE = 40;

    /**
     * Constructor that initializes a entity.BasicEnemy object. Sets the size of fish,
     * depending on the level that was inputted in the constructor
     */
    public BasicEnemy(Point2D position, Point2D size, Point2D velocity, int level) {
        super(position, size, velocity, level, true);

        setSize();
    }

    /**
     * setSize sets the size and offset of the colliders and sprites based on the level of the fish.
     * @param Nothing.
     * @return Nothing.
     */
    @Override
    protected void setSize(){
        switch(level){
            case 1: // Small fish
                size = new Point2D(SMALL_WIDTH, SMALL_HEIGHT);
                colliderSize = size;
                mouthSize = 10;
                sprite = AppPanel.getImageManager().getSpriteHashMap().get("SMALL FISH");
                break;
            case 2: // Medium fish
                size = new Point2D(MEDIUM_WIDTH, MEDIUM_HEIGHT);
                colliderSize = size;
                mouthSize = MED_MOUTH_SIZE;
                sprite = AppPanel.getImageManager().getSpriteHashMap().get("MEDIUM FISH");
                break;
            case 3: // Large fish
                size = new Point2D(LARGE_WIDTH, LARGE_HEIGHT);
                mouthSize = LAR_MOUTH_SIZE;
                sprite = AppPanel.getImageManager().getSpriteHashMap().get("LARGE FISH");
                colliderSize = new Point2D(LARGE_WIDTH, 100);
                colliderOffset = new Point2D(0, 10);
                mouthOffsetY = 1;
                break;
            default:
                colliderSize = size;
                super.setSize();
        }
    }
}
