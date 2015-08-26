package com.multimedia.room.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.CommonUtil;
import com.multimedia.room.R;
import com.multimedia.room.media.AudioPlayerManager;
import com.multimedia.room.media.ExtAudioRecorder;

public class SelfRecordFragment extends BaseFragment {
	private static final String TAG = SelfRecordFragment.class.getSimpleName();

	private static AudioPlayerManager mPlayerManager;
	private Button mRecordImageView;
	private Button mPauseImageView;
	// private ImageView mStopImageView;
	private Handler mPlayHandler;

	private String mCurrentTime;
	private String mTotalTime;
	private TextView mShowTimeView;
	private LinearLayout mPlaybackLyout;
	private SeekBar mProgressBar;
	private boolean mStartRecording = true;
	private String mBasePath;
	private static final String mAudioPath = "mmclassradio";
	// private static final String mAudioName = "RadioAudio.pcm";
	private static final String mAudioName = "RadioAudio.mp3";
	private String mPath;
	private long DELAY_TIME_NORMAL = 1 * 1000;// 1 minutes

	// 音频获取源
	private int audioSource = MediaRecorder.AudioSource.MIC;
	private boolean isRecording;
	private static AudioTrack audioTrack;
	// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	private static int sampleRateInHz = 44100;
	// private static int sampleRateInHz = 8000;
	// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道 值为 2，CHANNEL_CONFIGURATION_MONO为单声道 值为 1
	private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_STEREO;
	// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

	private TextView mRecordInfoView;

	private UIHandler uiHandler;

	private UIThread uiThread;
	private Context mContext;

	private boolean mIsStartPlay = false;

	private static AudioRecord sAudioRecord;
	private static int mState = 0;

	public static SelfRecordFragment newInstance() {
		SelfRecordFragment fragment = new SelfRecordFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.self_record_layout, null);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {

		mPlaybackLyout = (LinearLayout) rootView
				.findViewById(R.id.playback_layout);
		mPlaybackLyout.setVisibility(View.GONE);
		mPauseImageView = (Button) rootView.findViewById(R.id.pause);
		mPauseImageView.setEnabled(false);
		mPauseImageView.setOnClickListener(mPauseListener);
		mPauseImageView.requestFocus();
		mRecordInfoView = (TextView) rootView.findViewById(R.id.record_info);
		mShowTimeView = (TextView) rootView.findViewById(R.id.showtime);
		mProgressBar = (SeekBar) rootView.findViewById(R.id.progress_bar);
		mProgressBar
				.setOnSeekBarChangeListener(new OnPlayingTimeSeekBarChangeListener());
		mRecordImageView = (Button) rootView.findViewById(R.id.recoder);
		mRecordImageView.setOnClickListener(mRecoderListener);
		mContext = this.getActivity();
		mPlayerManager = AudioPlayerManager.getInstance(this.getActivity());
		uiHandler = new UIHandler();
		// mBasePath =
		// Environment.getExternalStorageDirectory().getAbsolutePath();
		mBasePath = this.getActivity().getFilesDir().getAbsolutePath();
		Log.d(TAG, mBasePath);
		// mBasePath = "/storage/emulated/legacy";
		mPath = mBasePath + "/" + mAudioPath + "/" + mAudioName;
	}

	private void refreshViewStatus(int state) {
		if (mState == 1) {
			mPauseImageView.setEnabled(false);
			mRecordImageView.setText(R.string.end_record);
			mRecordInfoView.setVisibility(View.VISIBLE);
			// mStopImageView.setEnabled(true);

		} else if (mState == 0) {
			mRecordImageView.setText(R.string.start_record);
			mRecordInfoView.setVisibility(View.VISIBLE);
			mPauseImageView.setEnabled(true);
			// mStopImageView.setEnabled(false);
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
							mCurrentTime = CommonUtil.convertTime(currentTime);

							mShowTimeView.setText(mCurrentTime + " / "
									+ mTotalTime);

							Log.d("current time",
									"current pos "
											+ CommonUtil
													.convertTime(currentTime));
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
			mPlaybackLyout.setVisibility(View.VISIBLE);
			mRecordInfoView.setVisibility(View.GONE);
			if (!mIsStartPlay) {
				mPlayerManager.setDataSource(mPath, true);
				mPlayerManager.pause();
				mIsStartPlay = true;
				setTimeInfo();
			}
			doPauseResume();
		}
	};

	private View.OnClickListener mRecoderListener = new View.OnClickListener() {
		public void onClick(View v) {
			mPlaybackLyout.setVisibility(View.GONE);
			mRecordInfoView.setVisibility(View.VISIBLE);
			onRecord(mStartRecording);
			if (mStartRecording) {
				mState = 1;
			} else {
				mState = 0;
			}
			mStartRecording = !mStartRecording;
			mIsStartPlay = false;
			refreshViewStatus(mState);
			Log.d(TAG, "state " + mState);
		}
	};

	private View.OnClickListener mStopListener = new View.OnClickListener() {
		public void onClick(View v) {
			mPlayerManager.stop();
			setTimeInfo();
		}
	};

	private void doPauseResume() {
		if (mPlayerManager != null) {
			if (mPlayerManager.isPlaying()) {
				mPauseImageView.setText(R.string.pause);
				mPlayerManager.pause();
			} else {
				mPauseImageView.setText(R.string.playback);
				mPlayerManager.play();
			}
		}
	}

	/**
	 * 播放进度条
	 * 
	 * @author SuHao
	 */
	private class OnPlayingTimeSeekBarChangeListener implements
			OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
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

	public File recordChat(ExtAudioRecorder extAudioRecorder, String savePath,
			String fileName) {
		File dir = new File(savePath);
		if (dir.list() == null) {
			dir.mkdirs();
		}
		File file = new File(savePath + fileName);
		extAudioRecorder.setOutputFile(savePath + fileName);
		extAudioRecorder.prepare();
		extAudioRecorder.start();
		uiThread = new UIThread();

		new Thread(uiThread).start();
		return file;
	}

	public void stopRecord(final ExtAudioRecorder extAudioRecorder) {
		extAudioRecorder.stop();
		if (uiThread != null) {
			uiThread.stopThread();
		}
		stop();

		extAudioRecorder.release();
	}

	/**
	 * /** 停止录音
	 */

	private void stop() {

		if (uiThread != null) {

			uiThread.stopThread();

		}

		if (uiHandler != null)

			uiHandler.removeCallbacks(uiThread);

		Message msg = new Message();

		Bundle b = new Bundle();// 存放数据

		b.putInt("cmd", CMD_STOP);

		msg.setData(b);

		uiHandler.sendMessageDelayed(msg, 1000); // 向Handler发送消息,更新UI

	}

	private final static int CMD_RECORDING_TIME = 2000;

	private final static int CMD_RECORDFAIL = 2001;

	private final static int CMD_STOP = 2002;

	class UIHandler extends Handler {

		public UIHandler() {

		}

		@Override
		public void handleMessage(Message msg) {

			// TODO Auto-generated method stub

			Log.d("MyHandler", "handleMessage......");

			super.handleMessage(msg);

			Bundle b = msg.getData();

			int vCmd = b.getInt("cmd");

			switch (vCmd)

			{

			case CMD_RECORDING_TIME:

				int vTime = b.getInt("msg");

				mRecordInfoView.setText("正在录音中，已录制：" + vTime + " s");

				break;

			case CMD_STOP:

				int vFileType = b.getInt("msg");

				mRecordInfoView.setText("录音已停止.录音文件:" + mAudioName + "\n文件大小："
						+ getFileSize(getWavFilePath()) + " KB");
				break;

			default:

				break;

			}

		}
	};

	class UIThread implements Runnable {

		int mTimeMill = 0;

		boolean vRun = true;

		public void stopThread() {

			vRun = false;

		}

		public void run() {

			while (vRun) {

				try {

					Thread.sleep(1000);

				} catch (InterruptedException e) {

					// TODO Auto-generated catch block

					e.printStackTrace();

				}

				mTimeMill++;

				Log.d("thread", "mThread........" + mTimeMill);

				Message msg = new Message();

				Bundle b = new Bundle();// 存放数据

				b.putInt("cmd", CMD_RECORDING_TIME);

				b.putInt("msg", mTimeMill);

				msg.setData(b);

				uiHandler.sendMessage(msg); // 向Handler发送消息,更新UI

			}

		}

	}

	private String getWavFilePath() {
		return mBasePath + "/" + mAudioPath + "/" + mAudioName;
	}

	/**
	 * 获取文件大小
	 * 
	 * @param path
	 *            ,文件的绝对路径
	 * @return
	 */
	public static long getFileSize(String path) {
		File mFile = new File(path);
		if (!mFile.exists())
			return -1;
		return mFile.length() / 1024;
	}

	// 当录音按钮被click时调用此方法，开始或停止录音
	private void onRecord(boolean start) {
		if (start) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					startRecording();
				}
			}).start();

		} else {
			stopRecording();
		}
	}

	private void startRecording() {
		uiThread = new UIThread();
		new Thread(uiThread).start();
		String floderPath = mBasePath + File.separator + mAudioPath;
		File floder = new File(floderPath);
		File file = new File(floderPath + File.separator + mAudioName);
		if (floder.exists()) {

		} else {
			floder.mkdirs();
		}
		// 删除录音文件
		if (file.exists())
			file.delete();
		// 创建录音文件
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create "
					+ file.toString());
		}
		try {
			// Create a DataOuputStream to write the audio data into the
			// saved file.
			FileOutputStream fos = new FileOutputStream(file);// 建立一个可存取字节的文件
			// Create a new AudioRecord object to record the audio.
			// 获得满足条件的最小缓冲区大小
			int bufferSizeInBytes = AudioRecord.getMinBufferSize(
					sampleRateInHz, channelConfig, audioFormat);
			CommandManager.mp3LameInit(2, sampleRateInHz, audioFormat);
			// 创建AudioRecord对象
			 sAudioRecord = new AudioRecord(audioSource,
					sampleRateInHz, channelConfig, audioFormat,
					bufferSizeInBytes);
			byte[] buffer = new byte[bufferSizeInBytes];
			byte[] tempBuffer = new byte[bufferSizeInBytes];
			sAudioRecord.startRecording();
			isRecording = true;
			while (isRecording) {
				int readSize = sAudioRecord.read(buffer, 0, bufferSizeInBytes);
				Log.d(TAG, "readsize ==========" + readSize);
				if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {
					tempBuffer = CommandManager.mp3LameEncode(buffer,
							bufferSizeInBytes);
					fos.write(tempBuffer);
				}
			}
			byte[] flushByte = CommandManager.mp3LameFlush();
			if (flushByte != null && flushByte.length > 0) {
				fos.write(flushByte);
			}
			fos.close();
		} catch (Throwable t) {
			Log.e("AudioRecord", "Recording Failed");
		}
	}

	private void stopRecording() {
		sAudioRecord.stop();
		sAudioRecord.release();// 释放资源
		CommandManager.mp3LameDestroy();
		if (uiThread != null) {
			uiThread.stopThread();
		}
		stop();
		isRecording = false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mPlayerManager != null) {
			mPath = null;
			mPlayerManager.setDataSource(mPath, true);
			mPlayerManager.release();
			mPlayerManager = null;
		}

		String floderPath = mBasePath + File.separator + mAudioPath;
		File floder = new File(floderPath);
		File file = new File(floderPath + File.separator + mAudioName);
		if (floder.exists()) {

		} else {
			floder.mkdirs();
		}
		// 删除录音文件
		if (file.exists())
			file.delete();
		isRecording = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

}
