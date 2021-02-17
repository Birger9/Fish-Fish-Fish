package entity;

import game.AppPanel;
import util.Point2D;
import util.PropertiesLoaderBorrowedCode;

/**
 * The BasicEnemy class contains information about basic enemies that the player will
 * encounter in the universe. This is the most common type of fish.
 */
public class BasicEnemy extends Fish {

    private PropertiesLoaderBorrowedCode defaultSettings = new PropertiesLoaderBorrowedCode("src/defaultsettings");

    /**
     * Constructor that initializes a BasicEnemy object. Sets the size of fish,
     * depending on the level that was inputted in the constructor
     */
    public BasicEnemy(Point2D position, Point2D size, Point2D velocity, int level, FishFactory fishFactory, AppPanel appPanel) {
        super(position, size, velocity, level, true, fishFactory, appPanel);

        setSize();
        setSprite(level);
    }

    /**
     * setSize Set the size and offset of the colliders based on the level of the fish.
     */
    @Override
    protected void setSize(){
        // Magic constants in switch case: shouldn't be avoided - would just look cluttered to introduce constants.
        switch(level){
            case 1: // Small fish
                final int smallWidth = (int) defaultSettings.getValue("basicEnemy.small.width", int.class);
                final int smallHeight = (int) defaultSettings.getValue("basicEnemy.small.height", int.class);
                size = new Point2D(smallWidth, smallHeight);
                colliderSize = size;
                mouthSize = 10; // Magic constant: This is just to make debug mode prettier to look at. Arbitrary small value.
                break;
            case 2: // Medium fish
                final int mediumWidth = (int) defaultSettings.getValue("basicEnemy.medium.width", int.class);
                final int mediumHeight = (int) defaultSettings.getValue("basicEnemy.medium.height", int.class);
                final int medMouthSize = (int) defaultSettings.getValue("basicEnemy.medium.mouthSize", int.class);
                size = new Point2D(mediumWidth, mediumHeight);
                colliderSize = size;
                mouthSize = medMouthSize;
                break;
            case 3: // Large fish
                final int largeWidth = (int) defaultSettings.getValue("basicEnemy.large.width", int.class);
                final int largeHeight = (int) defaultSettings.getValue("basicEnemy.large.height", int.class);
                final int larMouthSize = (int) defaultSettings.getValue("basicEnemy.medium.mouthSize", int.class);
                size = new Point2D(largeWidth, largeHeight);
                mouthSize = larMouthSize;
                colliderSize = new Point2D(largeWidth, 100); // Magic constant: Additional fine-tuning.
                colliderOffset = new Point2D(0, 10); // Magic constant: Minor, unimportant fine-tuning.
                mouthOffsetY = 1; // Magic constant: Minor, unimportant fine-tuning.
                break;
            default:
                colliderSize = size;
                super.setSize();
        }
    }

    /**
     * setSprite Automatically assign a sprite based on the level of the fish.
     * @param level The level of the fish.
     * @return Nothing.
     */
    private void setSprite(int level) {
        switch (level) {
            case 1:
                sprite = appPanel.getImageManager().getSpriteHashMap().get("SMALL FISH");
                break;
            case 2:
                sprite = appPanel.getImageManager().getSpriteHashMap().get("MEDIUM FISH");
                break;
            case 3:
                sprite = appPanel.getImageManager().getSpriteHashMap().get("LARGE FISH");
        }
    }
}
