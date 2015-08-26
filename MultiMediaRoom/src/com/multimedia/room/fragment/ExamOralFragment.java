package com.multimedia.room.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mmclass.libsiren.net.FormFile;
import com.mmclass.libsiren.net.SocketHttpRequester;
import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.CommonUtil;
import com.multimedia.room.ISwitch;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.R;
import com.multimedia.room.SwitchManager;

public class ExamOralFragment extends BaseFragment implements ISwitch {
	private TextView mExamOverView;
	private String mBasePath;
	private static final String TAG = ExamOralFragment.class.getSimpleName();
	private static final String mAudioPath = "mmclassradio/";
	private static final String mAudioName = "exam";
	private static int mExamIndex = 1;
	private String mFloderPath;
	private String mUploadUrl;
	private boolean mIsRecordOver = false;
	private static MediaMessage mCurrentMessage;

	public final static int AUDIO_SAMPLE_RATE = 44100; // 44.1KHz,普遍使用的频率
	private SwitchManager mSwitchManager;

	private AudioManager mAudioManager;

	// 音频获取源
	private int audioSource = MediaRecorder.AudioSource.MIC;
	private boolean isRecording = false;
	private static AudioTrack audioTrack;
	private static AudioRecord sAudioRecord;
	// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
	private static int sampleRateInHz = 44100;
	// private static int sampleRateInHz = 8000;
	// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
	private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

	public static ExamOralFragment newInstance(MediaMessage message) {
		mCurrentMessage = message;
		ExamOralFragment fragment = new ExamOralFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.exam_oral_layout, null);
		mExamOverView = (TextView) rootView.findViewById(R.id.exam_oral_over);
		mSwitchManager = SwitchManager.getInstance();
		mSwitchManager.registerExamSwitch(this);
		mAudioManager = (AudioManager) this.getActivity().getSystemService(
				Context.AUDIO_SERVICE);
		int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				(max / 10) * 8, -2);
		mBasePath = this.getActivity().getFilesDir().getAbsolutePath();
		Log.d(TAG, mBasePath);
		mUploadUrl = mCurrentMessage.getParams();
		mFloderPath = mBasePath + File.separator + mAudioPath;

		return rootView;
	}

	@Override
	public void switchOn() {
		if (isRecording) {
			isRecording = false;
			if (!mIsRecordOver)
				return;
		}

		if (mExamIndex > 3) {
			mExamIndex = 1;
		}
		String path = mFloderPath + CommonUtil.getSeatNo() + "_" + mAudioName
				+ mExamIndex + ".mp3";
		String alertMsg = "第" + mExamIndex + "部分考试开始";
		mExamOverView.setText(alertMsg);
		Log.d(TAG, "local path" + path);
		Log.d(TAG, "start oral exam");
		new Thread(new Runnable() {

			@Override
			public void run() {
				mIsRecordOver = false;
				startRecording(mExamIndex);
			}
		}).start();

	}

	@Override
	public void switchOff() {
		String alertMsg = "第" + mExamIndex + "部分考试结束";
		mExamOverView.setText(alertMsg);
		Log.d(TAG, "end oral exam");
		stopRecording(mExamIndex);
		mExamIndex++;
		// TODO .. post the mp3 file to the server.
	}

	private void startRecording(int index) {

		CommandManager.openMic();
		String fileName = CommonUtil.getSeatNo() + "_" + mAudioName + index
				+ ".mp3";
		File floder = new File(mFloderPath);
		File file = new File(mFloderPath + File.separator + fileName);
		if (floder.exists()) {
			// Nothing to do
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
			android.os.Process
					.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
			CommandManager.mp3LameInit(1, sampleRateInHz, audioFormat);
			// 创建AudioRecord对象
			sAudioRecord = new AudioRecord(audioSource, sampleRateInHz,
					channelConfig, audioFormat, bufferSizeInBytes);
			ByteBuffer directBuffer = ByteBuffer
					.allocateDirect(bufferSizeInBytes);
			byte[] buffer = new byte[bufferSizeInBytes];
			byte[] tempBuffer = new byte[bufferSizeInBytes];
			sAudioRecord.startRecording();
			isRecording = true;
			Log.d(TAG, "start record");
			while (isRecording) {
				int readSize = sAudioRecord.read(directBuffer.array(), 0,
						bufferSizeInBytes);
				Log.d(TAG, "readsize ==========" + readSize);
				if (AudioRecord.ERROR_INVALID_OPERATION != readSize) {
					tempBuffer = CommandManager.mp3LameEncode(
							directBuffer.array(), bufferSizeInBytes);
					fos.write(tempBuffer);
				}
				// fos.write(buffer);
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

	private void stopRecording(final int index) {
		isRecording = false;
		sAudioRecord.stop();
		sAudioRecord.release();// 释放资源
		CommandManager.mp3LameDestroy();
		CommandManager.closeMic();
		Log.d(TAG, "record over");
		mIsRecordOver = true;
		// 录音结束 上传文件
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				uploadFile(index);
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isRecording = false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mSwitchManager.unregisterExamSwitch(this);

	}

	private void uploadFile(int index) {
		boolean isUploadFinish = false;
		final int order = index;
		while (!isUploadFinish) {
			String testFileName = "123.jpg";
			String fileName = CommonUtil.getSeatNo() + "_" + mAudioName + order
					+ ".mp3";
			File file = new File(mFloderPath, fileName);
			File testFile = new File(mFloderPath, testFileName);
			FormFile formfile = new FormFile(fileName, file, "file",
					"application/octet-stream");
			// FormFile[] files = {formfile,new FormFile(fileName, testFile,
			// "file",
			// "application/octet-stream")};
			try {
				Log.d("upload",
						"upload start "
								+ fileName
								+ new Date(System.currentTimeMillis())
										.toString());
				SocketHttpRequester.post(mUploadUrl, null, formfile);
				Log.d("upload",
						"upload end "
								+ fileName
								+ new Date(System.currentTimeMillis())
										.toString());
				isUploadFinish = true;
			} catch (Exception e) {
				isUploadFinish = false;
				Log.e(TAG, e.toString());
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}
