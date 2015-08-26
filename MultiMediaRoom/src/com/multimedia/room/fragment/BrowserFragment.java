
package com.multimedia.room.fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.multimedia.room.BaseFragment;
import com.multimedia.room.R;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class BrowserFragment extends BaseFragment {

    public static BrowserFragment newInstance() {
        BrowserFragment fragment = new BrowserFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.browser_layout, null);
        Button mButton = (Button) rootView.findViewById(R.id.start_browser);
        mButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				killOtherProcess();
				Intent intent = new Intent();
				intent.setClassName("cn.mozilla.firefox",
						"cn.mozilla.firefox.App");
				startActivity(intent);
				
			}
		});
        return rootView;
    }
    @Override
	public void onDetach() {
		super.onDetach();
	}
    
    public void killOtherProcess() {
		try {
			ActivityManager activityManager = (ActivityManager) this.getActivity()
					.getSystemService(Context.ACTIVITY_SERVICE);
			Method forceStopPackage;
			forceStopPackage = activityManager.getClass().getDeclaredMethod(
					"forceStopPackage", String.class);

			forceStopPackage.setAccessible(true);

			forceStopPackage
					.invoke(activityManager, "cn.mozilla.firefox");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
