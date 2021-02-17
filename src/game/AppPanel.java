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
import java.util.ArrayList;
import java.util.List;

import media.ImageManager;
import media.HUD;
import media.AudioManagerBorrowedCode;
import media.AudioLoader;
import media.MovingText;

import util.Point2D;
import util.PropertiesLoaderBorrowedCode;

/**
 * The game.AppPanel class serves as the GUI Window for the application and contains high-level game logic through method calls
 * @author eribi813, andfr210
 * @version 1.0
 * @since 2020-03-22
 */
public class AppPanel extends JComponent implements ActionListener, MouseMotionListener, MouseListener
{
    private PropertiesLoaderBorrowedCode defaultSettings = new PropertiesLoaderBorrowedCode("src/defaultsettings");

    private final int screenWidth = (int) defaultSettings.getValue("screen.width", int.class); // Window width
    private final int screenHeight = (int) defaultSettings.getValue("screen.height", int.class); // Window height

    private final int mapWidth = (int) defaultSettings.getValue("map.width", int.class);
    private final int mapHeight = (int) defaultSettings.getValue("map.height", int.class);
    private final float spawnRate = (float) defaultSettings.getValue("enemy.spawnRate", float.class);
    private final int startSize = (int) defaultSettings.getValue("player.startSize", int.class); // Start size for the player fish

    private final static int MAX_FPS = 60;

    private Player player = null;
    private Point mouse = new Point(); // Mouse position

    private final ImageManager imageManager = new ImageManager();
    private final AudioManagerBorrowedCode audioManagerBorrowedCode = new AudioManagerBorrowedCode();

    private AudioLoader audioLoader = new AudioLoader(audioManagerBorrowedCode);

    private final Universe universe = new Universe();
    private Background background;
    private HUD hud;
    private Camera mainCam;
    private List<MovingText> movingTexts = new ArrayList<>();

    private Timer timer = new Timer(1000 / MAX_FPS, this); // 60 fps
    private static final boolean DEBUG_MODE = false;

    private FishFactory fishFactory;

    /**
     * Constructor that initializes window, mouse listeners, audio clips, images
     * and creates a player object
     */
    public AppPanel() {
        setDoubleBuffered(true);
        setPreferredSize(new Dimension(screenWidth, screenHeight));
        addMouseMotionListener(this);
        addMouseListener(this);

        audioLoader.createAudioClips();
        imageManager.initImages();
        imageManager.loadImages();

        playMusic();

        background = new Background(this);
        fishFactory = new FishFactory(this);

        if (player == null)
            player = new Player(new Point2D(screenWidth / 2, screenHeight / 2), new Point2D(startSize, startSize), 1, this, fishFactory);

        mainCam = new Camera(this);
        player.setCamera(mainCam);
        hud = new HUD(this, player);
    }

    @Override
    public void update(Graphics g) {
	paintComponent(g);
    }

    @Override
    protected void paintComponent(Graphics g) {
	super.paintComponent(g);

        fishFactory.spawnFishAroundPlayer(spawnRate);

        // Background
        background.render(g);

        // Render and update fish
        for(Fish fish : fishFactory.getFishList()) {
            fish.render(g);
            fish.update();
        }
        fishFactory.removeDead(); // Removes dead fish

        player.update();
        player.render(g);

        drawHUD(g);

	timer.start();
    }

    /**
     * drawHud renders a media.HUD with a progress bar and a score counter and updates moving text objects.
     * @param Graphics object.
     */
    private void drawHUD(Graphics g) {
        float alpha = player.getExperience() / (float)player.getXpToNextLevel(); // Progress value
        hud.drawProgressBar(alpha, g);

        hud.drawScore("SCORE: " + hud.getAnimatedScore(), g, Color.WHITE);
        updateMovingTexts(hud, g);
    }


    /**
     * playMusic plays a random music track.
     */
    private void playMusic() {
        audioLoader.loopClip("MUSIC", 1);
    }

    /**
     * updateMovingTexts handles score texts and moves them to the Score HUD position.
     * @param hud The HUD.
     * @param g The graphics object.
     */
    private void updateMovingTexts(HUD hud, Graphics g) {
        List<MovingText> toRemove = new ArrayList<>();
        for (MovingText mt : movingTexts) {
            mt.render(g);
            mt.moveToPosition(hud.getScorePosition());
            if (mt.getHasReached()) {
                toRemove.add(mt);
            }
        }
        for (MovingText mt : toRemove) {
            movingTexts.remove(mt);
        }
        toRemove.clear();
    }
    public List<MovingText> getMovingTexts() {
        return movingTexts;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public Point getMouse() {
        return mouse;
    }

    public Player getPlayer() {return player; }

    public Universe getUniverse() { return universe; }

    public ImageManager getImageManager() {
        return imageManager;
    }

    public Camera getMainCam() {
        return mainCam;
    }

    public static boolean inDebugMode() { return DEBUG_MODE; }

    public AudioLoader getAudioLoader() {
        return audioLoader;
    }

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
	mouse = e.getPoint(); // Updates mouse position
    }
}
