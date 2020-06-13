package entity;

import util.Point2D;

import java.util.ArrayList;
import java.util.Random;

/**
 * The School class creates a group of level 1 BasicEnemies that travel in the same direction.
 */
public class School extends BasicEnemy {

    private static final int SPACING = 20;
    private static final float Y_VEL_OFFSET = 0.5f;
    private static final float X_VEL_OFFSET = 0.7f;

    private ArrayList<BasicEnemy> schoolList = new ArrayList<>();
    private int count; // Number of fish in school object

    private static final Random RANDOM = new Random();
    private int direction = RANDOM.nextBoolean() ? 1 : -1;

    /**
     * Constructor that initializes a School object. Calls the super class, BasicEnemy, where the size, position, velocity
     * and level is set. Calls the method instantiateSchool.
     */
    public School(Point2D position, Point2D size, Point2D velocity, int count) {
        super(position, size, velocity, 1);
        this.count = count;

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
        float yVelocity = (float) Math.random() - Y_VEL_OFFSET;
	for (int x = 0; x < count; x++) {
	    float xDisplacement = (float)(Math.random() * SPACING/4);
	    float yDisplacement = (float)(Math.random() * SPACING/4);

	    BasicEnemy fish = new BasicEnemy(
		    new Point2D(position.getX() + xDisplacement + SPACING * x,
				position.getY() + yDisplacement + (SPACING * Math.random() * count)),
		    new Point2D(),
		    new Point2D(X_VEL_OFFSET * direction, yVelocity),
		    1
	    );
	    schoolList.add(fish);
	}
    }

    public ArrayList<BasicEnemy> getSchoolList() {
        return schoolList;
    }

    public boolean isDead() {
        return schoolList.isEmpty();
    }

}
