package entity;

import game.AppPanel;
import game.Camera;
import media.MovingText;
import util.Point2D;
import util.PropertiesLoaderBorrowedCode;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The player itself. The player constantly follows the mouse, dashes/thrusts upon clicking the left mouse button and can
 * eat other fish. Upon eating a fish, experience is awarded. With enough experience the player grows to the next level
 * and can therefore eat bigger fish.
 */
public class Player extends Fish {

    private Camera camera;
    private PropertiesLoaderBorrowedCode defaultSettings = new PropertiesLoaderBorrowedCode("src/defaultsettings");

    private final float maxVelocity = (float) defaultSettings.getValue("player.maxVelocity", float.class);
    private final float thrustTimeIncrement = (float) defaultSettings.getValue("player.thrust.timeIncrement", float.class);

    private final double thrustSpeed = (double) defaultSettings.getValue("player.thrust.speed", double.class); // Thrust speed on mouse click
    private final double thrustDuration = (double) defaultSettings.getValue("player.thrust.duration", double.class); // Thrust duration on mouse click
    private boolean isThrusting = false;

    private static final double SCALAR = 0.5f; // Used to half the player size

    // Moving text that is instantiated as the player dies.
    private final float ohNoTextVel = (float) defaultSettings.getValue("player.movingText.vel", float.class);
    private final int ohNoTextSize = (int) defaultSettings.getValue("player.movingText.size", int.class);

    private final float mouthSizeFactor = (float) defaultSettings.getValue("player.collider.mouthSizeFactor", float.class);

    private final int intialXpToNextLevel = (int) defaultSettings.getValue("player.xp.initialXpToNextLvl", int.class);
    private int xpToNextLevel = intialXpToNextLevel;

    private final int maxLevel = (int) defaultSettings.getValue("player.xp.maxLevel", int.class);
    private int experience = 0;
    private int score = 0;
    private boolean hasWon = false;

    private Point2D playerCenter = new Point2D(0, 0);
    private Point2D intialSize;

    public Player(Point2D position, Point2D size, int level, AppPanel appPanel, FishFactory fishFactory) {
        super(position, size, level, false, fishFactory, appPanel, appPanel.getImageManager().getSpriteHashMap().get("PLAYER"), true);
        setSize();

        intialSize = size;
        fishFactory.getFishList().add(this);

        camera = appPanel.getMainCam();
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     */
    public void update() {
        mouthSize = (int)(size.getY() * mouthSizeFactor);
        super.update();
        moveToMouse();

        playerCenter = Point2D.sum(position, Point2D.product(size, SCALAR)); // Position + size/2

        if (experience >= xpToNextLevel) {
            levelUp();
        }
    }

    /**
     * moveToMouse method moves the player towards the mouse
     */
    private void moveToMouse() {
        double mouseX = appPanel.getMouse().x;
        double mouseY = appPanel.getMouse().y;

        double dist = Point2D.distance(new Point2D(mouseX, mouseY), playerCenter); // Distance from mouse to player
        final int dashSmoothness = 100;
        double dx = (mouseX - playerCenter.getX()) / (dist + dashSmoothness); // Magic constant: arbitrary value
	double dy = (mouseY - playerCenter.getY()) / (dist + dashSmoothness);

	velocity.setX(maxVelocity * dx);
	velocity.setY(maxVelocity * dy);

	position.add(velocity);

	camera.update(appPanel.getUniverse());
    }

    /**
     * thrust method gives a burst of speed
     * @param Point2D new position.
     */
    public void thrust(Point2D targetPosition, double thrustSpeed, double thrustDuration) {
        appPanel.getAudioLoader().playClip("DASH");

        Point2D deltaPosition = new Point2D(
                targetPosition.getX() - playerCenter.getX(),
                targetPosition.getY() - playerCenter.getY()
        );
        double thrustAngle = Math.atan2(deltaPosition.getY(), deltaPosition.getX());

        if (!isThrusting) {
            isThrusting = true;

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            final Runnable thrust = new Runnable()
            {
                private double currentThrustSpeed;
                private float timeElapsed = 0;

                @Override public void run() {
                    currentThrustSpeed = thrustSpeed - (thrustSpeed / thrustDuration) * timeElapsed;

                    Point2D deltaVelocity = Point2D.xyComponents(currentThrustSpeed, thrustAngle);
                    position.add(deltaVelocity);
                    final int cameraDashVel = 6;
                    camera.updateMovList(Point2D.product(deltaVelocity, new Point2D(cameraDashVel, cameraDashVel)));
                    timeElapsed += thrustTimeIncrement;

                    if (currentThrustSpeed < 0.01d) { // Magic constant: arbitrary small value.
                        currentThrustSpeed = 0;
                        isThrusting = false;
                        scheduler.shutdown();
                    }
                }
            };
            scheduler.scheduleAtFixedRate(thrust, 0, 1, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * checkMouthCollision method handles the logic for if a fish eats another fish
     * @param other The other fish.
     */
    protected void updateMouthCollision(Fish other) {
        if (other.isInvulnerable) return;

        if (level >= other.level) {
            if (hasMouthCollision(other)) {
                other.die();
            }
        }
    }

    /**
     * checkMouthCollision method handles the logic for if the body of this fish collides with the mouth of another fish
     * @param other The other fish.
     */
    protected void updateBodyCollision(Fish other) {
        if (isInvulnerable) return;

        if (level < other.level) {
            if (hasBodyCollision(other)) {
                die();
            }
        }
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     */
    public void gainExperience(int increment) {
        experience += increment;
        score += increment;
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     */
    private void levelUp() {
        if(level < maxLevel) {
            experience = 0;
            level++;

            size = Point2D.product(size, 2); // Magic constant: Size is always doubled
            colliderSize = size;
            final int nextLevelXpFactor = 3;
            xpToNextLevel *= nextLevelXpFactor;
        } else if (!hasWon) {
            hasWon = true;
            final Point2D textAlignmentDivisor = new Point2D(4.0f, 2.0f);
            Point2D middleOfScreen = new Point2D(appPanel.getScreenWidth()/textAlignmentDivisor.getX(),
                                                 appPanel.getScreenHeight()/textAlignmentDivisor.getY());
            final int victoryTextSize = 100;
            appPanel.getMovingTexts().add(
                    new MovingText(middleOfScreen, 0, "YOU WIN!", victoryTextSize, Color.GREEN)
            );
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            final Runnable exitGame = new Runnable(){
                    @Override
                    public void run() {
                        System.exit(0); // Terminates the program
                    }
                };
            scheduler.schedule(exitGame, 3, TimeUnit.SECONDS); // Magic constant: Doesn't particularly matter - wait for an arbitrary small amount of time
            scheduler.shutdown(); // Closes the thread
        }
    }

    @Override
    public void die() {
        appPanel.getAudioLoader().playClip("BITE");
        final int blinkDur = 5;
        final int blinkFreq = 2;
        blink(blinkDur, blinkFreq);
        grantInvulnerability(blinkDur);
        resetStats();
        appPanel.getMovingTexts().add(
                new MovingText(position, ohNoTextVel, "OH NO!", ohNoTextSize, Color.RED)
        );
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     */
    private void resetStats() {
        level = 1; // Magic constant: Level 1 is always default, and always will be.
        experience = 0;
        score = 0;
        xpToNextLevel = intialXpToNextLevel; // Reset experience required to level up
        size = intialSize; // Reset size
        colliderSize = intialSize;
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     * @return Nothing.
     */
    @Override
    protected boolean isFacingRight() {
        return appPanel.getMouse().x > playerCenter.getX();
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     */
    public void thrust(Point2D targetPosition) {
        thrust(targetPosition, thrustSpeed, thrustDuration);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Point2D getPlayerCenter() {
        return playerCenter;
    }

    public int getScore() {
        return score;
    }

    public int getExperience() {
        return experience;
    }

    public int getXpToNextLevel() {
        return xpToNextLevel;
    }
}
