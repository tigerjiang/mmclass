
package com.mmclass.libsiren;

/*****************************************************************************
 * EventHandler.java
 *****************************************************************************
 *  @author Jensen<Jensen@connect2.com.cn>
 *****************************************************************************/

/**
 * EventHandler: event handler by jni
 */

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class EventHandler {

    /*
     * Be sure to subscribe to events you need in the JNI too.
     */

    public static final int GlobalControlInited = 1;
    public static final int Global_TEACH_EVENT = 2;

    public static final int Global_EXAM_EVENT = 3;

    public static final int Global_STUDY_EVENT = 4;

    public static final int Global_GROUP_EVENT = 5;

    public static final int SUB_EXAM_ORAL_EVENT = 6;
    public static final int SUB_EXAM_STANDARD_EVENT = 7;
    public static final int SUB_GROUP_DISCUSS_EVENT = 8;

    public static final int SUB_TEACH_DEMONSTRATION_EVENT = 9;
    public static final int SUB_TEACH_DICTATION_EVENT = 10;
    public static final int SUB_TEACH_INTERCOM_EVENT = 11;
    public static final int SUB_TEACH_MONITORE_EVENT = 12;
    public static final int SUB_TEACH_TEST_EVENT = 13;
    public static final int SUB_TEACH_TRANSLATE_EVENT = 14;

    private ArrayList<Handler> mEventHandler;
    private static EventHandler mInstance;

    private EventHandler() {
        mEventHandler = new ArrayList<Handler>();
    }

    public static EventHandler getInstance() {
        if (mInstance == null) {
            mInstance = new EventHandler();
        }
        return mInstance;
    }

    public void addHandler(Handler handler) {
        if (!mEventHandler.contains(handler))
            mEventHandler.add(handler);
    }

    public void removeHandler(Handler handler) {
        mEventHandler.remove(handler);
    }

    /** This method is called by a native thread **/
    public void callback(int event, Bundle b) {
        b.putInt("event", event);
        for (int i = 0; i < mEventHandler.size(); i++) {
            Message msg = Message.obtain();
            msg.setData(b);
            mEventHandler.get(i).sendMessage(msg);
        }
    }
}
