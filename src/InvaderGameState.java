import java.awt.Graphics2D;
import java.util.ArrayList;
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

        removeDeadCritters(shooter.missiles);
        removeDeadCritters(enemyGroup.enemies);

        checkAndHandleCollisions(shooter.missiles, enemyGroup.enemies);

    }

    public void removeDeadCritters(ArrayList<? extends DefaultCritter> group) {
        Iterator<? extends DefaultCritter> critterIterator = group.iterator();
        while (critterIterator.hasNext()) {
            DefaultCritter critter = critterIterator.next();
            if (critter.mayBeRemoved()) {
                critterIterator.remove();
            }
        }
    }

    public void checkAndHandleCollisions(ArrayList<? extends DefaultCritter> group1,
            ArrayList<? extends DefaultCritter> group2) {
        for (DefaultCritter critter1 : group1) {
            for (DefaultCritter critter2 : group2) {
                if (critter1.isCollidingWith(critter2)) {
                    critter1.handleCollisionWith(critter2);
                }
            }
        }
    }

    public void checkAndHandleCollisions(DefaultCritter critter1, ArrayList<? extends DefaultCritter> group2) {
        for (DefaultCritter critter2 : group2) {
            if (critter1.isCollidingWith(critter2)) {
                critter1.handleCollisionWith(critter2);
            }
        }
    }

}