
package com.multimedia.room.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.R;

public class SelfHelpFragment extends BaseFragment {
    public static SelfHelpFragment newInstance() {
        SelfHelpFragment fragment = new SelfHelpFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.help_layut, null);
        return rootView;
    }
    @Override
	public void onDetach() {
		super.onDetach();
	}
}
