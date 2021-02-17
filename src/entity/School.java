package entity;

import game.AppPanel;
import util.Point2D;

import java.util.Random;

/**
 * The School class creates a group of level 1 BasicEnemies that travel in the same direction.
 */
public class School extends BasicEnemy {
    private int count; // Number of fish in school object

    private static final Random RANDOM = new Random();
    private int direction = RANDOM.nextBoolean() ? 1 : -1;
    private FishFactory fishFactory;

    /**
     * Constructor that initializes a School object. Calls the super class, BasicEnemy, where the size, position, velocity
     * and level is set. Calls the method instantiateSchool.
     */
    public School(Point2D position, Point2D size, Point2D velocity, FishFactory fishFactory, AppPanel appPanel, int count) {
        super(position, size, velocity, 1, fishFactory, appPanel);
        this.count = count;
        this.fishFactory = fishFactory;

        instantiateSchool();
    }

    /**
     * instantiateSchool creates a number of BasicEnemy objects, as many as the variable "count", and adds them
     * to the ArrayList schoolList. All the BasicEnemy objects have the same direction and there are
     * spacing between that is randomized
     * @param Nothing.
     * @return Nothing.
     */
    private void instantiateSchool() {
        final int spacing = 20;
        final float yVelOffset = 0.5f;
        final float xVelOffset = 0.7f;
        final float displacementFactor = 0.25f;
        float yVelocity = (float) Math.random() - yVelOffset;

	for (int x = 0; x < count; x++) {
	    float xDisplacement = (float)(Math.random() * spacing * displacementFactor);
	    float yDisplacement = (float)(Math.random() * spacing * displacementFactor);

	    BasicEnemy fish = new BasicEnemy(
		    new Point2D(position.getX() + xDisplacement + spacing * x,
				position.getY() + yDisplacement + (spacing * Math.random() * count)),
		    new Point2D(),
		    new Point2D(xVelOffset * direction, yVelocity),
		    1, fishFactory, appPanel
	    );
	}
    }

}
