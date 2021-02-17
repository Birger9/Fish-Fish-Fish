package entity;

import game.AppPanel;
import media.MovingText;
import media.Sprite;
import util.Point2D;
import util.PropertiesLoaderBorrowedCode;

import java.awt.*;
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

    private PropertiesLoaderBorrowedCode defaultSettings = new PropertiesLoaderBorrowedCode("src/defaultsettings");

    // Load default props
    // Moving text variables
    protected final float movingTextVel = (float) defaultSettings.getValue("fish.movingText.vel", float.class);
    protected final int movingTextSize = (int) defaultSettings.getValue("fish.movingText.size", int.class);
    private final int distanceOfRemoval = (int) defaultSettings.getValue("fish.distanceOfRemoval", int.class); // Remove fish that are this many pixels away from the player

    // EXPERIENCE FORMULA
    // TOTAL_FACTOR * (EXP_FACTOR * LEVEL^EXPONENT)
    private final static float TOTAL_FACTOR = 100;
    private final static float EXP_FACTOR = 3;
    private final static float EXPONENT = 1.5f;

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

    private FishFactory factory;

    /**
     * Constructor that initializes a Fish object, adds the object to the universe and
     * the fishList.
     */
    public Fish(Point2D position, Point2D size, Point2D velocity, int level, boolean addToUniverse, FishFactory factory, AppPanel appPanel) {
        super(position, size, appPanel);
        this.velocity = velocity;
        this.level = level;

        colliderSize = size;
        factory.getFishList().add(this);
        this.factory = factory;

        if (addToUniverse)
            appPanel.getUniverse().addEntity(this);
    }

    /**
     * Constructor that initializes a Fish object, adds the object to the universe and
     * the fishList. This is for fish that have no specific velocity upon initialization
     */
    public Fish(Point2D position, Point2D size, int level, boolean addToUniverse, FishFactory factory, AppPanel appPanel) {
        super(position, size, appPanel);
        this.level = level;

        velocity = new Point2D(0, 0);
        colliderSize = size;
        factory.getFishList().add(this);
        this.factory = factory;

        if (addToUniverse)
            appPanel.getUniverse().addEntity(this);
    }

    public Fish(Point2D position, Point2D size, int level, boolean addToUniverse, FishFactory factory, AppPanel appPanel, Sprite sprite, boolean flipSprite) {
        super(position, size, sprite, appPanel);
        this.level = level;

        velocity = new Point2D(0, 0);
        colliderSize = size;
        this.flipSprite = flipSprite;
        factory.getFishList().add(this);
        this.factory = factory;

        if (addToUniverse)
            appPanel.getUniverse().addEntity(this);
    }

    public Fish(Point2D position, Point2D size, Point2D velocity, int level, boolean addToUniverse, FishFactory factory, AppPanel appPanel, Sprite sprite) {
        super(position, size, sprite, appPanel);
        this.velocity = velocity;
        this.level = level;

        colliderSize = size; // Default size (if the size parameter is specified)
        factory.getFishList().add(this);
        this.factory = factory;

        if (addToUniverse)
            appPanel.getUniverse().addEntity(this);
    }

    /**
     * update method updates collision boxes, movement, and kills the Fish
     * if it is too far from the player
     */
    public void update() {
        facingRight = isFacingRight(); // Set look direction based on x velocity

        if (velocity != null) {
            move(velocity);
        }
        // Kill this fish if distance to the player is too great
        if (!this.equals(appPanel.getPlayer())) {
            if (appPanel.getPlayer().getPlayerCenter().distanceTo(this.position) > distanceOfRemoval) {
                isDead = true;
            }
        }
        updateBodyCollider();
        updateMouthCollider();

        updateCollision();
    }

    /**
     * Check mouth/body collision with all the other fish.
     */
    protected void updateCollision() {
        for(Fish other : factory.getFishList()) {
            if (other.equals(this) || other.isDead) {
                continue;
            }
            updateMouthCollision(other);
            updateBodyCollision(other);
        }

    }

    /**
     * checkMouthCollision method handles the logic for if a fish eats another fish
     * @param other The other fish.
     */
    protected void updateMouthCollision(Fish other) {
        if (other.isInvulnerable) return;

        if (level > other.level) {
            if (hasMouthCollision(other)) {
                other.isDead = true;
            }
        }
    }

    /**
     * checkMouthCollision method handles the logic for if the body of this fish collides with the mouth of another fish
     * @param other The other fish.
     */
    protected void updateBodyCollision(Fish other) {

    }

    /**
     * die method is called when a fish dies. This should, amongst other things, award xp to the player
     */
    public void die() {
        if (isDead) return;
        int experience = xpFromLevel(level);
        appPanel.getPlayer().gainExperience(experience);
        appPanel.getMovingTexts().add(
                new MovingText(appPanel.getPlayer().position, movingTextVel, "+" + experience, movingTextSize, Color.WHITE)
        );
        appPanel.getAudioLoader().playClip("BITE");
        isDead = true;
    }

    /**
     * xpFromLevel method returns how much experience the fish should give upon dying
     * @param level The level of the fish.
     * @return The amount of experience that should be gained.
     */
    protected static int xpFromLevel(int level) {
        return (int) (TOTAL_FACTOR * (EXP_FACTOR * Math.pow(level, EXPONENT)));
    }

    /**
     * setSize method sets the the mouthsize and size of the fish. This should normally be overridden in each respective
     * subclass that extends Fish in order to properly scale the fish to fit its corresponding sprite dimensions.
     */
    protected void setSize(){
        size = Point2D.product(size, level); // Scale manually
        final int mouthScaleFactor = 10;
        mouthSize = level * mouthScaleFactor;
    }

    /**
     * render method renders the sprite of the fish, depending on which way
     * it is facing.
     * @param g The graphics object.
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
     */
    @Override
    protected void remove() {
        appPanel.getUniverse().removeEntity(this);
        factory.getFishList().remove(this);
    }

    /**
     * grantInvulnerability method makes a fish invulnerable for a set amount of time.
     * @param durationInSeconds The blink duration in seconds.
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
            scheduler.schedule(turnOffInvulnerability, (long) (1000 * durationInSeconds), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * mouthCollidesWith method returns True if a mouthCollider collides with an other
     * fish bodyCollider. Otherwise False
     * @param other The other fish.
     * @return true if the given mouthCollider colliders with the bodyCollider of another (specified) fish.
     */
    protected boolean hasMouthCollision(Fish other) {
        return mouthCollider.intersects(other.bodyCollider);
    }

    /**
     * Return true if a bodyCollider collides with the mouthCollider of another fish.
     * @param other The other fish.
     * @return true if the given bodyCollider colliders with the mouthCollider of another (specified) fish.
     */
    protected boolean hasBodyCollision(Fish other) {
        return bodyCollider.intersects(other.mouthCollider);
    }


    /**
     * Return whether or not a fish should be facing left or right, based on its velocity.
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
