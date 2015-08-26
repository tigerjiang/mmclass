
package com.multimedia.room.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.R;

public class SelfIPPhoneFragment extends BaseFragment implements OnClickListener, OnItemClickListener,
        OnItemSelectedListener {
    private Button mDialButton;
    private Button mHangButton;
    private TextView mPhoneText;
    private GridView mCharacterGridView;
    private GridView mDigitalGridView;
    private String mCharacterPrefix;
    private String mDigitalSuffix;
    private String mPhoneNumber;
    private static String[] mCharacter = {
            "A", "B", "C", "D", "E", "F", "H", "I", "J", "K", "L"
    };
    private static String[] mDitigal = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16"
    };
    private PhoneAdapter mCharacterAdapter, mDigitalAdapter;

    public static SelfIPPhoneFragment newInstance() {
        SelfIPPhoneFragment fragment = new SelfIPPhoneFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.self_pone_layout, null);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {
        mDialButton = (Button) rootView.findViewById(R.id.dial_button);
        mHangButton = (Button) rootView.findViewById(R.id.hang_button);
        mPhoneText = (TextView) rootView.findViewById(R.id.phone_number);
        mDialButton.setOnClickListener(this);
        mHangButton.setOnClickListener(this);
        mCharacterGridView = (GridView) rootView.findViewById(R.id.character_layout);
        mDigitalGridView = (GridView) rootView.findViewById(R.id.digital_layout);
        mCharacterAdapter = new PhoneAdapter(mCharacter, getActivity());
        mDigitalAdapter = new PhoneAdapter(mDitigal, getActivity());
        mCharacterGridView.setAdapter(mCharacterAdapter);
        mDigitalGridView.setAdapter(mDigitalAdapter);
        mCharacterGridView.setOnItemSelectedListener(this);
        mDigitalGridView.setOnItemSelectedListener(this);
        mCharacterGridView.setOnItemClickListener(this);
        mDigitalGridView.setOnItemClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
        switch (arg0.getId()) {

            case R.id.character_layout:
                mCharacterPrefix = mCharacter[position];
                break;
            case R.id.digital_layout:
                mDigitalSuffix = mDitigal[position];
                break;
        }
        mPhoneNumber = mCharacterPrefix + mDigitalSuffix;
        Log.d("phone", mPhoneNumber);
        mPhoneText.setText(mPhoneNumber);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
        switch (arg0.getId()) {

            case R.id.character_layout:
                mCharacterPrefix = mCharacter[position];
                break;
            case R.id.digital_layout:
                mDigitalSuffix = mDitigal[position];
                break;
        }
        mPhoneNumber = mCharacterPrefix  + mDigitalSuffix;
        Log.d("phone", mPhoneNumber);
        mPhoneText.setText(mPhoneNumber);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dial_button:
                CommandManager.sendIPcallMessage(mPhoneNumber);

                break;
            case R.id.hang_button:
                // TODO ... hang up...
                break;
        }

    }

    static class PhoneAdapter extends BaseAdapter {
        private String[] mData;
        private Context mContext;

        public PhoneAdapter(String[] data, Context context) {
            mContext = context;
            mData = data;

        }

        @Override
        public int getCount() {

            return mData.length;
        }

        @Override
        public Object getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.phone_item, null);
            ((TextView) convertView).setText(mData[position]);
            return convertView;
        }

    }
    @Override
	public void onDetach() {
		super.onDetach();
	}
}
