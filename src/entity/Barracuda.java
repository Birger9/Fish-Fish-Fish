package entity;

import game.AppPanel;
import media.AudioLoader;
import media.MovingText;
import util.Point2D;

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
    private static final float WAIT_TIME_SECONDS = 3;
    private static final float CHASE_TIME_SECONDS = 5;

    // Barracuda fish variables
    private static final int WIDTH_SIZE = 450; // Width of the barracuda fish
    private static final int HEIGHT_SIZE = 180; // Height of the barracuda fish

    private static final int OFFSET_X = 150; // Collider offset
    private static final int OFFSET_Y = 50;

    private static final int COLL_SIZE_X = 50; // Collider size
    private static final int COLL_SIZE_Y = 50;

    private static final int MOUTH_SIZE = 50;
    private static final int MOUTH_OFF_X = -180; // Mouth offset
    private static final int MOUTH_OFF_Y = 80;

    private static final Point2D BITTEN_WARNING_SIGN_SIZE = new Point2D(10, 10);
    private static final float WAIT_VELOCITY = 0.001f; // This is not 0 in order to maintain look direction
    private static final Point2D VELOCITY_AFTER_BITTEN = new Point2D(4, 0);
    private static final float CHASE_VELOCITY = 5;

    private static final int TAIL_BITE_XP = 1000;
    public static final int TAIL_BITE_TEXT_OFFSET_Y = 40;

    public static final float SEC_TO_MS = 1000.0f;

    private int livesLeft = 3;
    private Behaviour state = Behaviour.DEFAULT;

    private static Point2D mouthOffset = null;

    public Barracuda(final Point2D position, final Point2D size, final Point2D velocity, final int level,
		     final boolean addToUniverse)
    {
	super(position, size, velocity, level, true);
	setSize();
	sprite = AppPanel.getImageManager().getSpriteHashMap().get("BARRACUDA");
    }

    @Override
    protected void updateBodyCollider() {
        bodyCollider = getRectangle(colliderSize, colliderOffset);
    }

    @Override
    protected void updateMouthCollider() {
	mouthCollider = getRectangle(new Point2D(mouthSize, mouthSize), mouthOffset);
    }

    @Override public void update() {
	super.update();

	if (state == Behaviour.CHASING) {
	    followPlayer();
	}
    }

    /**
     * followPlayer updates the position in order to follow the player. It calculates the angle between the player and
     * the mouth collider and moves linearly in that direction. It uses velocity to set the look direction.
     * @param Nothing.
     * @return Nothing.
     */
    private void followPlayer() {
        velocity = new Point2D(0, 0);
        Point2D mouthCenter = new Point2D(mouthCollider.getX() + mouthCollider.getWidth(),
					  mouthCollider.getY() + mouthCollider.getHeight());
	Point2D playerCenter = AppPanel.getPlayer().getPlayerCenter();

        double angleToPlayer = Math.atan2(mouthCenter.getY() - playerCenter.getY(),
					  mouthCenter.getX() - playerCenter.getX());
        position.addX(-CHASE_VELOCITY * Math.cos(angleToPlayer));
        position.addY(-CHASE_VELOCITY * Math.sin(angleToPlayer));

        if (AppPanel.getPlayer().getPosition().getX() > position.getX()){
            velocity.setX(WAIT_VELOCITY);
	}
        else {
	    velocity.setX(-WAIT_VELOCITY);
	}
    }

    /**
     * checkCollision checks collision in the same way as in Fish, with a few exceptions, most notably
     * when the player "eats" the barracuda (i.e bites its tail). The level is ignored in this case.
     * @param Nothing.
     * @return Nothing.
     */

    @Override
    protected void checkMouthCollision(Fish other){
        if (mouthCollidesWith(other)) {
	    if (other.equals(AppPanel.getPlayer())) {
	        if (!AppPanel.getPlayer().isInvulnerable) {
		    other.die();
		}
	    } else {
		if (level > other.level) {
		    other.isDead = true;
		}
	    }
	}
    }

    @Override
    protected void checkBodyCollision(Fish other){
	if (other.equals(AppPanel.getPlayer())) {
	    if (bodyCollidesWith(other)) {
		if (!isInvulnerable) {
		    if (livesLeft == 1) {
		        die();
		    }
		    else {
		        initiatePlayerChase(WAIT_TIME_SECONDS);
		    }
		}
	    }
	}
    }

    /**
     * initiatePlayerChase makes the barracuda stop for a few seconds, and then calls beginChase.
     * @param waitTimeInSeconds The time to wait in seconds.
     * @return Nothing.
     */
    private void initiatePlayerChase(float waitTimeInSeconds) {

	if (state != Behaviour.WAITING) {
	    giveTailBiteXp();
	    AudioLoader.playClip("BITE");
	    velocity = new Point2D(WAIT_VELOCITY * Math.signum(velocity.getX()), 0);
	    isInvulnerable = true;
	    livesLeft--;
	}
	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	final Runnable startChase = new Runnable()
	{
	    @Override public void run() {
		beginChase(CHASE_TIME_SECONDS);
	    }
	};
	if (state != Behaviour.WAITING) {
	    state = Behaviour.WAITING;
	    scheduler.schedule(startChase, (long) (SEC_TO_MS * waitTimeInSeconds), TimeUnit.MILLISECONDS);
	}
    }

    /**
     * beginChase makes the barracuda chase the player for a few seconds, and then goes back to its default state.
     * @param chaseTimeSeconds The time to chase the player in seconds.
     * @return Nothing.
     */
    private void beginChase(float chaseTimeSeconds) {
	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	final Runnable stopChase = new Runnable()
	{
	    @Override public void run() {
	        velocity = Point2D.product(VELOCITY_AFTER_BITTEN, Math.signum(velocity.getX()));
		state = Behaviour.DEFAULT;
		isInvulnerable = false;
	    }
	};
	if (state != Behaviour.CHASING) {
	    state = Behaviour.CHASING;
	    scheduler.schedule(stopChase, (long) (SEC_TO_MS * chaseTimeSeconds), TimeUnit.MILLISECONDS);
	}
    }

    /**
     * getRectangle retrieves a rectangle that automatically gets mirrored on its x-axis if the barracuda changes its direction.
     * @param colSize The size of the collider.
     * @param colOffset The offset of the collider.
     * @return Nothing.
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
     * @param Nothing
     * @return Nothing.
     */
    private void giveTailBiteXp() {
	AppPanel.getPlayer().gainExperience(TAIL_BITE_XP);
	MovingText.getMovingTexts().add(
		new MovingText(AppPanel.getPlayer().position, MOVING_TEXT_VEL, "+" + TAIL_BITE_XP, MOVING_TEXT_SIZE, Color.WHITE)
	);
	MovingText.getMovingTexts().add(
		new MovingText(Point2D.sum(AppPanel.getPlayer().position, new Point2D(0, TAIL_BITE_TEXT_OFFSET_Y)),
			       MOVING_TEXT_VEL, "TAIL BITE!", MOVING_TEXT_SIZE, Color.ORANGE)
	);
    }

    @Override
    protected void setSize() {
        size = new Point2D(WIDTH_SIZE, HEIGHT_SIZE);
        colliderOffset = new Point2D(OFFSET_X, OFFSET_Y);
        colliderSize = new Point2D(COLL_SIZE_X, COLL_SIZE_Y);
        mouthOffset = new Point2D(MOUTH_OFF_X, MOUTH_OFF_Y);
	mouthSize = MOUTH_SIZE;
    }

    /**
     * Enum to hold the current behaviour.
     */
    private enum Behaviour
    {
        DEFAULT, WAITING, CHASING
    }

}
