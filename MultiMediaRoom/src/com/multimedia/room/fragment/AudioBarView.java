
package com.multimedia.room.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.multimedia.room.CommonUtil;
import com.multimedia.room.FilesEntity;
import com.multimedia.room.R;
import com.multimedia.room.media.AudioPlayerManager;
import com.multimedia.room.media.AudioPlayerManager.IUpdateUIListenner;

import java.util.ArrayList;

public class AudioBarView extends RelativeLayout implements  IUpdateUIListenner {
    private static final String TAG = AudioBarView.class.getSimpleName();
    private static AudioPlayerManager mPlayerManager;

    private TextView mShowTime;
    private SeekBar mProgressBar;
    private ImageView mPrevImageView;
    private ImageView mPauseImageView;
    private ImageView mNextImageView;
    private Handler mPlayHandler;
    
    private TextView mTitleView;

    private ArrayList<FilesEntity> mFileList;
    private String mPath;
    private int mIndex;
    private String mTitle;
    private Context mContext;
    private String mTotalTime,mCurrentTime;

    private long DELAY_TIME_NORMAL = 1 * 1000;// 1 minutes

    public void setAudioFiles(ArrayList<FilesEntity> fileList) {
        mFileList = fileList;

    }

    public void setPath(String mPath) {
        this.mPath = mPath;
        mPlayerManager.setDataSource(mPath, true);
    }

    public void setIndex(int mIndex) {
        this.mIndex = mIndex;
        mPlayerManager.setFileList(mFileList);
        mPlayerManager.setCurrentIndex(mIndex);
        if (mFileList.size() == 1) {
            mNextImageView.setEnabled(false);
            mPrevImageView.setEnabled(false);
        }
       
        // setTimeInfo();
        update(mFileList.get(mIndex), mIndex);
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.GONE) {
            // release player
            if (mPlayerManager != null) {
            	 mPlayerManager.setDataSource("", true);
                // unRegister report listener.
                mPlayerManager.release();
                mPlayerManager = null;
            }
        } else if (visibility == View.VISIBLE) {
        	  mPlayerManager = AudioPlayerManager.getInstance(mContext);
              mPlayerManager.setUpdateUIListenner(this);
             
        }
    }

    public AudioBarView(Context context) {
        super(context);
        initView(context);
    }

    public AudioBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public AudioBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        inflate(context, R.layout.play_audio_layout, this);
        mPrevImageView = (ImageView) findViewById(R.id.audio_prev);
        mPrevImageView.setOnClickListener(mPrevListener);
        mPauseImageView = (ImageView) findViewById(R.id.audio_pause);
        mPauseImageView.setOnClickListener(mPauseListener);
        mPauseImageView.requestFocus();
        mNextImageView = (ImageView) findViewById(R.id.audio_next);
        mNextImageView.setOnClickListener(mNextListener);
        mTitleView = (TextView) findViewById(R.id.title);
        mShowTime = (TextView) findViewById(R.id.show_currenttime);
        mProgressBar = (SeekBar) findViewById(R.id.audio_progress_bar);
        mProgressBar.setOnSeekBarChangeListener(new OnPlayingTimeSeekBarChangeListener());
    }

    private void setTimeInfo() {
    	 mTitleView.setText(mFileList.get(mIndex).getName());
        if (mPlayHandler == null) {
            mPlayHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (mPlayerManager == null) {
                        Log.e(TAG, "mPlayerManager is null");
                        return;
                    }
                    if (msg.what == 1) {
                        if (mPlayerManager.isPlaying()) {
                            int currentTime = mPlayerManager.getCurrentPos();
                            mProgressBar.setProgress(currentTime);
                            mCurrentTime = CommonUtil.convertTime(currentTime);
                            
                            mShowTime.setText(mCurrentTime+" / "+mTotalTime);

                            Log.d("current time",
                                    "current pos " + CommonUtil.convertTime(currentTime));
                        }
                        mPlayHandler.sendEmptyMessageDelayed(1, 1000);
                    } else if (msg.what == 2) {
                        int totalTime = mPlayerManager.getDuration();
                        Log.d("total time", " total time " + totalTime);
                        // If the totalTime less than 0 or more than 4 hours ,
                        // retry get the duration .
                        if (totalTime <= 0 || totalTime > 4 * 60 * 60 * 1000) {
                            mPlayHandler.sendEmptyMessageDelayed(2, 1000);
                            return;
                        }
                        mProgressBar.setMax(totalTime);
                        mTotalTime = CommonUtil.convertTime(totalTime);
                    } else {

                    }
                }

            };
        }
        mPlayHandler.sendEmptyMessage(1);
        mPlayHandler.sendEmptyMessageDelayed(2, DELAY_TIME_NORMAL);
    }

    private View.OnClickListener mPauseListener = new View.OnClickListener() {
        public void onClick(View v) {
            doPauseResume();
        }
    };

    private View.OnClickListener mPrevListener = new View.OnClickListener() {
        public void onClick(View v) {
            mPlayerManager.previous();
            setTimeInfo();
        }
    };

    private View.OnClickListener mNextListener = new View.OnClickListener() {
        public void onClick(View v) {
            mPlayerManager.next();
            setTimeInfo();
        }
    };

    private void doPauseResume() {
        if (mPlayerManager != null) {
            if (mPlayerManager.isPlaying()) {
                mPlayerManager.pause();
            } else {
                mPlayerManager.play();
            }
            setPauseButtonImage();
        }
    }

    /**
     * 播放进度条
     * 
     * @author SuHao
     */
    private class OnPlayingTimeSeekBarChangeListener implements OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mPlayerManager.seekTo(progress);
            }
            Log.d(TAG, "seek to " + progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

    }

    /**
     * 播放暂停
     */
    public void setPauseButtonImage() {

        if (mPlayerManager.isPlaying()) {
            mPauseImageView.setImageResource(R.drawable.button_pause);
        } else {
            mPauseImageView.setImageResource(R.drawable.button_play);
        }
    }

    @Override
    public void update(FilesEntity filesEntity, int index) {
        mIndex = index;
        Log.d(TAG, "index " + mIndex);
        if (mIndex == 0) {
            mPrevImageView.setEnabled(false);
            mNextImageView.setEnabled(true);
        }
        if (mIndex == mFileList.size() - 1) {
            mNextImageView.setEnabled(false);
            mPrevImageView.setEnabled(true);
        }
        if (mIndex > 0 && mIndex < mFileList.size() - 1) {
            mNextImageView.setEnabled(true);
            mPrevImageView.setEnabled(true);
        }
        if (mFileList.size() == 1) {
            mNextImageView.setEnabled(false);
            mPrevImageView.setEnabled(false);
        }
        setTimeInfo();
    }

    @Override
    protected void onDetachedFromWindow() {
    	super.onDetachedFromWindow();
    	realse();
    }
    public void realse(){
    	Log.d(TAG, "realse");
    	if(mPlayerManager!=null){
    		mPlayerManager.release();
    	}
    	
    }
   
}
