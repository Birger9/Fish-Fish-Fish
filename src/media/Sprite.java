package media;

import java.awt.image.BufferedImage;

/**
 * The Sprite class is used to represent the various images in the game. A sprite uses BufferedImage for the image data
 * (such as dimensions) and a string for the file path (although there are better approaches available).
 */
public class Sprite
{
    private BufferedImage bufferedImage;
    private String path;
    private boolean hasPath = true;

    /**
     * Constructor that sets the bufferedImage and the path to that file in Sprite Object
     */
    public Sprite(BufferedImage bufferedImage, String path) {
        this.bufferedImage = bufferedImage;
        this.path = path;
    }

    public void setBufferedImage(final BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImage() {
	return bufferedImage;
    }

    public void setHasPath(boolean hasPath) {
        this.hasPath = hasPath;
    }

    public boolean getHasPath() {
        return hasPath;
    }

    public String getPath() {
	return path;
    }
}
