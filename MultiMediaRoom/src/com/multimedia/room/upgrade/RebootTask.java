/*
 * Copyright 2014 - Jamdeo
 */

package com.multimedia.room.upgrade;

import android.content.Context;
import android.os.RecoverySystem;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * RebootTask case reboot the machine.
 */

public class RebootTask implements Runnable {

    private Context mContext;
    private Session mSession;

    public RebootTask(Session session) {
        mSession = session;
        mContext = session.getContext();
    }

    @Override
    public void run() {
        Log.d("Upgrade","RebootTask.run...");
        try {
            RecoverySystem.installPackage(mContext, new File(mSession.getDownloadFile()));
        } catch (IOException e) {
            Log.e("Upgrade",e.getMessage());
        } catch (Throwable e) {
            Log.e("Upgrade",e.getMessage());
        }
    }
}
