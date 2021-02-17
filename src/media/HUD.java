package media;

import entity.Player;
import game.AppPanel;
import util.Point2D;
import util.PropertiesLoaderBorrowedCode;

import java.awt.*;

/**
 * The HUD class handles the GUI for the progressbar and score.
 * @author eribi813, andfr210
 * @version 1.0
 * @since 2020-03-22
 */
public class HUD {

    private PropertiesLoaderBorrowedCode defaultSettings = new PropertiesLoaderBorrowedCode("src/defaultsettings");

    private final int progressBarBorderThickness = (int) defaultSettings.getValue("hud.progressBar.borderThickness", int.class);
    private final int progressBarRoundness = (int) defaultSettings.getValue("hud.progressBar.roundness", int.class);

    private int animatedScore = 0; // Current score
    private int targetScore = 0;
    private final int scoreIncrement = (int) defaultSettings.getValue("hud.score.increment", int.class);
    private final int scoreTextSize = (int) defaultSettings.getValue("hud.score.textSize", int.class);

    private final Point2D scorePosition;
    private Player player;

    public HUD(AppPanel appPanel, Player player) {
	final int scoreRightMargin = 200;
	final int scoreTopMargin = 30;
	scorePosition = new Point2D(appPanel.getScreenWidth() - scoreRightMargin, scoreTopMargin);
        this.player = player;
    }

    /**
     * drawProgressBar method draws a progress bar where alpha corresponds to
     * the progress decimal value. 0 = 0%, 1 = 100%
     * @param alpha The decimal value of how much of the progress bar should be filled (0-1).
     * @param g The awt graphics object.
     */
    public void drawProgressBar(float alpha, Graphics g) {
        final int progressBarX = (int) defaultSettings.getValue("hud.progressBar.x", int.class);
        final int progressBarY = (int) defaultSettings.getValue("hud.progressBar.y", int.class);
        final int progressBarW = (int) defaultSettings.getValue("hud.progressBar.width", int.class);
        final int progressBarH = (int) defaultSettings.getValue("hud.progressBar.height", int.class);
        drawProgressBar(
        	new Point2D(progressBarX, progressBarY),
		new Point2D(progressBarW, progressBarH),
		alpha, g
	);
    }

    /**
     * drawProgressBar method draws a progress bar where alpha corresponds to
     * the progress decimal value. 0 = 0%, 1 = 100%
     * @param position the position where the progress bar should be,
     * @param size the size of the progress bar
     * @param alpha the value of how much of the progress bar should be filled.
     * @param g the graphics object.
     */
    public void drawProgressBar(Point2D position, Point2D size, float alpha, Graphics g) {

	alpha = (alpha < 0) ? 0 : alpha; // If alpha is less    than 0, set alpha to 0
	alpha = (alpha > 1) ? 1 : alpha; // If alpha is greater than 1, set alpha to 1

	// BACKGROUND RECTANGLE
	g.setColor(Color.BLACK);
	g.fillRoundRect(
		(int)(position.getX() - progressBarBorderThickness),
		(int)(position.getY() - progressBarBorderThickness),
		(int)(size.getX() + progressBarBorderThickness * 2),
		(int)(size.getY() + progressBarBorderThickness * 2), progressBarRoundness, progressBarRoundness
	);

	// FOREGROUND RECTANGLE
	g.setColor(Color.GRAY);
	g.fillRoundRect(
		(int)position.getX(),
		(int)position.getY(),
		(int)size.getX(),
		(int)size.getY(), progressBarRoundness, progressBarRoundness
	);

	// PROGRESS RECTANGLE
	int progressInPixels = (int)(size.getX() * alpha);
	g.setColor(Color.YELLOW);
	g.fillRoundRect(
		(int)position.getX(),
		(int)position.getY(),
		progressInPixels,
		(int)size.getY(), progressBarRoundness, progressBarRoundness
	);

	// Show experience if in debug mode
	if (AppPanel.inDebugMode()) {
	    g.setColor(Color.WHITE);
	    g.drawString(player.getExperience() + " / " + player.getXpToNextLevel(),
			 (int)(position.getX() + size.getX()), (int)position.getY());
	}
    }

    /**
     * drawScore method draws the score on the screen and updates its value
     * @param textSize size of the text
     * @param value the score value
     * @param g the graphics object
     * @param color the color of the text.
     */
    public void drawScore(int textSize, String value, Graphics g, Color color) {
	increaseScoreAnimated(scoreIncrement);
	g.setColor(color);
	g.setFont(new Font("Courier New", Font.BOLD, textSize));
	g.drawString(value, (int) scorePosition.getX(), (int) scorePosition.getY());
    }

    /**
     * @param value the value to print.
     * @param g the graphics object.
     * @param color the text color.
     */
    public void drawScore(String value, Graphics g, Color color) {
        drawScore(scoreTextSize, value, g, color);
    }

    /**
     * increaseScoreAnimated method increases the field animatedScore, that
     * is used for keep track of the users current score
     * @param increment the rate at which to increase the animated score each frame
     */
    private void increaseScoreAnimated(int increment) {
        targetScore = player.getScore();
        if (animatedScore > targetScore)
            animatedScore = targetScore;

        animatedScore += Math.min(targetScore - animatedScore, increment);
    }

    public Point2D getScorePosition() {
	return scorePosition;
    }

    public int getAnimatedScore() {
	return animatedScore;
    }
}
