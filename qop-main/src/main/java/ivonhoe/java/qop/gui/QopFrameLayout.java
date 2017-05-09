package ivonhoe.java.qop.gui;

import ivonhoe.java.qop.device.Controller;
import ivonhoe.java.qop.device.Device;
import ivonhoe.java.qop.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class QopFrameLayout extends JFrame {

    public static final int WIDTH = 360;
    public static final int HEIGHT_TITLE_BAR = 30;
    public static final int HEIGHT_RECORD_PANEL = 60;
    public static final int HEIGHT_MENU_PANEL = 60;

    public QopFrameLayout() {
        final Controller controller = new Controller();

        addComponentsToPane(getContentPane(), controller);

        int width = WIDTH;
        Device device = controller.getDevice();
        int height = WIDTH * device.getHeight() / device.getWidth() +
                HEIGHT_TITLE_BAR + HEIGHT_RECORD_PANEL + HEIGHT_MENU_PANEL;

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (dim.width - width) / 2;
        int y = 0;
        setLocation(x, y);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            }
        });
        setVisible(true);
        pack();

        setSize(width, height);
    }

    public void addComponentsToPane(Container pane, Controller controller) {
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }

        RecordPanel actionPanel = new RecordPanel(controller);
        PhoneScreenPanel screenPanel = new PhoneScreenPanel(controller);
        MenuPanel menuPanel = new MenuPanel(controller);

        pane.add(actionPanel, BorderLayout.PAGE_START);
        pane.add(screenPanel, BorderLayout.CENTER);
        pane.add(menuPanel, BorderLayout.PAGE_END);
    }

    public static void main(String[] args) {
        new QopFrameLayout();
    }

}
