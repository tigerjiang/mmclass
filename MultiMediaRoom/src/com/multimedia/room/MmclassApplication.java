package com.multimedia.room;

import android.app.Application;

public class MmclassApplication extends Application {

	public void onCreate() {  
        super.onCreate();  
        CrashHandler crashHandler = CrashHandler.getInstance();  
        // 注册crashHandler  
        crashHandler.init(getApplicationContext());  
        // 发送以前没发送的报告(可选)  
        crashHandler.sendPreviousReportsToServer();  
    }  
}
