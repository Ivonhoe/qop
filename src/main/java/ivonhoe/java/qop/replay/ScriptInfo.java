package ivonhoe.java.qop.replay;

import java.io.Serializable;

/**
 * Created by Ivonhoe on 2017/4/10.
 */
public class ScriptInfo implements Serializable {

    // 脚本版本号
    private int version;
    // 当前脚本对应的屏幕分辨率
    private int width;
    private int height;
    // 手机serial id
    private String serial;
    // 屏幕录制的开始时间
    private long startTime;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
