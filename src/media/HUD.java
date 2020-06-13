package media;

import game.AppPanel;
import util.Point2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 * The HUD class handles the GUI for the progressbar and score.
 * @author eribi813, andfr210
 * @version 1.0
 * @since 2020-03-22
 */
public class HUD {

    private HUD() {}

    // PROGRESS BAR
    private static final int PROGRESS_BAR_BORDER_THICKNESS = 2;
    private static final int PROGRESS_BAR_ROUNDNESS = 7;

    private static int animatedScore = 0; // Current score
    private static int targetScore = 0;
    private static final int SCORE_INCREMENT = 50;

    private static final Point2D SCORE_POSITION = new Point2D(AppPanel.getScreenWidth() - 200, 30);

    /**
     * drawProgressBar method draws a progress bar where alpha corresponds to
     * the progress decimal value. 0 = 0%, 1 = 100%
     * @param Point2D the position where the progress bar should be,
     * @param Point2D the size of the progress bar
     * @param float the value of how much of the progress bar should be filled.
     * @return Nothing.
     */
    public static void drawProgressBar(Point2D position, Point2D size, float alpha, Graphics g) {

	alpha = (alpha < 0) ? 0 : alpha; // If alpha is less    than 0, set alpha to 0
	alpha = (alpha > 1) ? 1 : alpha; // If alpha is greater than 1, set alpha to 1

	// BACKGROUND RECTANGLE
	g.setColor(Color.BLACK);
	g.fillRoundRect(
		(int)(position.getX() - PROGRESS_BAR_BORDER_THICKNESS),
		(int)(position.getY() - PROGRESS_BAR_BORDER_THICKNESS),
		(int)(size.getX() + PROGRESS_BAR_BORDER_THICKNESS*2),
		(int)(size.getY() + PROGRESS_BAR_BORDER_THICKNESS*2),
		PROGRESS_BAR_ROUNDNESS, PROGRESS_BAR_ROUNDNESS
	);

	// FOREGROUND RECTANGLE
	g.setColor(Color.GRAY);
	g.fillRoundRect(
		(int)position.getX(),
		(int)position.getY(),
		(int)size.getX(),
		(int)size.getY(),
		PROGRESS_BAR_ROUNDNESS, PROGRESS_BAR_ROUNDNESS
	);

	// PROGRESS RECTAGLE
	int progressInPixels = (int)(size.getX() * alpha);
	g.setColor(Color.YELLOW);
	g.fillRoundRect(
		(int)position.getX(),
		(int)position.getY(),
		progressInPixels,
		(int)size.getY(),
		PROGRESS_BAR_ROUNDNESS, PROGRESS_BAR_ROUNDNESS
	);

	// Show experience if in debug mode
	if (AppPanel.inDebugMode()) {
	    g.setColor(Color.WHITE);
	    g.drawString(AppPanel.getPlayer().getExperience() + " / " + AppPanel.getPlayer().getXpToNextLevel(),
			 (int)(position.getX() + size.getX()), (int)position.getY());
	}
    }

    /**
     * drawScore method draws the score on the screen and updates its value
     * @param int size of the text
     * @param String the score value
     * @param Graphics graphics object
     * @param Color the color of the text.
     * @return Nothing.
     */
    public static void drawScore(int textSize, String value, Graphics g, Color color) {
	increaseScoreAnimated(SCORE_INCREMENT);
	g.setColor(color);
	g.setFont(new Font("Courier New", Font.BOLD, textSize));
	g.drawString(value, (int)SCORE_POSITION.getX(), (int)SCORE_POSITION.getY());
    }

    /**
     * increaseScoreAnimated method increases the field animatedScore, that
     * is used for keep track of the users current score
     * @param int the rate at which to increase the animated score each frame
     * @return Nothing.
     */
    private static void increaseScoreAnimated(int increment) {
        targetScore = AppPanel.getPlayer().getScore();
        if (animatedScore > targetScore)
            animatedScore = targetScore;

        animatedScore += Math.min(targetScore - animatedScore, increment);
    }

    public static Point2D getScorePosition() {
	return SCORE_POSITION;
    }

    public static int getAnimatedScore() {
	return animatedScore;
    }
}
