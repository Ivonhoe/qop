package ivonhoe.java.loganalyzer.analyzer;

import ivonhoe.java.loganalyzer.model.PrintInfo;
import ivonhoe.java.loganalyzer.model.PrintTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 匹配时间信息
 * Created by Ivonhoe on 2017/5/9.
 */
public class TimeLineAnalyzer implements LogAnalyzer {

    private String TIME = "(\\d*-\\d*\\s\\d*:\\d*:\\d*\\.\\d*)";

    private Pattern mPattern;

    public TimeLineAnalyzer() {
        mPattern = Pattern.compile(TIME);
    }

    @Override
    public PrintInfo compare(String log) {
        Matcher m = mPattern.matcher(log);
        if (m != null && m.groupCount() > 0) {
            PrintTime printTime = new PrintTime();
            return printTime;
        }
        return null;
    }

}
