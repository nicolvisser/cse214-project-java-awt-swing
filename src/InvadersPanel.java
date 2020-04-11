import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class InvadersPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final int pWidth;
    private final int pHeight;

    InvaderGameState igs;

    String[] menuOptions = { "Option1", "Option2" };
    MenuScreen menu;

    public InvadersPanel(int width, int height) {
        pWidth = width;
        pHeight = height;
        setPreferredSize(new Dimension(width, height));
        setIgnoreRepaint(true);
        setKeyBindings();

        // igs = new InvaderGameState(pWidth, pHeight);
        // add(igs);

        menu = new MenuScreen(width, height, "TestTitle", "TestSubTitle", menuOptions);
        add(menu);
    }

    public void update() {
        // igs.update();
    }

    public void draw(Graphics2D g2) {

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, pWidth, pHeight);

        // igs.draw(g2);
        menu.draw(g2);

    }

    private void setKeyBindings() {
        // Special thanks to https://www.youtube.com/watch?v=LNizNHaRV84&t=1484s
        // https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        KeyStroke quitKeyPress = KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false);
        inputMap.put(quitKeyPress, "quitKeyPress");
        actionMap.put("quitKeyPress", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }

        });
    }

}