package ivonhoe.java.qop.device;

import com.android.ddmlib.IDevice;
import ivonhoe.java.qop.minicomponent.MiniCap;
import ivonhoe.java.qop.minicomponent.MiniComponent;
import ivonhoe.java.qop.minicomponent.MiniTouch;
import ivonhoe.java.qop.record.Recorder;
import ivonhoe.java.qop.replay.Replay;
import ivonhoe.java.qop.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ivonhoe.java.qop.device.Controller.ReplayStatus.*;

/**
 * Created by Ivonhoe on 2017/4/12.
 */
public class Controller implements Replay.ReplayExecuteListener {

    private Device mDevice;

    private Recorder mRecorder;

    private MiniTouch mMiniTouch;

    private MiniCap mMinicap;

    private Replay mReplay;

    private boolean isRecording;

    private ReplayStatus replayStatus = STOP;

    public enum ReplayStatus {
        START, PAUSE, RESTART, STOP
    }

    private List<RecordStatusListener> mRecordListenerList = new ArrayList<>();

    private List<ReplayStatusListener> mReplayListenerList = new ArrayList<>();

    public Controller() {
        Adb adb = new Adb();
        if (adb.getDevices().length <= 0) {
            Logger.d("无连接设备,请检查");
            return;
        }
        IDevice iDevice = adb.getDevices()[0];
        mDevice = new Device(iDevice);

        mMiniTouch = new MiniTouch(mDevice);
        mMinicap = new MiniCap(mDevice);

        mRecorder = new Recorder();
        mReplay = new Replay();
        mReplay.setReplayListener(this);
    }

    public Device getDevice() {
        return mDevice;
    }

    public MiniTouch getMiniTouch() {
        return mMiniTouch;
    }

    public MiniCap getMiniCap() {
        return mMinicap;
    }

    public Recorder getRecorder() {
        return mRecorder;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;

        notifyRecordListenerChanged();

        if (isRecording) {
            startRecord();
        } else {
            stopRecord();
        }
    }

    public void addRecordStatusListener(RecordStatusListener listener) {
        if (!mRecordListenerList.contains(listener)) {
            mRecordListenerList.add(listener);
        }
    }

    public void addReplayStatusListener(ReplayStatusListener listener) {
        if (!mReplayListenerList.contains(listener)) {
            mReplayListenerList.add(listener);
        }
    }

    private void notifyRecordListenerChanged() {
        for (RecordStatusListener listener : mRecordListenerList) {
            listener.onRecordStatusChanged(isRecording);
        }
    }

    private void notifyReplayStatusChanged() {
        for (ReplayStatusListener listener : mReplayListenerList) {
            listener.onReplayStatusChanged(replayStatus);
        }
    }

    public void startRecord() {
        Device device = getDevice();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//设置日期格式

        String filePath = MiniComponent.ROOT + File.separator + "record" + File.separator +
                device.getSerial() + "_" + df.format(new Date());
        mRecorder.startRecord(filePath, getDevice());
    }

    public void stopRecord() {
        mRecorder.stopRecord();
    }

    public void executeTouch(String command) {
        Logger.d("execute touch:" + command);
        mMiniTouch.executeTouch(command);
        if (isRecording) {
            mRecorder.append(command);
        }

        String endCommand = "c\n";
        mMiniTouch.executeTouch(endCommand);
        if (isRecording) {
            mRecorder.append(endCommand);
        }
    }

    public ReplayStatus getReplayStatus() {
        return replayStatus;
    }

    public void replayClicked() {
        switch (replayStatus) {
            case STOP:
                setReplaying(START);
                startReplay();
                break;
            case START:
                setReplaying(PAUSE);
                pauseReplay();
                break;
            case PAUSE:
                setReplaying(START);
                restartReplay();
                break;
        }
    }

    public void setReplaying(ReplayStatus replaying) {
        replayStatus = replaying;

        notifyReplayStatusChanged();
    }

    public void startReplay() {
        mReplay.start(null);
    }

    public void pauseReplay() {
        mReplay.pause();
    }

    public void stopReplay() {
        mReplay.stop();
    }

    public void restartReplay() {
        mReplay.restart();
    }

    @Override
    public void onReplayExecute(String command) {
        executeTouch(command);
    }

    @Override
    public void onReplayDone(String filePath) {
        setReplaying(STOP);
    }

    public interface RecordStatusListener {
        public void onRecordStatusChanged(boolean recording);
    }

    public interface ReplayStatusListener {
        public void onReplayStatusChanged(ReplayStatus replayStatus);
    }


}
