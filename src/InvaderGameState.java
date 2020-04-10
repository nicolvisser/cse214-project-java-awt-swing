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

        enemyGroup = new EnemyGroup(300, 200, 600, 400, 10, 6);
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

        // remove missiles if neccessary
        Iterator<Missile> shooterMissileIterator = shooter.missiles.iterator();
        while (shooterMissileIterator.hasNext()) {
            Missile shooterMissile = shooterMissileIterator.next();
            if (shooterMissile.state == Missile.MissileState.DEAD) {
                shooterMissileIterator.remove();
            }
        }

        // remove enemies if neccessary
        Iterator<Enemy> enemyIterator = enemyGroup.enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (enemy.state == Enemy.EnemyState.DEAD) {
                enemyIterator.remove();
            }
        }

        // check collissions between missiles and enemies
        for (Missile shooterMissile : shooter.missiles) {
            for (Enemy enemy : enemyGroup.enemies) {
                if (shooterMissile.isCollidingWith(enemy)) {
                    shooterMissile.handleCollisionWith(enemy);
                }
            }
        }

    }

}