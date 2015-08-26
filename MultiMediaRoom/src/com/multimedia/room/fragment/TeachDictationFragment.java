
package com.multimedia.room.fragment;
import java.io.ByteArrayOutputStream;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.CommandManager;
import com.multimedia.room.ISwitch;
import com.multimedia.room.R;
import com.multimedia.room.SwitchManager;

public class TeachDictationFragment extends BaseFragment implements TextWatcher, ISwitch {
    private String mDictationResult= "";
    private EditText mDictationView;
    private TextView mOverView;
    private SwitchManager mSwitchManager;

    public static TeachDictationFragment newInstance() {
        TeachDictationFragment fragment = new TeachDictationFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teach_dictation_layout, null);
        mDictationView = (EditText) rootView.findViewById(R.id.dictation_context);
        mDictationView.addTextChangedListener(this);
        mOverView = (TextView) rootView.findViewById(R.id.dictation_over);
        mOverView.setVisibility(View.GONE);
        mSwitchManager = SwitchManager.getInstance();
        mSwitchManager.registerDictationSwitch(this);
        return rootView;
    }

    @Override
    public void afterTextChanged(Editable s) {
        mDictationResult = s.toString();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSwitchManager.unregisterDictationSwitch(this);

    }

    @Override
    public void switchOn() {
        mDictationView.setEnabled(true);
        CommandManager.openMic();
        mOverView.setVisibility(View.GONE);
    }

    @Override
    public void switchOff() {
        // stop dictation
        CommandManager.closeMic();
        mOverView.setVisibility(View.VISIBLE);
        mDictationView.setEnabled(false);
//        CommonUtil.showDialog(this.getActivity().getApplicationContext(), R.string.dictation,
//                R.string.end_dictation);
       
        CommandManager.sendDictationMessage(encode(mDictationResult));
 
    }
    @Override
	public void onDetach() {
		super.onDetach();
	}

    	/*
	 * 16进制数字字符集
	 */
	private static String hexString = "0123456789ABCDEF";

	/*
	 * 将字符串编码成16进制数字,适用于所有字符（包括中文）
	 */
	public static String encode(String str) {
		// 根据默认编码获取字节数组
		byte[] bytes = str.getBytes();
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		// 将字节数组中每个字节拆解成2位16进制整数
		for (int i = 0; i < bytes.length; i++) {
			sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
			sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
		}
		return sb.toString();
	}

	/*
	 * 将16进制数字解码成字符串,适用于所有字符（包括中文）
	 */
	public static String decode(String bytes) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(
				bytes.length() / 2);
		// 将每2位16进制整数组装成一个字节
		for (int i = 0; i < bytes.length(); i += 2)
			baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
					.indexOf(bytes.charAt(i + 1))));
		return new String(baos.toByteArray());
	}
}
