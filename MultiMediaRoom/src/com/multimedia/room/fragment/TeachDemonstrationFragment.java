package com.multimedia.room.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.multimedia.room.BaseActivity;
import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.GroupInfo;
import com.multimedia.room.ICleanListener;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.R;

public class TeachDemonstrationFragment extends BaseFragment implements
		ICleanListener {
	private TextView mStatusView;
	private static MediaMessage mCurrentMessage;
	public static boolean mIsDemonstration = false;
	private static String mGroupAddr;
	public static TeachDemonstrationFragment newInstance(MediaMessage message) {
		TeachDemonstrationFragment fragment = new TeachDemonstrationFragment();
		mCurrentMessage = message;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.teach_intercom_layout, null);
		mStatusView = (TextView) rootView
				.findViewById(R.id.intercom_standard_status);
		Log.d("TeachDemonstrationFragment", mCurrentMessage.toString());
		if (mCurrentMessage.isMe()) {
			mStatusView.setText(R.string.send_demonstrate);
			mIsDemonstration = true;
			mGroupAddr = GroupInfo.getGroupId(mCurrentMessage.getGroup());
		}
		if (mIsDemonstration) {
			CommandManager.openMic();
			mStatusView.setText(R.string.send_demonstrate);
		} else {
			CommandManager.closeMic();
			mStatusView.setText(R.string.receive_demonstrate);
		}
		if (this.getActivity() instanceof BaseActivity) {
			((BaseActivity) this.getActivity()).registerCleanListener(this);
		}
		return rootView;
	}

	@Override
	public void clean() {
		CommandManager.closeMic();
		mIsDemonstration = false;
		mStatusView.setText(R.string.receive_demonstrate);
	}

	@Override
	public void allClean() {
		CommandManager.closeMic();
		mStatusView.setText(R.string.receive_teacher_demonstrate);
		mIsDemonstration = false;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		((BaseActivity) this.getActivity()).unregisterCleanListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
//		mIsDemonstration = false;
	}
}
