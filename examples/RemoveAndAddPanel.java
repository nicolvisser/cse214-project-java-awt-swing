import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RemoveAndAddPanel implements ActionListener{
    JFrame frame;
    JPanel firstPanel;
    JPanel secondPanel;
    JPanel controlPanel;
    JButton nextButton;
    JPanel panelContainer;
    JButton preButton;
    JPanel contentPane;

    public RemoveAndAddPanel() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        firstPanel = new JPanel();
        firstPanel.add(new JLabel("FirstPanel"));
        firstPanel.setPreferredSize(new Dimension(100,100));

        secondPanel = new JPanel();
        secondPanel.add(new JLabel("Second panel"));
        secondPanel.setPreferredSize(new Dimension(100,100));

        panelContainer = new JPanel();
        contentPane = new JPanel(new BorderLayout());

        nextButton = new JButton("Next panel");
        preButton = new JButton("PreButton");
        controlPanel = new JPanel();

        nextButton.addActionListener(this);
        preButton.addActionListener(this);
        preButton.setEnabled(false);

        controlPanel.add(preButton);
        controlPanel.add(nextButton);

        panelContainer.setLayout(new BorderLayout());
        panelContainer.add(firstPanel,BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.SOUTH);
        contentPane.add(panelContainer,BorderLayout.CENTER);

        frame.setContentPane(contentPane);

        frame.setVisible(true);
        frame.setSize(300,100);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == nextButton) {
            panelContainer.removeAll();
            panelContainer.setSize(0,0);
            panelContainer.setSize(secondPanel.getSize());
            panelContainer.add(secondPanel,BorderLayout.CENTER);
            panelContainer.revalidate();


            nextButton.setEnabled(false);
            preButton.setEnabled(true);
        }
        if (e.getSource() == preButton) {
            panelContainer.removeAll();
            panelContainer.setSize(0,0);
            panelContainer.setSize(firstPanel.getSize());
            panelContainer.add(firstPanel,BorderLayout.CENTER);
            nextButton.setEnabled(true);
            preButton.setEnabled(false);
        }
    }
    public static void main(String args[]) {
        new RemoveAndAddPanel();
    }
}