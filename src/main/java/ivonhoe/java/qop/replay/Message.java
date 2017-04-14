package ivonhoe.java.qop.replay;

/**
 * Created by Ivonhoe on 2017/4/6.
 */
public class Message {

    long when;

    Object what;

    Message next;

    @Override
    public String toString() {
        return "[when:" + when + ", what:" + what + "]";
    }


    public static class EndMessage {

        String replayFile;
    }
}
