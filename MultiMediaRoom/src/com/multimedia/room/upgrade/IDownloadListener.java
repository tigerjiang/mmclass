/*
 * Copyright 2014 - Jamdeo
 */

package com.multimedia.room.upgrade;

/**
 * IDownloadListener for listen the download state.
 */

public interface IDownloadListener {

    /**
     * Listen the download path change.
     *
     * @return true or false
     */
    boolean onChangeDownloadPath(Session session);

    /**
     * Download complete.
     */
    void onCompleted(Session session);

    /**
     * Download occur error.
     */
    void onError(Session session, int errorCode);
}
