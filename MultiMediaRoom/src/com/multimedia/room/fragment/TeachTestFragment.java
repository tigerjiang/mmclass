
package com.multimedia.room.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.ISwitch;
import com.multimedia.room.MediaMessage;
import com.multimedia.room.R;
import com.multimedia.room.SwitchManager;

public class TeachTestFragment extends BaseFragment implements OnCheckedChangeListener, ISwitch {
    private RadioButton mCheckA, mCheckB, mCheckC, mCheckD;
    private RadioGroup mGroup;
    private Button mSubmitButton;
    private String mAnswer;
    private TextView mTestOverView;
    private static MediaMessage mCurrentMessage;
    private SwitchManager mSwitchManager;

    public static TeachTestFragment newInstance(MediaMessage message) {
        TeachTestFragment fragment = new TeachTestFragment();
        mCurrentMessage = message;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teach_test_layout, null);
        initView(rootView);
        mSwitchManager = SwitchManager.getInstance();
        mSwitchManager.registerTestSwitch(this);
        CommandManager.closeMic();
        return rootView;
    }

    private void initView(View rootView) {
        mGroup = (RadioGroup) rootView.findViewById(R.id.answer_group);
        mGroup.setOnCheckedChangeListener(this);
        mCheckA = (RadioButton) rootView.findViewById(R.id.check_a);
        mCheckB = (RadioButton) rootView.findViewById(R.id.check_b);
        mCheckC = (RadioButton) rootView.findViewById(R.id.check_c);
        mCheckD = (RadioButton) rootView.findViewById(R.id.check_d);
        mSubmitButton = (Button) rootView.findViewById(R.id.submit);
        mTestOverView = (TextView) rootView.findViewById(R.id.test_over);
        mTestOverView.setVisibility(View.GONE);
        mSubmitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (TextUtils.isEmpty(mAnswer)) {
                   
                    Toast.makeText(getActivity(), R.string.no_selected_answer, Toast.LENGTH_LONG)
                            .show();
                }else {
                    CommandManager.sendTestMessage(mAnswer);
                }

            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkId) {
        switch (checkId) {
            case R.id.check_a:
                mAnswer = "A";
                break;
            case R.id.check_b:
                mAnswer = "B";
                break;
            case R.id.check_c:
                mAnswer = "C";
                break;
            case R.id.check_d:
                mAnswer = "D";
                break;
        }
    }

    @Override
    public void switchOn() {
        // TODO Auto-generated method stub

    }

    @Override
    public void switchOff() {
        if (mCurrentMessage.getParams().equals(mAnswer)) {
            mTestOverView.setVisibility(View.VISIBLE);
            mTestOverView.setText(R.string.answer_right);
        } else {
            String answer = getString(R.string.answer_wrong) + mCurrentMessage.getParams();
            mTestOverView.setText(answer);
            mTestOverView.setVisibility(View.VISIBLE);
        }
//        CommandManager.sendTestMessage(mAnswer);

    }
    @Override
	public void onDetach() {
		super.onDetach();
	}
}
