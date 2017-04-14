package ivonhoe.java.qop.device;

import com.android.ddmlib.*;

import java.io.IOException;
import java.io.Serializable;

import static ivonhoe.java.qop.minicomponent.MiniComponent.*;

/**
 * Created by Ivonhoe on 2017/4/11.
 */
public class Device implements Serializable {

    private IDevice device;
    private Banner deviceBanner;

    // 脚本版本号
    private int version = 1;
    private String abi;
    private String sdk;
    private String size;
    // 手机serial id
    private String serial;
    // 当前脚本对应的屏幕分辨率
    private int width;
    private int height;
    // 屏幕录制的开始时间
    private long startTime;

    public Device() {
    }

    public Device(IDevice iDevice) {
        device = iDevice;

        abi = null;
        while (abi == null) {
            abi = device.getProperty(COMMAND_ABI);
        }
        sdk = device.getProperty(COMMAND_SDK);
        // 获取设备屏幕的尺寸
        size = executeShellCommand(SHELL_COMMAND_WM_SIZE).split(":")[1].trim();
        serial = device.getSerialNumber();

        String[] strings = size.split("x");
        if (strings != null && strings.length == 2) {
            width = Integer.valueOf(strings[0]);
            height = Integer.valueOf(strings[1]);
        }
    }

    public void setVersion(int version) {
        this.version = version;
    }
    public void setAbi(String abi) {
        this.abi = abi;
    }
    public void setSdk(String sdk) {
        this.sdk = sdk;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setSerial(String serial) {
        this.serial = serial;
    }
    public String getAbi() {
        return abi;
    }
    public String getSdk() {
        return sdk;
    }
    public String getSize() {
        return size;
    }
    public String getSerial() {
        return serial;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public IDevice getIDevice() {
        return device;
    }
    public Banner getBanner() {
        return deviceBanner;
    }
    public void setBanner(Banner banner) {
        this.deviceBanner = banner;
    }
    public int getVersion() {
        return version;
    }
    public long getStartTime() {
        return startTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void push(String localPath, String remotePath) {
        try {
            device.pushFile(localPath, remotePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (SyncException e) {
            e.printStackTrace();
        }
    }

    public void createForward(int port, String param) {
        try {
            device.createForward(port, param, IDevice.DeviceUnixSocketNamespace.ABSTRACT);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String executeShellCommand(String command) {
        CollectingOutputReceiver output = new CollectingOutputReceiver();
        try {
            device.executeShellCommand(command, output, 0);
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ShellCommandUnresponsiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return output.getOutput();
    }

    //    public void takeScreenShotOnce() {
//        String savePath = "/data/local/tmp/screenshot.jpg";
//        String takeScreenShotCommand = String.format(
//                MINICAP_TAKESCREENSHOT_COMMAND, MINICAP_FILE, size,
//                size, savePath);
//        String localPath = System.getProperty("user.dir") + "/screenshot.jpg";
//        String pullCommand = String.format(ADB_PULL_COMMAND,
//                device.getSerialNumber(), savePath, localPath);
//        try {
//            executeShellCommand(takeScreenShotCommand);
//            device.pullFile(savePath, localPath);
//            Runtime.getRuntime().exec(pullCommand);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (SyncException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (AdbCommandRejectedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//    }

    //判断是否支持minicap
    public boolean isSupportMinicap() {
        String supportCommand = String.format("LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/minicap %sx%s@%sx%s/0 -P -t",
                width, height, width, height);
        String output = executeShellCommand(supportCommand);
        if (output.trim().endsWith("OK")) {
            return true;
        }
        return false;
    }
}
