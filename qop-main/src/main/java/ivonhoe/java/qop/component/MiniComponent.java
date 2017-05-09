package ivonhoe.java.qop.component;

import ivonhoe.java.qop.device.*;
import ivonhoe.java.qop.utils.Logger;

import java.io.File;

/**
 * Created by Ivonhoe on 2017/4/11.
 */
public abstract class MiniComponent {

    public static final String ROOT = System.getProperty("user.dir");

    public static final String MINITOUCH = "minitouch";
    public static final String MINICAP = "minicap";
    public static final String MINICAP_SO = "minicap.so";
    public static final String MINICAP_NOPIE = "minicap-nopie";
    public static final String MINITOUCH_NOPIE = "minitouch-nopie";

    public static final String DIR_MINITOUCH = ROOT + File.separator + "qop-main" + File.separator + "bin" +
            File.separator + MINITOUCH + File.separator;
    public static final String DIR_MINICAP = ROOT + File.separator + "qop-main" + File.separator + "bin" + File.separator + MINICAP + File.separator
            + "bin" + File.separator;

//    public static final String DIR_MINICAP = ROOT + File.separator + MINICAP + File.separator;

    // TODO
    public static final String DIR_MINICAP_SHARE = ROOT + File.separator + "qop-main" + File.separator + "bin" + File.separator + MINICAP
            + File.separator + "shared" + File.separator;

    public static final String REMOTE_PATH = "/data/local/tmp/%s";

    public static final String COMMAND_ABI = "ro.product.cpu.abi";
    public static final String COMMAND_SDK = "ro.build.version.sdk";
    public static final String SHELL_COMMAND_CHMOD = "chmod 777 %s";
    public static final String SHELL_COMMAND_WM_SIZE = "wm size";
    public static final String SHELL_COMMAND_MINICAP = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/%s -P %s@%s/0";

    public static final String SHELL_COMMAND_TAKE_SCREENSHOT = "LD_LIBRARY_PATH=/data/local/tmp /data/local/tmp/%s -P %s@%s/0 -s >%s";
    public static final String SHELL_COMMAND_ADB_PULL = "adb -s %s pull %s %s";

    protected Device mDevice;
    protected int mPort;

    public MiniComponent(Device device) {
    }

    public MiniComponent(Device device, int port) {
        mDevice = device;
        mPort = port;

        setupComponent();

        startComponent();
    }

    public abstract String getComponentDir();

    public abstract String getComponentName();

    public abstract String getStartCommand();

    /**
     * 将minicap的二进制和.so文件push到/data/local/tmp文件夹下，启动minicap服务
     */
    protected void setupComponent() {
        String abi = mDevice.getAbi();
        String sdk = mDevice.getSdk();
        File componentFile = new File(getComponentDir() + abi + File.separator + getComponentName());

        String remotePath = String.format(REMOTE_PATH, getComponentName());

        System.out.println("DIR_MINITOUCH:" + DIR_MINITOUCH);
        // 将minicap的可执行文件和.so文件一起push到设备中
        mDevice.push(componentFile.getAbsolutePath(), remotePath);

        String chmodCommand = String.format(SHELL_COMMAND_CHMOD, remotePath);
        mDevice.executeShellCommand(chmodCommand);
    }

    public void startComponent() {
        // 端口转发
        mDevice.createForward(mPort, getComponentName());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String output = mDevice.executeShellCommand(getStartCommand());
                Logger.d("^^^^^^^output:" + output);
            }
        });
        thread.start();
    }

}
