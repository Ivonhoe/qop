package ivonhoe.java.qop.minicomponent;

import ivonhoe.java.qop.device.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static ivonhoe.java.qop.utils.Utilities.subByteArray;

public class MiniTouch extends MiniComponent {

    private static final int PORT_MINITOUCH = 1111;

    private Socket mSocket;
    private OutputStream mSocketOutput;

    public MiniTouch(Device device) {
        super(device, PORT_MINITOUCH);
    }

    @Override
    public String getComponentName() {
        return MINITOUCH;
    }

    @Override
    public String getStartCommand() {
        return String.format(REMOTE_PATH, getComponentName());
    }

    @Override
    public String getComponentDir() {
        return DIR_MINITOUCH;
    }

    @Override
    public void startComponent() {
        super.startComponent();
        try {
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mSocket = new Socket("localhost", mPort);
            InputStream stream = mSocket.getInputStream();
            mSocketOutput = mSocket.getOutputStream();
            int len = 4096;

            byte[] buffer;
            buffer = new byte[len];
            int realLen = stream.read(buffer);
            if (buffer.length != realLen) {
                buffer = subByteArray(buffer, 0, realLen);
            }

            String result = new String(buffer);
            String array[] = result.split(" |\n");

            mDevice.setBanner(getBanner(array));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeTouch(String command) {
        if (mSocketOutput != null) {
            try {
                mSocketOutput.write(command.getBytes());
                mSocketOutput.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Banner getBanner(String array[]) {
        Banner banner = new Banner();
        banner.setVersion(Integer.valueOf(array[1]));
        banner.setMaxPoint(Integer.valueOf(array[3]));
        banner.setMaxPress(Integer.valueOf(array[6]));
        banner.setMaxX(Integer.valueOf(array[4]));
        banner.setMaxY(Integer.valueOf(array[5]));

        return banner;
    }

}
