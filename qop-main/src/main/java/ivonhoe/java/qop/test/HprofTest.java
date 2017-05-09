package ivonhoe.java.qop.test;

import com.android.ddmlib.IDevice;
import ivonhoe.java.qop.component.HprofDump;
import ivonhoe.java.qop.device.Adb;
import ivonhoe.java.qop.device.Device;

/**
 * Created by Ivonhoe on 2017/4/18.
 */
public class HprofTest {

    public static void main(String[] args) {
        IDevice iDevice = Adb.instance().getDevice(0);
        final Device device = new Device(iDevice);

        HprofDump hprofDump = new HprofDump(device);

    }
}
