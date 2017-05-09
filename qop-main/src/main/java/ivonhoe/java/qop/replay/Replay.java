package ivonhoe.java.qop.replay;

import com.google.gson.Gson;
import ivonhoe.java.qop.replay.Message.EndMessage;
import ivonhoe.java.qop.utils.Logger;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Replay {

    MessageQueue mMsgQueue;
    MessageLooper mLooper;

    Thread mReplayLoadThread;
    Thread mReplayWorkThread;

    ReplayExecuteListener mReplayListener;

    BlockingQueue<File> mLoadingReplayFile;

    volatile boolean pause = false;
    long pauseStartTime;

    public Replay() {
        mLoadingReplayFile = new LinkedBlockingQueue();

        mMsgQueue = new MessageQueue();
        mLooper = new MessageLooper(mMsgQueue);
        mReplayLoadThread = new Thread(new ReplayLoaderRunnable());
        mReplayWorkThread = new Thread(new ReplayWorkRunnable());

        mReplayLoadThread.start();
        mReplayWorkThread.start();
    }

    public void setReplayListener(ReplayExecuteListener listener) {
        mReplayListener = listener;
    }

    public void start(String filePath) {
        final File file = new File("/Users/Ivonhoe/Workspace/qop/record/4d005f198e0440a5_2017-04-14_15-56-04");
        if (!file.exists()) {
            return;
        }

        try {
            mLoadingReplayFile.put(file);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Logger.d("work thread:" + mReplayLoadThread.isInterrupted() + ",alive:" + mReplayLoadThread.isAlive());
        Logger.d("work thread:" + mReplayWorkThread.isInterrupted() + ",alive:" + mReplayWorkThread.isAlive());
    }

    public void pause() {
        pause = true;
        pauseStartTime = System.currentTimeMillis();
    }

    public void restart() {
        long pausedTime = System.currentTimeMillis() - pauseStartTime;

        mMsgQueue.delayAllMessage(pausedTime);

        pause = false;
    }

    public void stop() {

    }

    private class ReplayLoaderRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    File file = mLoadingReplayFile.take();
                    Logger.d("-----" + file.getAbsolutePath());

                    InputStream inputStream = new FileInputStream(file);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    String line = null;
                    ScriptInfo scriptInfo = null;
                    long lastCommandTime = 0;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.startsWith("#")) {
                            String info = line.replace("#", "");
                            scriptInfo = new Gson().fromJson(info, ScriptInfo.class);
                            Message msg = new Message();
                            msg.what = scriptInfo;

                            mMsgQueue.postMessageDelay(msg, 0);
                        } else if (scriptInfo == null) {
                            throw new RuntimeException("Script info is NULL!");
                        } else if (line.contains("@")) {
                            String[] strings = line.split("@");
                            if (strings == null || strings.length != 2) {
                                continue;
                            }
                            String command = strings[0];
                            long time = Long.valueOf(strings[1]);
                            lastCommandTime = Math.max(time, lastCommandTime);

                            Message message = new Message();
                            message.what = command.endsWith("\n") ? command : command + "\n";

                            mMsgQueue.postMessageDelay(message, (int) (time - scriptInfo.getStartTime()));
                        }
                    }

                    Message message = new Message();
                    EndMessage endMessage = new EndMessage();
                    endMessage.replayFile = file.getAbsolutePath();
                    message.what = endMessage;
                    mMsgQueue.postMessageDelay(message, (int) (lastCommandTime + 100 - scriptInfo.getStartTime()));

                    Thread.interrupted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ReplayWorkRunnable implements Runnable {

        public void run() {
            for (; ; ) {
                if (pause) {
                    continue;
                }

                // block until get msg
                Message msg = mMsgQueue.next();
                if (msg == null) {
                    continue;
                }

                if (msg.what instanceof String) {
                    mReplayListener.onReplayExecute((String) msg.what);
                } else if (msg.what instanceof ScriptInfo) {
                    System.out.println("Script info:" + new Gson().toJson(msg.what));
                } else if (msg.what instanceof EndMessage) {
                    EndMessage endMessage = (EndMessage) msg.what;
                    mReplayListener.onReplayDone(endMessage.replayFile);
                    Thread.interrupted();
                }
            }
        }
    }

    public interface ReplayExecuteListener {
        void onReplayExecute(String command);

        void onReplayDone(String filePath);
    }
}
