
package com.multimedia.room.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.multimedia.room.CommandManager;
import com.multimedia.room.GroupInfo;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.R;

public class TeachMonitorFragment extends Fragment {
    private static MediaMessage mCurrentMessage;
    private TextView mMonitorView;

    public static TeachMonitorFragment newInstance(MediaMessage message) {
        TeachMonitorFragment fragment = new TeachMonitorFragment();
        mCurrentMessage = message;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teach_monitor_layout, null);
        mMonitorView = (TextView) rootView.findViewById(R.id.monitor_alert);
        if (mCurrentMessage.isMe()) {
            CommandManager.setMonitor(GroupInfo.getGroupId(mCurrentMessage.getGroup()));
        } else {
            CommandManager.setMonitor(GroupInfo.getGroupId(mCurrentMessage.getGroup()));
        }
        return rootView;
    }
}
