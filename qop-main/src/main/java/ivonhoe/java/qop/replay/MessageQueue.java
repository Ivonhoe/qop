package ivonhoe.java.qop.replay;

import com.android.ddmlib.Log;
import ivonhoe.java.qop.utils.Logger;

/**
 * Created by Ivonhoe on 2017/4/6.
 */
public class MessageQueue {

    Message mMessages;

    public boolean postMessage(Message msg) {
        return enqueueMessage(msg, 0);
    }

    public boolean postMessageDelay(Message msg, int delay) {
        long now = System.currentTimeMillis();

        return enqueueMessage(msg, now + delay);
    }

    private boolean enqueueMessage(Message msg, long when) {
        if (msg == null) {
            return false;
        }

        msg.when = when;
        synchronized (this) {
            Message p = mMessages;
            if (p == null || when == 0 || when < p.when) {
                msg.next = p;
                mMessages = msg;
            } else {
                Message prev;
                for (; ; ) {
                    prev = p;
                    p = p.next;

                    if (p == null || when < p.when) {
                        break;
                    }
                }

                prev.next = msg;
                msg.next = p;
            }

            // printMessageQueue();
        }

        return true;
    }

    public Message next() {
        for (; ; ) {
            synchronized (this) {
                Message msg = mMessages;
                final long now = System.currentTimeMillis();

                if (msg == null || msg.when > now) {
                    continue;
                }

                mMessages = msg.next;
                msg.next = null;

                return msg;
            }
        }
    }

    /**
     * 将消息队列里的所有消息都延迟delayTime
     *
     * @param delayTime
     */
    public void delayAllMessage(long delayTime) {
        Message msg = mMessages;
        while (msg != null && msg.next != null) {
            System.out.print(msg);

            msg.when = msg.when + delayTime;
            msg = msg.next;
        }

        if (msg != null) {
            msg.when += delayTime;
        }
    }

    void printMessageQueue() {
        Message msg = mMessages;
        while (msg != null && msg.next != null) {
            System.out.print(msg);

            msg = msg.next;
        }

        System.out.println(msg);
    }
}
