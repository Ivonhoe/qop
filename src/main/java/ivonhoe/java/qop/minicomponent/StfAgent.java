package ivonhoe.java.qop.minicomponent;

import ivonhoe.java.qop.device.KeyCode;
import ivonhoe.java.qop.device.Adb;
import ivonhoe.java.qop.device.Device;
import ivonhoe.java.qop.utils.Logger;
import jp.co.cyberagent.stf.proto.Wire;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Ivonhoe on 2017/4/17.
 */
public class StfAgent {

    private static final int PORT_AGENT = 1090;

    private static final String STRING_ERROR = "Not found";

    private static final String SERVICE_NAME = "jp.co.cyberagent.stf";
    private static final String COMMAND_COMPONENT_SERVICE = "am startservice jp.co.cyberagent.stf/.Service -a %s";
    private static final String COMMAND_START_SERVICE = "jp.co.cyberagent.stf.ACTION_START";
    private static final String COMMAND_STOP_SERVICE = "jp.co.cyberagent.stf.ACTION_STOP";

    private static final String COMMAND_APK_PATH = "APK=$(adb shell pm path jp.co.cyberagent.stf | tr -d '\\r' | awk -F: '{print $2}')";

    private static final String COMMAND_START_AGENT = "export CLASSPATH=\"/data/app/jp.co.cyberagent.stf-1/base.apk\"\\; \\\n" +
            "    exec app_process /system/bin jp.co.cyberagent.stf.Agent";

    private static final String PATH_SERVICE_APK = MiniComponent.ROOT + File.separator + "bin" + File.separator + "apk" + File.separator + "STFService.apk";

    private int mPort;
    private Device device;
    private Socket mSocket;

    private OutputStream mSocketOutput;
    private InputStream mSocketInput;

    public StfAgent(Device device) {
        mPort = PORT_AGENT;
        this.device = device;

        startService();
        startForward();
        startAgent();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        connect();
    }

    private void connect() {
        try {
            mSocket = new Socket("localhost", mPort);

            mSocketInput = mSocket.getInputStream();
            mSocketOutput = mSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeKeyEvent(Wire.Envelope input) {
        if (mSocketInput == null) {
            return;
        }

        try {
            input.writeDelimitedTo(mSocketOutput);
            mSocketOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean startService() {
        String command = String.format(COMMAND_COMPONENT_SERVICE, COMMAND_START_SERVICE);
        String result = device.executeShellCommand(command);

        Logger.d(result);
        if (result == null) {
            return false;
        }

        if (result.contains(STRING_ERROR)) {
            installService();
        }

        result = device.executeShellCommand(command);

        Logger.d("result:" + result);
        if (result.contains(STRING_ERROR)) {
            return false;
        }

        return true;
    }

    private boolean installService() {
        String result = Adb.install(device.getSerial(), PATH_SERVICE_APK);
        return true;
    }

    private void startForward() {
        device.createForward(PORT_AGENT, "stfagent");
    }

    private boolean startAgent() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String result = Adb.shell(device.getSerial(), "pm path " + SERVICE_NAME);
                String path = result.replace("package:", "");

                String command = String.format("export CLASSPATH=\"%s\"\\;\r" +
                        "exec app_process /system/bin jp.co.cyberagent.stf.Agent", path);

                result = device.executeShellCommand(command);
                Logger.d("command:" + command + ",result2:     " + result);
            }
        });
        thread.start();

        return true;
    }

    public void backEvent() {
        onKeyEvent(KeyCode.KEYCODE_BACK);
    }

    public void menuEvent() {
        onKeyEvent(KeyCode.KEYCODE_MENU);
    }

    public void homeEvent() {
        onKeyEvent(KeyCode.KEYCODE_HOME);
    }

    public void onKeyEvent(int keyCode) {
        Wire.KeyEventRequest.Builder builder = Wire.KeyEventRequest.newBuilder();
        builder.setKeyCode(keyCode);
        builder.setEvent(Wire.KeyEvent.DOWN);

        Wire.KeyEventRequest request = builder.build();

        Wire.Envelope.Builder envBuild = Wire.Envelope.newBuilder();
        envBuild.setType(Wire.MessageType.DO_KEYEVENT);
        envBuild.setMessage(request.toByteString());
        Wire.Envelope envelope = envBuild.build();
        executeKeyEvent(envelope);

        Wire.KeyEventRequest.Builder builder2 = Wire.KeyEventRequest.newBuilder();
        builder2.setKeyCode(keyCode);
        builder2.setEvent(Wire.KeyEvent.UP);

        Wire.Envelope.Builder envBuild2 = Wire.Envelope.newBuilder();
        envBuild2.setType(Wire.MessageType.DO_KEYEVENT);
        envBuild2.setMessage(builder2.build().toByteString());

        executeKeyEvent(envBuild2.build());
    }
}
