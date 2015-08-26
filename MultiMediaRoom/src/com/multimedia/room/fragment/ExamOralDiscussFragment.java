
package com.multimedia.room.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.ISwitch;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.R;
import com.multimedia.room.SwitchManager;

public class ExamOralDiscussFragment extends BaseFragment implements ISwitch{
    private TextView mGroupTitleView, mGroupNumberView, mGroupMemberView;
    private static MediaMessage mCurrentMessage;
    private String[] mMembers;
    private String mGroupId;
    private View mRootView;
    private SwitchManager mSwitchManager;
    private static final String TAG = "ExamOralDiscussFragment";

    public static ExamOralDiscussFragment newInstance(MediaMessage message) {
        ExamOralDiscussFragment fragment = new ExamOralDiscussFragment();
        mCurrentMessage = message;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.group_layout, null);
        mGroupTitleView = (TextView) mRootView.findViewById(R.id.group_title);
        mGroupNumberView = (TextView) mRootView.findViewById(R.id.group_number);
        mGroupMemberView = (TextView) mRootView.findViewById(R.id.group_member);
        mMembers = mCurrentMessage.getParams().split(",");
        mGroupId = mCurrentMessage.getGroup();
        refresh(mMembers);
        mSwitchManager = SwitchManager.getInstance();
        mSwitchManager.registerExamSwitch(this);
        Log.d(TAG, "onCreateView()");
        return mRootView;
    }

    private void refresh(String[] members) {
        if (members == null)
            return;
        String title = members.length + getResources().getString(R.string.group_title);
        mGroupTitleView.setText(title);
        String No = getResources().getString(R.string.group_order) + " " + mGroupId;
        mGroupNumberView.setText(No);
        StringBuilder sb = new StringBuilder();
        for (String mb : members) {
            sb.append(mb).append(" ");
        }
        Log.d("group ", sb.toString());
        String member = getResources().getString(R.string.group_member) + " " + sb.toString();
        mGroupMemberView.setText(member);
        CommandManager.joinGroup(mGroupId);

    }


    @Override
    public void switchOn() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void switchOff() {
        // TODO Auto-generated method stub
        
    }

    @Override
	public void onDetach() {
		super.onDetach();
	}
}
