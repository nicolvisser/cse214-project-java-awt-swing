import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Iterator;

import geom.Circle;
import geom.Rectangle;

public class Bunker extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    public class Block extends DefaultCritter {
        private static final long serialVersionUID = 1L;
        private Color color;

        public Block(double x, double y, double width, double height) {
            super(x, y, width, height, 0);
            int r = 80 + (int) (Math.random() * 20);
            color = new Color(r, r, r);
        }

        @Override
        public void update() {
            // does not move or rotate
        }

        @Override
        public void draw(Graphics2D g2) {

            g2.setColor(color);

            int x = (int) (position.x - width / 2);
            int y = (int) (position.y - height / 2);

            g2.fillRect(x, y, (int) width, (int) height);

            // Show Collision Boundary for Debugging: --->>
            if (InvadersFrame.DEBUG)
                super.draw(g2);
            // <-------------------------------------------
        }
    }

    int spacing = 1;
    ArrayList<Block> blocks = new ArrayList<>();

    public Bunker(double xcenter, double ycenter, double width, double height, int numRows, int numCols) {
        super(xcenter, ycenter, width, height, 0);
        double blockWidth = (width - (numCols - 1) * spacing) / numCols;
        double blockHeight = (height - (numRows - 1) * spacing) / numRows;

        Rectangle collisionRect = (Rectangle) getCollisionShape();

        for (double x = collisionRect.xmin() + blockWidth / 2; x < collisionRect.xmax(); x += blockWidth + spacing) {
            for (double y = collisionRect.ymin() + blockHeight / 2; y < collisionRect
                    .ymax(); y += (blockHeight + spacing)) {
                Block block = new Block(x, y, blockWidth, blockHeight);
                blocks.add(block);
            }
        }
    }

    public boolean isCleared() {
        return blocks.size() == 0;
    }

    public void draw(Graphics2D g2) {
        for (Block block : blocks) {
            block.draw(g2);
        }

        // Show Collision Boundary for Debugging: --->>
        if (InvadersFrame.DEBUG)
            super.draw(g2);
        // <-------------------------------------------
    }

    @Override
    public boolean isCollidingWith(DefaultCritter critter) {
        if (critter instanceof Missile) {
            return isCollidingWith((Missile) critter);
        } else {
            return super.isCollidingWith(critter);
        }
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
    public void handleCollisionWith(DefaultCritter critter) {
        if (critter instanceof Missile) {
            handleCollisionWith((Missile) critter);
        } else {
            super.handleCollisionWith(critter);
        }
    }

    public void handleCollisionWith(Missile missile) {
        missile.explode();
        Circle burstCircle = new Circle(missile.position, 20); // Todo fix hardcoding NB NB
        Iterator<Block> blockIterator = blocks.iterator();
        while (blockIterator.hasNext()) {
            Block block = blockIterator.next();
            if (block.getCollisionShape().intersects(burstCircle)) {
                blockIterator.remove();
            }
        }
    }

    @Override
    public boolean mayBeRemoved() {
        return blocks.size() == 0;
    }
}