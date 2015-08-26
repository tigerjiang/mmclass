package com.multimedia.room.fragment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.R.color;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.multimedia.room.BaseActivity;
import com.multimedia.room.BaseActivity.IUpdateResources;
import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommonUtil;
import com.multimedia.room.FilesEntity;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.PlayActivity;
import com.multimedia.room.R;
import com.multimedia.room.fragment.SelfStudyFragment.IDestroyListener;

public class SelfAodFragment extends BaseFragment implements
		OnItemClickListener, OnClickListener, IUpdateResources,
		IDestroyListener {

	// private ListView mDirListView;
	private ListView mPlayListView;
	private static final String TAG = SelfAodFragment.class.getSimpleName();

	private Button mVideoButton, mAudioButton, mDocButton;

	// private String mPath = "/storage/external_storage/sda4/M2.mp4";
	private ArrayList<FilesEntity> mFileList;
	private ArrayList<FilesEntity> mVideoList;
	private ArrayList<FilesEntity> mAudioList;
	private ArrayList<FilesEntity> mDocList;
	private FileAdapter mFileAdapter;
	private static MediaMessage mMessage;
	private Context mContext;
	private int mCurrentIndex = 1;
	private String testUrl = "http://192.168.1.5:8080/list.xml";

	private static String mUrl;
	private String mPath = null;
	private AudioBarView mAudioBarView;
	private Handler mHandler;

	private HandlerThread mThread = new HandlerThread("study");

	public static SelfAodFragment newInstance(MediaMessage message) {
		mMessage = message;
		Log.d(TAG, "message" + message.toString());
		SelfAodFragment fragment = new SelfAodFragment();
		return fragment;
	}

	public static SelfAodFragment newInstance() {
		SelfAodFragment fragment = new SelfAodFragment();
		return fragment;
	}
	private void init(){
		mThread.start();
		mHandler = new Handler(mThread.getLooper()){

			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1){
                                 	final String result  = (String) msg.obj;
					SelfAodFragment.this.getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							if (!TextUtils.isEmpty(result)) {
								Log.d("result ", result);
								mFileList = CommonUtil
										.getFilesEntityFromXML(new ByteArrayInputStream(result
												.getBytes()));
								Log.d(TAG, mFileList.toString());
								parseFileList();
								if (mCurrentIndex == 1) {
									mVideoButton.setEnabled(false);
									mFileAdapter = new FileAdapter(mVideoList);
								} else if (mCurrentIndex == 2) {
									mAudioButton.setEnabled(false);
									mFileAdapter = new FileAdapter(mAudioList);
									mAudioBarView.setAudioFiles(mAudioList);
									mAudioBarView.setVisibility(View.VISIBLE);
								} else if (mCurrentIndex == 3) {
									mDocButton.setEnabled(false);
									mFileAdapter = new FileAdapter(mDocList);
								}
								mPlayListView.setAdapter(mFileAdapter);

							}
						}
					});
				}
				super.handleMessage(msg);
			}
		
		};
	}

	@Override
	public void onAttach(Activity activity) {
		mContext = activity;
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.self_aod_layout, null);
		initView(rootView);
		return rootView;
	}

	private void initView(View rootView) {
		mPlayListView = (ListView) rootView.findViewById(R.id.file_list);
		mAudioBarView = (AudioBarView) rootView.findViewById(R.id.audio_bar);
		mPlayListView.setOnItemClickListener(this);
		mVideoButton = (Button) rootView.findViewById(R.id.video_button);
		mAudioButton = (Button) rootView.findViewById(R.id.audio_button);
		mDocButton = (Button) rootView.findViewById(R.id.doc_button);
		mVideoButton.setActivated(true);
		mVideoButton.setOnClickListener(this);
		mAudioButton.setOnClickListener(this);
		mDocButton.setOnClickListener(this);
		mVideoList = new ArrayList<FilesEntity>();
		mAudioList = new ArrayList<FilesEntity>();
		mDocList = new ArrayList<FilesEntity>();
		// mAudioList = MediaRoomDemo.sTestFiles;
		// mFileAdapter = new FileAdapter(mFileList);
		// mPlayListView.setAdapter(mFileAdapter);
		init();
		implementsIupdate();
		SelfStudyFragment.registerDestroyListener(this);
		Log.d(TAG,
				"message"
						+ (mMessage == null ? "message is null" : mMessage
								.toString()));
		/*
		 * if (mMessage == null || TextUtils.isEmpty(mMessage.getParams())) {
		 * return; } mUrl = mMessage.getParams();
		 */
		if (mUrl == null) {
			return;
		}
		new MyTask().execute(mUrl);
//		downloadResources(mUrl);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	class MyTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {
			String url = arg0[0];
			if (TextUtils.isEmpty(url)) {
				return "";
			}
			String result = CommonUtil.getFileData(url);
			return result;
		}

		protected void onPostExecute(String result) {
			if (result == null) {
				return;
			}
			if (!TextUtils.isEmpty(result)) {
				Log.d("result ", result);
				mFileList = CommonUtil
						.getFilesEntityFromXML(new ByteArrayInputStream(result
								.getBytes()));
				Log.d(TAG, mFileList.toString());
				parseFileList();
				if (mCurrentIndex == 1) {
					mVideoButton.setEnabled(false);
					mFileAdapter = new FileAdapter(mVideoList);
				} else if (mCurrentIndex == 2) {
					mAudioButton.setEnabled(false);
					mFileAdapter = new FileAdapter(mAudioList);
					mAudioBarView.setAudioFiles(mAudioList);
					mAudioBarView.setVisibility(View.VISIBLE);
				} else if (mCurrentIndex == 3) {
					mDocButton.setEnabled(false);
					mFileAdapter = new FileAdapter(mDocList);
				}
				mPlayListView.setAdapter(mFileAdapter);

			}
		};
	};
	
	private void downloadResources(String url){
		IDownLoadListener l =new IDownLoadListener(){

			@Override
			public void downloadComplete(String result) {
				if (result == null) {
					return;
				}
                          Message msg = mHandler.obtainMessage(1);
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
			
		};
		DownLoadTask task= new DownLoadTask(url,l);
		mHandler.post(task);
	};
	
	class DownLoadTask implements Runnable{
		private String url;
		private IDownLoadListener l;

		public DownLoadTask(String url,IDownLoadListener listener) {
			this.url = url;
			this.l = listener;
		}
		@Override
		public void run() {
			String result = CommonUtil.getFileData(url);
			if(!TextUtils.isEmpty(result)){
				l.downloadComplete(result);
			}
		}
	}

	interface IDownLoadListener{
		void downloadComplete(String result);
	}
	class FileAdapter extends BaseAdapter {

		private List<FilesEntity> mFiles;
		private int mIndex = -1;

		public void setSelected(int index) {
			mIndex = index;
			notifyDataSetChanged();
		}

		public FileAdapter(List<FilesEntity> files) {
			mFiles = files;
		}

		@Override
		public int getCount() {
			return mFiles.size();
		}

		@Override
		public Object getItem(int position) {
			return mFiles.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.listem_item, null);
			TextView fileNameView = (TextView) convertView
					.findViewById(R.id.item);
			fileNameView.setText((position + 1) + ". "
					+ mFiles.get(position).getName());
			fileNameView.setHeight(60);
//			if (position == mIndex) {
//				fileNameView.setTextColor(color.darker_gray);
//			} else {
//				fileNameView.setTextColor(color.black);
//			}

			return convertView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// mFileAdapter.setSelected(arg2);
		if (mCurrentIndex == 1) {
			if (mVideoList != null && mVideoList.size() > 0) {
				((BaseActivity) this.getActivity()).killPlayerProcess();
				Intent intent = new Intent();
				intent.setClassName("com.mxtech.videoplayer.ad",
						"com.mxtech.videoplayer.ad.ActivityScreen");

//				intent.setClass(mContext, PlayActivity.class);
//				intent.putParcelableArrayListExtra("files", mVideoList);
//				intent.putExtra("path", mVideoList.get(arg2).getUrl());
//				intent.putExtra("title", mVideoList.get(arg2).getName());
//				intent.putExtra("index", arg2);
				
				intent.setData(Uri.parse(mVideoList.get(arg2).getUrl()));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				this.getActivity().overridePendingTransition(R.anim.fade,
						R.anim.hold);
			}
		} else if (mCurrentIndex == 2) {
			if (mAudioList != null && mAudioList.size() > 0) {
				mAudioBarView.setAudioFiles(mAudioList);
				mAudioBarView.setIndex(arg2);
				mPath = mAudioList.get(arg2).getUrl();
				mAudioBarView.setPath(mPath);
			}

		} else if (mCurrentIndex == 3) {

			if (mDocList != null && mDocList.size() > 0) {
				FilesEntity file = mDocList.get(arg2);
				DownLoadDialogFragment downloadDialogFragment = DownLoadDialogFragment
						.newInstance(file);
				final FragmentManager fm = getFragmentManager();
				fm.beginTransaction();
				downloadDialogFragment.show(fm, "Download  fragment");
			}
		}

	}

	@Override
	public void updateUrl(String url) {
		Log.d(TAG, "update resources " + url);
		mUrl = url;
//		downloadResources(mUrl);
		new MyTask().execute(url);
	}

	private void implementsIupdate() {
		if (this.getActivity() instanceof BaseActivity) {
			((BaseActivity) this.getActivity()).setUpdateResources(this);
		}
	}

	@Override
	public void onClick(View view) {
		setActivatedDefault();
		view.setActivated(true);
		switch (view.getId()) {
		case R.id.video_button:
			mCurrentIndex = 1;
			mFileAdapter = new FileAdapter(mVideoList);
			mVideoButton.setEnabled(false);
			mAudioButton.setEnabled(true);
			mDocButton.setEnabled(true);
			mAudioBarView.setVisibility(View.GONE);
			mAudioBarView.realse();
			break;
		case R.id.audio_button:
			mCurrentIndex = 2;
			mFileAdapter = new FileAdapter(mAudioList);
			mVideoButton.setEnabled(true);
			mAudioButton.setEnabled(false);
			mAudioBarView.setVisibility(View.VISIBLE);
			mDocButton.setEnabled(true);
			break;
		case R.id.doc_button:
			mCurrentIndex = 3;
			mFileAdapter = new FileAdapter(mDocList);
			mVideoButton.setEnabled(true);
			mAudioButton.setEnabled(true);
			mDocButton.setEnabled(false);
			mAudioBarView.setVisibility(View.GONE);
			mAudioBarView.realse();
			break;
		default:
			break;
		}
		mPlayListView.setAdapter(mFileAdapter);

	}

	private void parseFileList() {
		mVideoList.clear();
		mAudioList.clear();
		mDocList.clear();
		for (FilesEntity fe : mFileList) {
			if (fe.getType().equalsIgnoreCase(".mp4")
					|| fe.getType().equalsIgnoreCase(".avi")
					|| fe.getType().equalsIgnoreCase(".rmvb")
					|| fe.getType().equalsIgnoreCase(".rm")
					|| fe.getType().equalsIgnoreCase(".wmv")
					|| fe.getType().equalsIgnoreCase(".3pg")
					|| fe.getType().equalsIgnoreCase(".flv")
					|| fe.getType().equalsIgnoreCase(".mkv")) {
				mVideoList.add(fe);
			} else if (fe.getType().equalsIgnoreCase(".mp3")
					|| fe.getType().equalsIgnoreCase(".wav")
					|| fe.getType().equalsIgnoreCase(".wma")) {
				mAudioList.add(fe);
			} else if (fe.getType().equalsIgnoreCase(".txt")
					|| fe.getType().equalsIgnoreCase(".doc")
					|| fe.getType().equalsIgnoreCase(".docx")
					|| fe.getType().equalsIgnoreCase(".xls")
					|| fe.getType().equalsIgnoreCase(".xlsx")
					|| fe.getType().equalsIgnoreCase(".pdf")
					|| fe.getType().equalsIgnoreCase(".ppt")
					|| fe.getType().equalsIgnoreCase(".pptx")
					|| fe.getType().equalsIgnoreCase(".png")
					|| fe.getType().equalsIgnoreCase(".jpg")
					|| fe.getType().equalsIgnoreCase(".jpeg")
					|| fe.getType().equalsIgnoreCase(".bmp")) {
				mDocList.add(fe);
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mAudioBarView.realse();
	}

	@Override
	public void onDestroyView() {

		super.onDestroyView();
		mAudioBarView.realse();
		SelfStudyFragment.unRegisterDestroyListener(this);
	}

	@Override
	public void destroy() {
		Log.d(TAG, "destroy()");
		mUrl = null;
		mVideoList = null;
		mDocList = null;
		mAudioList = null;
	}

	private void setActivatedDefault() {
		mVideoButton.setActivated(false);
		mAudioButton.setActivated(false);
		mDocButton.setActivated(false);
	}
}
