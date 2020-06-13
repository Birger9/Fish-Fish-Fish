package entity;

import game.AppPanel;
import media.AudioLoader;
import media.MovingText;
import util.Point2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Fish class contains all the logic and information for a Fish object. A fish is an entity, moving or otherwise, that
 * has support for collision with a mouth collider as well as a body collider. Upon death, it awards experience to the player.
 * It can be instantiated directly, but is mainly intended to be extended by each corresponding type of fish (such as the
 * player, barracudas, etc)
 */
public class Fish extends Entity {

    // Moving text varibles
    protected static final float MOVING_TEXT_VEL = 10.0f;
    protected static final int MOVING_TEXT_SIZE = 15;
    public static final float SEC_TO_MS = 1000.0f;

    protected Point2D velocity;
    protected int level;

    protected boolean facingRight = true;
    protected boolean flipSprite = false;

    // MOUTH COLLIDER
    protected Rectangle mouthCollider = new Rectangle();
    protected int mouthSize;
    protected int mouthOffsetY = 0;

    // BODY COLLIDER
    protected Rectangle bodyCollider = new Rectangle();
    protected Point2D colliderSize;
    protected Point2D colliderOffset = new Point2D(0, 0);

    protected boolean isInvulnerable = false;
    protected boolean isDead = false; // Entities that should be removed from the game (incl. universe & fishList)
    private static final int DISTANCE_OF_REMOVAL = 1900; // Remove fish that are this many pixels away from the player

    // EXPERIENCE FORMULA
    // TOTAL_FACTOR * (EXP_FACTOR * LEVEL^EXPONENT)
    private static final float TOTAL_FACTOR = 100.0f;
    private static final float EXP_FACTOR = 3.0f;
    private static final float EXPONENT = 1.5f;


    private static final Random RANDOM = new Random();

    /**
     * Constructor that initializes a Fish object, adds the object to the universe and
     * the fishList.
     */
    public Fish(Point2D position, Point2D size, Point2D velocity, int level, boolean addToUniverse) {
        super(position, size);
        this.velocity = velocity;
        this.level = level;

        colliderSize = size; // Default size (if the size parameter is specified)
        FishFactory.getFishList().add(this);

        if (addToUniverse)
            AppPanel.getUniverse().addEntity(this);
    }

    /**
     * Constructor that initializes a Fish object, adds the object to the universe and
     * the fishList. This is for fish that have no specific velocity upon initialization
     */
    public Fish(Point2D position, Point2D size, int level, boolean addToUniverse) {
        super(position, size);
        this.level = level;

        velocity = new Point2D(0, 0);
        colliderSize = size; // Default size (if the size parameter is specified)
        FishFactory.getFishList().add(this);

        if (addToUniverse)
            AppPanel.getUniverse().addEntity(this);
    }

    /**
     * update method updates collision boxes, movement, and kills the Fish
     * if it is too far from the player
     * @param Nothing.
     * @return Nothing.
     */
    public void update() {
        facingRight = isFacingRight(); // Set look direction based on x velocity

        if (velocity != null) {
            move(velocity);
        }
        // Kill this fish if distance to the player is too great
        if (!this.equals(AppPanel.getPlayer())) {
            if (AppPanel.getPlayer().getPlayerCenter().distanceTo(this.position) > DISTANCE_OF_REMOVAL) {
                isDead = true;
            }
        }
        updateBodyCollider();
        updateMouthCollider();

        checkCollision();
    }

    protected void checkCollision() {
        for(Fish other : FishFactory.getFishList()) {
            if (other.equals(this) || other.isDead) {
                continue;
            }
            checkMouthCollision(other);
            checkBodyCollision(other);
        }

    }

    /**
     * checkMouthCollision method handles the logic for if a fish eats another fish
     * @param Nothing.
     * @return Nothing.
     */
    protected void checkMouthCollision(Fish other) {
        if (other.isInvulnerable) return;

        if (level > other.level) {
            if (mouthCollidesWith(other)) {
                other.isDead = true;
            }
        }
    }

    /**
     * checkMouthCollision method handles the logic for if the body of this fish collides with the mouth of another fish
     * @param Nothing.
     * @return Nothing.
     */
    protected void checkBodyCollision(Fish other) {

    }

    /**
     * die method is called when a fish dies. This should, amongst other things, award xp to the player
     * @param Nothing.
     * @return Nothing.
     */
    public void die() {
        if (isDead) return;
        int experience = xpFromLevel(level);
        AppPanel.getPlayer().gainExperience(experience);
        MovingText.getMovingTexts().add(
                new MovingText(AppPanel.getPlayer().position, MOVING_TEXT_VEL, "+" + experience, MOVING_TEXT_SIZE, Color.WHITE)
        );
        AudioLoader.playClip("BITE");
        isDead = true;
    }

    /**
     * removeDead method removes fish that are marked as dead from the FishList and Universe. This has no effect on the player.
     * @param Nothing.
     * @return Nothing.
     */
    public static void removeDead() {
        List<Fish> toRemove = new ArrayList<>();
        for(Fish fish : FishFactory.getFishList()){
            if (fish.isDead && !(fish.equals(AppPanel.getPlayer()))) {
                toRemove.add(fish);
            }
        }
        for (Fish fish : toRemove) {
            AppPanel.getUniverse().getEntityList().remove(fish);
            FishFactory.getFishList().remove(fish);
        }
    }

    /**
     * xpFromLevel method returns how much experience the fish should give upon dying
     * @param int, the level of the fish.
     * @return int, the amount of experience that should be gained.
     */
    protected static int xpFromLevel(int level) {
        return (int) (TOTAL_FACTOR * (EXP_FACTOR * Math.pow(level, EXPONENT)));
    }

    /**
     * setSize method sets the the mouthsize and size of the fish. This should normally be overridden in each respective
     * subclass that extends Fish in order to properly scale the fish to fit its corresponding sprite dimensions.
     * @param Nothing.
     * @return Nothing.
     */
    protected void setSize(){
        size = Point2D.product(size, level); // Scale manually
        mouthSize = level * 10;
    }

    /**
     * render method renders the sprite of the fish, depending on which way
     * it is facing.
     * @param Graphics object.
     * @return Nothing.
     */
    @Override
    public void render(Graphics g) {
        if (!render) return;
        if(hasSprite()) {
            if (facingRight != flipSprite)
                g.drawImage(sprite.getBufferedImage(),
                            (int)(position.getX() + size.getX()),
                            (int)position.getY(),
                            -(int)size.getX(),
                            (int)size.getY(),
                            null);
            else
                g.drawImage(sprite.getBufferedImage(),
                            (int)position.getX(), (int)position.getY(),
                            (int)size.getX(),
                            (int)size.getY(),
                            null);
        } else {
            g.setColor(Color.WHITE);
            g.fillRect((int)position.getX(), (int)position.getY(), (int)size.getX(), (int)size.getY());
        }

        // DEBUG COLLIDER BOUNDS
        if (AppPanel.inDebugMode()) {
            renderColliderBounds(g);
        }
    }

    /**
     * renderColliderBounds method renders the mouth and body collide boxes for the fish
     * @param Graphics object.
     * @return Nothing.
     */
    private void renderColliderBounds(Graphics g) {
        // DRAW BODY COLLIDER BOUNDS
        g.setColor(Color.GREEN);
        g.drawRect(bodyCollider.x, bodyCollider.y, bodyCollider.width, bodyCollider.height);

        // DRAW MOUTH COLLIDER BOUNDS
        g.setColor(Color.RED);
        g.drawRect(mouthCollider.x, mouthCollider.y, mouthCollider.width, mouthCollider.height);
    }

    /**
     * updateBodyCollider method sets the bounds of the bodyCollider
     * @param Nothing.
     * @return Nothing.
     */
    protected void updateBodyCollider() {
        bodyCollider.setBounds(
                (int) ((position.getX() + (size.getX() - colliderSize.getX()) / 2) + colliderOffset.getX()),
                (int) ((position.getY() + (size.getY() - colliderSize.getY()) / 2) + colliderOffset.getY()),
                (int) colliderSize.getX(),
                (int) colliderSize.getY()
        );
    }

    /**
     * updateMouthCollider method sets the mouth collider bounds and moves it to the left/right side of the fish based on the
     * direction it is facing.
     * @param Nothing.
     * @return Nothing.
     */
    protected void updateMouthCollider() {
        int xPos = (int)(position.getX()); // Collider should be on the left side
        if (facingRight) // entity.Fish is headed right
            xPos += (int)(size.getX() - mouthSize); // Collider should be on the right side

        mouthCollider.setBounds(xPos,
                               (int) (bodyCollider.y + (colliderSize.getY() - mouthSize) / 2) + mouthOffsetY,
                               mouthSize,
                               mouthSize
        );
    }

    /**
     * remove method removes the fish from the FishList in addition to removing it from the universe.
     * @param Nothing.
     * @return Nothing.
     */
    @Override
    protected void remove() {
        AppPanel.getUniverse().removeEntity(this);
        FishFactory.getFishList().remove(this);
    }

    /**
     * render method periodically toggles the visibility of an entity
     * @param durationInSeconds The blink duration in seconds.
     * @param frequency The blink frequency in seconds.
     * @return Nothing.
     */
    public void grantInvulnerability(float durationInSeconds) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        final Runnable turnOffInvulnerability = new Runnable()
        {
            @Override public void run() {
                isInvulnerable = false;
            }
        };
        if (!isInvulnerable) {
            isInvulnerable = true;
            scheduler.schedule(turnOffInvulnerability, (long) (SEC_TO_MS * durationInSeconds), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * mouthCollidesWith method returns True if a mouthCollider collides with an other
     * fish bodyCollider. Otherwise False
     * @param other The other fish.
     * @return true if the given mouthCollider colliders with the bodyCollider of another (specified) fish.
     */
    protected boolean mouthCollidesWith(Fish other) {
        return mouthCollider.intersects(other.bodyCollider);
    }

    /**
     * mouthCollidesWith method returns true if a bodyCollider collides with the
     * mouthCollider of another fish, and false otherwise.
     * @param other The other fish.
     * @return true if the given bodyCollider colliders with the mouthCollider of another (specified) fish.
     */
    protected boolean bodyCollidesWith(Fish other) {
        return bodyCollider.intersects(other.mouthCollider);
    }


    /**
     * isFacingRight method returns true if the velocity is greater than 0. Then we know that the fish
     * is traveling to the right. Returns false otherwise.
     * @param Nothing.
     * @return Nothing.
     */
    protected boolean isFacingRight() {
        if (velocity != null)
            return velocity.getX() > 0;
        return true;
    }

    public Point2D getVelocity() {
        return velocity;
    }


}
