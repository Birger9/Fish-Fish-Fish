package entity;

import game.AppPanel;
import media.Sprite;
import util.Point2D;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The class Entity handles the position, size and sprite of an entity e.g a normal fish or a barracuda.
 * It can not be instantiated, and is meant to act as a base for objects with:
 * (1) a position
 * (2) a size
 * (3) a sprite, if there is one
 * This can range from anything between for example a background and the player.
 */
public abstract class Entity {

    protected Point2D position;
    protected Point2D size;
    protected Sprite sprite = null;
    protected boolean render = true;
    protected boolean isBlinking = false;
    protected AppPanel appPanel;

    protected Entity(Point2D position, Point2D size, AppPanel appPanel) {
        this.position = position;
        this.size = size;
        this.appPanel = appPanel;
    }

    protected Entity(Point2D position, Point2D size, Sprite sprite, AppPanel appPanel) {
        this.position = position;
        this.size = size;
        this.sprite = sprite;
        this.appPanel = appPanel;
    }

    /**
     * move sets the position of an entity, with an x- and y pos
     * @param Point2D new position.
     */
    public void move(Point2D delta) {
	position = Point2D.sum(position, delta);
    }

    /**
     * render method renders the entitys sprite, if it has one
     * @param Graphics g.
     */
    public void render(Graphics g) {
        if (!render) return;
        if (hasSprite()) {
            g.drawImage(sprite.getBufferedImage(), (int) position.getX(), (int) position.getY(), (int) size.getX(), (int) size.getY(), null);
        } else {
            g.setColor(Color.RED);
            g.fillRect((int)position.getX(), (int)position.getY(), (int)size.getX(), (int)size.getY());
        }
    }

    /**
     * render method periodically toggles the visibility of an entity
     * @param durationInSeconds The blink duration in seconds.
     * @param frequency The blink frequency in seconds.
     */
    public void blink(float durationInSeconds, float frequency) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        final Runnable blink = new Runnable()
        {
            private float nBlinks = 0;

            @Override public void run() {
                render = !render; // toggle render
                nBlinks++;
                if (nBlinks > durationInSeconds * frequency * 2) {
                    isBlinking = false;
                    scheduler.shutdown();
                    render = true;
                }
            }
        };
        if (!isBlinking) {
            isBlinking = true;
            final float msToSec = 1000.0f;
            scheduler.scheduleAtFixedRate(blink, 0, (long) (msToSec / (frequency * 2)), TimeUnit.MILLISECONDS);
        }
    }

    /**
     * remove method removes the entity from the universe
     */
    protected void remove() {
        appPanel.getUniverse().removeEntity(this);
    }

    /**
     * getPosition method returns the position of the entity.
     * @return The position.
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * hasSprite method returns true if the entity has a sprite, otherwise false.
     * @return Whether the entity has a sprite.
     */
    public boolean hasSprite() {
        return sprite.getHasPath();
    }

}
