import java.awt.Graphics2D;

import javax.swing.ImageIcon;

public class Enemy extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    private static final ImageIcon IMAGE_ICON_SINGLE_ENEMY = new ImageIcon("resources/enemy.png");

    public Enemy(double x, double y, double radius, double orientation) {
        super(x, y, radius, orientation);

        angularVelocity = 0.1 + Math.random() / 10;
    }

    @Override
    public void draw(Graphics2D g2) {
        //// super.draw(g2);

        g2.rotate(orientation, position.x, position.y);

        int w = (int) (width * 1.4);
        int h = (int) (height * 1.4);
        int x = (int) (position.x - w / 2);
        int y = (int) (position.y - h / 2);
        g2.drawImage(IMAGE_ICON_SINGLE_ENEMY.getImage(), x, y, w, h, null);

        g2.rotate(-orientation, position.x, position.y);
    }

    @Override
    public void update() {
        super.updateRotation();
    }

}