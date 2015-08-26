
package com.multimedia.room.fragment;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.KeyEvent;
import android.widget.Toast;
import android.util.Log;
import com.multimedia.room.Command;
import com.multimedia.room.CommandManager;
import com.multimedia.room.CommonUtil;
import com.multimedia.room.ISwitch;
import com.multimedia.room.R;
import com.multimedia.room.SwitchManager;

public class HeadBarView extends RelativeLayout implements OnClickListener, ISwitch {
    private ImageView mDownVolumnBtn;
    private ImageView mUpVolumnBtn;
    private ImageView mHandUpBtn,mAboutBtn;
    private AudioManager mAudioManager;
    private Context mContext;
    private SwitchManager mSwitchManager;
    private TextView mTitleView,mModelView;
    private static int mDefault = 18;
    private double mRate = 31.0/100; 
    private double mSound = 0.0;
    private int mGlobalVolume = 6;
    private int mMax;
    private static boolean sIsHandUp = false;
    public HeadBarView(Context context) {
        super(context);
        initView(context);
    }

    public HeadBarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public HeadBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mSwitchManager = SwitchManager.getInstance();
        mSwitchManager.registerHandUpSwitch(this);
        inflate(context, R.layout.headbar_layout, this);
        mTitleView = (TextView) findViewById(R.id.title);
        mDownVolumnBtn = (ImageView) findViewById(R.id.down_volumn);
        mUpVolumnBtn = (ImageView) findViewById(R.id.up_volumn);
        mHandUpBtn = (ImageView) findViewById(R.id.hand_up);
        mAboutBtn = (ImageView) findViewById(R.id.about_device);
        mModelView = (TextView) findViewById(R.id.left_model);
        mAboutBtn.setOnClickListener(this);
        mDownVolumnBtn.setOnClickListener(this);
        mUpVolumnBtn.setOnClickListener(this);
        mHandUpBtn.setOnClickListener(this);
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mMax = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mMax / 10)*8, -2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up_volumn:
 //               mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
 //                       AudioManager.ADJUST_LOWER,
 //                      AudioManager.FX_FOCUS_NAVIGATION_UP);
               if (mGlobalVolume >= 12) {
					mGlobalVolume = 12;
				} else {
					mGlobalVolume++;
				}
                 CommandManager.setSound(mDefault +mGlobalVolume);
                 mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mMax / 10)*10-((12-mGlobalVolume)*5), -2);
                break;
            case R.id.down_volumn:
//                mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
//                        AudioManager.ADJUST_RAISE,
//                        AudioManager.FX_FOCUS_NAVIGATION_UP);
                if (mGlobalVolume <= 0) {
					mGlobalVolume = 0;
				} else {
					mGlobalVolume--;
				}
                CommandManager.setSound(mDefault+mGlobalVolume);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mMax / 10)*10-((12-mGlobalVolume)*5), -2);
                break;
            case R.id.hand_up:
                CommandManager.sendSetHandUpMessage();
                Toast.makeText(mContext, "hand up", Toast.LENGTH_SHORT).show();
                break;
            case R.id.about_device:
                View infoView = LayoutInflater.from(mContext).inflate(R.layout.update_main, null);
                CommonUtil.showCustomDialog(mContext, 600,infoView, "硬件信息", null);
                break;

        }
    }

       @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
    	 int keyCode = event.getKeyCode();
         int action = event.getAction();
         if (keyCode == KeyEvent.KEYCODE_F6) {
             if (action == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                       mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                       AudioManager.ADJUST_RAISE,
                       AudioManager.FX_FOCUS_NAVIGATION_UP);
             if (mGlobalVolume >= 12) {
					mGlobalVolume = 12;
				} else {
					mGlobalVolume++;
				}
                 CommandManager.setSound(mDefault +mGlobalVolume);
                 mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mMax / 10)*10-((12-mGlobalVolume)*5), -2);
             }
             return true;
         }else if (keyCode == KeyEvent.KEYCODE_F5) {
             if (action == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            	  mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                          AudioManager.ADJUST_LOWER,
                          AudioManager.FX_FOCUS_NAVIGATION_UP);
             if (mGlobalVolume <= 0) {
					mGlobalVolume = 0;
				} else {
					mGlobalVolume--;
				}
                CommandManager.setSound(mDefault+mGlobalVolume);
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (mMax / 10)*10-((12-mGlobalVolume)*5), -2);
             }
             return true;
         }else if (keyCode == KeyEvent.KEYCODE_F7) {
			if (action == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
				if (sIsHandUp) {
					CommandManager.sendCancelHandUpMessage();
					sIsHandUp = false;
				} else {
					CommandManager.sendSetHandUpMessage();
					sIsHandUp = true;
				}
			}
             return true;
		} else if (keyCode == KeyEvent.KEYCODE_ENTER) {
			// Dones't care the enter key event
			return true;
		}
    	return super.dispatchKeyEvent(event);
    }
    
    public void setTitle(String title){
        mTitleView.setText(title);
    }
    
    public void setModel(int resId){
    	mModelView.setText(resId);
    }
    @Override
    public void switchOn() {
        mHandUpBtn.setEnabled(true);

    }

    @Override
    public void switchOff() {
        mHandUpBtn.setEnabled(false);

    }

/*   @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	 if (keyCode == KeyEvent.KEYCODE_F1) {
             if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
              mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                       AudioManager.FX_FOCUS_NAVIGATION_UP);

            	 mDefault +=mRate;
                 CommandManager.setSound(mDefault);
             }
             return true;
         }else if (keyCode == KeyEvent.KEYCODE_F2) {
             if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            	  mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                          AudioManager.ADJUST_LOWER,
                          AudioManager.FX_FOCUS_NAVIGATION_UP); 
				mDefault -= mRate;
                 CommandManager.setSound(mDefault);
             }
             return true;
         }else if (keyCode == KeyEvent.KEYCODE_F3) {
             if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            	 CommandManager.sendSetHandUpMessage();
             }
             return true;
         }
    	return super.onKeyUp(keyCode, event);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_F1) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {

              mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE,
                       AudioManager.FX_FOCUS_NAVIGATION_UP);
           	 mDefault +=mRate;
                CommandManager.setSound(mDefault);
            }
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_F2) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            	  mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                          AudioManager.ADJUST_LOWER,
                          AudioManager.FX_FOCUS_NAVIGATION_UP);
				mDefault -= mRate;
                CommandManager.setSound(mDefault);
            }
            return true;
        }else if (keyCode == KeyEvent.KEYCODE_F3) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
           	 CommandManager.sendSetHandUpMessage();
            }
            return true;
        }
    	return super.onKeyDown(keyCode, event);
    }
*/
}
