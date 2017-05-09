package ivonhoe.java.qop.record;

import com.google.gson.Gson;
import ivonhoe.java.qop.device.Device;
import ivonhoe.java.qop.replay.ScriptInfo;
import ivonhoe.java.qop.utils.Logger;

import java.io.*;
import java.util.LinkedList;

/**
 * Created by Ivonhoe on 2017/4/11.
 */
public class Recorder {

    /**
     * @param fileName 当前日志指向的文件路径
     */
    private String mCurrent;

    private ScriptInfo mScriptInfo;

    private LinkedList<String> mActionList;

    public void startRecord(String fileName, Device device) {
        if (mCurrent != null) {
            throw new RuntimeException("Stop recorder first!");
        }

        mCurrent = fileName;
        mScriptInfo = new ScriptInfo();
        mScriptInfo.setHeight(device.getHeight());
        mScriptInfo.setWidth(device.getWidth());
        mScriptInfo.setSerial(device.getSerial());
        mScriptInfo.setStartTime(System.currentTimeMillis());
        mScriptInfo.setVersion(device.getVersion());
        mActionList = new LinkedList<>();
    }

    public void append(String log) {
        long time = System.currentTimeMillis();

        mActionList.add(log.replace("\n", "") + "@" + time + "\n");
    }

    public void stopRecord() {
        // 保存到文件
        saveCurrentRecordFile();

        mActionList.clear();
        mCurrent = null;
        mScriptInfo = null;
    }

    private void saveCurrentRecordFile() {
        if (mCurrent == null) {
            throw new RuntimeException("__________");
        }

        File file = new File(mCurrent);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }


        FileOutputStream fileOutput = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            fileOutput = new FileOutputStream(new File(mCurrent));
            bufferedOutput = new BufferedOutputStream(fileOutput);

            String head = "#" + new Gson().toJson(mScriptInfo) + '\n';
            bufferedOutput.write(head.getBytes());

            if (mActionList != null) {
                while (!mActionList.isEmpty()) {
                    String log = mActionList.pop();
                    bufferedOutput.write(log.getBytes("utf-8"));

                    Logger.d(log);
                }
            }

            bufferedOutput.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutput.close();
                bufferedOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
