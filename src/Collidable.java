
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import geom.Shape;

public interface Collidable {

    public Shape getCollisionShape();

    public boolean isCollidingWith(Collidable otherCollidable);

    public void handleCollisionWith(Collidable otherCollidable);

    // to crosscheck collidable in two different groups
    public static void checkAndHandleCollisions(ArrayList<? extends Collidable> group1,
            ArrayList<? extends Collidable> group2) {
        try {
            for (Collidable collidable1 : group1) {
                for (Collidable collidable2 : group2) {
                    if (collidable1.isCollidingWith(collidable2)) {
                        collidable1.handleCollisionWith(collidable2);
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            // handled by skipping check for this frame;
        }
    }

    // to check single collidable against a group of collidables:
    public static void checkAndHandleCollisions(Collidable collidable1, ArrayList<? extends Collidable> group2) {
        try {
            for (Collidable collidable2 : group2) {
                if (collidable1.isCollidingWith(collidable2)) {
                    collidable1.handleCollisionWith(collidable2);
                }
            }
        } catch (ConcurrentModificationException e) {
            // handled by skipping check for this frame;
        }
    }

}