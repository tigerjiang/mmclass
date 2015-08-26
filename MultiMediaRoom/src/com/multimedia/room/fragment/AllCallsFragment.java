package com.multimedia.room.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.reflect.Field;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.R;

public class AllCallsFragment extends BaseFragment {

	public static AllCallsFragment newInstance() {
		AllCallsFragment fragment = new AllCallsFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.all_call_layout, null);
		return rootView;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}
