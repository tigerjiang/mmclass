package com.multimedia.room;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.Context;

public class MyPlayer extends MediaPlayer  implements
OnBufferingUpdateListener, OnCompletionListener,
OnPreparedListener, OnVideoSizeChangedListener, SurfaceHolder.Callback {
	public static MyPlayer mPlayer = new MyPlayer();
	private SurfaceHolder mHolder;
	private SurfaceView mSurfaceView;
    private boolean mIsVideoSizeKnown = false;
    private boolean mIsVideoReadyToBePlayed = false;
    private static final String TAG = "MyPlayer";
    private int mVideoWidth;
    private int mVideoHeight;
    private String mCurrentPath;
    private String mPreviousPath;
	private Context mContext;
	private Object mLocak = new Object();
//	private SystemWriter mSystemWriter;

	private MyPlayer(){
	}
	
	public static MyPlayer getInstance(){
		if(mPlayer ==null)
			mPlayer = new MyPlayer();
		return mPlayer;
	}

	public MyPlayer setContext(Context context)
	{
		mContext = context;
//		mSystemWriter = new SystemWriter(mContext);
		return mPlayer;
	}	
	private  void init(){
	     mPlayer.setOnBufferingUpdateListener(this);
	     mPlayer.setOnCompletionListener(this);
	     mPlayer.setOnPreparedListener(this);
	     mPlayer.setOnVideoSizeChangedListener(this);
	     mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	}

	public void setSurfaceView(SurfaceView _surfaceView){
		init();
		this.mSurfaceView =  _surfaceView;
		this.mHolder = this.mSurfaceView.getHolder();
		this.mHolder.addCallback(this);
		this.mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	
	public void setPath(String _path){
		this.mPreviousPath = this.mCurrentPath;
		this.mCurrentPath = _path;
		 if(this.mCurrentPath.equals(this.mPreviousPath)){
      	   return;
         }else{
        	 PlayVideo();
         }
		Log.d(TAG, "previous: " + this.mPreviousPath + "current: "+mCurrentPath);
	}
	public void PlayVideo(){
		try {
			Log.d(TAG, "previous: " + this.mPreviousPath + "current: "+mCurrentPath);
			doCleanUp();
			mPlayer.reset();
			mPlayer.setDataSource(mCurrentPath);
			mPlayer.setDisplay(this.mHolder);
			mPlayer.prepareAsync();
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		  Log.d(TAG, "surfaceChanged called");
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		  Log.d(TAG, "surfaceCreated called");
//		mSystemWriter.enableScaler();
		  PlayVideo();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		  Log.d(TAG, "surfaceDestroyed called");
		  //mPlayer.stop();
		  //mPlayer.release();
		  synchronized (mLocak) {
			  releaseMediaPlayer();
		}
		  synchronized (mLocak) {
//			  mSystemWriter.disableScaler();	
		}
	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		   Log.v(TAG, "onVideoSizeChanged called");
	        if (width == 0 || height == 0) {
	            Log.e(TAG, "invalid video width(" + width + ") or height(" + height + ")");
	            return;
	        }
	        mIsVideoSizeKnown = true;
	        mVideoWidth = width;
	        mVideoHeight = height;
	        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
	            startVideoPlayback();
	        }
		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		 Log.d(TAG, "onPrepared called");
	        mIsVideoReadyToBePlayed = true;
	        if (mIsVideoReadyToBePlayed && mIsVideoSizeKnown) {
	            startVideoPlayback();
	        }
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		 Log.d(TAG, "onCompletion called");
		
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		  Log.d(TAG, "onBufferingUpdate percent:" + percent);
		
	}
	
	   private void releaseMediaPlayer() {
	        if (mPlayer != null) {
			mPlayer.stop();
	        	mPlayer.release();
	        }
	        
		mPlayer = null;
	    }

	    private void doCleanUp() {
	        mVideoWidth = 0;
	        mVideoHeight = 0;
	        mIsVideoReadyToBePlayed = false;
	        mIsVideoSizeKnown = false;
	    }

	    private void startVideoPlayback() {
	        Log.v(TAG, "startVideoPlayback");
	        mHolder.setFixedSize(mVideoWidth, mVideoHeight);
	        mPlayer.start();
	        
	    }

}
