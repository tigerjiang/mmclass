package com.multimedia.room;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.os.PowerManager;
import android.os.Build;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.mmclass.libsiren.EventHandler;
import com.mmclass.libsiren.LibSiren;
import com.mmclass.libsiren.LibSirenException;
import com.mmclass.libsiren.WeakHandler;
import com.multimedia.room.fragment.AllCallsFragment;
import com.multimedia.room.fragment.ExamOralDiscussFragment;
import com.multimedia.room.fragment.ExamOralFragment;
import com.multimedia.room.fragment.ExamReadyFragment;
import com.multimedia.room.fragment.ExamStandardFragment;
import com.multimedia.room.fragment.GroupDiscussFragment;
import com.multimedia.room.fragment.HeadBarView;
import com.multimedia.room.fragment.SelfStudyFragment;
import com.multimedia.room.fragment.TeachDemonstrationFragment;
import com.multimedia.room.fragment.TeachDictationFragment;
import com.multimedia.room.fragment.TeachIntercomFragment;
import com.multimedia.room.fragment.TeachTestFragment;
import com.multimedia.room.fragment.TeachTranslateFragment;
import com.multimedia.room.fragment.WelcomeFragment;
import com.multimedia.room.upgrade.Constants;
import com.multimedia.room.upgrade.DownloadTask;
import com.multimedia.room.upgrade.IDownloadListener;
import com.multimedia.room.upgrade.RebootTask;
import com.multimedia.room.upgrade.Session;
import android.view.KeyEvent;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;


public class BaseActivity extends Activity{
	protected static final String PKG = "com.multimedia.room";
	private static final String TAG = "BaseActivity";
	private static LibSiren mLibSiren;
	protected static EventHandler em = EventHandler.getInstance();
	protected static Context mBaseContext;
	protected static String mSeatNo;
	private static CommonUtil mUtil;
	static String seatPrefix;
	static String seatSuffix;

	private static boolean mIsInitialized;
	protected static String mCurrentMOde;

	private static SwitchManager mSwitchManager;
	private static HeadBarView mHeadBarView;

	private boolean mIsInternetConnected = false;
	private ConnectivityManager mConnectivityManager;
	private final NetworkReceiver mNetworkReceiver = new NetworkReceiver();
	private static boolean mNetworkReceiverRegistered = false;

	protected static Object sObjectLock = new Object();
	private MessageHandler mHandler;
	private EthernetManager mEthManager;
	private EthernetDevInfo mEthInfo;
	private long mOldTime, mNewTime;

	private static boolean sIsDownloaded = false;
	private DownloadTask mDownloadTask;

	private Handler mDownloadHandler;

	private Session mSession;
	private HandlerThread mThread = new HandlerThread("SystemUpgrade");

	private class MessageHandler extends Handler {
		public static final int UPDATE_NET_STATUS = 0;

		MessageHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case UPDATE_NET_STATUS:
				if (checkNetworkAvailable()) {
					if (mLibSiren != null) {
						mLibSiren.destroy();
					}
					synchronized (sObjectLock) {
						new Thread(new Runnable() {

							@Override
							public void run() {
								preInvokeNativeMethod();
							}
						}).start();
					}
					mIsInitialized = true;
				} else {
					mIsInitialized = false;
					return;
				}
				break;

			default:
				break;
			}
		}
	}

	// clean listener
	private static ArrayList<ICleanListener> mCleanerList = new ArrayList<ICleanListener>();
	private ISwitch mISwitch = new ISwitch() {

		@Override
		public void switchOn() {
			// hand_up on
		}

		@Override
		public void switchOff() {
			// hand_up off
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity_layout);
		mEthManager = (EthernetManager) getSystemService(Context.ETH_SERVICE);
		mEthInfo = mEthManager.getSavedEthConfig();
		mHandler = new MessageHandler(Looper.getMainLooper());
		mHeadBarView = (HeadBarView) findViewById(R.id.title_layout);
		registerNetworkReceiver();
		loadWelcomeFragment();
		init();
	}

	@Override
	protected void onStart() {
		super.onStart();

	}

	private void init() {
		initDownload();
		mSwitchManager = SwitchManager.getInstance();
		mSwitchManager.registerHandUpSwitch(mISwitch);
		mBaseContext = BaseActivity.this;
		mUtil = CommonUtil.getInstance(mBaseContext);
		mConnectivityManager = (ConnectivityManager) mBaseContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		// CommonUtil.reStoreValueIntoSharePreferences(CommonUtil.SEATNO, "A1");
		mSeatNo = CommonUtil.getSeatNo();
		CommandManager.SetSeatNo(mSeatNo);
		mHeadBarView.setTitle(mSeatNo);
		if (!CommonUtil.isConfigIP()) {
			handle_saveconf(CommonUtil.getIpPrefix());
			if (mHandler != null) {
				mHandler.removeMessages(MessageHandler.UPDATE_NET_STATUS);
				mHandler.sendEmptyMessageDelayed(
						MessageHandler.UPDATE_NET_STATUS, 2000);
			}
			CommonUtil.configIP(true);
		} else {
			if (checkNetworkAvailable()) {
				preInvokeNativeMethod();
			}
		}

	}

	private void preInvokeNativeMethod() {
		LibSiren.updateAddress();
		// parseMessage(content);
		Log.d(TAG, "LibSiren initialisation");
		try {

			mLibSiren = LibSiren.getInstance();

		} catch (LibSirenException e) {
			Log.d(TAG, "LibSiren initialisation failed");
			return;
		}
		try {
			// ToDo:return value
			mLibSiren.init(getBaseContext());
			try {
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (mLibSiren == null) {
				return;
			}
			CommandManager.SetLibSiren(mLibSiren);
			CommandManager.sendSetOnlineMessage();
			CommandManager.sendSetSyncMessage();
			loadFirstFragment();
			// instance the event handler
			em.addHandler(baseEventHandler);

		} catch (LibSirenException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	/**
	 * Handle libsiren asynchronous events
	 */
	private Handler baseEventHandler = new BaseJNIEventHandler(this);

	private class BaseJNIEventHandler extends WeakHandler<BaseActivity> {
		public BaseJNIEventHandler(BaseActivity owner) {
			super(owner);
		}

		@Override
		public void handleMessage(Message msg) {
			BaseActivity activity = getOwner();
			if (activity == null)
				return;
			// Do not handle events if we are leaving the TestJniActivity
			// ToDo:
			String data = msg.getData().getString("data");

			final MediaMessage message = CommonUtil.parseMessage(data);
			if (message == null) {
				return;
			}
			Log.d(TAG, "message " + message.toString());
			mCurrentMOde = message.getMode();
			if (!mCurrentMOde.equals(Command.Modes.MODE_GROUP)
					&& message.getReveiver().equals("teacher")) {
				return;
			}
			try {

				if (mCurrentMOde.equals(Command.Modes.MODE_TEACH)) {
					killOtherProcess();
					killPlayerProcess();
					mHeadBarView.setModel(R.string.teach_model);
					loadTeachingFragment(message);
				} else if (mCurrentMOde.equals(Command.Modes.MODE_EXAM)) {
					killOtherProcess();
					killPlayerProcess();
					mHeadBarView.setModel(R.string.exam_model);
					loadExamFragment(message);
				} else if (mCurrentMOde.equals(Command.Modes.MODE_GROUP)) {
					killOtherProcess();
					killPlayerProcess();
					mHeadBarView.setModel(R.string.group_model);
					loadGroupFragment(message);
				} else if (mCurrentMOde.equals(Command.Modes.MODE_SELF_STUDY)) {
					killOtherProcess();
					killPlayerProcess();
					mHeadBarView.setModel(R.string.study_model);
					loadStudyFragment(message);
				} else if (mCurrentMOde.equals(Command.Modes.MODE_GLOBAL)) {
					if (!message.getTpye().equals("cmd")) {
						// Nothing to do ..
						return;
					}
					if (message.getCommand().equals(
							Command.COMMANDS.COMMAND_GROUP_BROADCAST)) {
						if ("on".equals(message.getParams())) {
							CommandManager.sendRemoteVGAOut();
						} else {
							CommandManager.sendLocalVGAOut();
							return;
						}
					} else if (message.getCommand().equals(
							Command.COMMANDS.COMMAND_SETTING)) {
						Intent intent = new Intent();
						intent.setClassName("com.android.settings",
								"com.android.settings.Settings");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					} else if (message.getCommand().equals(
							Command.COMMANDS.COMMAND_UPGRADE)) {
						Log.d(TAG, "upgrade " + message.getParams());
						if (!sIsDownloaded) {
							if (!TextUtils.isEmpty(message.getParams())) {
								download(message.getParams());
								Log.d(TAG, "system update!");
							} else {
								Log.e(TAG, "system update error ,Url is null");
							}
						} else {
							Log.e(TAG, "no need upgrade!");
						}
					} else if (message.getCommand().equals(
							Command.COMMANDS.COMMAND_REBOOT)) {
						Log.d(TAG, "rebot!");
						PowerManager pManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
						pManager.reboot("reboot");
						/*
						 * String cmd = "su -c reboot"; try {
						 * Runtime.getRuntime().exec(cmd); } catch (Exception e)
						 * { e.printStackTrace();
						 * 
						 * }
						 */
					} else if (message.getCommand().equals(
							Command.COMMANDS.COMMAND_VERSION)) {
						Log.d(TAG, "version!");
						CommonUtil.showDialog(mBaseContext, "系统版本", "系统版本号为: "
								+ Build.DISPLAY);
					} else if (message.getCommand().equals(
							Command.COMMANDS.COMMAND_RECOVERY)) {
						RebootTask task = new RebootTask(mSession);
						mDownloadHandler.post(task);
					}
					// TODO ...
					else if (!message.getReveiver().equals("teacher")
							&& Command.COMMANDS.COMMAND_GLOBAL_ONLINE
									.equals(message.getCommand())) {
						CommandManager.sendSetOnlineMessage();

					}
					// TODO ...
					else if (!message.getReveiver().equals("teacher")
							&& Command.COMMANDS.COMMAND_CHANGE_IP
									.equals(message.getCommand())) {
						CommonUtil.setIpPrefix(message.getParams());
						handle_saveconf(message.getParams());
						if (mHandler != null) {
							mHandler.removeMessages(MessageHandler.UPDATE_NET_STATUS);
							mHandler.sendEmptyMessageDelayed(
									MessageHandler.UPDATE_NET_STATUS, 2000);
						}
						CommonUtil.configIP(true);
					}

					// Set seat
					else if (Command.COMMANDS.COMMAND_SET_SEAT.equals(message
							.getCommand())) {
						View dialogView = LayoutInflater.from(activity)
								.inflate(R.layout.dialog_seat, null);
						Spinner prefixSpinner = (Spinner) dialogView
								.findViewById(R.id.seat_prefix);
						Spinner suffixSpinner = (Spinner) dialogView
								.findViewById(R.id.seat_suffix);
						prefixSpinner.setAdapter(new ArrayAdapter<String>(
								activity, android.R.layout.simple_spinner_item,
								CommonUtil.sSeatPrefix));
						suffixSpinner.setAdapter(new ArrayAdapter<String>(
								activity, android.R.layout.simple_spinner_item,
								CommonUtil.sSeatsuffix));

						prefixSpinner
								.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

									@Override
									public void onItemSelected(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										seatPrefix = CommonUtil.sSeatPrefix[arg2];

									}

									@Override
									public void onNothingSelected(
											AdapterView<?> arg0) {
										// TODO nothing to do.

									}
								});
						suffixSpinner
								.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

									@Override
									public void onItemSelected(
											AdapterView<?> arg0, View arg1,
											int arg2, long arg3) {
										seatSuffix = CommonUtil.sSeatsuffix[arg2];
									}

									@Override
									public void onNothingSelected(
											AdapterView<?> arg0) {
										// TODO nothing to do.

									}
								});
						DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								String seatNo = seatPrefix + seatSuffix;
								if (!TextUtils.isEmpty(seatNo)) {
									mUtil.reStoreValueIntoSharePreferences(
											CommonUtil.SEATNO, seatNo);
									mSeatNo = seatNo;
									// CommandManager.sendSetSeatMessage();
									Log.d(TAG, "seat no " + mSeatNo);
									CommandManager.SetSeatNo(seatNo);
									mHeadBarView.setTitle(mSeatNo);
									CommandManager.sendSetSeatMessage();
									handle_saveconf(CommonUtil.getIpPrefix());
									if (mHandler != null) {
										mHandler.removeMessages(MessageHandler.UPDATE_NET_STATUS);
										mHandler.sendEmptyMessageDelayed(
												MessageHandler.UPDATE_NET_STATUS,
												2000);
									}

									CommonUtil.configIP(true);
								} else {
									Toast.makeText(mBaseContext,
											"Seat no is null!",
											Toast.LENGTH_SHORT).show();

								}

							}
						};
						CommonUtil.showCustomDialog(
								mBaseContext,
								dialogView,
								mBaseContext.getResources().getString(
										R.string.set_seat), clickListener);

					}
					// TODO ...
					else if (Command.COMMANDS.COMMAND_GLOBAL_HANDSUP
							.equals(message.getCommand())) {
						boolean isOn;
						if ("on".equals(message.getParams())) {
							isOn = true;
							// hand_up on
						} else {
							isOn = false;
							// hand_up off
						}
						mSwitchManager.notifyHandUpSwitchStatus(isOn);

					}

					else if (Command.COMMANDS.COMMAND_CLEAN.equals(message
							.getCommand())) {
						boolean isOn;
						// CommandManager.leaveGroup();

					}
					return;
				}
			} catch (Exception e) {
				Log.d(TAG, "exception : " + e.toString());
			}
		};
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterNetworkReceiver();
		mLibSiren.destroy();
		android.os.Process.killProcess(android.os.Process.myPid());
		mCurrentMOde = null;
	}

	private class NetworkReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mNewTime = System.currentTimeMillis();
			if (mNewTime - mOldTime >= 3000) {
				mOldTime = mNewTime;
				/*
				 * if (mHandler != null) {
				 * mHandler.removeMessages(MessageHandler.UPDATE_NET_STATUS);
				 * mHandler.sendEmptyMessageDelayed(
				 * MessageHandler.UPDATE_NET_STATUS, 200); }
				 */
				Log.d(TAG, "connect change");
			}
		}
	}

	private void registerNetworkReceiver() {
		if (!mNetworkReceiverRegistered) {
			// we only care about the network connect state change
			IntentFilter filter = new IntentFilter(
					ConnectivityManager.CONNECTIVITY_ACTION);
			registerReceiver(mNetworkReceiver, filter);
			mNetworkReceiverRegistered = true;
		}
	}

	private void unregisterNetworkReceiver() {
		if (mNetworkReceiverRegistered) {
			unregisterReceiver(mNetworkReceiver);
			mNetworkReceiverRegistered = false;
		}
	}

	/**
	 * Check the internet state and notify the state.
	 * 
	 * @return true if network available.
	 */
	public boolean checkNetworkAvailable() {
		NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			mIsInternetConnected = networkInfo.isConnected();
		} else {
			mIsInternetConnected = false;
		}
		Log.d(TAG, "connect status " + mIsInternetConnected);
		return mIsInternetConnected;
	}

	
	private void loadWelcomeFragment() {
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		Fragment currentFragment = WelcomeFragment.newInstance();

		fragmentTransaction.replace(R.id.content_container, currentFragment);
		fragmentTransaction.commitAllowingStateLoss();

	}

	private void loadFirstFragment() {

		mHeadBarView.setModel(R.string.teach_model);
		MediaMessage firstMessage = new MediaMessage();
		firstMessage.setCommand(Command.COMMANDS.COMMAND_TEACH_ALLCALLS);
		loadTeachingFragment(firstMessage);
	}

	private void loadTeachingFragment(MediaMessage message) {
        if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_ALLCALLS)) {
            TeachDemonstrationFragment.mIsDemonstration = false;
            CommandManager.startSat();
            CommandManager.leaveGroup("");
            CommandManager.joinGroup("230.1.2.3");
            CommandManager.closeMic();
        } else if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_DICTATION)) {
            boolean isOn;
            if ("on".equals(message.getParams())) {
                isOn = true;
                mSwitchManager.notifyDictationSwitchStatus(isOn);
            } else {
                isOn = false;
                mSwitchManager.notifyDictationSwitchStatus(isOn);
                return;
            }
        } else if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_TRANSLATE)) {
            boolean isOn;
            if ("off".equals(message.getParams())) {
                isOn = false;
                mSwitchManager.notifyTranslateSwitchStatus(isOn);
                return;
            } else {
                isOn = true;
                mSwitchManager.notifyTranslateSwitchStatus(isOn);
                // return;
            }
        }

        else if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_TEST)) {
            boolean isOn;
            if ("off".equals(message.getParams())) {
                isOn = false;
                mSwitchManager.notifyTestSwitchStatus(isOn);
                return;
            } else {
                isOn = true;
                mSwitchManager.notifyTestSwitchStatus(isOn);
                // return;
            }
        }

        else if (message.getCommand().equals(Command.COMMANDS.COMMAND_CLEAN)) {
            if (message.isMe()) {
                notifyCleanListenner();
            } else if (message.getReveiver().equals("all")) {
                notifyAllCleanListenner();
            }
        }

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment currentFragment = null;
        if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_ALLCALLS)) {
            currentFragment = AllCallsFragment.newInstance();
        } else if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_DEMONSTRATION)) {
            currentFragment = TeachDemonstrationFragment.newInstance(message);
        } else if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_DICTATION)) {
            currentFragment = TeachDictationFragment.newInstance();
        } else if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_INTERCOM)) {
            currentFragment = TeachIntercomFragment.newInstance(message);
        } else if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_MONITOR)) {
            if (message.isMe()) {
                Log.d(TAG, "I am monitored");
            } else {
                Log.d(TAG, "other was monitored");
            }
            return;
            // currentFragment = TeachMonitorFragment.newInstance(message);
        } else if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_TEST)) {
            currentFragment = TeachTestFragment.newInstance(message);
        } else if (message.getCommand().equals(Command.COMMANDS.COMMAND_TEACH_TRANSLATE)) {
            currentFragment = TeachTranslateFragment.newInstance(message);
        } else {
            return;
        }

        fragmentTransaction.replace(R.id.content_container, currentFragment);
        fragmentTransaction.commitAllowingStateLoss();
	}

	// load group fragment
	private void loadGroupFragment(MediaMessage message) {
        if (message.getCommand().equals(Command.COMMANDS.COMMAND_GROUP_DISCUSS)) {
            if (!message.isMe()) {
                return;
            }
            CommandManager.startSat();
            CommandManager.openMic();
            Fragment currentFragment = GroupDiscussFragment.newInstance();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.content_container, currentFragment);
            Bundle args = new Bundle();
            args.putSerializable("message", message);
            currentFragment.setArguments(args);
            fragmentTransaction.commitAllowingStateLoss();
        }
        else if (message.getCommand().equals(Command.COMMANDS.COMMAND_GROUP_ADJUST)) {
            if (message.isMe()) {
                Log.d(TAG, "join me");
                if (mUpdateGroupMembersListenner != null) {
                    mUpdateGroupMembersListenner.joinMeGroup(message);
                    return;
                }
            } else {
                if (mUpdateGroupMembersListenner != null) {
                    mUpdateGroupMembersListenner.leaveGroup(message.getReveiver());
                    mUpdateGroupMembersListenner.joinGroup(message.getGroup(),
                            message.getReveiver());
                    return;
                }
            }

        }
        else if (message.getCommand().equals(Command.COMMANDS.COMMAND_GROUP_ATTEND_DISCUSS)) {
            if (message.isMe()) {
                // TODO how show the receiver view;
            } else {
                if (mUpdateGroupMembersListenner != null) {
                    mUpdateGroupMembersListenner.joinGroup(message.getGroup(),
                            message.getReveiver());
                    return;
                }
            }
        } else if (message.getCommand().equals(Command.COMMANDS.COMMAND_GROUP_EXIT_DISCUSS)) {
            if (message.isMe()) {
                // TODO how show the receiver view;
            } else {
                if (mUpdateGroupMembersListenner != null) {
                    mUpdateGroupMembersListenner.leaveGroup(message.getReveiver());
                    return;
                }
            }

        }
	}

	// load exam fragments
	private void loadExamFragment(MediaMessage message) {
		if (!mSeatNo.startsWith(message.getReveiver())
				&& !message.getReveiver().equals("all")) {
			return;
		}
		Fragment replaceFragment = null;
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		Bundle args = new Bundle();
		args.putString("params", message.getParams());
		args.putString("receiver", message.getReveiver());

		if (Command.COMMANDS.COMMAND_EXAM.equals(message.getCommand())) {
			replaceFragment = ExamReadyFragment.newInstance();
			fragmentTransaction
					.replace(R.id.content_container, replaceFragment);
		} else if (Command.COMMANDS.COMMAND_EXAM_ORAL.equals(message
				.getCommand())) {
			boolean isOn;
			if ("off".equals(message.getParams())) {
				isOn = false;
				mSwitchManager.notifyExamSwitchStatus(isOn);
				return;
			} else if ("on".equals(message.getParams())) {
				isOn = true;
				mSwitchManager.notifyExamSwitchStatus(isOn);
				return;
			}
			replaceFragment = ExamOralFragment.newInstance(message);
			fragmentTransaction
					.replace(R.id.content_container, replaceFragment);
		} else if (Command.COMMANDS.COMMAND_EXAM_STANDARD.equals(message
				.getCommand())) {
			boolean isOn;
			if ("off".equals(message.getParams())) {
				isOn = false;
				mSwitchManager.notifyExamSwitchStatus(isOn);
				return;
			} else if ("on".equals(message.getParams())) {
				isOn = true;
				mSwitchManager.notifyExamSwitchStatus(isOn);
				return;
			}
			replaceFragment = ExamStandardFragment.newInstance(message);
			fragmentTransaction
					.replace(R.id.content_container, replaceFragment);
		} else if (Command.COMMANDS.COMMAND_EXAM_DISCUSS.equals(message
				.getCommand())) {
			boolean isOn;
			if ("off".equals(message.getParams())) {
				isOn = false;
				mSwitchManager.notifyExamSwitchStatus(isOn);
				return;
			} else {
				isOn = true;
				mSwitchManager.notifyExamSwitchStatus(isOn);
			}
			replaceFragment = ExamOralDiscussFragment.newInstance(message);
			replaceFragment.setArguments(args);
			fragmentTransaction
					.replace(R.id.content_container, replaceFragment);
		}
		fragmentTransaction.commitAllowingStateLoss();
	}

	// load self study fragments
	private void loadStudyFragment(final MediaMessage message) {
		CommandManager.destorySat();
		if (Command.COMMANDS.COMMAND_SELF_STUDY.equals(message.getCommand())) {
			{

				Fragment replaceFragment = null;
				FragmentTransaction fragmentTransaction = getFragmentManager()
						.beginTransaction();
				replaceFragment = SelfStudyFragment.newInstance(message);
				Bundle args = new Bundle();
				args.putSerializable("message", message);
				fragmentTransaction.replace(R.id.content_container,
						replaceFragment);
				fragmentTransaction.commitAllowingStateLoss();
			}

		}
		else if (Command.COMMANDS.COMMAND_SELF_IP_CALL.equals(message.getCommand())) {
			if (message.getReveiver().equals(mSeatNo)) {
				new AlertDialog.Builder(this)
						.setTitle("Ip Call")
						.setMessage("Call from " + message.getParams())
						.setPositiveButton("receive",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface arg0,
											int arg1) {
										CommandManager.joinGroup(GroupInfo
												.getGroupId(message.getGroup()));

									}
								}).setNegativeButton("cancel", null).create()
						.show();
			}
		}

		if (Command.COMMANDS.COMMAND_SELF_REFRESH.equals(message.getCommand())) {
			if (mUpdateResources != null) {
				mUpdateResources.updateUrl(message.getParams());
			}
		}
	}

	private IUpdateGroupMembersListenner mUpdateGroupMembersListenner;

	public void registerUpdateGroupMembersListenner(
			IUpdateGroupMembersListenner l) {
		mUpdateGroupMembersListenner = l;
	}

	public void unregisterUpdateGroupMembersListenner() {
		mUpdateGroupMembersListenner = null;
	}

	public void registerCleanListener(ICleanListener l) {
		mCleanerList.add(l);
	}

	public void unregisterCleanListener(ICleanListener l) {
		mCleanerList.remove(l);
	}

	public void notifyCleanListenner() {
		for (int i = 0; i < mCleanerList.size(); i++) {
			mCleanerList.get(i).clean();
		}
	}

	public void notifyAllCleanListenner() {
		for (int i = 0; i < mCleanerList.size(); i++) {
			mCleanerList.get(i).allClean();
		}
	}

	private IUpdateResources mUpdateResources;

	public interface IUpdateResources {
		public void updateUrl(String url);
	}

	public void setUpdateResources(IUpdateResources updateResources) {
		mUpdateResources = updateResources;
	}

	public void killPlayerProcess() {
		try {
			ActivityManager activityManager = (ActivityManager) mBaseContext
					.getSystemService(Context.ACTIVITY_SERVICE);
			Method forceStopPackage;
			forceStopPackage = activityManager.getClass().getDeclaredMethod(
					"forceStopPackage", String.class);

			forceStopPackage.setAccessible(true);

			forceStopPackage.invoke(activityManager,
					"com.mxtech.videoplayer.ad");

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

	public void killOtherProcess() {
		try {
			ActivityManager activityManager = (ActivityManager) mBaseContext
					.getSystemService(Context.ACTIVITY_SERVICE);
			Method forceStopPackage;
			forceStopPackage = activityManager.getClass().getDeclaredMethod(
					"forceStopPackage", String.class);

			forceStopPackage.setAccessible(true);

			forceStopPackage.invoke(activityManager, "cn.mozilla.firefox");
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

	private void handle_saveconf(String mIpPrefix) {
		Log.d("ethernet info", "IP :" + mEthInfo.getIpAddress() + " "
				+ mEthInfo.getConnectMode());
		EthernetDevInfo info = new EthernetDevInfo();
		info.setIfName(mEthInfo.getIfName());

		info.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_MANUAL);
		info.setIpAddress(mIpPrefix + "." + GroupInfo.sAddressMap.get(mSeatNo));
		info.setRouteAddr(mIpPrefix + ".1");
		info.setDnsAddr(mIpPrefix + ".1");
		info.setNetMask("255.255.255.0");
		mEthManager.updateEthDevInfo(info);
	}

	@Override
	public void onBackPressed() {
	}

	private void initDownload() {
		mThread.start();
		mDownloadHandler = new Handler(mThread.getLooper());
		mSession = new Session();
		mSession.setContext(this);
	}

	/*
	 * Download the file.
	 * 
	 * @param dest
	 * 
	 * @throws RemoteException
	 */
	public void download(String url) throws RemoteException {
		Log.d("Download", "SystemUpdateServiceStub.download");
		// mSession.setRunInBackgroud(false);
		// Tiger - hard code /cache as the first try. 2013.12.31
		mSession.setDownloadTo("/cache/update/", Constants.UPGRADE_FILE_BODY);
		mSession.setNewVersionURL(url);
		Log.d("Download", "download start");
		IDownloadListener l = new IDownloadListener() {

			@Override
			public boolean onChangeDownloadPath(Session session) {
				// brljzhou - this should not be called. 2013.12.31
				Log.e("Download", "this should not be called\n");
				return false;

			}

			@Override
			public void onCompleted(Session session) {
				Log.e("Download", "onCompleted()");
				mSession.setState(Constants.STATE_REBOOTING);
				Log.e("Download", "reboot and upgrade!");
				// RebootTask task = new RebootTask(mSession);
				// mDownloadHandler.post(task);
				sIsDownloaded = true;
				DialogInterface.OnClickListener l = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						RebootTask task = new RebootTask(mSession);
						mDownloadHandler.post(task);

					}
				};
				CommonUtil.showWarnDialog(mBaseContext, "系统升级",
						"系统有更新，请升级到最新版本!", l);
			}

			@Override
			public void onError(Session session, int errorCode) {
				Log.e("Download", "onError()");
			}
		};
		mDownloadTask = new DownloadTask(mSession, l);
		mDownloadHandler.post(mDownloadTask);
	}

	/*
	 * @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
	 * Log.d(TAG, "keycode : "+keyCode); return mHeadBarView.onKeyUp(keyCode,
	 * event); }
	 * 
	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
	 * Log.d(TAG, "keycode :"+keyCode); return mHeadBarView.onKeyDown(keyCode,
	 * event); }
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d(TAG, "keycode :" + event.getKeyCode());
		return mHeadBarView.dispatchKeyEvent(event);
	}


}
