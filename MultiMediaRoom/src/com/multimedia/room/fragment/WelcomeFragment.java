
package com.multimedia.room.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.R;

public class WelcomeFragment extends BaseFragment {

    public static WelcomeFragment newInstance() {
        WelcomeFragment fragment = new WelcomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.welcome_layout, null);
        return rootView;
    }
    @Override
	public void onDetach() {
		super.onDetach();
	}
}
