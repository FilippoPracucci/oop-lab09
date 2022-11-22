package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;

    private final JFrame frame = new JFrame();
    private final JLabel display = new JLabel();
    private JButton stop = new JButton("Stop");
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("Down");

    private final Agent agent = new Agent();

    /**
     * Creates a new AnotherConcurrentGUI.
     */
    public AnotherConcurrentGUI() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.frame.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(this.display);
        panel.add(this.up);
        panel.add(this.down);
        panel.add(this.stop);
        this.frame.getContentPane().add(panel);
        this.frame.setVisible(true);

        new Thread(agent).start();

        stop.addActionListener((e) -> agent.stopCounting());
        up.addActionListener((e) -> agent.changeState(false));
        down.addActionListener((e) -> agent.changeState(true));

        new Thread(() -> {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
                } catch (InterruptedException e) {
                    e.printStackTrace(); //NOPMD: allowed for the exercise
                }
                try {
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.agent.stopCounting());
                } catch (InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
    }

    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean isUp = true;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final String nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (isUp) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    e.printStackTrace(); //NOPMD: allowed for the exercise
                }
            }
        }

        public void changeState(final boolean stateIsUp) {
            this.isUp = !stateIsUp;
        }

        public void stopCounting() {
            this.stop = true;
            SwingUtilities.invokeLater(() -> {
                AnotherConcurrentGUI.this.up.setEnabled(false);
                AnotherConcurrentGUI.this.down.setEnabled(false);
                AnotherConcurrentGUI.this.stop.setEnabled(false);
            });
        }
    }
}
