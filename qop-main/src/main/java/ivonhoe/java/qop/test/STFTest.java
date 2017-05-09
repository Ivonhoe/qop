package ivonhoe.java.qop.test;

import com.android.ddmlib.IDevice;
import ivonhoe.java.qop.device.Adb;
import ivonhoe.java.qop.device.Device;
import ivonhoe.java.qop.component.StfAgent;
import ivonhoe.java.qop.utils.Logger;

/**
 * Created by Ivonhoe on 2017/4/17.
 */
public class STFTest {

    public static void main(String[] args) {
        Adb adb = Adb.instance();
        IDevice iDevice = adb.getDevices()[0];

        Device device = new Device(iDevice);

        StfAgent stfAgent = new StfAgent(device);

        stfAgent.homeEvent();
    }
}
