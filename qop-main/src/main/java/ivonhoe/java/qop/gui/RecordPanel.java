package ivonhoe.java.qop.gui;

import ivonhoe.java.qop.device.Controller;
import ivonhoe.java.qop.device.Controller.ReplayStatus;
import ivonhoe.java.qop.utils.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

import static ivonhoe.java.qop.component.MiniComponent.ROOT;

/**
 * Created by Ivonhoe on 2017/4/13.
 */
public class RecordPanel extends JPanel implements Controller.RecordStatusListener, Controller.ReplayStatusListener {

    private static final String DIR_ICON = ROOT + File.separator + "qop-main" + File.separator;

    private Controller mController;
    private OnClickListener mStartRecordListener;
    private OnClickListener mStopRecordListener;
    private OnClickListener mReplayListener;
    private OnClickListener mDumpListener;

    JLabel startLabel;
    JLabel stopLabel;
    JLabel replayLabel;
    JLabel dumpLabel;

    ImageIcon iconRecordEnable;
    ImageIcon iconRecordDisable;
    ImageIcon iconRecordDown;

    ImageIcon iconStopEnable;
    ImageIcon iconStopDisable;
    ImageIcon iconStopDown;

    ImageIcon iconReplayEnable;
    ImageIcon iconReplayDisable;
    ImageIcon iconReplayDown;

    ImageIcon iconPauseEnable;
    ImageIcon iconPauseDisable;
    ImageIcon iconPauseDown;

    ImageIcon iconDumpEnable;
    ImageIcon iconDumpDisable;
    ImageIcon iconDumpDown;

    public RecordPanel(Controller controller) {
        mController = controller;
        mController.addRecordStatusListener(this);
        mController.addReplayStatusListener(this);

        mStartRecordListener = new StartRecord();
        mStopRecordListener = new StopRecord();
        mReplayListener = new Replay();
        mDumpListener = new DumpListener();

        iconRecordEnable = new ImageIcon(DIR_ICON + "image/record_enabled.png");
        iconRecordDisable = new ImageIcon(DIR_ICON + "image/record_disabled.png");
        iconRecordDown = new ImageIcon(DIR_ICON + "image/record_enabled_down.png");

        startLabel = new JLabel(iconRecordEnable);
        startLabel.setBounds(0, 0, iconRecordEnable.getIconWidth(), iconRecordEnable.getIconHeight());
        startLabel.addMouseListener(mStartRecordListener);

        iconStopEnable = new ImageIcon(DIR_ICON + "image/stop_enabled.png");
        iconStopDisable = new ImageIcon(DIR_ICON + "image/stop_disabled.png");
        iconStopDown = new ImageIcon(DIR_ICON + "image/stop_enabled_down.png");

        stopLabel = new JLabel(iconStopDisable);
        stopLabel.setBounds(0, 0, iconStopDisable.getIconWidth(), iconStopDisable.getIconHeight());
        stopLabel.addMouseListener(mStopRecordListener);

        JLabel stopLabel2 = new JLabel(iconStopDisable);
        stopLabel2.setBounds(0, 0, iconStopDisable.getIconWidth(), iconStopDisable.getIconHeight());
        stopLabel2.addMouseListener(mStopRecordListener);

        iconReplayEnable = new ImageIcon(DIR_ICON + "image/play_enabled.png");
        iconReplayDisable = new ImageIcon(DIR_ICON + "image/play_disabled.png");
        iconReplayDown = new ImageIcon(DIR_ICON + "image/play_enabled_down.png");

        iconPauseEnable = new ImageIcon(DIR_ICON + "image/pause_enabled.png");
        iconPauseDisable = new ImageIcon(DIR_ICON + "image/pause_disabled.png");
        iconPauseDown = new ImageIcon(DIR_ICON + "image/pause_enabled_down.png");

        replayLabel = new JLabel(iconReplayEnable);
        replayLabel.setBounds(0, 0, iconReplayEnable.getIconWidth(), iconReplayEnable.getIconHeight());
        replayLabel.addMouseListener(mReplayListener);

        iconDumpEnable = new ImageIcon(DIR_ICON + "image/dump_enabled.png");
        iconDumpDisable = new ImageIcon(DIR_ICON + "image/dump_disabled.png");
        iconDumpDown = new ImageIcon(DIR_ICON + "image/dump_enabled_down.png");

        dumpLabel = new JLabel(iconDumpEnable);
        dumpLabel.setBounds(0, 0, iconReplayEnable.getIconWidth(), iconReplayEnable.getIconHeight());
        dumpLabel.addMouseListener(mDumpListener);

        add(startLabel);
        add(stopLabel);
        add(replayLabel);
        //add(dumpLabel);

        setLayout(new FlowLayout());
    }

    @Override
    public void onRecordStatusChanged(boolean recording) {
        if (recording) {
            startLabel.setIcon(iconRecordDisable);
            stopLabel.setIcon(iconStopEnable);
        } else {
            startLabel.setIcon(iconRecordEnable);
            stopLabel.setIcon(iconStopDisable);
        }
    }

    @Override
    public void onReplayStatusChanged(ReplayStatus status) {
        ReplayStatus replayStatus = mController.getReplayStatus();

        switch (replayStatus) {
            case START:
                replayLabel.setIcon(iconPauseEnable);
                break;
            case STOP:
                replayLabel.setIcon(iconReplayEnable);
                break;
            case PAUSE:
                replayLabel.setIcon(iconReplayEnable);
                break;
        }
    }

    private boolean isReplaying() {
        return mController.getReplayStatus() != ReplayStatus.STOP;
    }

    private class StartRecord extends OnClickListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (isReplaying()) {
                return;
            }
            if (!mController.isRecording()) {
                mController.setRecording(true);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (isReplaying()) {
                return;
            }
            if (!mController.isRecording()) {
                startLabel.setIcon(iconRecordDown);
            }
        }
    }

    private class StopRecord extends OnClickListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (isReplaying()) {
                return;
            }

            if (mController.isRecording()) {
                mController.setRecording(false);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (isReplaying()) {
                return;
            }

            if (mController.isRecording()) {
                stopLabel.setIcon(iconStopDown);
            }
        }
    }

    private class Replay extends OnClickListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            mController.replayClicked();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            ReplayStatus replayStatus = mController.getReplayStatus();

            switch (replayStatus) {
                case STOP:
                    replayLabel.setIcon(iconReplayDown);
                    break;
                case PAUSE:
                    replayLabel.setIcon(iconReplayDown);
                    break;
                case START:
                    replayLabel.setIcon(iconPauseDown);
                    break;
            }
        }
    }

    private class DumpListener extends OnClickListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            mController.dumpTopTaskHeap();
        }

        @Override
        public void mousePressed(MouseEvent e) {
            dumpLabel.setIcon(iconDumpDown);
        }
    }
}
