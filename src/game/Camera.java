package game;

import util.Point2D;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Camera class handles the camera movement and follows the player on the screen. This is done by moving every object
 * on the screen.
 */
public class Camera {

    private Point2D currentPosition = new Point2D(0, 0);
    private Point2D desiredPosition = new Point2D(AppPanel.getScreenWidth() / 2, AppPanel.getScreenHeight() / 2);
    private List<Point2D> movementList = new CopyOnWriteArrayList<>();

    private static final float CAMERA_RETURN_VELOCITY = 0.5f;
    private static final int MAX_MOV_LIST_SAMPLES = 40;
    private static final int CAMERA_RETURN_DISTANCE = 40; // Distance at which to start moving the camera towards the player

    public Camera() {

    }

    /**
     * update method updates the camera position from the player's current position
     * @param Universe the universe to move.
     * @return Nothing.
     */
    public void update(Universe universe) {
	Point2D newPoint = new Point2D(AppPanel.getPlayer().getVelocity());

	updateMovList(newPoint);

	if(desiredPosition.distanceTo(AppPanel.getPlayer().getPosition()) > CAMERA_RETURN_DISTANCE) {
	    Point2D centerDelta = Point2D.difference(desiredPosition, AppPanel.getPlayer().getPlayerCenter());
	    double angleToCenter = Math.atan2(centerDelta.getY(), centerDelta.getX());
	    Point2D toCenterDelta = Point2D.xyComponents(-CAMERA_RETURN_VELOCITY, angleToCenter);
	    toCenterDelta = Point2D.clamp(toCenterDelta, mapBoundry()[0], mapBoundry()[1]);
	    //move(toCenterDelta, universe);
	}
	Point2D listAvg = Point2D.listAverage(movementList);
	move(listAvg, universe);
    }

    /**
     * move method moves the universe and the player and updates the camera
     * @param Point2D, how much to move in pixels
     * @param Universe, the universe to move
     * @return Nothing.
     */
    private void move(Point2D delta, Universe universe) {
	universe.move(delta.inverted());
	AppPanel.getPlayer().move(delta.inverted());
	currentPosition.add(delta);
    }

    /**
     * updateMovList updates the movementList that the camera follows and clamps the allowed points within the map boundries
     * @param Point2D, the next point that should be added to the movementlist.
     * @return Nothing.
     */
    public void updateMovList(Point2D sample) {
	// Set velocity sample to 0 if the player is close to a map border
	if (currentPosition.getX() < mapBoundry()[0].getX()) sample.setX(Math.max(0, sample.getX()));
	if (currentPosition.getX() > mapBoundry()[1].getX()) sample.setX(Math.min(0, sample.getX()));
	if (currentPosition.getY() < mapBoundry()[0].getY()) sample.setY(Math.max(0, sample.getY()));
	if (currentPosition.getY() > mapBoundry()[1].getY()) sample.setY(Math.min(0, sample.getY()));

        // Add samples and remove the first sample if the list gets too big
	movementList.add(sample);
        if (movementList.size() > MAX_MOV_LIST_SAMPLES){
            movementList.remove(0);
	}
    }

    /**
     * mapBoundry returns the location of the border of the map with regard to the screen dimensions
     * This tells us how much we can move the screen without the camera leaving the map
     * @param PNothing.
     * @return Point2D, the location of the border.
     */

    private static Point2D[] mapBoundry() {
	float right = AppPanel.getMapWidth()/2 - AppPanel.getScreenWidth()/2;
	float left = -right;
	float down = AppPanel.getMapHeight()/2 - AppPanel.getScreenHeight()/2;
	float up = -down;

	return new Point2D[]{new Point2D(left, up), new Point2D(right, down)};
    }
}
