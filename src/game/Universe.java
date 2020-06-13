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

    private List<Entity> entityList = new ArrayList<>();

    public Universe() {
    }

    /**
     * move method moves the all the entities in the universe
     * @param delta Delta value.
     * @return Nothing.
     */
    public void move(Point2D delta) {
	for (Entity e : entityList) {
	    e.move(delta);
	}
    }

    /**
     * addEntities method adds a collection of entities to the universe.
     * @param entities The entities to add.
     * @return nothing.
     */
    public void addEntities(Collection<Entity> entities) {
	entityList.addAll(entities);
    }

    /**
     * addEntity method adds an entity to the universe..
     * @param entity The entity to add.
     * @return Nothing.
     */
    public void addEntity(Entity entity) {
	entityList.add(entity);
    }

    /**
     * removeEntity method removes an entity to the universe..
     * @param entity The entity to remove.
     * @return Nothing.
     */
    public void removeEntity(Entity entity) {
        entityList.remove(entity);
    }

    public List<Entity> getEntityList() {
	    return entityList;
    }
}
