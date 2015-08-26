
package com.multimedia.room.upgrade;

import android.os.Handler;
import android.os.HandlerThread;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;


import android.util.Log;

/**
 * DownloadTask provide the download function.
 */

public final class DownloadTask implements Runnable {


    private HandlerThread mCPUThread = new HandlerThread("CPUChecker");
    private Handler mHandler = null;
    private IDownloadListener mListener;
    private Session mSession;

    private int mTryCount = 0;

    public DownloadTask(Session session, IDownloadListener listener) {
        mSession = session;
        mListener = listener;
        mCPUThread.start();
        mHandler = new Handler(mCPUThread.getLooper());
    }

    private void doDownload(File tmpFile) throws IOException {
        Log.d("Download","doDownload " + tmpFile.getAbsolutePath());
        RandomAccessFile out = null;
        InputStream in = null;
        try {
            in = IOUtils.getInputStream(mSession.getNewVersionURL(),
                    mSession.getDownlaodedFileSize());
            out = IOUtils.getRandomAccessFile(tmpFile);
            byte[] buffer = new byte[2048];

            int count = 0;
            long length = 0, totalLength = 0;
            long time = System.currentTimeMillis(), startTime = time;
            while (!mSession.isStop()) {
                // Check internet speed ，if the speed over 2m/s then stop
                long currentTime = System.currentTimeMillis();
                long interval = currentTime - time;
                if (interval >= 50) {
                    if (mSession.isAuto() && length > interval * 2048) {
                        waitFor(length / 2048 - interval);
                    }
                    length = 0;
                    time = currentTime;
                }

                if ((count = in.read(buffer)) == -1) {
                    Log.d("Download","Download average speed = " + totalLength + "/"
                            + (System.currentTimeMillis() - startTime));
                    break;
                }

                out.write(buffer, 0, count);
                length += count;
                totalLength += count;
                mTryCount = 0;
                mSession.setDownlaodedFileSize(mSession.getDownlaodedFileSize() + count);
            }
            if (mSession.isStop()) {
                Log.d("Download","stop");
                return;
            }
            Log.d("Download","mSession.isStop() = " + mSession.isStop());
            rename(tmpFile);
            mListener.onCompleted(mSession);
        } finally {
            IOUtils.close(in);
            IOUtils.close(out);
        }
    }

    /**
     * Download file
     * 
     * @param out
     * @param in
     * @param tmpFileName
     * @throws IOException
     */
    private void download(File tmpFile) throws IOException {
        try {
            doDownload(tmpFile);
        } catch (SocketException e) {
            Log.w(e.getMessage(), e);
            retryDelayed(e, tmpFile, 10000);
        } catch (IOException e) {
            Log.w("Download", e);
            if (e.getMessage() != null && e.getMessage().contains("I/O error")) {
                Log.d("Download",tmpFile.getAbsolutePath() + " has been damaged!!");

                mSession.setDownlaodedFileSize(0);

                int counter = 0;
                while (tmpFile.exists()) {
                    if (mSession.isStop()) {
                        return;
                    }
                    synchronized (this) {
                        try {
                            this.wait(1000);
                        } catch (InterruptedException e1) {
                        }
                    }
                    if (tmpFile.delete()) {
                        Log.d("Download","delete temp file succeed.");
                        break;
                    } else {
                        counter++;
                        Log.d("Download","delete temp file failed..." + counter);
                        if (counter > 10000) {
                            Log.d("Download","error");
                            return;
                        }
                    }
                }

                retryDelayed(e, tmpFile, 1000);
            } else {
                retryDelayed(e, tmpFile, 10000);
            }
        }
    }

    private long getContentLength() throws IOException {
        URL url = new URL(mSession.getNewVersionURL());
        URLConnection conn = url.openConnection();
        if (!mSession.isAuto()) {
            conn.setConnectTimeout(5000);
        }
        return conn.getContentLength();
    }

    private File getTempFile() {
        File tempFile = new File(getTempFileName());
        return tempFile;
    }

    private String getTempFileName() {
        return mSession.getDownloadTo()  + ".tmp";
    }

    /**
     * Download completed state.
     *
     * @return true or false
     */
    private boolean hasDownloadCompleted() {
        Log.d("Download","mSession.getDownloadFile():" + mSession.getDownloadFile());
        File updateFile = new File(mSession.getDownloadFile());
        if (updateFile.exists()) {
            Log.d("Download",mSession.getDownloadFile() + " size is " + updateFile.length());
            Log.d("Download","NewVersionFileSize is " + mSession.getNewVersionFileSize());
            if (updateFile.length() == mSession.getNewVersionFileSize()) {
                return true;
            } else {
                while (updateFile.exists()) {
                    if (mSession.isStop()) {
                        return false;
                    }
                    updateFile.delete();
                }
            }
        }
        return false;
    }


    private void rename(File sourceFile) {
        String destName = mSession.getDownloadFile();
        File destFile = new File(destName);
        while (destFile.exists()) {
            destFile.delete();
        }
        try {
            sourceFile.renameTo(destFile);
        } catch (Exception e) {
            Log.w("", e);
        }
    }

    private void retryDelayed(IOException cause, File tmpFile, long time) throws IOException {
        if (mTryCount > 1000) {
            Log.w("Download","tryCount > 3,stop download");
            if (mSession.isAuto()) {
                mSession.setStop(true);
                Log.w("Download","stop");
            } else {
                throw cause;
            }
            return;
        }
        if (time > 0) {
            synchronized (this) {
                try {
                    this.wait(time);
                } catch (InterruptedException e) {
                }
            }
        }
        mTryCount++;
        download(tmpFile);
    }


    @Override
    public void run() {
        Log.w("Download","DownloadTask.run...");
        Log.i("Download","downdload file from " + mSession.getNewVersionURL());
        mSession.setState(Constants.STATE_DOWNLOADING);
        try {
            mSession.setNewVersionFileSize(getContentLength());
            File tempFile = null;
            while (true) {
                if (mSession.isStop()) {
                    return;
                }
                if (hasDownloadCompleted()) {
                    mSession.setDownlaodedFileSize(mSession.getNewVersionFileSize());
                    mListener.onCompleted(mSession);
                    return;
                }
                tempFile = getTempFile();
                mSession.setDownlaodedFileSize(tempFile.length());
                if (tempFile.length() == mSession.getNewVersionFileSize()) {
                    this.rename(tempFile);
                    mListener.onCompleted(mSession);
                    return;
                }
                if (!isSpaceNotEnough()) {
                    break;
                }
            }
            if (tempFile != null) {
                download(tempFile);
            }
        } catch (IOException e) {
            Log.e(e.getMessage(), e.toString());
            mListener.onError(mSession, Constants.EXCEPTION_DOWNLOAD_IO);
        } catch (RuntimeException e) {
            Log.e(e.getMessage(), e.toString());
            mListener.onError(mSession, Constants.EXCEPTION_RUNTIME);
        } catch (Exception e) {
            Log.e(e.getMessage(), e.toString());
            mListener.onError(mSession, Constants.EXCEPTION_UNKOWN);
        } finally {
        }
        Log.i("Download","DownloadTask end");
    }


    private boolean isSpaceNotEnough() {
        long needSize = mSession.getNewVersionFileSize() - mSession.getDownlaodedFileSize();
        long freeSize = StateFsUtils.getFreeSize(mSession.getDownloadTo());
        Log.i("Download",mSession.getDownloadTo() + " freeSize is " + freeSize);
        Log.i("Download","needSize is " + needSize);
        // 100 is random digit.
        return (freeSize < needSize - 100);
    }

    private void waitFor(long time) {
        if (time <= 0) {
            return;
        }
        synchronized (this) {
            // Logger.i("pause download " + time );
            try {
                this.wait(time);
            } catch (InterruptedException e) {
            }
        }
    }

}
