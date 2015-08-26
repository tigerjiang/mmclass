/*
 * Copyright 2013 - Jamdeo
 */

package com.multimedia.room.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.multimedia.room.FilesEntity;
import com.multimedia.room.SystemWriter;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

/**
 * The class VideoPlayerManager provide a interface to play the live TV program.
 */
public class AudioPlayerManager implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener {

    private static String TAG = AudioPlayerManager.class.getSimpleName();
    private static boolean DEBUG = true;

    private WeakReference<Context> mContextRef;

    private String mPath;
    // current channel number
    private static MediaPlayer mMediaPlayer = null;

    private static int PLAYER_START_TIMEOUT = 10000;

    // all possible internal states
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private int mCurrentState = STATE_IDLE;

    private boolean mIsInternetConnected = false;
    private ConnectivityManager mConnectivityManager;
    private final NetworkReceiver mNetworkReceiver = new NetworkReceiver();
    private boolean mNetworkReceiverRegistered = false;
    private Handler mHandler;
    // Buffer time
    private static final int MEDIA_PLAYER_READY_DELAY = 5000;
    private int mCurrentIndex;
    private List<FilesEntity> mFileList;


    /**
     * Interface definition of a callback to be invoked to communicate some
     * states of playback and Network.
     */
    public interface onStateListener {
        /**
         * Called to indicate an error.
         * 
         * @param what the type of error that has occurred:
         * @param extra an extra code, specific to the error. Typically
         *            implementation dependent.
         * @return True if the method handled the error, false if it didn't.
         *         Returning false, or not having an OnErrorListener at all,
         *         will cause the OnCompletionListener to be called.
         */
        boolean onError(int what, int extra);

        /**
         * Called to indicate an info or a warning.
         * 
         * @param what the type of info or warning.
         * @param extra an extra code, specific to the info. Typically
         *            implementation dependent.
         * @return True if the method handled the info, false if it didn't.
         *         Returning false, or not having an OnErrorListener at all,
         *         will cause the info to be discarded.
         */
        boolean onInfo(int what, int extra);

        /**
         * Called to indicate an state of network.
         * 
         * @param connected the network connected or not
         */
        void onNetworkConnected(boolean connected);
    }

    /**
     * Register a callback to be invoked when an state of MediaPlayer or Network
     * has happened during an asynchronous operation.
     * 
     * @param listener the callback that will be run
     */
    public void setOnStateListener(onStateListener listener) {
        mOnStateListener = listener;
    }

    private onStateListener mOnStateListener;

    // singleton instance holder
    private static class SingletonHolder {
        public static AudioPlayerManager instance = new AudioPlayerManager();
    }

    public static AudioPlayerManager getInstance(Context context) {
        if (context != null && SingletonHolder.instance.mContextRef == null) {
            SingletonHolder.instance.mContextRef = new WeakReference<Context>(
                    context.getApplicationContext());
            SingletonHolder.instance.init();
        }
        return SingletonHolder.instance;
    }

    private Context getContext() {
        if (mContextRef != null) {
            return mContextRef.get();
        }
        return null;
    }

    private void init() {

        mConnectivityManager = (ConnectivityManager) getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mHandler = new NotifyBufferHandler();
        registerNetworkReceiver();
    }

    public void setCurrentIndex(int index) {
        mCurrentIndex = index;
    }

    /**
     * Get current source index
     * 
     * @return source index
     */
    public int getCurrentSourceIndex() {
        return mCurrentIndex;
    }

    /**
     * Sets the data source (file-path or http/rtsp URL) to use.
     * 
     * @param channelNumber the current channel numuber.
     * @param path the path of the file, or the http/rtsp URL of the stream you
     *            want to play
     * @param force the boolean flag to set path by force
     */
    public void setDataSource(String path, boolean force) {
        mPath = path;
        if (path == null || path.isEmpty()) {
            Log.w(TAG, "set data source path is illegal");
            return;
        }
        if (!force && mPath != null && mPath.equals(path)) {
            Log.i(TAG, "set data source same path");
            return;
        }
        Log.d(TAG, "path " + mPath);
        // release media player at first
        if (mMediaPlayer != null) {
            releasePlayer();
        }

        // create new player
        new CreatePlayerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Pause the playing.
     */
    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
        }
    }

    /**
     * Pause the playing.
     */
    public void play() {
        Log.d(TAG, "--------------play");
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mCurrentState = STATE_PAUSED;
        }
    }

    /**
     * Start to play.
     */
    public void start() {
        Log.d(TAG, "-----------------start");
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
    }

    /**
     * Seek to play.
     */
    public void seekTo(int progress) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(progress);
        }
    }

    /**
     * Stop to play.
     */
    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public int getCurrentPos() {
        int currentPos = 0;
        if (mMediaPlayer != null) {
            currentPos = mMediaPlayer.getCurrentPosition();
        }
        return currentPos;
    }

    public int getDuration() {
        int duration = 0;
        if (mMediaPlayer != null) {
            duration = mMediaPlayer.getDuration();
        }
        return duration;
    }

    public void setFileList(List<FilesEntity> files) {
        mFileList = files;
    }

    public void next() {
        Log.d(TAG, "current " + mCurrentIndex + " " + mPath);
        if (mCurrentIndex == mFileList.size()) {
            return;
            // mCurrentIndex = 0;
        } else {
            mCurrentIndex++;
        }
        try {
            mPath = mFileList.get(mCurrentIndex).getUrl();
            setDataSource(mPath, true);
            mIUpdateUIListenner.update(mFileList.get(mCurrentIndex), mCurrentIndex);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "next " + mCurrentIndex + " " + mPath);
    }

    public void previous() {
        Log.d(TAG, "current " + mCurrentIndex + " " + mPath);
        if (mCurrentIndex == 0) {
            return;
            // mCurrentIndex = mFileList.size();
        } else {
            mCurrentIndex--;
        }
        Log.d(TAG, "index" + mCurrentIndex);
        try {
            mPath = mFileList.get(mCurrentIndex).getUrl();
            setDataSource(mPath, true);
            mIUpdateUIListenner.update(mFileList.get(mCurrentIndex), mCurrentIndex);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "previous " + mCurrentIndex + " " + mPath);
    }

    /**
     * Checks whether the MediaPlayer is playing.
     * 
     * @return true if currently playing, false otherwise
     */
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        Log.w(TAG, "isPlaying - MediaPlayer is not initialized");
        return false;
    }

    /**
     * Release the resource of TV player.
     */
    public void release() {
        releasePlayer();
        unregisterNetworkReceiver();
        mContextRef = null;
        mPath = null;
    }

    /**
     * Release player
     */
    public void releasePlayer() {
        Log.d(TAG, "---------releasePlayer");
        mHandler.removeCallbacksAndMessages(null);
        if (DEBUG)
            Log.d(TAG, "release player");
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (DEBUG)
                Log.d(TAG, "NetworkReceiver.onReceive: " + intent);
            if (checkNetworkAvailable()) {
                // when the network reconnect , Then restart the play
                if (mMediaPlayer != null) {
                    releasePlayer();
                }
                if (mPath == null || mPath.isEmpty()) {
                    Log.w(TAG, "set data source path is illegal");
                    return;
                }
                new CreatePlayerTask()
                        .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                Log.i(TAG, "NetworkReceiver network is disconnect");
                releasePlayer();
            }
        }
    }

    private void registerNetworkReceiver() {
        if (!mNetworkReceiverRegistered) {
            // we only care about the network connect state change
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            getContext().registerReceiver(mNetworkReceiver, filter);
            mNetworkReceiverRegistered = true;
        }
    }

    private void unregisterNetworkReceiver() {
        if (mNetworkReceiverRegistered) {
            getContext().unregisterReceiver(mNetworkReceiver);
            mNetworkReceiverRegistered = false;
        }
    }

    private void registerListener(MediaPlayer player) {
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnInfoListener(this);
        player.setOnPreparedListener(this);
    }

    private void prepareMediaPlayer(MediaPlayer emp) {
        try {
            emp.setLooping(false);
            registerListener(emp);
            // mCurrentSourceIndex = mCurrentSourceIndex % mAllPlayerUrl.length;
            // // Avoid array index out of bounds
            // emp.setDataSource(mAllPlayerUrl[mCurrentSourceIndex]);

            emp.setDataSource(mPath);
            emp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            emp.prepareAsync();

            /*
             * Everything seems OK. Now notify the user that we are ready.
             */
            handleStartPlayerPop();
            mCurrentState = STATE_PREPARING;
        } catch (IOException exception) {
            Log.e(TAG, "prepare player ioexception" + exception);
        } catch (IllegalArgumentException exception) {
            Log.e(TAG, "prepare player argument is illegal" + exception);
        } catch (SecurityException exception) {
            Log.e(TAG, "prepare player security exception" + exception);
        } catch (IllegalStateException exception) {
            Log.e(TAG, "prepare player state is illegal" + exception);
        }
    }

    private class PrepareMediaPlayerThread extends Thread {
        private final MediaPlayer mMediaPlayer;

        private PrepareMediaPlayerThread(MediaPlayer player) {
            this.mMediaPlayer = player;
        }

        @Override
        public void run() {
            prepareMediaPlayer(mMediaPlayer);
        }

    }

    private class CreatePlayerTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... arg0) {
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Log.w(TAG, "Unable to acquire video decoder");
                // Try to start playback anyway
            }

            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mCurrentState = STATE_IDLE;
            }

            new PrepareMediaPlayerThread(mMediaPlayer).start();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "MediaPlayer onPrepared");
        mCurrentState = STATE_PREPARED;
        mMediaPlayer.start();
        mCurrentState = STATE_PLAYING;
    }

    /**
     * Notify the loading state when the time more than 1s that the time between
     * start buffer and filling buffer
     */
    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {

        mHandler.removeMessages(MEDIA_PLAYER_READY);
        if (mIsInternetConnected && mOnStateListener != null) {
            mOnStateListener.onInfo(what, extra);
        }
        if (DEBUG)
            Log.d(TAG, "MediaPlayer onInffo " + what);
        return true;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mHandler.removeMessages(MEDIA_PLAYER_READY);

        if (DEBUG)
            Log.d(TAG, "MediaPlayer onError " + what);
        mCurrentState = STATE_ERROR;

        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (DEBUG)
            Log.d(TAG, "MediaPlayer onCompletion");
        mCurrentState = STATE_PLAYBACK_COMPLETED;
//        next();

    }

    /**
     * Check the internet state and notify the state.
     * 
     * @return true if network available.
     */
    public boolean checkNetworkAvailable() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (DEBUG) {
                Log.d(TAG,
                        "network status connected: "
                                + networkInfo.isConnected());
            }
            mIsInternetConnected = networkInfo.isConnected();
        } else {
            // the active network info null, then it's disconnected
            if (DEBUG) {
                Log.d(TAG, "network status: disconnected");
            }
            mIsInternetConnected = false;
        }
        if (mOnStateListener != null)
            mOnStateListener.onNetworkConnected(mIsInternetConnected);
        return mIsInternetConnected;
    }

    // Sometimes the media player doesn't invoke onPrepared() long time. If so
    // alert a loading message.
    private void handleStartPlayerPop() {
        Message msg = mHandler.obtainMessage(MEDIA_PLAYER_READY);
        mHandler.sendMessageDelayed(msg, MEDIA_PLAYER_READY_DELAY);
    }

    private static boolean sIsLoading = false;
    private static final int MEDIA_PLAYER_READY = 0;

    private class NotifyBufferHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MEDIA_PLAYER_READY:
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    sIsLoading = true;
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    break;
            }
            if (mIsInternetConnected && mOnStateListener != null) {
                mOnStateListener.onInfo(msg.what, mCurrentIndex + 1);
            }
        }
    }

    public IUpdateUIListenner mIUpdateUIListenner;

    public void setUpdateUIListenner(IUpdateUIListenner l) {
        mIUpdateUIListenner = l;
    }

    public static interface IUpdateUIListenner {
        public void update(FilesEntity filesEntity, int index);
    }
}
