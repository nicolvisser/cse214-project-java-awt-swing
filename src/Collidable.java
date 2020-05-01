import geom.Shape;

public interface Collidable {

    public Shape getCollisionShape();

    public boolean isCollidingWith(Collidable otherCollidable);

    public void handleCollisionWith(Collidable otherCollidable);

}