/*
 * Copyright 2014 - Jamdeo
 */

package com.multimedia.room.upgrade;

import android.os.StatFs;
import android.util.Log;

public class StateFsUtils {

    /**
     * Get the device free size.
     *
     * @param The device
     * @return The free size
     */
    public static long getFreeSize(String device) {
        try {
            StatFs stateFs = new StatFs(device);
            return stateFs.getAvailableBlocks() * (long) stateFs.getBlockSize();
        } catch (IllegalArgumentException e) {
            Log.w("Download",device + " is not exist");
            return 0;
        }
    }
}
