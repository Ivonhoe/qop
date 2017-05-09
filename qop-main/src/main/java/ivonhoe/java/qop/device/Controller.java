package ivonhoe.java.qop.device;

import com.android.ddmlib.IDevice;
import ivonhoe.java.qop.component.*;
import ivonhoe.java.qop.record.Recorder;
import ivonhoe.java.qop.replay.Replay;
import ivonhoe.java.qop.utils.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static ivonhoe.java.qop.device.Controller.ReplayStatus.*;

/**
 * Created by Ivonhoe on 2017/4/12.
 */
public class Controller implements Replay.ReplayExecuteListener {

    private static final String KEY_COMMAND = "key ";

    private Device mDevice;

    private Recorder mRecorder;

    private MiniTouch mMiniTouch;

    private MiniCap mMinicap;

    private Replay mReplay;

    private StfAgent mStfAgent;

    private HprofDump mHeapDumper;

    private boolean isRecording;

    private ReplayStatus replayStatus = STOP;

    private boolean isDumpHeap = false;

    public enum ReplayStatus {
        START, PAUSE, STOP
    }

    private List<RecordStatusListener> mRecordListenerList = new ArrayList<>();

    private List<ReplayStatusListener> mReplayListenerList = new ArrayList<>();

    public Controller() {
        Adb adb = Adb.instance();
        IDevice iDevice = adb.getDevices()[0];
        mDevice = new Device(iDevice);

        mMiniTouch = new MiniTouch(mDevice);
        mMinicap = new MiniCap(mDevice);

        mRecorder = new Recorder();
        mReplay = new Replay();
        mReplay.setReplayListener(this);

        mStfAgent = new StfAgent(mDevice);

        //mHeapDumper = new HprofDump(mDevice);
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

        String filePath = MiniComponent.ROOT + File.separator + "qop-main" + File.separator + "output" + File.separator
                + "record" + File.separator + device.getSerial() + "_" + df.format(new Date());
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
        if (command.startsWith(KEY_COMMAND)) {
            int keycode = Integer.valueOf(command.replace(KEY_COMMAND, "").replace("\n", ""));
            mStfAgent.onKeyEvent(keycode);
        } else {
            executeTouch(command);
        }
    }

    @Override
    public void onReplayDone(String filePath) {
        setReplaying(STOP);
    }

    public void executorBackEvent() {
        mStfAgent.backEvent();

        recordKeyEvent(KeyCode.KEYCODE_BACK);
    }

    public void executorHomeEvent() {
        mStfAgent.homeEvent();

        recordKeyEvent(KeyCode.KEYCODE_HOME);
    }

    public void executorMenuEvent() {
        mStfAgent.menuEvent();

        recordKeyEvent(KeyCode.KEYCODE_MENU);
    }

    public void executorPowerEvent() {
        mStfAgent.onKeyEvent(KeyCode.KEYCODE_POWER);

        recordKeyEvent(KeyCode.KEYCODE_POWER);
    }

    private void recordKeyEvent(int keyCode) {
        if (isRecording) {
            String command = KEY_COMMAND + keyCode + "\n";
            mRecorder.append(command);
        }
    }

    public void dumpTopTaskHeap() {
        if (mHeapDumper == null) {
            return;
        }
        mHeapDumper.dumpTopTaskHprof();
    }

    public interface RecordStatusListener {
        public void onRecordStatusChanged(boolean recording);
    }

    public interface ReplayStatusListener {
        public void onReplayStatusChanged(ReplayStatus replayStatus);
    }


}
