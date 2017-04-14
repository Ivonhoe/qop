package ivonhoe.java.qop.gui;

import ivonhoe.java.qop.device.Controller;
import ivonhoe.java.qop.minicomponent.AndroidScreenObserver;
import ivonhoe.java.qop.device.Banner;
import ivonhoe.java.qop.minicomponent.MiniCap;
import ivonhoe.java.qop.utils.Logger;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * Created by Ivonhoe on 2017/4/11.
 */
public class PhoneScreenPanel extends Panel implements AndroidScreenObserver {

    private BufferedImage image = null;

    private int width = 360;
    private int height = 600;

    private Controller mController;

    public PhoneScreenPanel(Controller controller) {
        mController = controller;

        MiniCap minicap = controller.getMiniCap();
        minicap.registerObserver(this);

        MouseListener mouseListener = new ScreenMouseListener();
        addMouseListener(mouseListener);

        MouseMotionListener motionListener = new ScreenMotionListener();
        addMouseMotionListener(motionListener);
    }

    public void paint(Graphics g) {
        try {
            if (image == null) {
                return;
            }
            g.drawImage(image, 0, 0, width, height, null);
            setSize(width, height);
            image.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void frameImageChange(Image image) {
        this.image = (BufferedImage) image;
        int w = this.image.getWidth();
        int h = this.image.getHeight();
        float radio = (float) width / (float) w;
        height = (int) (radio * h);
        this.repaint();
    }

    private Point pointConvert(Point point) {
        Banner banner = mController.getDevice().getBanner();
        int x = (int) ((point.getX() * 1.0 / width) * banner.getMaxX());
        int y = (int) ((point.getY() * 1.0 / height) * banner.getMaxY());
        return new Point(x, y);
    }

    private class ScreenMouseListener extends OnClickListener {
        @Override
        public void mousePressed(MouseEvent e) {
            Point point = pointConvert(e.getPoint());
            String command = String.format("d 0 %s %s 50\n", (int) point.getX(), (int) point.getY());
            mController.executeTouch(command);
        }

        public void mouseReleased(MouseEvent e) {
            String command = "u 0\n";
            mController.executeTouch(command);
        }
    }

    private class ScreenMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            Point point = pointConvert(e.getPoint());
            String command = String.format("m 0 %s %s 50\n", (int) point.getX(), (int) point.getY());
            mController.executeTouch(command);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }
}
