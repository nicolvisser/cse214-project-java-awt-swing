import java.awt.Graphics;
import java.util.ArrayList;

public class EnemyGroup extends DefaultCritter {

    private static final long serialVersionUID = 1L;

    private static final int DEFUALT_ENEMY_RADIUS = vmin * 2 / 100;

    public ArrayList<DefaultCritter> enemies = new ArrayList<>();

    public EnemyGroup(double x, double y, double width, double height, int numEnemiesInRow, int numEnemiesInCol) {
        super(x, y, width, height, 0);

        double xmin = x - width / 2;
        double xmax = x + width / 2;
        double ymin = y - height / 2;
        double ymax = y + height / 2;

        double r = DEFUALT_ENEMY_RADIUS;

        double xSpacing = (width - 2 * r * numEnemiesInRow) / (numEnemiesInRow - 1);
        double ySpacing = (height - 2 * r * numEnemiesInCol) / (numEnemiesInCol - 1);

        for (double eX = xmin + r; eX < xmax; eX += xSpacing + 2 * r) {
            for (double eY = ymin + r; eY < ymax; eY += ySpacing + 2 * r) {
                enemies.add(new DefaultCritter(eX, eY, r, 0));
            }
        }

    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        for (DefaultCritter enemy : enemies) {
            enemy.draw(g);
        }
    }

    @Override
    public void update() {
        super.update();
    }

}