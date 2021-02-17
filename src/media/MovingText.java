package media;

import entity.Entity;
import util.Point2D;

import java.awt.*;

/**
 * The MovingText class displays text that moves to a specified position and disappears once it reaches its target position
 */
public class MovingText extends Entity
{
    private float velocity;
    private String value;
    private int textSize;
    private Color color;

    private boolean hasReached = false;
    private static final int DISTANCE_MARGIN = 10;

    /**
     * Constructor that sets velocity, value, text size and color of the moving text object
     */
    public MovingText(Point2D position, float velocity, String value, int textSize, Color color){
        super(position, new Point2D(), null);

	this.velocity = velocity;
	this.value = value;
	this.textSize = textSize;
	this.color = color;
    }

    /**
     * moveToPosition method sets a target position and increments the position of the
     * text both in x- and y-pos. When the distance left is lesser than the distance margin.
     * Set hasReached to true, the text has reached the target position
     * @param target The position of the target coordinates.
     */
    public void moveToPosition(Point2D target) {
	double dist = Point2D.distance(target, position);
	double dx = (target.getX() - position.getX()) / (dist + 100);
	double dy = (target.getY() - position.getY()) / (dist + 100);

	position.addX(dx * velocity);
	position.addY(dy * velocity);

	if(dist < DISTANCE_MARGIN) {
	    hasReached = true;
	}
    }

    /**
     * The overrided render method sets a color of the moving text, font and draws it
     * @param Graphics object.
     */
    @Override
    public void render(Graphics g) {
	g.setColor(color);
	g.setFont(new Font("Courier New", Font.BOLD, textSize));
	g.drawString(value, (int) position.getX(), (int) position.getY());
    }

    public boolean getHasReached() {
        return hasReached;
    }
}
