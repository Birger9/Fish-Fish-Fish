package entity;

import game.AppPanel;
import media.MovingText;
import util.Point2D;
import util.PropertiesLoaderBorrowedCode;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Barracuda is a large fish that eats everything else. If the player bites its tail, it stops for a few seconds and
 * then starts chasing the player. If the player bites the barracuda three times, it dies.
 * It uses an enum that cointains its current state, DEFAULT (its normal behaviour), WAITING (which it enters when its tail
 * is bitten) and CHASING (when it chases the player). After chasing the player for a few seconds, it returns to DEFAULT with
 * a slightly lower velocity.
 */
public class Barracuda extends Fish {

    private PropertiesLoaderBorrowedCode defaultSettings = new PropertiesLoaderBorrowedCode("src/defaultsettings");

    private final float waitTimeSeconds = (float) defaultSettings.getValue("barracuda.wait.time", float.class);
    private final float chaseTimeSeconds = (float) defaultSettings.getValue("barracuda.chase.time", float.class);

    private final int width = (int) defaultSettings.getValue("barracuda.size.width", int.class); // Width of the barracuda fish
    private final int height = (int) defaultSettings.getValue("barracuda.size.height", int.class); // Height of the barracuda fish

    private static final float WAIT_VELOCITY = 0.001f; // Arbitrary small value. Used to maintain look direction.
    private final float chaseVelocity = (int) defaultSettings.getValue("barracuda.chase.velocity", int.class);
    private final Point2D velocityAfterBitten = new Point2D(4, 0);

    private final int tailBiteXp = (int) defaultSettings.getValue("barracuda.xp.tailBite", int.class);
    private final int tailBiteTextOffsetY = (int) defaultSettings.getValue("barracuda.xp.tailBite.text.offset.y", int.class);

    private int livesLeft = 3;
    private Behaviour state = Behaviour.DEFAULT;

    private Point2D mouthOffset = null;

    public Barracuda(final Point2D position, final Point2D size, final Point2D velocity,
		     final boolean addToUniverse, FishFactory fishFactory, AppPanel appPanel)
    {
	super(position, size, velocity, 15, true, fishFactory, appPanel, // Magic number: Arbitrary high level (doesn't really matter).
	      appPanel.getImageManager().getSpriteHashMap().get("BARRACUDA"));
	setSize();
    }

    /**
     * Update body collider.
     */
    @Override
    protected void updateBodyCollider() {
        bodyCollider = getRectangle(colliderSize, colliderOffset);
    }

    /**
     * Update mouth collider.
     */
    @Override
    protected void updateMouthCollider() {
	mouthCollider = getRectangle(new Point2D(mouthSize, mouthSize), mouthOffset);
    }

    /**
     * Update overridden to account for behaviour.
     */
    @Override public void update() {
	super.update();

	if (state == Behaviour.CHASING) {
	    followPlayer();
	}
    }

    /**
     * followPlayer updates the position in order to follow the player. It calculates the angle between the player and
     * the mouth collider and moves linearly in that direction. It uses velocity to set the look direction.
     */
    private void followPlayer() {
        velocity = new Point2D(0, 0);
        Point2D mouthCenter = new Point2D(mouthCollider.getX() + mouthCollider.getWidth(),
					  mouthCollider.getY() + mouthCollider.getHeight());
	Point2D playerCenter = appPanel.getPlayer().getPlayerCenter();

        double angleToPlayer = Math.atan2(mouthCenter.getY() - playerCenter.getY(),
					  mouthCenter.getX() - playerCenter.getX());
        position.addX(-chaseVelocity * Math.cos(angleToPlayer));
        position.addY(-chaseVelocity * Math.sin(angleToPlayer));

        if (appPanel.getPlayer().getPosition().getX() > position.getX()){
            velocity.setX(WAIT_VELOCITY);
	}
        else {
	    velocity.setX(-WAIT_VELOCITY);
	}
    }

    /**
     * checkCollision checks collision in the same way as in Fish, with a few exceptions, most notably
     * when the player "eats" the barracuda (i.e bites its tail). The level is ignored in this case.
     * @param other The other fish.
     */
    @Override
    protected void updateMouthCollision(Fish other){
        if (hasMouthCollision(other)) {
	    if (other.equals(appPanel.getPlayer())) {
	        if (!appPanel.getPlayer().isInvulnerable) {
		    other.die();
		}
	    } else {
		if (level > other.level) {
		    other.isDead = true;
		}
	    }
	}
    }

    /**
     * checkCollision checks collision in the same way as in Fish, with a few exceptions, most notably
     * that the barracuda has three lives. The level is ignored in this case.
     * @param other The other fish.
     */
    @Override
    protected void updateBodyCollision(Fish other){
	if (other.equals(appPanel.getPlayer())) {
	    if (hasBodyCollision(other)) {
		if (!isInvulnerable) {
		    if (livesLeft == 1) { // Magic number: Given the context of the usage, what it refers to seems obvious enough.
		        die();
		    }
		    else {
		        initiatePlayerChase(waitTimeSeconds);
		    }
		}
	    }
	}
    }

    /**
     * initiatePlayerChase makes the barracuda stop for a few seconds, and then calls beginChase.
     * @param waitTimeInSeconds The time to wait in seconds.
     */
    private void initiatePlayerChase(float waitTimeInSeconds) {

	if (state != Behaviour.WAITING) {
	    giveTailBiteXp();
	    appPanel.getAudioLoader().playClip("BITE");
	    velocity = new Point2D(WAIT_VELOCITY * Math.signum(velocity.getX()), 0);
	    isInvulnerable = true;
	    livesLeft--;
	}
	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	final Runnable startChase = new Runnable()
	{
	    @Override public void run() {
		beginChase(chaseTimeSeconds);
	    }
	};
	if (state != Behaviour.WAITING) {
	    state = Behaviour.WAITING;
	    final float msToSec = 1000.0f;
	    scheduler.schedule(startChase, (long) (msToSec * waitTimeInSeconds), TimeUnit.MILLISECONDS); // Magic constant: Used to convert sec --> ms, seems obvious enough.
	}
    }

    /**
     * beginChase makes the barracuda chase the player for a few seconds, and then goes back to its default state.
     * @param chaseTimeSeconds The time to chase the player in seconds.
     */
    private void beginChase(float chaseTimeSeconds) {
	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	final Runnable stopChase = new Runnable()
	{
	    @Override public void run() {
	        velocity = Point2D.product(velocityAfterBitten, Math.signum(velocity.getX()));
		state = Behaviour.DEFAULT;
		isInvulnerable = false;
	    }
	};
	if (state != Behaviour.CHASING) {
	    state = Behaviour.CHASING;
	    final float msToSec = 1000.0f;
	    scheduler.schedule(stopChase, (long) (msToSec * chaseTimeSeconds), TimeUnit.MILLISECONDS); // Magic constant: Used to convert sec --> ms, seems obvious enough.
	}
    }

    /**
     * getRectangle retrieves a rectangle that automatically gets mirrored on its x-axis if the barracuda changes its direction.
     * @param colSize The size of the collider.
     * @param colOffset The offset of the collider.
     * @return the rectangle.
     */
    private Rectangle getRectangle(Point2D colSize, Point2D colOffset) {
	double centerX = position.getX() + size.getX()/2;

	double colliderOffsetX = colOffset.getX();
	if (facingRight) {
	    colliderOffsetX = -colOffset.getX();
	}
	Point2D colliderPos = new Point2D(centerX + colliderOffsetX - colSize.getX()/2, position.getY() + colOffset.getY());
	return new Rectangle(
		(int)colliderPos.getX(),
		(int)colliderPos.getY(),
		(int)colSize.getX(),
		(int)colSize.getY()
	);
    }

    /**
     * giveTailBiteXp Gives the player xp for biting the tail, and adds a "tail bite" text.
     */
    private void giveTailBiteXp() {
	appPanel.getPlayer().gainExperience(tailBiteXp);
	appPanel.getMovingTexts().add(
		new MovingText(appPanel.getPlayer().position, movingTextVel, "+" + tailBiteXp, movingTextSize, Color.WHITE)
	);
	appPanel.getMovingTexts().add(
		new MovingText(Point2D.sum(appPanel.getPlayer().position, new Point2D(0, tailBiteTextOffsetY)),
			       movingTextVel, "TAIL BITE!", movingTextSize, Color.ORANGE)
	);
    }

    /**
     * setSize sets the size of the barracuda to its default values.
     */
    @Override
    protected void setSize() {
        size = new Point2D(width, height);

	final int colliderOffsetX = (int) defaultSettings.getValue("barracuda.collider.offset.x", int.class); // Collider offset
	final int colliderOffsetY = (int) defaultSettings.getValue("barracuda.collider.offset.y", int.class);
	final int colliderSizeX = (int) defaultSettings.getValue("barracuda.collider.size.x", int.class); // Collider size
	final int colliderSizeY = (int) defaultSettings.getValue("barracuda.collider.size.y", int.class);

        colliderOffset = new Point2D(colliderOffsetX, colliderOffsetY);
        colliderSize = new Point2D(colliderSizeX, colliderSizeY);

	final int mouthCollOffsetX = (int) defaultSettings.getValue("barracuda.collider.mouth.offset.x", int.class); // Mouth offset
	final int mouthCollOffsetY = (int) defaultSettings.getValue("barracuda.collider.mouth.offset.y", int.class);
        mouthOffset = new Point2D(mouthCollOffsetX, mouthCollOffsetY);
	mouthSize = (int) defaultSettings.getValue("barracuda.collider.mouth.size", int.class);
    }

    /**
     * Enum to hold the current behaviour.
     */
    private enum Behaviour
    {
        DEFAULT, WAITING, CHASING
    }

}
