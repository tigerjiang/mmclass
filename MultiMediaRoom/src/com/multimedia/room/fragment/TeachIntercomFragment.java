
package com.multimedia.room.fragment;

import android.os.Bundle;
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
import android.util.Log;

public class TeachIntercomFragment extends BaseFragment implements ICleanListener {
    private static final String TAG = "MMClass/TeachIntercomFragment";
    private TextView mStatusView;
    private static MediaMessage mCurrentMessage;
    private static String mGroupAddr ;
    public static TeachIntercomFragment newInstance(MediaMessage message) {
	mCurrentMessage = message;
        TeachIntercomFragment fragment = new TeachIntercomFragment();
        return fragment;
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teach_intercom_layout, null);
        mStatusView = (TextView) rootView.findViewById(R.id.intercom_standard_status);
        if(mCurrentMessage.isMe()){
            CommandManager.startSat();
            CommandManager.openMic();
            mStatusView.setText(R.string.co_talk_teacher);
            CommandManager.leaveGroup(mGroupAddr);
            mGroupAddr = GroupInfo.getGroupId(mCurrentMessage.getGroup());
            CommandManager.setIntercom(mGroupAddr);
        }else{
            CommandManager.closeMic();
            mStatusView.setText(R.string.receive_demonstrate);
        }   
        if(this.getActivity() instanceof BaseActivity){
        	((BaseActivity)this.getActivity()).registerCleanListener(this);
        }
        return rootView;
    }

    @Override
    public void clean() {
        CommandManager.leaveGroup(mGroupAddr);
        CommandManager.joinGroup("230.1.2.3");
	CommandManager.closeMic();
        mStatusView.setText(R.string.receive_teacher_demonstrate);
    }

    @Override
    public void allClean() {
        CommandManager.leaveGroup(mGroupAddr);
        CommandManager.joinGroup("230.1.2.3");
	CommandManager.closeMic();
        mStatusView.setText(R.string.receive_teacher_demonstrate);
    }
    @Override
    public void onDetach() {
	super.onDetach();
	((BaseActivity) this.getActivity()).unregisterCleanListener(this);
    }

    @Override
    public void onDestroyView() {
	super.onDestroyView();
    }    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
