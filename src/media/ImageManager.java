package media;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The ImageManager class loads and retrieves BufferedImages
 */
public class ImageManager
{
    // Background sprite
    private static final int BACKGROUND_WIDTH = 3264;
    private static final int BACKGROUND_HEIGHT = 2448;

    // Player sprite
    private static final int PLAYER_SIZE = 128;

    // Small fish sprite
    private static final int SMALL_WIDTH = 50;
    private static final int SMALL_HEIGHT = 16;

    // Medium sprite
    private static final int MED_WIDTH = 64;
    private static final int MED_HEIGHT = 32;

    // Large sprite
    private static final int LARGE_SIZE = 128;

    // Barracuda sprite
    private static final int BARR_WIDTH = 480;
    private static final int BARR_HEIGHT = 180;

    // Warning sign sprite
    private static final int WARN_SIZE = 20;

    private HashMap<String, Sprite> spriteHashMap = new HashMap<>();

    public ImageManager() {}

    /**
     * initImages method sets image dimensions and adds sprites to the sprite hashmap
     * @param Nothing.
     * @return Nothing.
     */
    public void initImages() {
        spriteHashMap.put("BACKGROUND", new Sprite(new BufferedImage(BACKGROUND_WIDTH, BACKGROUND_HEIGHT, 2), "/sprites/background.png"));
        spriteHashMap.put("PLAYER", new Sprite(new BufferedImage(PLAYER_SIZE, PLAYER_SIZE, 2), "/sprites/player.png")); // RGBA 32 bit
        spriteHashMap.put("SMALL FISH", new Sprite(new BufferedImage(SMALL_WIDTH, SMALL_HEIGHT, 2), "/sprites/LevelOne.png"));
        spriteHashMap.put("MEDIUM FISH", new Sprite(new BufferedImage(MED_WIDTH, MED_HEIGHT, 2), "/sprites/LevelTwo.png"));
        spriteHashMap.put("LARGE FISH", new Sprite(new BufferedImage(LARGE_SIZE, LARGE_SIZE, 2), "/sprites/LevelThree.png"));
        spriteHashMap.put("BARRACUDA", new Sprite(new BufferedImage(BARR_WIDTH, BARR_HEIGHT, 2), "/sprites/barracuda.png"));
        spriteHashMap.put("WARNING SIGN", new Sprite(new BufferedImage(WARN_SIZE, WARN_SIZE, 2), "/sprites/warningSign.png"));
    }

    /**
     * loadImages method loads images from the given file paths
     * @param Nothing.
     * @return Nothing.
     */
    public void loadImages() {
        for (Sprite sprite : spriteHashMap.values()) {
            try {
                sprite.setBufferedImage(getResourceURI(sprite.getPath()));
            } catch (URISyntaxException | IOException e) {
                Logger.getLogger(ImageManager.class.getName()).log(Level.WARNING, null, e);
                System.out.println("WARNING: Image at path " + sprite.getPath() + " not found!");
                //e.printStackTrace();
            }
        }
    }

    /**
     * getResourceURI method loads images from the given file paths
     * @param String path, the path to the image file.
     * @return BufferedImage the bufferedImage.
     */
    private static BufferedImage getResourceURI(String path) throws URISyntaxException, IOException {
        return ImageIO.read(new File(ImageManager.class.getResource(path).toURI()));
    }

    /**
     * getSpriteHashMap method returns the spriteHashMap
     * @param Nothing.
     * @return HashMap<String, Sprite> containing the sprites
     */
    public HashMap<String, Sprite> getSpriteHashMap() {
        return spriteHashMap;
    }

    /**
     * getInstance method returns the instance of ImageManager
     * @param Nothing.
     * @return ImageManager object
     */
}
