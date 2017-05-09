package ivonhoe.java.loganalyzer.model;

/**
 * Created by Ivonhoe on 2017/5/9.
 */
public class LaunchTime implements PrintInfo {

    private PrintInfo printInfo;
    private String activityName;
    private int displayMillisecond;

    public PrintInfo getPrintInfo() {
        return printInfo;
    }

    public void setPrintInfo(PrintInfo printInfo) {
        this.printInfo = printInfo;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getDisplayMillisecond() {
        return displayMillisecond;
    }

    public void setDisplayMillisecond(int displayMillisecond) {
        this.displayMillisecond = displayMillisecond;
    }
}
