package game;

import entity.Player;
import util.Point2D;
import util.PropertiesLoaderBorrowedCode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The Camera class handles the camera movement and follows the player on the screen. This is done by moving every object
 * on the screen.
 */
public class Camera {

    private Point2D currentPosition = new Point2D(0, 0);
    private Point2D desiredPosition;
    private List<Point2D> movementList = new CopyOnWriteArrayList<>();

    private PropertiesLoaderBorrowedCode defaultSettings = new PropertiesLoaderBorrowedCode("src/defaultsettings");

    private final float cameraReturnVelocity = (float) defaultSettings.getValue("camera.returnVelocity", float.class);
    private final int maxMovListSamples = (int) defaultSettings.getValue("camera.maxMovListSamples", int.class);
    private final int cameraReturnDistance = (int) defaultSettings.getValue("camera.returnDistance", int.class);

    private Player player;

    private AppPanel appPanel;

    public Camera(AppPanel appPanel) {
	this.appPanel = appPanel;
	player = appPanel.getPlayer();
	desiredPosition = new Point2D(appPanel.getScreenWidth() / 2, appPanel.getScreenHeight() / 2);
    }

    /**
     * update method updates the camera position from the player's current position
     * @param Universe the universe to move.
     */
    public void update(Universe universe) {
	Point2D newPoint = new Point2D(player.getVelocity());

	updateMovList(newPoint);

	if(desiredPosition.distanceTo(player.getPosition()) > cameraReturnDistance) {
	    Point2D centerDelta = Point2D.difference(desiredPosition, player.getPlayerCenter());
	    double angleToCenter = Math.atan2(centerDelta.getY(), centerDelta.getX());
	    Point2D toCenterDelta = Point2D.xyComponents(-cameraReturnVelocity, angleToCenter);
	    toCenterDelta = Point2D.clamp(toCenterDelta, mapBoundary()[0], mapBoundary()[1]);
	    //move(toCenterDelta, universe);
	}
	Point2D listAvg = Point2D.listAverage(movementList);
	move(listAvg, universe);
    }

    /**
     * move method moves the universe and the player and updates the camera
     * @param Point2D, how much to move in pixels
     * @param Universe, the universe to move
     */
    private void move(Point2D delta, Universe universe) {
	universe.move(delta.inverted());
	player.move(delta.inverted());
	currentPosition.add(delta);
    }

    /**
     * updateMovList updates the movementList that the camera follows and clamps the allowed points within the map boundries
     * @param Point2D, the next point that should be added to the movementlist.
     */
    public void updateMovList(Point2D sample) {
	// Set velocity sample to 0 if the player is close to a map border
	if (currentPosition.getX() < mapBoundary()[0].getX()) sample.setX(Math.max(0, sample.getX()));
	if (currentPosition.getX() > mapBoundary()[1].getX()) sample.setX(Math.min(0, sample.getX()));
	if (currentPosition.getY() < mapBoundary()[0].getY()) sample.setY(Math.max(0, sample.getY()));
	if (currentPosition.getY() > mapBoundary()[1].getY()) sample.setY(Math.min(0, sample.getY()));

        // Add samples and remove the first sample if the list gets too big
	movementList.add(sample);
        if (movementList.size() > maxMovListSamples){
            movementList.remove(0);
	}
    }

    /**
     * mapBoundary returns the location of the border of the map with regard to the screen dimensions
     * This tells us how much we can move the screen without the camera leaving the map
     * @return Point2D, the location of the border.
     */

    private Point2D[] mapBoundary() {
	float right = appPanel.getMapWidth()/2 - appPanel.getScreenWidth()/2;
	float left = -right;
	float down = appPanel.getMapHeight()/2 - appPanel.getScreenHeight()/2;
	float up = -down;

	return new Point2D[]{new Point2D(left, up), new Point2D(right, down)};
    }
}
