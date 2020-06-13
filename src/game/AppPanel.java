package game;

import entity.Fish;
import entity.FishFactory;
import entity.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import media.ImageManager;
import media.HUD;
import media.AudioManager;
import media.AudioLoader;
import media.MovingText;

import util.Point2D;

/**
 * The game.AppPanel class serves as the GUI Window for the application and contains high-level game logic through method calls
 * @author eribi813, andfr210
 * @version 1.0
 * @since 2020-03-22
 */
public class AppPanel extends JComponent implements ActionListener, MouseMotionListener, MouseListener
{
    private static Camera mainCam = new Camera();

    private static final int SCREEN_WIDTH = 1000; // Window width
    private static final int SCREEN_HEIGHT = 700; // Window height

    private static final int MAP_WIDTH = 1600;
    private static final int MAP_HEIGHT = 1200;
    private static final int START_SIZE = 30; // Start size for the player fish
    private static final float SPAWN_RATE = 0.02f;
    private static final int FPS = 60;

    private static final int SCORE_TEXT_SIZE = 20;
    // Progressbar
    private static final int XPOS = 20; // X-pos for the progressbar
    private static final int YPOS = 20; // Y-pos for the progressbar
    private static final int WIDTH_SIZE = 400; // Width of the progressbar
    private static final int HEIGHT_SIZE = 8; // Height of the progressbar

    private static Player player = null;//new Player(new Point2D(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2), new Point2D(START_SIZE, START_SIZE), 1);
    private static Point mouse = new Point(); // Mouse position

    private static ImageManager imageManager = new ImageManager();
    private static AudioManager audioManager = new AudioManager();

    private static Universe universe = new Universe();
    private Background background;

    private Timer timer = new Timer(1000/FPS, this); // 60 fps
    private static final boolean DEBUG_MODE = false;

    /**
     * Constructor that initializes window, mouse listeners, audio clips, images
     * and creates a player object
     */
    public AppPanel() {
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        addMouseMotionListener(this);
        addMouseListener(this);

        AudioLoader.createAudioClips();
        imageManager.initImages();
        imageManager.loadImages();

        playMusic();

        background = new Background();

        if (player == null)
            player = new Player(new Point2D(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2), new Point2D(START_SIZE, START_SIZE), 1);
    }

    @Override
    public void update(Graphics g) {
	paintComponent(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);

        FishFactory.spawnFishAroundPlayer(SPAWN_RATE);

        // Background
        background.render(g);

        // Render and update fish
        for(Fish fish : FishFactory.getFishList()) {
            fish.render(g);
            fish.update();
        }
        Fish.removeDead(); // Removes dead fish

        player.update();
        player.render(g);

        drawHUD(g);

	timer.start();
    }

    /**
     * drawHud renders a media.HUD with a progress bar and a score counter and updates moving text objects.
     * @param Graphics object.
     * @return Nothing.
     */
    private void drawHUD(Graphics g) {
        float alpha = player.getExperience() / (float)player.getXpToNextLevel(); // Progress value
        HUD.drawProgressBar(new Point2D(XPOS, YPOS), new Point2D(WIDTH_SIZE, HEIGHT_SIZE), alpha, g);

        HUD.drawScore(SCORE_TEXT_SIZE, "SCORE: " + HUD.getAnimatedScore(), g, Color.WHITE);
        updateMovingTexts(g);
    }


    /**
     * playMusic plays a random music track.
     * @param
     * @return Nothing.
     */
    private void playMusic() {
        AudioLoader.loopClip("MUSIC", 1);
    }

    /**
     * updateMovingTexts handles score texts and moves them to the Score media.HUD position.
     * @param
     * @return Nothing.
     */
    private void updateMovingTexts(Graphics g) {
        for (MovingText mt : MovingText.getMovingTexts()) {
            mt.render(g);
            mt.moveToPosition(HUD.getScorePosition()); //Moves the score text to the position of the SCORE media.HUD
        }
        MovingText.removeReached(); // Removes scores, from the screen, that have reached the position of the SCORE media.HUD
    }

    private static void setPlayer(Player p) {
        AppPanel.player = p;
    }

    public static int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public static int getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    public static int getMapWidth() {
        return MAP_WIDTH;
    }

    public static int getMapHeight() {
        return MAP_HEIGHT;
    }

    public static Point getMouse() {
        return mouse;
    }

    public static Player getPlayer() {return player; }

    public static Universe getUniverse() { return universe; }

    public static ImageManager getImageManager() {
        return imageManager;
    }

    public static AudioManager getAudioManager() {
        return audioManager;
    }

    public static Camera getMainCam() {
        return mainCam;
    }

    public static boolean inDebugMode() { return DEBUG_MODE; }

    @Override
    public void actionPerformed(ActionEvent e) {
	repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == 1)
            player.thrust(new Point2D(mouse.x, mouse.y));
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
	AppPanel.mouse = e.getPoint(); // Updates mouse position
    }
}
