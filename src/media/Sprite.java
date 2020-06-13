package media;

import java.awt.image.BufferedImage;

/**
 * The Sprite class handles BufferedImages for the fishes in the universe
 */
public class Sprite
{
    private BufferedImage bufferedImage;
    private String path;

    /**
     * Constructor that sets the buffereImage and the path to that file in Sprite Object
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

    public String getPath() {
	return path;
    }
}
