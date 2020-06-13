package entity;

import game.AppPanel;
import game.Camera;
import media.AudioLoader;
import media.MovingText;
import util.Point2D;

import java.awt.*;
import java.util.Timer;
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

    private static final double SCALAR = 0.5; // Used to half the player size
    private static final float MAX_VELOCITY = 1.5f;
    private static final float THRUST_TIME_INCREMENT = 0.01f;

    //When the player dies, moving text
    private static final float OH_NO_TEXT_VEL = 2.0f;
    private static final int OH_NO_TEXT_SIZE = 30;

    private double angle = 0;

    public static Timer endTimer = null;

    private static final double THRUST_SPEED = 1.5d; // Thrust speed on mouse click
    private static final double THRUST_DURATION = 1.5d; // Thrust duration on mouse click
    private boolean isThrusting = false;

    private static final int MAX_LEVEL = 3;
    private int experience = 0;
    private static final int INTIAL_XP_TO_NEXT_LEVEL = 7000;
    private int xpToNextLevel = INTIAL_XP_TO_NEXT_LEVEL;
    private int score = 0;
    private boolean hasWon = false;

    private Point2D playerCenter = new Point2D(0, 0);
    private Point2D positionLastFrame = null;
    private Point2D intialSize;
    private static final float MOUTH_SIZE_FACTOR = 0.5f;

    public Player(Point2D position, Point2D size, int level) {
        super(position, size, level, false);
        setSize();

        intialSize = size;
        sprite = AppPanel.getImageManager().getSpriteHashMap().get("PLAYER");
        flipSprite = true;
        FishFactory.getFishList().add(this);

        camera = AppPanel.getMainCam();
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     * @return Nothing.
     */
    public void update() {
        mouthSize = (int)(size.getY() * MOUTH_SIZE_FACTOR);
        super.update();
        moveToMouse();

        playerCenter = Point2D.sum(position, Point2D.product(size, SCALAR)); // Position + size/2

        if (experience >= xpToNextLevel) {
            levelUp();
        }
    }

    /**
     * moveToMouse method moves the player towards the mouse
     * @param Nothing
     * @return Nothing.
     */
    private void moveToMouse() {
        double mouseX = AppPanel.getMouse().x;
        double mouseY = AppPanel.getMouse().y;

        double dist = Point2D.distance(new Point2D(mouseX, mouseY), playerCenter); // Distance from mouse to player
        double dx = (mouseX - playerCenter.getX()) / (dist + 100);
	double dy = (mouseY - playerCenter.getY()) / (dist + 100);

	velocity.setX(MAX_VELOCITY * dx);
	velocity.setY(MAX_VELOCITY * dy);

	position.add(velocity);

	camera.update(AppPanel.getUniverse());
    }

    /**
     * thrust method gives a burst of speed
     * @param Point2D new position.
     * @return Nothing.
     */
    public void thrust(Point2D targetPosition, double thrustSpeed, double thrustDuration) {
        AudioLoader.playClip("DASH");

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
                    camera.updateMovList(Point2D.product(deltaVelocity, new Point2D(6, 6)));
                    timeElapsed += THRUST_TIME_INCREMENT;

                    if (currentThrustSpeed < 0.01d) {
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
     * @param Nothing.
     * @return Nothing.
     */
    protected void checkMouthCollision(Fish other) {
        if (other.isInvulnerable) return;

        if (level >= other.level) {
            if (mouthCollidesWith(other)) {
                other.die();
            }
        }
    }

    /**
     * checkMouthCollision method handles the logic for if the body of this fish collides with the mouth of another fish
     * @param Nothing.
     * @return Nothing.
     */
    protected void checkBodyCollision(Fish other) {
        if (isInvulnerable) return;

        if (level < other.level) {
            if (bodyCollidesWith(other)) {
                die();
            }
        }
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     * @return Nothing.
     */
    public void gainExperience(int increment) {
        experience += increment;
        score += increment;
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     * @return Nothing.
     */
    private void levelUp() {
        if(level < MAX_LEVEL) {
            experience = 0;
            level++;

            size = Point2D.product(size, 2);
            colliderSize = size;
            xpToNextLevel *= 3;
        }else if (!hasWon) {
                hasWon = true;
                Point2D middleOfScreen = new Point2D(AppPanel.getScreenWidth()/4, AppPanel.getScreenHeight()/2);
                // It's divided by 4 so the text "You win!" is in the middle of the screen

                MovingText.getMovingTexts().add(
                        new MovingText(middleOfScreen, 0, "YOU WIN!", 100, Color.GREEN)
                );

                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                final Runnable exitGame = new Runnable(){
                    @Override
                    public void run() {
                        System.exit(0); // Terminates the program
                    }
                };
                scheduler.schedule(exitGame, (3), TimeUnit.SECONDS); // Schedules a new action in 3 seconds
                scheduler.shutdown(); // Closes the thread
            }

    }

    @Override
    public void die() {
        AudioLoader.playClip("BITE");
        blink(5, 2);
        grantInvulnerability(5);
        resetStats();
        MovingText.getMovingTexts().add(
                new MovingText(AppPanel.getPlayer().position, OH_NO_TEXT_VEL, "OH NO!", OH_NO_TEXT_SIZE, Color.RED)
        );
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     * @return Nothing.
     */
    private void resetStats() {
        level = 1;
        experience = 0;
        score = 0;
        xpToNextLevel = INTIAL_XP_TO_NEXT_LEVEL; // Reset experience required to level up
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
        return AppPanel.getMouse().x > playerCenter.getX();
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     * @return Nothing.
     */
    public void thrust(Point2D targetPosition) {
        thrust(targetPosition, THRUST_SPEED, THRUST_DURATION);
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     * @return Nothing.
     */
    public Point2D velocityVector() {
        return Point2D.xyComponents(MAX_VELOCITY, angle);
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
