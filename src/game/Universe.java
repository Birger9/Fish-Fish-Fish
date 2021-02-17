package game;

import entity.Entity;
import util.Point2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The Universe class handles all the entities that exist in the game.
 */
public class Universe {

    private List<Entity> entities = new ArrayList<>();

    public Universe() {
    }

    /**
     * move method moves the all the entities in the universe
     * @param delta Delta value.
     */
    public void move(Point2D delta) {
	for (Entity e : entities) {
	    e.move(delta);
	}
    }

    /**
     * addEntities method adds a collection of entities to the universe.
     * @param entities The entities to add.
     */
    public void addEntities(Collection<Entity> entities) {
	this.entities.addAll(entities);
    }

    /**
     * addEntity method adds an entity to the universe..
     * @param entity The entity to add.
     */
    public void addEntity(Entity entity) {
	entities.add(entity);
    }

    /**
     * removeEntity method removes an entity to the universe..
     * @param entity The entity to remove.
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public List<Entity> getEntities() {
	    return entities;
    }
}
