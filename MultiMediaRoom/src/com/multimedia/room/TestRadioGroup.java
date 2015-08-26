package com.multimedia.room;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TestRadioGroup extends Activity {

	private LinearLayout view;
	private Map<Integer, String> mAnswerMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_gropu);
		view = (LinearLayout) findViewById(R.id.view);
		for (int i = 0; i < 100; i++) {
			View child = initView(i);
			view.addView(child);
		}
		mAnswerMap = new HashMap<Integer, String>();
		// for (Integer i : mAnswerMap.keySet()) {
		// Log.d("answer", mAnswerMap.get(i));
		// }
	}

	private LinearLayout initView(final int id) {
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		LinearLayout childView = (LinearLayout) inflater.inflate(
				R.layout.answer_item, null);
		childView.setId(id);
		TextView answerNo = (TextView) childView.findViewById(R.id.answer_no);
		RadioGroup group = (RadioGroup) childView
				.findViewById(R.id.answer_group);
		answerNo.setText(String.valueOf(id));
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				if (arg1 == R.id.item_a) {
					mAnswerMap.put(id, "A");
				} else if (arg1 == R.id.item_b) {
					mAnswerMap.put(id, "B");
				} else if (arg1 == R.id.item_c) {
					mAnswerMap.put(id, "C");
				} else if (arg1 == R.id.item_d) {
					mAnswerMap.put(id, "D");
				}
				Log.d("answer", "answer" + id + " " + mAnswerMap.get(id));
			}
		});
		return childView;
	}

}
