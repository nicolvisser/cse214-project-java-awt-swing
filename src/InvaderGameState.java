import java.awt.Graphics2D;
import java.util.Iterator;

import javax.swing.JComponent;

public class InvaderGameState extends JComponent {

    private static final long serialVersionUID = 1L;

    Starfield starfield;
    Shooter shooter;
    EnemyGroup enemyGroup;

    public InvaderGameState(int w, int h) {
        // before adding critters override canvas size --- NOT SURE IF THIS IS IDEAL
        DefaultCritter.setCanvasSize(w, h);

        starfield = new Starfield(w, h);

        shooter = new Shooter();
        add(shooter); // add shooter JComponent to link key bindings

        enemyGroup = new EnemyGroup(300, 200, 600, 400, 10, 5);
        add(enemyGroup);
    }

    public void draw(Graphics2D g) {
        starfield.draw(g);
        shooter.draw(g);
        enemyGroup.draw(g);
    }

    public void update() {
        starfield.update(shooter.velocity);
        shooter.update();
        enemyGroup.update();

        Iterator<Missile> shooterMissileIterator = shooter.missiles.iterator();
        while (shooterMissileIterator.hasNext()) {
            Missile shooterMissile = shooterMissileIterator.next();
            if (shooterMissile.getCollisionShape().intersects(enemyGroup.getCollisionShape())) {
                Iterator<DefaultCritter> enemyIterator = enemyGroup.enemies.iterator();
                while (enemyIterator.hasNext()) {
                    DefaultCritter enemy = enemyIterator.next();
                    if (shooterMissile.getCollisionShape().intersects(enemy.getCollisionShape())) {
                        shooterMissileIterator.remove();
                        enemyIterator.remove();
                        break;
                    }
                }
            }
        }

    }

}