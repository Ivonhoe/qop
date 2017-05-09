package ivonhoe.java.qop.gui;

import ivonhoe.java.qop.device.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by Ivonhoe on 2017/4/13.
 */
public class MenuPanel extends JPanel {

    private Controller mController;

    public MenuPanel(Controller controller) {
        mController = controller;

        JButton backButton = new JButton();
        backButton.addMouseListener(new BackClickListener());
        backButton.setText("back");

        JButton homeButton = new JButton();
        homeButton.addMouseListener(new HomeClickListener());
        homeButton.setText("home");

        JButton menuButton = new JButton();
        menuButton.addMouseListener(new MenuClickListener());
        menuButton.setText("menu");

        JButton powerButton = new JButton();
        powerButton.addMouseListener(new PowerClickListener());
        powerButton.setText("power");

        add(backButton);
        add(homeButton);
        add(menuButton);
        add(powerButton);

        setLayout(new FlowLayout());
    }

    private class BackClickListener extends OnClickListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            mController.executorBackEvent();
        }
    }

    private class MenuClickListener extends OnClickListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            mController.executorMenuEvent();
        }
    }

    private class HomeClickListener extends OnClickListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            mController.executorHomeEvent();
        }
    }

    private class PowerClickListener extends OnClickListener{
        @Override
        public void mouseClicked(MouseEvent e) {
            mController.executorPowerEvent();
        }
    }
}
