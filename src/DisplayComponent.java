import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * Convenience class. For example in InvadersPanel, there are multiple
 * DisplayComponents defined, but only one is active at a time. Thus can define
 * a variable DisplayState activeDisplayState and use that to call say
 * activeDisplayState.draw(g2) which simplifies code.
 */
public class DisplayComponent extends JComponent implements Drawable {

    private static final long serialVersionUID = 1L;

    @Override
    public void draw(Graphics2D g2) {
    }

}