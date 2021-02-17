package media;

import util.PropertiesLoaderBorrowedCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The ImageManager class loads and retrieves BufferedImages
 */
public class ImageManager
{
    private PropertiesLoaderBorrowedCode defaultSettings = new PropertiesLoaderBorrowedCode("src/defaultsettings");

    // Background sprite
    private final int backgroundWidth = (int) defaultSettings.getValue("background.sprite.width", int.class);
    private final int backgroundHeight = (int) defaultSettings.getValue("background.sprite.height", int.class);

    // Player sprite
    private final int playerSize = (int) defaultSettings.getValue("player.spriteSize", int.class);

    // Small fish sprite
    private final int smallWidth = (int) defaultSettings.getValue("basicEnemy.small.width", int.class);
    private final int smallHeight = (int) defaultSettings.getValue("basicEnemy.small.height", int.class);

    // Medium sprite
    private final int medWidth = (int) defaultSettings.getValue("basicEnemy.medium.width", int.class);
    private final int medHeight = (int) defaultSettings.getValue("basicEnemy.medium.height", int.class);

    // Large sprite
    private final int largeSize = (int) defaultSettings.getValue("basicEnemy.large.width", int.class);

    // Barracuda sprite
    private final int barrWidth = (int) defaultSettings.getValue("barracuda.size.width", int.class);
    private final int barrHeight = (int) defaultSettings.getValue("barracuda.size.height", int.class);

    // Warning sign sprite
    //private static final int WARN_SIZE = 20;

    private Map<String, Sprite> spriteHashMap = new HashMap<>();

    public ImageManager() {}

    /**
     * initImages method sets image dimensions and adds sprites to the sprite hashmap
     */
    public void initImages() {
        spriteHashMap.put("BACKGROUND", new Sprite(new BufferedImage(backgroundWidth, backgroundHeight, 2), "/sprites/background.png"));
        spriteHashMap.put("PLAYER", new Sprite(new BufferedImage(playerSize, playerSize, 2), "/sprites/player.png")); // RGBA 32 bit
        spriteHashMap.put("SMALL FISH", new Sprite(new BufferedImage(smallWidth, smallHeight, 2), "/sprites/LevelOne.png"));
        spriteHashMap.put("MEDIUM FISH", new Sprite(new BufferedImage(medWidth, medHeight, 2), "/sprites/LevelTwo.png"));
        spriteHashMap.put("LARGE FISH", new Sprite(new BufferedImage(largeSize, largeSize, 2), "/sprites/LevelThree.png"));
        spriteHashMap.put("BARRACUDA", new Sprite(new BufferedImage(barrWidth, barrHeight, 2), "/sprites/barracuda.png"));
        //spriteHashMap.put("WARNING SIGN", new Sprite(new BufferedImage(WARN_SIZE, WARN_SIZE, 2), "/sprites/warningSign.png"));
    }

    /**
     * loadImages method loads images from the given file paths.
     * If a file cannot be found, we handle the exception by logging the warning, and we also print the file path that couldn't be located.
     * In addition to this, we also replace the sprite with a simple rectangle (so that the game is still in a playable state).
     */
    public void loadImages() {
        for (Sprite sprite : spriteHashMap.values()) {
            try {
                BufferedImage resourceURI = getResourceURI(sprite.getPath());
                if (resourceURI != null)
                    sprite.setBufferedImage(resourceURI);
            } catch (URISyntaxException | IOException e) { // getResourceURI has to throw an IOException, and must therefore be caught.
                Logger.getLogger(ImageManager.class.getName()).log(Level.WARNING, null, e);
                System.out.println("WARNING: Image at path " + sprite.getPath() + " not found!");
                sprite.setHasPath(false);
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
        if (ImageManager.class.getResource(path) == null) {
            throw new IOException("Could not locate file!");
        }
        File file = new File(ImageManager.class.getResource(path).toURI());
        return ImageIO.read(file);
    }

    /**
     * getSpriteHashMap method returns the spriteHashMap
     * @return HashMap<String, Sprite> containing the sprites
     */
    public Map<String, Sprite> getSpriteHashMap() {
        return spriteHashMap;
    }

}
