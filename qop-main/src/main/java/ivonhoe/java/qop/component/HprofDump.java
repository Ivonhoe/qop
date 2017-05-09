package ivonhoe.java.qop.component;

import com.android.ddmlib.Client;
import com.android.ddmlib.ClientData;
import com.android.ddmlib.IDevice;
import ivonhoe.java.bitmapanalyzer.BitmapAnalyzer;
import ivonhoe.java.qop.device.Adb;
import ivonhoe.java.qop.device.Device;
import ivonhoe.java.qop.device.ShellCommand;
import ivonhoe.java.qop.utils.Logger;
import ivonhoe.java.qop.utils.Utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivonhoe on 2017/4/18.
 */
public class HprofDump {

    private static final String DUMP_TOP_ACTIVITY = "dumpsys activity top";

    private Device mDevice;
    private boolean isDumping = false;

    /**
     * 设置HprofDumpHandler，对dump事件做监听处理dump成功或者失败的回调，并保存hprof文件数据
     *
     * @param device
     */
    public HprofDump(Device device) {
        mDevice = device;

        ClientData.IHprofDumpHandler hprofDumpHandler = new ClientData.IHprofDumpHandler() {

            @Override
            public void onSuccess(String remoteFilePath, Client client) {
                String hprofPath = getHprofPath(client.getClientData().getClientDescription());
                mDevice.pull(hprofPath, remoteFilePath);

                conversionAndRemoveHprof(hprofPath);

                isDumping = false;
            }

            @Override
            public void onSuccess(byte[] bytes, Client client) {
                String hprofPath = getHprofPath(client.getClientData().getClientDescription());
                Utilities.saveFile(bytes, hprofPath);

                conversionAndRemoveHprof(hprofPath);

                isDumping = false;
            }

            @Override
            public void onEndFailure(Client client, String s) {
                isDumping = false;
            }
        };
        ClientData.setHprofDumpHandler(hprofDumpHandler);
    }

    /**
     * dump栈顶进程的hprof
     */
    public void dumpTopTaskHprof() {
        if (isDumping) {
            return;
        }

        IDevice iDevice = mDevice.getIDevice();

        /**
         * 这里自动选择手机上栈顶进程做dump操作，获取applicationName
         */
        String topApp = getTopApplication();
        Client client = iDevice.getClient(topApp);

        if (client == null) {
            throw new RuntimeException("Can not dump app:" + topApp);
        }
        client.dumpHprof();
        isDumping = true;
    }

    private String getTopApplication() {
        String result = mDevice.executeShellCommand(DUMP_TOP_ACTIVITY);
        String[] lines = result.split("\n");
        if (lines == null || lines.length == 0) {
            return null;
        }

        String pattern = "TASK\\s(.*)\\sid=\\d*";
        Pattern r = Pattern.compile(pattern);

        for (String line : lines) {
            Matcher matcher = r.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }

        return null;
    }

    private void conversionAndRemoveHprof(String hprofPath) {
        String convHprofPath = getConvHprofPath(hprofPath);
        hprofConv(hprofPath, convHprofPath);
        removeFile(hprofPath);

        analyzerBitmap(convHprofPath);
    }

    private void hprofConv(String hprofPath, String convHprofPath) {
        String adbPath = Adb.instance().getAdbPath();
        String hprofConvPath = adbPath.replace("adb", "hprof-conv");
        ShellCommand.exec(hprofConvPath + " " + hprofPath + " " + convHprofPath);
    }

    private void removeFile(String file) {
        ShellCommand.exec("rm " + file);
    }

    private String getHprofPath(String description) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//设置日期格式
        final String hprofName = description + "_" + df.format(new Date()) + File.separator + description + ".hprof";
        return System.getProperty("user.dir") + File.separator + "output" + File.separator + "hprof" +
                File.separator + hprofName;
    }

    private String getConvHprofPath(String description) {
        return description.replace(".hprof", "_conv.hprof");
    }

    private void analyzerBitmap(String hprofPath) {
        String[] args = new String[2];
        args[0] = hprofPath;
        args[1] = "bitmap";
        BitmapAnalyzer.main(args);
    }
}
