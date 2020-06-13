package entity;

import game.AppPanel;
import util.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The FishFactory class uses the factory pattern to retrieve new fish. The class uses an enum to select fish based on its type.
 */
public class FishFactory {

    private static final Random RANDOM = new Random();
    private static final float Y_VEL_OFFSET = 0.5f;
    private static final int BARR_LEVEL = 15;
    private static final float SPAWN_CAP_BARR = 0.2f;
    private static final float SPAWN_CAP_LAR = 0.2f;
    private static final float SPAWN_CAP_MED = 0.6f;
    private static final float SPAWN_CAP_SCHOOL = 0.9f;
    public static final int BARRACUDA_ADDITIONAL_MARGIN = 500;
    private static List<Fish> fishList = new ArrayList<>();
    private static final int SCREEN_MARGIN = 320; // Dictates how far away from the outside of the screen that fish should spawn
    private static final float FISH_SPEED = 0.7f;

    private FishFactory(){
    }

    /**
     * getFish method retrieves a new fish based on the specified type.
     * @param FishType the type of fish that should be spawned.
     * @return Fish, the new fish.
     */
    public static Fish getFish(FishType fishType) {
	int direction = RANDOM.nextBoolean() ? 1 : -1;
	int xPos = direction == 1 ? -SCREEN_MARGIN : AppPanel.getScreenWidth() + SCREEN_MARGIN;

        switch(fishType) {
	    case SMALL_FISH:
	        return new BasicEnemy(
	        	new Point2D(xPos, RANDOM.nextDouble() * AppPanel.getMapHeight()),
			new Point2D(),
			new Point2D(FISH_SPEED * direction, RANDOM.nextDouble() - Y_VEL_OFFSET),
			1
		);
	    case MEDIUM_FISH:
		return new BasicEnemy(
			new Point2D(xPos, RANDOM.nextDouble() * AppPanel.getMapHeight()),
			new Point2D(),
			new Point2D(FISH_SPEED * direction, RANDOM.nextDouble() - Y_VEL_OFFSET),
			2
		);
	    case LARGE_FISH:
		return new BasicEnemy(
			new Point2D(xPos, RANDOM.nextDouble() * AppPanel.getMapHeight()),
			new Point2D(),
			new Point2D(FISH_SPEED * direction, RANDOM.nextDouble() - Y_VEL_OFFSET),
			3
		);
	    case BARRACUDA:
	        return new Barracuda(
			new Point2D(xPos + BARRACUDA_ADDITIONAL_MARGIN * Math.signum(xPos), RANDOM.nextDouble() * AppPanel.getMapHeight()),
			new Point2D(),
			new Point2D(7 * direction, 0), BARR_LEVEL, true
		);
	    case SCHOOL:
	        return new School(
			new Point2D(xPos, RANDOM.nextDouble() * AppPanel.getMapHeight()),
			new Point2D(),
			new Point2D(FISH_SPEED * direction, RANDOM.nextDouble() - Y_VEL_OFFSET),
			3 + (int)(Math.random() * 4) // 3-7
		);

	}
	return new Fish(new Point2D(), new Point2D(), new Point2D(), 0, false);
    }

    /**
     * spawnFishAroundPlayer method creates new fish around the player to populate the beautiful ocean
     * @param SpawnRate the rate of which fish should spawn.
     * @return Nothing
     */
    public static void spawnFishAroundPlayer(float spawnRate) {
	if (RANDOM.nextFloat() > spawnRate)
	    return;

	if (Math.random() <= SPAWN_CAP_BARR) { // 20% chance to spawn
	    Barracuda barracuda = (Barracuda) getFish(FishType.BARRACUDA);
	}

	float spawnChance = RANDOM.nextFloat();
	if (spawnChance < SPAWN_CAP_LAR) { // 20% chance to spawn
	    Fish fish = getFish(FishType.LARGE_FISH);
	}
	else if (spawnChance < SPAWN_CAP_MED) { // 40% chance to spawn
	    Fish fish = getFish(FishType.MEDIUM_FISH);
	}
	else if (spawnChance < SPAWN_CAP_SCHOOL) { // 30% chance to spawn
	    School school = (School) getFish(FishType.SCHOOL);
	}
	else { // 10%
	    Fish fish = getFish(FishType.SMALL_FISH);
	}

    }

    public static List<Fish> getFishList() {
	return fishList;
    }


}
