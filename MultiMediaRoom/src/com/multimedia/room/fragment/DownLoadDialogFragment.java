package com.multimedia.room.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.multimedia.room.CommonUtil;
import com.multimedia.room.FilesEntity;
import com.multimedia.room.OpenFiles;
import com.multimedia.room.R;
import com.multimedia.room.upgrade.Constants;
import com.multimedia.room.upgrade.DownloadTask;
import com.multimedia.room.upgrade.IDownloadListener;
import com.multimedia.room.upgrade.RebootTask;
import com.multimedia.room.upgrade.Session;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class DownLoadDialogFragment extends DialogFragment {

	private String mBasePath;
	private static FilesEntity mFilesEntity;

	private String mDownDir;
	private String mFileName;
	private DownloadTask mDownloadTask;

	private Handler mDownloadHandler;

	private Session mSession;
	private HandlerThread mThread = new HandlerThread("SystemUpgrade");

	/**
	 * Create a {@code TagDialogFragment}.
	 */
	static public DownLoadDialogFragment newInstance(FilesEntity file) {
		final DownLoadDialogFragment dialogFragment = new DownLoadDialogFragment();
		mFilesEntity = file;
		return dialogFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.download_layout, null);
		init(rootView);
		initDownload();
		download(mFilesEntity);
		return rootView;
	}

	public void init(View rootView) {
	}

	private boolean checkEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;
	}

	private void openFile(File file) {
		if (file != null) {
			String fileName = file.getName();
			Intent intent = null;
			try {
				if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingImage))) {
					intent = OpenFiles.getImageFileIntent(file);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingWebText))) {
					intent = OpenFiles.getHtmlFileIntent(file);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPackage))) {
					intent = OpenFiles.getApkFileIntent(file);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingText))) {
					intent = OpenFiles.getWordFileIntent(file);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPdf))) {
					intent = OpenFiles.getPdfFileIntent(file);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingWord))) {
					intent = OpenFiles.getWordFileIntent(file);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingExcel))) {
					intent = OpenFiles.getExcelFileIntent(file);
				} else if (checkEndsWithInStringArray(fileName, getResources()
						.getStringArray(R.array.fileEndingPPT))) {
					intent = OpenFiles.getPPTFileIntent(file);

				} else {
					CommonUtil.showDialog(this.getActivity(),
							R.string.error_msg, R.string.no_app);
				}
				if (intent != null) {
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
			} catch (Exception e) {
				Log.e("file", "no app", e);
				Toast.makeText(getActivity(), R.string.no_app,
						Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(getActivity(), R.string.invalid_msg,
					Toast.LENGTH_LONG).show();
		}
	}

	private void download(FilesEntity fileEntity) {
		File file = null;
		// 获取SD卡目录

		mDownDir = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/tmp/";
		String urlStr = fileEntity.getUrl();
		mFileName = fileEntity.getName();
		try {
			download(mDownDir, mFileName, urlStr);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void initDownload() {
		mThread.start();
		mDownloadHandler = new Handler(mThread.getLooper());
		mSession = new Session();
		mSession.setContext(this.getActivity());
	}

	/*
	 * Download the file.
	 * 
	 * @param dest
	 * 
	 * @throws RemoteException
	 */
	public void download(String dirPath, String fileName, String url)
			throws RemoteException {
		Log.d("Download", "SystemUpdateServiceStub.download");
		// mSession.setRunInBackgroud(false);
		// Tiger - hard code /cache as the first try. 2013.12.31
		mSession.setDownloadTo(dirPath, fileName);
		mSession.setNewVersionURL(url);
		Log.d("Download", "download start");
		IDownloadListener l = new IDownloadListener() {

			@Override
			public boolean onChangeDownloadPath(Session session) {
				// brljzhou - this should not be called. 2013.12.31
				Log.e("Download", "this should not be called\n");
				return false;

			}

			@Override
			public void onCompleted(Session session) {
				Log.e("Download", "onCompleted()");
				dismiss();
				openFile(new File(mDownDir + mFileName));
			}

			@Override
			public void onError(Session session, int errorCode) {
				Log.e("Download", "onError()");
			}
		};
		mDownloadTask = new DownloadTask(mSession, l);
		mDownloadHandler.post(mDownloadTask);
	}
}
