package com.multimedia.room.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.R;

public class SelfStudyFragment extends BaseFragment implements OnClickListener {

	private static final String TAG = "SelfStudyFragment";
	private Button mMediaDemandBtn;

	private Button mRecordPracticeBtn;
	private Button mElectronicDictionaryBtn;
	private Button mIPCallBtn;
	private Button mUserHelp;
	private static Context mContext;
	private static MediaMessage mMessage;

	public static SelfStudyFragment newInstance(MediaMessage message) {
		mMessage = message;
		SelfStudyFragment fragment = new SelfStudyFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.self_study_layout, null);
		initSubView(rootView);
		return rootView;
	}

	private void initSubView(View rootView) {
		mContext = this.getActivity();
		mMediaDemandBtn = (Button) rootView.findViewById(R.id.media_demand);
		mRecordPracticeBtn = (Button) rootView
				.findViewById(R.id.record_practice);
		mElectronicDictionaryBtn = (Button) rootView
				.findViewById(R.id.electronic_dictionary);
		mIPCallBtn = (Button) rootView.findViewById(R.id.ip_call);
		mUserHelp = (Button) rootView.findViewById(R.id.user_help);

		mMediaDemandBtn.setOnClickListener(this);
		mRecordPracticeBtn.setOnClickListener(this);
		mElectronicDictionaryBtn.setOnClickListener(this);
		mIPCallBtn.setOnClickListener(this);
		mUserHelp.setOnClickListener(this);
		mIPCallBtn.setClickable(true);
		mMediaDemandBtn.setActivated(true);
		Fragment replaceFragment = null;
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		replaceFragment = SelfAodFragment.newInstance();
		fragmentTransaction.replace(R.id.study_layout, replaceFragment);
		fragmentTransaction.commitAllowingStateLoss();

	}

	@Override
	public void onClick(View v) {

		// Do not handle events if we are leaving the TestJniActivity
		// ToDo:
		setActivatedDefault();
		Fragment replaceFragment = null;
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		v.setActivated(true);
		switch (v.getId()) {
		case R.id.media_demand:
			replaceFragment = SelfAodFragment.newInstance();
			break;

		case R.id.electronic_dictionary:
			replaceFragment = BrowserFragment.newInstance();
			break;
		case R.id.record_practice:
			replaceFragment = SelfRecordFragment.newInstance();
			break;
		case R.id.user_help:
			replaceFragment = SelfHelpFragment.newInstance();
			break;
		case R.id.ip_call:
			replaceFragment = SelfIPPhoneFragment.newInstance();
			break;
		default:
			break;
		}
		fragmentTransaction.replace(R.id.study_layout, replaceFragment);
		fragmentTransaction.commitAllowingStateLoss();

	};

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onDestroyView() {
		for (IDestroyListener listener : mDestroyListenerList) {
			listener.destroy();
		}
		mDestroyListenerList.clear();
		super.onDestroyView();
	}

	public static interface IDestroyListener {
		public void destroy();
	}

	private static ArrayList<IDestroyListener> mDestroyListenerList = new ArrayList<SelfStudyFragment.IDestroyListener>();

	public static void registerDestroyListener(IDestroyListener l) {
		mDestroyListenerList.add(l);
	}

	public static void unRegisterDestroyListener(IDestroyListener l) {
		mDestroyListenerList.remove(l);
	}

	private void setActivatedDefault() {
		mMediaDemandBtn.setActivated(false);
		mElectronicDictionaryBtn.setActivated(false);
		mIPCallBtn.setActivated(false);
		mUserHelp.setActivated(false);
		mRecordPracticeBtn.setActivated(false);
	}
}
