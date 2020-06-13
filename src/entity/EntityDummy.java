package entity;

import util.Point2D;

import java.awt.*;

/*
* Class to test camera movement etc.
*/
public class EntityDummy extends Entity {

    public EntityDummy(Point2D position, Point2D size) {
        super(position, size);
    }

    @Override
    public void render(Graphics g) {
	g.setColor(Color.RED);
	g.fillOval((int)position.getX(), (int)position.getY(), (int)size.getX() * 2, (int)size.getY() * 2);
    }
}
