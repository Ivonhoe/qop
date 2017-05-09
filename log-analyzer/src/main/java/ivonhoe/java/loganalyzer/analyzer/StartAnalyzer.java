package ivonhoe.java.loganalyzer.analyzer;

import ivonhoe.java.loganalyzer.model.LaunchTime;
import ivonhoe.java.loganalyzer.model.PrintInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivonhoe on 2017/5/9.
 */
public class StartAnalyzer implements LogAnalyzer {

    private String PATTERN_START_ACTIVITY = "START\\su0\\s(\\{.*\\})";
    private Pattern mPattern;

    public StartAnalyzer() {
        mPattern = Pattern.compile(PATTERN_START_ACTIVITY);
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
