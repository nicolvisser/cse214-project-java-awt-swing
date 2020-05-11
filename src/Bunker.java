import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import geom.Circle;
import geom.Rectangle;
import geom.BoundingShape;
import geom.Vector2D;

public class Bunker implements Collidable, Disposable, Updateable, Drawable {

    class Block implements Collidable, Drawable {

        private Rectangle rect;
        private Color color;

        Block(double x, double y, double width, double height) {
            rect = new Rectangle(new Vector2D(x, y), width, height);
            int r = 80 + (int) (Math.random() * 20);
            color = new Color(r, r, r);
        }

        @Override
        public void draw(Graphics2D g2) {

            g2.setColor(color);
            g2.fill(((Rectangle) getCollisionShape()).toRectangle2D());

            // Show Collision Boundary for Debugging: --->>
            if (GameSettings.DEBUG) {
                g2.setColor(Color.RED);
                rect.draw(g2);
            }
            // <-------------------------------------------
        }

        @Override
        public BoundingShape getCollisionShape() {
            return rect;
        }

        @Override
        public boolean isCollidingWith(Collidable otherCollidable) {
            this.getCollisionShape().intersects(otherCollidable.getCollisionShape());
            return false;
        }

        @Override
        public void handleCollisionWith(Collidable otherCollidable) {
            // don't handle here, but rather in Bunker class's handleCollitionWith method
        }
    }

    ArrayList<Block> blocks = new ArrayList<>();
    private Rectangle collisionRect;
    private static final int SPACING = 1; // for small space between adjacent blocks
    private int lastNumberOfBlocks = 0; // keeps track of number of blocks, if out of sync with actual then will trigger
                                        // a resize in bounding shape

    public Bunker(double xcenter, double ycenter, double width, double height, int numRows, int numCols) {
        collisionRect = new Rectangle(xcenter, ycenter, width, height);

        double blockWidth = (width - (numCols - 1) * SPACING) / numCols;
        double blockHeight = (height - (numRows - 1) * SPACING) / numRows;

        for (double x = collisionRect.xmin() + blockWidth / 2; x < collisionRect.xmax(); x += blockWidth + SPACING) {
            for (double y = collisionRect.ymin() + blockHeight / 2; y < collisionRect
                    .ymax(); y += (blockHeight + SPACING)) {
                Block block = new Block(x, y, blockWidth, blockHeight);
                blocks.add(block);
                lastNumberOfBlocks++;
            }
        }
    }

    public void recalculateCollisionShape() {

        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;

        for (Block block : blocks) {
            xmin = Math.min(xmin, block.rect.xmin());
            xmax = Math.max(xmax, block.rect.xmax());
            ymin = Math.min(ymin, block.rect.ymin());
            ymax = Math.max(ymax, block.rect.ymax());
        }

        double newWidth = xmax - xmin;
        double newHeight = ymax - ymin;
        double newX = xmin + newWidth / 2;
        double newY = ymin + newHeight / 2;
        this.collisionRect = new Rectangle(newX, newY, newWidth, newHeight);

    }

    @Override
    public void update(int dt) {

        // if meanwhile an enemy has died, recalculate collision boundary of group
        if (lastNumberOfBlocks != blocks.size() && blocks.size() > 0) {
            recalculateCollisionShape();
            lastNumberOfBlocks = blocks.size();
        }

        // dont call super does not move or rotate
    }

    @Override
    public void draw(Graphics2D g2) {
        for (Block block : blocks) {
            block.draw(g2);
        }

        // Show Collision Boundary for Debugging: --->>
        if (GameSettings.DEBUG) {
            g2.setColor(Color.RED);
            collisionRect.draw(g2);
        }
        // <-------------------------------------------
    }

    // ============ METHODS ASSOCIATED WITH COLIDABLE INTERFACE ============= >>>

    @Override
    public BoundingShape getCollisionShape() {
        return collisionRect;
    }

    @Override
    public boolean isCollidingWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Missile) {
            return isCollidingWith((Missile) otherCollidable);
        }
        return false;
    }

    public boolean isCollidingWith(Missile missile) {
        if (missile.state == Missile.MissileState.ALIVE) {
            if (getCollisionShape().intersects(missile.getCollisionShape())) {
                for (Block block : blocks) {
                    if (block.getCollisionShape().intersects(missile.getCollisionShape())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void handleCollisionWith(Collidable otherCollidable) {
        if (otherCollidable instanceof Missile) {
            handleCollisionWith((Missile) otherCollidable);
        }
    }

    public void handleCollisionWith(Missile missile) {
        missile.explode();
        int burstRadius = GameSettings.vmin / 30;
        Circle burstCircle = new Circle(missile.position, burstRadius);
        Iterator<Block> blockIterator = blocks.iterator();
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (block.getCollisionShape().intersects(burstCircle)) {
                blockIterator.remove();
            }
        }
    }

    // ============ METHODS ASSOCIATED WITH DISPOSABLE INTERFACE ============= >>>

    @Override
    public boolean mayBeDisposed() {
        return blocks.size() <= 0;
    }
}