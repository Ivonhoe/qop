package ivonhoe.java.loganalyzer.analyzer;

import ivonhoe.java.loganalyzer.model.LaunchTime;
import ivonhoe.java.loganalyzer.model.PrintInfo;
import ivonhoe.java.loganalyzer.model.PrintTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivonhoe on 2017/5/9.
 */
public class LaunchTimeAnalyzer implements LogAnalyzer {

    private String PATTERN_LAUNCH = "Displayed\\s([^/:]+)\\/([^/:]+):\\s\\+?(\\d*)s?(\\d*)ms";

    private Pattern mPattern;

    public LaunchTimeAnalyzer() {
        mPattern = Pattern.compile(PATTERN_LAUNCH);
    }

    @Override
    public PrintInfo compare(String log) {
        Matcher m = mPattern.matcher(log);
        if (m != null && m.groupCount() > 0) {
            LaunchTime launchTime = new LaunchTime();
            return launchTime;
        }

        return null;
    }
}
