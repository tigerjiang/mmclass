
package com.multimedia.room.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;

import com.multimedia.room.BaseActivity;
import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.GroupInfo;
import com.multimedia.room.IUpdateGroupMembersListenner;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.R;

public class GroupDiscussFragment extends BaseFragment implements  IUpdateGroupMembersListenner{
    private TextView mGroupTitleView, mGroupNumberView, mGroupMemberView;
    private MediaMessage mCurrentMessage;
    private String[] mMembers;
    private String mGroupId;
    private View mRootView;
    private TextView mTeacherView;
    private static final String TAG = "GroupDiscussFragment";

    public static GroupDiscussFragment newInstance() {
        GroupDiscussFragment fragment = new GroupDiscussFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.group_layout, null);
        mGroupTitleView = (TextView) mRootView.findViewById(R.id.group_title);
        mGroupNumberView = (TextView) mRootView.findViewById(R.id.group_number);
        mGroupMemberView = (TextView) mRootView.findViewById(R.id.group_member);
        mTeacherView = (TextView) mRootView.findViewById(R.id.teacher);
        mTeacherView.setVisibility(View.GONE);
        mMembers = mCurrentMessage.getParams().split(",");
        mGroupId = mCurrentMessage.getGroup();
        refresh(mMembers);
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
        mTeacherView.setVisibility(View.GONE);
        StringBuilder sb = new StringBuilder();
        for (String mb : members) {
        	if(mb.equals("teacher")){
        		mTeacherView.setVisibility(View.VISIBLE);
        		continue;
        	}
            sb.append(mb).append(" ");
        }
        Log.d("group ", sb.toString());
        String member = getResources().getString(R.string.group_member) + " " + sb.toString();
        mGroupMemberView.setText(member);
        CommandManager.leaveGroup("");
        CommandManager.joinGroup(GroupInfo.getGroupId("g"+mGroupId));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = this.getArguments();
        if (args != null) {
            mCurrentMessage = (MediaMessage) args.getSerializable("message");
            Log.d("receiver", mCurrentMessage.toString());
        }
        Log.d(TAG, "onCreate");
        implementsIupdate();
    }

    private void implementsIupdate() {
        if (this.getActivity() instanceof BaseActivity) {
            ((BaseActivity) this.getActivity()).registerUpdateGroupMembersListenner(this);
        }
    }

    @Override
    public void joinGroup(String newGroup, String member) {
        Log.d("group", "join group " + newGroup + " " + member);
        if (newGroup != null && newGroup.equals(mGroupId)) {
            List<String> membersList = new ArrayList<String>(Arrays.asList(mMembers));
            if (!membersList.contains(member)) {
                membersList.add(member);
                mMembers = membersList.toArray(new String[membersList.size()]);
                refresh(mMembers);
            }
        }

    }

    @Override
    public void leaveGroup(String member) {
        Log.d("group", "leave group " +" " + member);
        List<String> membersList = new ArrayList<String>(Arrays.asList(mMembers));
        if (membersList.contains(member)) {
            membersList.remove(member);
            mMembers = membersList.toArray(new String[membersList.size()]);
            refresh(mMembers);
        }
    }

    @Override
    public void joinMeGroup(MediaMessage message) {
        Log.d("group", "join me");
        mCurrentMessage = message;
        mMembers = mCurrentMessage.getParams().split(",");
        mGroupId = mCurrentMessage.getGroup();
        refresh(mMembers);

    }
    @Override
	public void onDetach() {
		super.onDetach();
	}

}
