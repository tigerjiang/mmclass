
package com.multimedia.room;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mmclass.libsiren.EventHandler;
import com.mmclass.libsiren.WeakHandler;
import com.multimedia.room.media.VideoPlayerManager;
import com.multimedia.room.media.VideoPlayerManager.IUpdateUIListenner;
import com.multimedia.room.media.VideoPlayerManager.onStateListener;

import java.util.ArrayList;

public class PlayActivity extends Activity implements OnClickListener,
        SurfaceHolder.Callback, IUpdateUIListenner,onStateListener {

    // private ListView mDirListView;
    private SurfaceView mSurfaceView;
    private static final String TAG = PlayActivity.class.getSimpleName();
    private VideoPlayerManager mPlayerManager;

    private TextView mCurrentTime;
    private TextView mTotalTime;
    private SeekBar mProgressBar;
    private Button mSetButton;
    private ImageView mPrevImageView;
    private ImageView mPauseImageView;
    private ImageView mNextImageView;
    private Handler mPlayHandler;
    private TextView mTitleView;
    private boolean mIsShowControlView;
    private LinearLayout mProcessLayout, mControllLayout;
    private long DELAY_TIME_LONG = 30 * 1000;// 1 minutes
    private long DELAY_TIME_NORMAL = 1 * 1000;// 1 minutes
    private long DELAY_TIME_SHORT = 200;// 1 minutes

    protected static EventHandler em = EventHandler.getInstance();
   
	private Handler baseEventHandler = new BaseJNIEventHandler(this);

	private class BaseJNIEventHandler extends WeakHandler<PlayActivity> {
		public BaseJNIEventHandler(PlayActivity owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			PlayActivity activity = getOwner();
			if (activity == null)
				return;
			// Do not handle events if we are leaving the TestJniActivity
			// ToDo:
			String data = msg.getData().getString("data");

			MediaMessage message = CommonUtil.parseMessage(data);
			if (message == null) {
				return;
			}
			if (message.getReveiver().equals("teacher")) {
				return;
			}
			PlayActivity.this.finish();
		};
	}
    private PlayerScreenOutputRect mCurrentOutputRect = PlayerScreenOutputRect.FULLSCREEN;

    /**
     * The available sizes for the Live TV output.
     * 
     * @see SourceManager#setScreenOutputRect(Rect)
     */
    public static enum PlayerScreenOutputRect {
        FULLSCREEN, PREVIEW_AREA,
    }

    // private String mPath = "/storage/external_storage/sda4/M2.mp4";
    private ArrayList<FilesEntity> mFileList;
    private String mPath;
    private int mIndex;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_video_layout);
        if (getIntent() != null) {
            mFileList = getIntent().getExtras().getParcelableArrayList("files");
            mPath = getIntent().getExtras().getString("path");
            mTitle = getIntent().getExtras().getString("title");
            mIndex = getIntent().getExtras().getInt("index");
        }
        initView();

    }

    @Override
    protected void onStart() {
    	em.addHandler(baseEventHandler);
    	super.onStart();
    }
    
    @Override
    protected void onStop() {
    	em.removeHandler(baseEventHandler);
    	super.onStop();
    }
    private void initView() {
        mProcessLayout = (LinearLayout) findViewById(R.id.process_layout);
        mControllLayout = (LinearLayout) findViewById(R.id.control_layout);
        mTitleView = (TextView) findViewById(R.id.title);
        mProcessLayout.setVisibility(View.VISIBLE);
        mControllLayout.setVisibility(View.VISIBLE);
        mTitleView.setVisibility(View.VISIBLE);
        mPrevImageView = (ImageView) findViewById(R.id.prev);
        mPrevImageView.setOnClickListener(mPrevListener);
        mPauseImageView = (ImageView) findViewById(R.id.pause);
        mPauseImageView.setOnClickListener(mPauseListener);
        mPauseImageView.requestFocus();
        mNextImageView = (ImageView) findViewById(R.id.next);
        mNextImageView.setOnClickListener(mNextListener);
        mCurrentTime = (TextView) findViewById(R.id.currenttime);
        mTotalTime = (TextView) findViewById(R.id.totaltime);
        mProgressBar = (SeekBar) findViewById(R.id.progress_bar);
        mProgressBar.setOnSeekBarChangeListener(new OnPlayingTimeSeekBarChangeListener());
        mSetButton = (Button) findViewById(R.id.set);
        mSetButton.setOnClickListener(this);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        mSurfaceView.setClickable(true);
        mSurfaceView.getHolder().addCallback(this);
        mSurfaceView.setOnClickListener(this);
        mPlayerManager = VideoPlayerManager.getInstance(this);
        mPlayerManager.setOnStateListener(this);
        mPlayerManager.setUpdateUIListenner(this);
        mPlayerManager.setDisplay(mSurfaceView.getHolder());
        mPlayerManager.setFileList(mFileList);
        mPlayerManager.setCurrentIndex(mIndex);
        mTitleView.setText(mTitle);
        if (mFileList.size() == 1) {
            mNextImageView.setEnabled(false);
            mPrevImageView.setEnabled(false);
        }
        // setTimeInfo();
        update(mFileList.get(mIndex), mIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mPlayerManager.setDataSource(mPath, true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        if (mSurfaceView != null && mPlayerManager != null) {
            SurfaceHolder sh = mSurfaceView.getHolder();
            sh.setSizeFromLayout();
            if (mCurrentOutputRect == PlayerScreenOutputRect.FULLSCREEN) {

                sh.setFixedSize(1920, 1080);
            } else if (mCurrentOutputRect == PlayerScreenOutputRect.PREVIEW_AREA) {
                sh.setFixedSize(800, 600);
            }
            Log.d(TAG, "size " + width + "  " + height);
            mPlayerManager.changeDisplay(sh);
        } else {
            Log.e("Test", "Can not set surface view as null pointer !");
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mPlayerManager != null) {
            mPlayerManager.releasePlayer();
        }
    }

    /**
     * Set the live tv content preview size.
     * 
     * @param size the target size in type of {@link #LiveTvScreenOutputRect}
     * @param destRectPosition the Rect contains detail position info
     */
    public void setSurfaceViewSize(PlayerScreenOutputRect size, Rect destRectPosition) {
        if (mSurfaceView != null) {
            switch (size) {
                case FULLSCREEN:
                    mSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(
                            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    mCurrentOutputRect = PlayerScreenOutputRect.FULLSCREEN;
                    break;

                case PREVIEW_AREA:
                    if (destRectPosition != null) {
                        final int width = destRectPosition.right - destRectPosition.left;
                        final int height = destRectPosition.bottom - destRectPosition.top;
                        android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
                                width, height);
                        params.leftMargin = destRectPosition.left;
                        params.topMargin = destRectPosition.top;

                        mSurfaceView.setLayoutParams(params);
                        mCurrentOutputRect = PlayerScreenOutputRect.PREVIEW_AREA;
                        break;
                    }

                default:
                    break;
            }
        } else {
            Log.e("test", "Can not re-set surface view size, mCurrentSurfaceView is null !");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.set) {
            if (mCurrentOutputRect == PlayerScreenOutputRect.FULLSCREEN) {
                Rect dest = new Rect(10, 10, 600, 450);
                setSurfaceViewSize(PlayerScreenOutputRect.PREVIEW_AREA, dest);
            } else {
                setSurfaceViewSize(PlayerScreenOutputRect.FULLSCREEN, null);
            }
        } else if (v.getId() == R.id.surfaceview) {
            mPlayHandler.sendEmptyMessageDelayed(4, DELAY_TIME_SHORT);
        }

    }

    private void setTimeInfo() {
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
                            mCurrentTime.setText(CommonUtil.convertTime(currentTime));

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
                        mTotalTime.setText(CommonUtil.convertTime(totalTime));
                    } else if (msg.what == 3) {
                        mIsShowControlView = false;
                        mProcessLayout.setVisibility(View.GONE);
                        mControllLayout.setVisibility(View.GONE);
                        mTitleView.setVisibility(View.GONE);
                    } else if (msg.what == 4) {
                        if (!mIsShowControlView) {
                            mIsShowControlView = true;
                            mProcessLayout.setVisibility(View.VISIBLE);
                            mControllLayout.setVisibility(View.VISIBLE);
                            mTitleView.setVisibility(View.VISIBLE);
                            mPlayHandler.sendEmptyMessageDelayed(3, DELAY_TIME_LONG);
                        }
                    } else if (msg.what == 5) {
                        finish();
                    }

                }
            };
        }
        mPlayHandler.sendEmptyMessage(1);
        mPlayHandler.sendEmptyMessageDelayed(2, DELAY_TIME_NORMAL);
        mPlayHandler.sendEmptyMessageDelayed(3, DELAY_TIME_LONG);
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
                Log.d(TAG, "seek to " + progress);
            }
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
        mTitleView.setText(filesEntity.getName());
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
    protected void onPause() {
        super.onPause();
        if(mPlayerManager!=null)
        mPlayerManager.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
       
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // release player
        if (mPlayerManager != null) {
            // unRegister report listener.
            mPlayerManager.release();
            mPlayerManager = null;
        }

    }

	@Override
	public boolean onError(int what, int extra) {
		Toast.makeText(this.getApplicationContext(), R.string.play_error_msg, Toast.LENGTH_LONG).show();
		this.finish();
		return false;
	}

	@Override
	public boolean onInfo(int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onNetworkConnected(boolean connected) {
		// TODO Auto-generated method stub
		
	}
}
