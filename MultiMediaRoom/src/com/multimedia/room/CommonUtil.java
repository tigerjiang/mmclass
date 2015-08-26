
package com.multimedia.room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.text.TextUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class CommonUtil {
	private static final boolean DEBUG = false;
    public static final String USER_NAME_KEY = "username";
    public static final String SEATNO = "seatno";
    public static final String CONFID_IP = "config_ip";
    public static final String IP_PRIFEX = "prefix";
    public static final String USER_PASSWORD_KEY = "password";
    private static SharedPreferences mSharedPreferences;
    private static final CommonUtil INSTANCE = new CommonUtil();
    private static Context sContext;
    private static final String INTERNAL_CONFIG_FILE_NAME = "config.cg";
    public static final String CHARSET_UTF_8 = "UTF-8";
    public static final String CHARSET_GBK = "GBK";
    public static final String[] sSeatPrefix = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
            "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    public static final String[] sSeatsuffix = {
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "15", "16", "17",
            "18", "19", "20"
    };

    private CommonUtil() {

    }

    public static String getSeatNo() {
        return mSharedPreferences.getString(SEATNO, "A1");
    }
    
    public static String getIpPrefix() {
        return mSharedPreferences.getString(IP_PRIFEX, "192.168.1");
    }
    public static void setIpPrefix(String prefix) {
    	Editor editor = mSharedPreferences.edit();
        editor.putString(IP_PRIFEX, prefix);
        editor.commit();
    }
    
    public static boolean isConfigIP() {
        return mSharedPreferences.getBoolean(CONFID_IP, false);
    }
    
    public static void configIP(boolean flag) {
    	Editor editor = mSharedPreferences.edit();
        editor.putBoolean(CONFID_IP, flag);
        editor.commit();
    }
    

    public static synchronized CommonUtil getInstance(Context context) {
            sContext = context;
            mSharedPreferences = sContext.getSharedPreferences(
                    INTERNAL_CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        return INSTANCE;
    }

    public static String getValueFromSharePreferences(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public static void reStoreValueIntoSharePreferences(String key, String value) {
        Editor editor = mSharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static boolean checkSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static void showDialog(Context context, int resId, int msgId) {
        new AlertDialog.Builder(context).setTitle(resId).setMessage(msgId)
                .setPositiveButton(R.string.ok, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }).create().show();
    }
    
   public static void showDialog(Context context, String title, String message) {
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton(R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				}).create().show();
	}

    public static void showCustomDialog(Context context, View view,
            String title, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context).setView(view).setTitle(title)
                .setPositiveButton(R.string.ok, listener).create().show();
    }
    
    
    public static void showCustomDialog(Context context, int width ,View view,
            String title, DialogInterface.OnClickListener listener) {
        AlertDialog dialog = new AlertDialog.Builder(context).setView(view).setTitle(title)
                .setPositiveButton(R.string.ok, listener).create();
        dialog.show();
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.width = width;
        layoutParams.height = LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    public static void showWarnDialog(Context context, String title,
            String msg, OnClickListener listener) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
                .setPositiveButton(R.string.ok, listener)
                .setNegativeButton(R.string.cancel, null).create().show();
    }

    public static void showSelectedDialog(Context context,
            OnClickListener listener) {
        new AlertDialog.Builder(context)
                .setItems(new String[] {
                        "查看更多", "返回"
                }, listener).create()
                .show();
    }

    public static String convertStreamToString(InputStream is, String charset) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, charset),
                    512 * 1024);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e("DataProvier convertStreamToString", e.getLocalizedMessage(),
                    e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static String getData(String url, String charSet){
        String result = "";
        while (TextUtils.isEmpty(result)) {
        	Log.d("result", result +"=====");
        HttpGet httpGet = new HttpGet(url);
        
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,6000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,6000);
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String charset = EntityUtils.getContentCharSet(httpEntity);
            Log.d("Charset", "charset= " + charset);
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                result = convertStreamToString(inputStream, charSet);

            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        }
        return result;

    }

    public static String convertTime(long mis) {
        int time = (int) (mis / 1000);
        StringBuilder sb = new StringBuilder();
        int leftseconds = time % 60;
        int minutes = time / 60;
        int leftMinutes = minutes % 60;
        int hour = minutes / 60;

        if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append("0").append(hour).append(":");
        }
        if (minutes < 10) {
            sb.append("0").append(leftMinutes).append(":");
        } else {
            sb.append(leftMinutes).append(":");
        }
        if (leftseconds < 10) {
            sb.append("0").append(leftseconds);
        } else {
            sb.append(leftseconds);
        }

        return sb.toString();
    }

    public static String getFileData(String url)
             {
        String result = "";
        while(TextUtils.isEmpty(result)){
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = null;
	     Log.d("httpResponse", httpResponse!=null?"not null":"is null");
        try {
            httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                result = convertStreamToString(inputStream, CHARSET_GBK);

            }
        } catch (ClientProtocolException e) {
             httpResponse = null;
             e.printStackTrace();
        } catch (IOException e) {
            httpResponse = null;
            e.printStackTrace();
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        }
        return result;

    }

    public static ArrayList<FilesEntity> getFilesEntityFromXML(
            InputStream inputStream) {
        ArrayList<FilesEntity> files = null;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(inputStream, CHARSET_UTF_8);
            int event = parser.getEventType();// 产生第一个事件
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
                        files = new ArrayList<FilesEntity>();// 初始化files集合
                        break;
                    case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
                        Log.d("start_tag", parser.getName());
                        if ("file".equals(parser.getName())) {// 判断开始标签元素是否是file
                            FilesEntity file = new FilesEntity();
                            String url = parser.getAttributeValue(0);
                            String type = parser.getAttributeValue(1);
                            String name = parser.nextText();
                            Log.d("file", "url " + url + " type " + type + " name "
                                    + name);
                            file.setUrl(url);// 得到file标签的属性值，并设置file的url
                            file.setType(type);
                            file.setName(name);
                            files.add(file);
                            Log.d("file", files.toString());
                        }

                        break;
                    case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
                        Log.d("end_tag", parser.getName());
                        break;
                }
                event = parser.next();// 进入下一个元素并触发相应事件
            }// end while
        } catch (Exception ex) {
            Log.d("purser error", "parser xml error!", ex);
        }
        return files;

    }
    

	public static MediaMessage parseMessage(String content) {
		MediaMessage message = null;
		try {
			JSONObject jsonStr = new JSONObject(content);
			String tpye = jsonStr.getString("type");
			String receiver = jsonStr.getString("receiver");
			String mode = jsonStr.getString("mode");
			String command = jsonStr.getString("command");
			String group = jsonStr.getString("group");
			String param = jsonStr.getString("param");

			message = new MediaMessage(tpye, receiver, mode, command, group,
					param);
			if(DEBUG){
			Log.d("message", "message +" + message.toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return message;
	}

}
