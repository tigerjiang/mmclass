/*
 * Copyright 2014 - Jamdeo
 */

package com.multimedia.room.upgrade;

import java.io.File;

import android.content.Context;

/**
 * Session as a entity for restore every step parameter.
 */

public class Session {

    private boolean mAuto;
    private Context mContext;
    private long mDownlaodedFileSize;
    private String mDownloadFile;
    private String mDownloadTo;
    private boolean mForcedUpgrade = false;
    private String mMachineType;
    // private String mMac;
    private long mNewVersionFileSize;
    private String mNewVersionId;
    private String mNewVersionURL;
    private String mOldVersionId;
    private boolean mRunInBackgroud = true;
    private boolean mStartByCLick = false;
    private int mState = Constants.STATE_IDLE;
    private boolean mStop;
    private int mVerifyProcess;
    private String mVersionMessage;


    /**
     * Get the context.
     * 
     * @return the context.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Get the downloaded file size.
     * 
     * @return the downlaodedFileSize.
     */
    public long getDownlaodedFileSize() {
        return mDownlaodedFileSize;
    }

    /**
     * Get downloaded file name.
     * 
     * @return the downlaodFile.
     */
    public String getDownloadFile() {
        return mDownloadFile;
    }

    /**
     * Get the folder path which download to .
     * 
     * @return the downlaodTo.
     */
    public String getDownloadTo() {
        return mDownloadTo;
    }

    /**
     * Get machineType.
     * 
     * @return the machineType.
     */
    public String getMachineType() {
        return mMachineType;
    }

    /**
     * Get the file size of the new version.
     * 
     * @return the newVersionFileSize.
     */
    public long getNewVersionFileSize() {
        return mNewVersionFileSize;
    }

    /**
     * Get new version identity.
     * 
     * @return the newVersionId.
     */
    public String getNewVersionId() {
        return mNewVersionId;
    }

    /**
     * Get download URL of new version .
     * 
     * @return the newVersionURL.
     */
    public String getNewVersionURL() {
        return mNewVersionURL;
    }

    /**
     * Get the old new version identity.
     * 
     * @return the oldVersionId.
     */
    public String getOldVersionId() {
        return mOldVersionId;
    }

    /**
     * Get current state.
     * 
     * @return the state.
     */
    public int getState() {
        return mState;
    }

    /**
     * Get verify process.
     * 
     * @return the verifyProcess.
     */
    public int getVerifyProcess() {
        return mVerifyProcess;
    }

    /**
     * Get version info.
     * 
     * @return the mVersionMessage.
     */
    public String getVersionMessage() {
        return mVersionMessage;
    }

    public boolean isAuto() {
        return mAuto;
    }

    /**
     * Check whether It is force upgrade or not.
     * 
     * @return the mForced.
     */
    public boolean isForcedUpgrade() {
        return mForcedUpgrade;
    }

    /**
     * Get whether run in backgroud.
     * 
     * @return the runInBackgroud.
     */
    public boolean isRunInBackgroud() {
        return mRunInBackgroud;
    }

    public boolean isStartByCLick() {
        return mStartByCLick;
    }

    /**
     * Get stop flag.
     * 
     * @return the stop.
     */
    public boolean isStop() {
        return mStop;
    }

    public void setAuto(boolean mAuto) {
        this.mAuto = mAuto;
    }

    /**
     * Set the context.
     * 
     * @param context the context to set.
     */
    public void setContext(Context context) {
        this.mContext = context;
    }

    /**
     * Set the downloaded file size.
     * 
     * @param downlaodedFileSize the downlaodedFileSize to set.
     */
    public void setDownlaodedFileSize(long downlaodedFileSize) {
        // Logger.d("DownlaodedFileSize/NewVersionFileSize "+downlaodedFileSize+"/"+this.mNewVersionFileSize);
        this.mDownlaodedFileSize = downlaodedFileSize;
    }

    /**
     * Set the folder path which download to .
     * 
     * @param dest
     */
    public void setDownloadTo(String dest,String fileName) {
        mDownloadTo = dest;
    	File fileDir = new File(mDownloadTo);
		// 创建下载目录
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
        mDownloadFile = dest  + fileName;
    }

    /**
     * Set force upgrade .
     * 
     * @param mForced the mForced to set.
     */
    public void setForcedUpgrade(boolean mForced) {
        this.mForcedUpgrade = mForced;
    }

    /**
     * Set machine type.
     * 
     * @param machineType the machineType to set.
     */
    public void setMachineType(String machineType) {
        this.mMachineType = machineType;
    }

    /**
     * Set downloaded file name.
     * 
     * @param downlaodFile the downlaodFile to set.
     */

    // public void setDownloadFile(String downloadFile) {
    // this.mDownloadFile = downloadFile;
    // }

    /**
     * Set file size of the new version.
     * 
     * @param newVersionFileSize the newVersionFileSize to set.
     */
    public void setNewVersionFileSize(long newVersionFileSize) {
        this.mNewVersionFileSize = newVersionFileSize;
    }

    /**
     * Set new version identity.
     * 
     * @param newVersionId the newVersionId to set.
     */
    public void setNewVersionId(String newVersionId) {
        this.mNewVersionId = newVersionId;
    }

    /**
     * Set the download URL of the new version.
     * 
     * @param newVersionURL the newVersionURL to set.
     */
    public void setNewVersionURL(String newVersionURL) {
        this.mNewVersionURL = newVersionURL;
    }

    /**
     * Set the old new version identity.
     * 
     * @param oldVersionId the oldVersionId to set.
     */
    public void setOldVersionId(String oldVersionId) {
        this.mOldVersionId = oldVersionId;
    }

    /**
     * Set flag of running in backgroud.
     * 
     * @param runInBackgroud the runInBackgroud to set.
     */
    public void setRunInBackgroud(boolean runInBackgroud) {
        this.mRunInBackgroud = runInBackgroud;
    }

    public void setStartByCLick(boolean mStartByCLick) {
        this.mStartByCLick = mStartByCLick;
    }

    /**
     * Set the current state.
     * 
     * @param state the state to set.
     */
    public void setState(int state) {
        this.mState = state;
    }

    /**
     * Set stop flag.
     * 
     * @param stop the stop to set.
     */
    public void setStop(boolean stop) {
        this.mStop = stop;
        mState = Constants.STATE_STOPPED;
    }

    /**
     * Set verify process.
     * 
     * @param verifyProcess the verifyProcess to set.
     */
    public void setVerifyProcess(int verifyProcess) {
        this.mVerifyProcess = verifyProcess;
    }

    /**
     * Set the version info.
     * 
     * @param versionMessage the versionMessage to set.
     */
    public void setVersionMessage(String versionMessage) {
        this.mVersionMessage = versionMessage;
    }
}
