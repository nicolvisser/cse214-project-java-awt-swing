import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

import javax.swing.JComponent;

public class InvaderGameState extends JComponent {

    private static final long serialVersionUID = 1L;

    Starfield starfield;
    Shooter shooter;
    EnemyGroup enemyGroup;
    ArrayList<Bunker> bunkers = new ArrayList<>();

    public InvaderGameState(int w, int h) {
        // before adding critters override canvas size --- NOT SURE IF THIS IS IDEAL
        DefaultCritter.setCanvasSize(w, h);

        starfield = new Starfield(w, h);

        shooter = new Shooter();
        add(shooter); // add shooter JComponent to link key bindings

        enemyGroup = new EnemyGroup(300, 200, 600, 400, 10, 6, shooter);
        add(enemyGroup);

        bunkers.add(new Bunker(w * 1 / 4, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
        bunkers.add(new Bunker(w * 2 / 4, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
        bunkers.add(new Bunker(w * 3 / 4, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
    }

    public void draw(Graphics2D g2) {
        starfield.draw(g2);
        shooter.draw(g2);
        enemyGroup.draw(g2);

        for (Bunker bunker : bunkers)
            bunker.draw(g2);

        shooter.drawAimLine(g2, enemyGroup);
    }

    public void update() {
        starfield.update(shooter.velocity);
        shooter.update();
        enemyGroup.update();

        removeDeadCritters(shooter.missiles);
        removeDeadCritters(enemyGroup.enemies);
        removeDeadCritters(enemyGroup.missiles);
        removeDeadCritters(bunkers);

        checkAndHandleCollisions(shooter.missiles, enemyGroup.enemies);
        checkAndHandleCollisions(shooter, enemyGroup.missiles);
        checkAndHandleCollisions(bunkers, shooter.missiles);
        checkAndHandleCollisions(bunkers, enemyGroup.missiles);

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
        try {
            for (DefaultCritter critter1 : group1) {
                for (DefaultCritter critter2 : group2) {
                    if (critter1.isCollidingWith(critter2)) {
                        critter1.handleCollisionWith(critter2);
                    }
                }
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            // skip checking in this frame
        }
    }

    public void checkAndHandleCollisions(DefaultCritter critter1, ArrayList<? extends DefaultCritter> group2) {
        try {
            for (DefaultCritter critter2 : group2) {
                if (critter1.isCollidingWith(critter2)) {
                    critter1.handleCollisionWith(critter2);
                }
            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
            // skip checking in this frame
        }
    }

}