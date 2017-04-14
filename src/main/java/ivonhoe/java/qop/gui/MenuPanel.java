package ivonhoe.java.qop.gui;

import ivonhoe.java.qop.device.Controller;

import javax.swing.*;

/**
 * Created by Ivonhoe on 2017/4/13.
 */
public class MenuPanel extends JPanel {

    private Controller mController;

    public MenuPanel(Controller controller) {
        mController = controller;

        JButton backButton = new JButton();
        backButton.setText("back");

        add(backButton);
    }
}
