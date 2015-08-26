
package com.multimedia.room.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.R;

public class ExamReadyFragment extends BaseFragment {

    public static ExamReadyFragment newInstance() {
        ExamReadyFragment fragment = new ExamReadyFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.exam_ready_layout, null);
        CommandManager.destorySat();
        return rootView;
    }

    @Override
	public void onDetach() {
		super.onDetach();
	}
}
