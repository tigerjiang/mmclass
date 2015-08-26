
package com.multimedia.room.fragment;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.CommonUtil;
import com.multimedia.room.GroupInfo;
import com.multimedia.room.ISwitch;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.R;
import com.multimedia.room.SwitchManager;

public class TeachTranslateFragment extends BaseFragment implements ISwitch, OnCheckedChangeListener {
    private static final String TAG = "TeachTranslateFragment";
    private RadioButton mCheckA, mCheckB, mCheckC, mCheckD;
    private RadioGroup mGroup;
    private Map<String, String> mTranslatorMap = new HashMap<String, String>();
    private String[] mTranslatorName;
    private static MediaMessage mCurrentMessage;
    private String mSelectTranslatorName;
    private TextView mTranslatorView,mTranslateOverView;
    private SwitchManager mSwitchManager;
    private static String mGroupAddr;


    public static TeachTranslateFragment newInstance(MediaMessage message) {
        TeachTranslateFragment fragment = new TeachTranslateFragment();
        mCurrentMessage = message;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View rootView = inflater.inflate(R.layout.teach_translate_layout, null);
        mTranslatorView = (TextView) rootView.findViewById(R.id.translate_alert);
        mTranslateOverView = (TextView) rootView.findViewById(R.id.translate_over);
        mTranslateOverView.setVisibility(View.GONE);
        mGroup = (RadioGroup) rootView.findViewById(R.id.answer_group);
        mGroup.setOnCheckedChangeListener(this);
        mCheckA = (RadioButton) rootView.findViewById(R.id.check_a);
        mCheckB = (RadioButton) rootView.findViewById(R.id.check_b);
        mCheckC = (RadioButton) rootView.findViewById(R.id.check_c);
        mCheckD = (RadioButton) rootView.findViewById(R.id.check_d);
        parserTranslatorInfo(mCurrentMessage.getParams());
        mSwitchManager = SwitchManager.getInstance();
        mSwitchManager.registerTranslateSwitch(this);
        return rootView;
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart()");
        super.onStart();
        if (isAdded()) {
            if (mTranslatorMap.containsKey(CommonUtil.getSeatNo())) {
                CommandManager.openMic();
                mTranslatorView.setText(R.string.begin_translation);
                mGroupAddr = mTranslatorMap.get(CommonUtil.getSeatNo());
                CommandManager.setTranslate(mGroupAddr);
                mGroup.setVisibility(View.GONE);
            } else {
                CommandManager.closeMic();
                mTranslatorView.setText(R.string.select_translator);
                mGroup.setVisibility(View.VISIBLE);
                updateTranslate();
            }
        }
    }

    private void parserTranslatorInfo(String info) {
        String[] group = info.split(",");
        Log.d("translate", info);
        mTranslatorName = new String[group.length];
        for (int i = 0; i < group.length; i++) {
            String translator = group[i];
            ((RadioButton) mGroup.getChildAt(i)).setText(translator);
            ((RadioButton) mGroup.getChildAt(i)).setVisibility(View.VISIBLE);
            mTranslatorName[i] = translator;
            mTranslatorMap.put(translator, GroupInfo.getGroupId("g"+GroupInfo.sAddressMap.get(translator)));
        }
    }

    private void updateTranslate() {
        for (int i = 0; i < mGroup.getChildCount(); i++) {
            ((RadioButton) mGroup.getChildAt(i)).setVisibility(View.GONE);
        }
        for (int i = 0; i < mTranslatorName.length; i++) {
            String translator = mTranslatorName[i];
            ((RadioButton) mGroup.getChildAt(i)).setText(translator);
            ((RadioButton) mGroup.getChildAt(i)).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void switchOn() {

    }

    @Override
    public void switchOff() {
        CommandManager.closeMic();
        CommandManager.leaveGroup(mGroupAddr);
//        mTranslateOverView.setVisibility(View.VISIBLE);
        mGroup.setVisibility(View.GONE);
        mTranslatorView.setText(R.string.end_translate);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkId) {
        switch (checkId) {
            case R.id.check_a:
                mSelectTranslatorName = mTranslatorName[0];
                break;
            case R.id.check_b:
                mSelectTranslatorName = mTranslatorName[1];
                break;
            case R.id.check_c:
                mSelectTranslatorName = mTranslatorName[2];
                break;
            case R.id.check_d:
                mSelectTranslatorName = mTranslatorName[3];
                break;
        }
        CommandManager.leaveGroup(mGroupAddr);
        String receiveTranslate = getResources().getString(
                R.string.receive_translation)
                + ":" + mSelectTranslatorName;
        mTranslatorView.setText(receiveTranslate);
        CommandManager.setTranslate(mTranslatorMap
                .get(mSelectTranslatorName));
        CommandManager.closeMic();
    }
    @Override
	public void onDetach() {
		super.onDetach();
	}
}
