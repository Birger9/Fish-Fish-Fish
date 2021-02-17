package entity;

import game.AppPanel;
import util.Point2D;
import util.PropertiesLoaderBorrowedCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The FishFactory class is used to retrieve new fish. The class uses an enum to select fish based on its type.
 */
public class FishFactory {

    private static final Random RANDOM = new Random();
    private PropertiesLoaderBorrowedCode defaultSettings = new PropertiesLoaderBorrowedCode("src/defaultsettings");

    private List<Fish> fishList = new ArrayList<>();

    private final int screenMargin = (int) defaultSettings.getValue("fishFactory.screenMargin", int.class); // Dictates how far away from the outside of the screen that fish should spawn
    private final int barracudaAdditionalMargin = (int) defaultSettings.getValue("fishFactory.barracuda.additionalMargin", int.class);
    private final float fishSpeed = (float) defaultSettings.getValue("fishFactory.fishSpeed", float.class);

    private AppPanel appPanel;
    private float barracudaSpawnRate;
    private float largeSpawnRate;
    private float mediumSpawnRate;
    private float schoolSpawnRate;

    /**
     * Read spawn rates from props.
     */
    public FishFactory (AppPanel appPanel) {
        this.appPanel = appPanel;
        barracudaSpawnRate = (float) defaultSettings.getValue("barracuda.spawnRate", float.class);
	largeSpawnRate = (float) defaultSettings.getValue("basicEnemy.large.spawnRate", float.class);
	mediumSpawnRate = (float) defaultSettings.getValue("basicEnemy.medium.spawnRate", float.class);
	schoolSpawnRate = (float) defaultSettings.getValue("basicEnemy.school.spawnRate", float.class);
    }

    /**
     * getFish method retrieves a new fish based on the specified type.
     * @param FishType the type of fish that should be spawned.
     * @return Fish, the new fish.
     */
    public Fish getFish(FishType fishType) {
	int direction = RANDOM.nextBoolean() ? 1 : -1;
	int xPos = direction == 1 ? -screenMargin : appPanel.getScreenWidth() + screenMargin;

	final float yVelOffset = 0.5f; 
	
        switch(fishType) {
	    case SMALL_FISH:
	        return new BasicEnemy(
	        	new Point2D(xPos, RANDOM.nextDouble() * appPanel.getMapHeight()),
			new Point2D(),
			new Point2D(fishSpeed * direction, RANDOM.nextDouble() - yVelOffset),
			1, this, appPanel
		);
	    case MEDIUM_FISH:
		return new BasicEnemy(
			new Point2D(xPos, RANDOM.nextDouble() * appPanel.getMapHeight()),
			new Point2D(),
			new Point2D(fishSpeed * direction, RANDOM.nextDouble() - yVelOffset),
			2, this, appPanel
		);
	    case LARGE_FISH:
		return new BasicEnemy(
			new Point2D(xPos, RANDOM.nextDouble() * appPanel.getMapHeight()),
			new Point2D(),
			new Point2D(fishSpeed * direction, RANDOM.nextDouble() - yVelOffset),
			3, this, appPanel
		);
	    case BARRACUDA:
	        final int vel = 7;
	        return new Barracuda(
			new Point2D(xPos + barracudaAdditionalMargin * Math.signum(xPos), RANDOM.nextDouble() * appPanel.getMapHeight()),
			new Point2D(),
			new Point2D(vel * direction, 0), true, this, appPanel
		);
	    case SCHOOL:
	        final int schoolCountVariation = 4;
	        final int schoolCountLowest = 3;
	        return new School(
			new Point2D(xPos, RANDOM.nextDouble() * appPanel.getMapHeight()),
			new Point2D(),
			new Point2D(fishSpeed * direction, RANDOM.nextDouble() - yVelOffset),
			this, appPanel,
			schoolCountLowest + (int)(Math.random() * schoolCountVariation) // 3-7
		);

	}
	return new Fish(new Point2D(), new Point2D(), new Point2D(), 0, false, this, appPanel);
    }

    /**
     * spawnFishAroundPlayer method creates new fish around the player to populate the beautiful ocean
     * @param SpawnRate the rate (or frequency) of which fish should spawn.
     */
    public void spawnFishAroundPlayer(float spawnRate) {
	if (RANDOM.nextFloat() > spawnRate)
	    return;

	if (Math.random() <= barracudaSpawnRate) { // 20% chance to spawn
	    getFish(FishType.BARRACUDA);
	}
	float spawnChance = RANDOM.nextFloat();
	if (spawnChance < largeSpawnRate) { // 20% chance to spawn
	    getFish(FishType.LARGE_FISH);
	}
	else if (spawnChance < mediumSpawnRate) { // 40% chance to spawn
	    getFish(FishType.MEDIUM_FISH);
	}
	else if (spawnChance < schoolSpawnRate) { // 30% chance to spawn
	    getFish(FishType.SCHOOL);
	}
	else { // 10%
	    getFish(FishType.SMALL_FISH);
	}

    }

    /**
     * removeDead method removes fish that are marked as dead from the FishList and Universe. This has no effect on the player.
     */
    public void removeDead() {
	List<Fish> toRemove = new ArrayList<>();
	for(Fish fish : fishList){
	    if (fish.isDead && !(fish.equals(appPanel.getPlayer()))) {
		toRemove.add(fish);
	    }
	}
	for (Fish fish : toRemove) {
	    appPanel.getUniverse().getEntities().remove(fish);
	    fishList.remove(fish);
	}
    }

    public List<Fish> getFishList() {
	return fishList;
    }


}
