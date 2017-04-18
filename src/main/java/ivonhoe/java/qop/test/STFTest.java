package ivonhoe.java.qop.test;

import com.android.ddmlib.IDevice;
import ivonhoe.java.qop.device.Adb;
import ivonhoe.java.qop.device.Device;
import ivonhoe.java.qop.minicomponent.StfAgent;
import ivonhoe.java.qop.utils.Logger;

/**
 * Created by Ivonhoe on 2017/4/17.
 */
public class STFTest {

    public static void main(String[] args) {
        Adb adb = new Adb();
        if (adb.getDevices().length <= 0) {
            Logger.d("无连接设备,请检查");
            return;
        }
        IDevice iDevice = adb.getDevices()[0];

        Device device = new Device(iDevice);

        StfAgent stfAgent = new StfAgent(device);

//        stfAgent.backEvent();

        stfAgent.homeEvent();
    }
}
