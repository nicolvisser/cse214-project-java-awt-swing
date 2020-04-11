import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
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

    int pWidth, pHeight;

    public InvaderGameState(int w, int h) {
        // before adding critters override canvas size --- NOT SURE IF THIS IS IDEAL
        DefaultCritter.setCanvasSize(w, h);

        this.pWidth = w;
        this.pHeight = h;

        starfield = new Starfield(w, h);

        shooter = new Shooter();
        add(shooter); // add shooter JComponent to link key bindings

        enemyGroup = new EnemyGroup(0.2 * w, 0.15 * h, 0.4 * w, 0.3 * h, 6, 4, shooter);
        add(enemyGroup);

        bunkers.add(new Bunker(0.25 * w, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
        bunkers.add(new Bunker(0.50 * w, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
        bunkers.add(new Bunker(0.75 * w, 0.7 * h, 0.2 * w, 0.05 * h, 4, 16));
    }

    public void draw(Graphics2D g2) {
        starfield.draw(g2);
        shooter.draw(g2);
        enemyGroup.draw(g2);

        for (Bunker bunker : bunkers)
            bunker.draw(g2);

        shooter.drawAimLine(g2, enemyGroup, bunkers);

        // HUD

        g2.setColor(new Color(0.6f, 0.2f, 0.2f, 0.9f));
        drawStatusBar(g2, pWidth * 3 / 100, pHeight * 75 / 100, pWidth / 100, pHeight * 20 / 100,
                shooter.getHealthPercentage());

        g2.setColor(new Color(0.2f, 0.5f, 0.7f, 0.9f));
        drawStatusBar(g2, pWidth * 5 / 100, pHeight * 75 / 100, pWidth / 100, pHeight * 20 / 100, 100);

    }

    public void drawStatusBar(Graphics2D g2, int x, int y, int w, int h, int perc) {
        final int SPACING = 2;

        double innerHeight = (h - 2 * SPACING) * perc / 100.0;
        double innerPositionY = y + SPACING + (h - 2 * SPACING) * (1 - perc / 100.0);

        RoundRectangle2D outer = new RoundRectangle2D.Double(x, y, w, h, w, w);
        RoundRectangle2D inner = new RoundRectangle2D.Double(x + SPACING, innerPositionY, w - 2 * SPACING, innerHeight,
                w - 2 * SPACING, w - 2 * SPACING);

        g2.draw(outer);
        g2.fill(inner);

    }

    public void update() {
        starfield.update(shooter.velocity);
        shooter.update();
        enemyGroup.update();

        for (Bunker bunker : bunkers)
            bunker.update();

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
            System.out.println("===== EXCEPTION IDENTIFIED: ================================");
            e.printStackTrace();
            System.out.println("------------------------------------------------------------");
            System.out.println("Handled by skipping draw of some critters for a single frame");
            System.out.println("============================================================");
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
            System.out.println("===== EXCEPTION IDENTIFIED: ================================");
            e.printStackTrace();
            System.out.println("------------------------------------------------------------");
            System.out.println("Handled by skipping draw of some critters for a single frame");
            System.out.println("============================================================");
        }
    }

}