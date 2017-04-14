package ivonhoe.java.qop.replay;

/**
 * Created by Ivonhoe on 2017/4/6.
 */
public class MessageLooper {

    MessageQueue mQueue;

    MessageLooper(MessageQueue messageQueue) {
        mQueue = messageQueue;
    }

    MessageQueue getMessageQueue() {
        return mQueue;
    }

    void looper() {
        for (; ; ) {
            Message msg = mQueue.next();

            if (msg == null) {
                return;
            }

            System.out.println("msg:" + msg + ",now:" + System.currentTimeMillis());
        }
    }
}
