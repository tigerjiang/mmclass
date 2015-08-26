package com.multimedia.room;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.SystemWriteManager;
import android.content.Context;
import android.util.Log;

public class SystemWriter {

		public static final String TAG="SystemWriter";
		
		public Context mContext;
		public SystemWriteManager mSystemWriter;
		
		public SystemWriter(Context context)
		{
			mSystemWriter = (SystemWriteManager) context.getSystemService(Context.SYSTEM_WRITE_SERVICE);
		}
		
		public void resetOsd()
		{
			if(null != mSystemWriter){
                mSystemWriter.writeSysfs("/sys/class/ppmgr/ppscaler_rect", "x:0,y:0,w:1920,h:1080");
                mSystemWriter.writeSysfs("/sys/class/graphics/fb0/free_scale", "1");
            }else{
                writeSysfs("/sys/class/ppmgr/ppscaler_rect", "x:0,y:0,w:1920,h:1080");
                writeSysfs("/sys/class/graphics/fb0/free_scale", "1");
            }
		}
		
		private int writeSysfs(String path, String val) {
	        if (!new File(path).exists()) {
	            Log.e(TAG, "File not found: " + path);
	            return 1; 
	        }
	        
	        try {
	            BufferedWriter writer = new BufferedWriter(new FileWriter(path), 64);
	            try {
	                writer.write(val);
	            } finally {
	                writer.close();
	            }       
	            return 0;
	            
	        } catch (IOException e) { 
	            Log.e(TAG, "IO Exception when write: " + path, e);
	            return 1;
	        }                 
	    }


	public void enableScaler()
	{
		Log.d(TAG, "******enableScaler");
		if(mSystemWriter != null)
		{
			mSystemWriter.writeSysfs("/sys/class/vfm/map" ,"rm default");
			mSystemWriter.writeSysfs("/sys/class/vfm/map" ,"add default decoder deinterlace amvideo");
			mSystemWriter.writeSysfs("/sys/class/graphics/fb0/free_scale", "0");
			mSystemWriter.writeSysfs("/sys/class/graphics/fb1/free_scale", "0");
			//mSystemWriter.writeSysfs("/sys/class/graphics/fb0/request2XScale", "16 1280 720");
			//mSystemWriter.writeSysfs("/sys/class/graphics/fb1/request2XScale", "16 1280 720");
			//mSystemWriter.writeSysfs("/sys/class/graphics/fb0/request2XScale", "16 1920 1080");
                        //mSystemWriter.writeSysfs("/sys/class/graphics/fb1/request2XScale", "16 1920 1080");
		}
		else
		{
			writeSysfs("/sys/class/vfm/map" ,"rm default");
			writeSysfs("/sys/class/vfm/map" ,"add default decoder deinterlace amvideo");
			writeSysfs("/sys/class/graphics/fb0/free_scale", "0");
                        writeSysfs("/sys/class/graphics/fb1/free_scale", "0");
                        //writeSysfs("/sys/class/graphics/fb0/request2XScale", "16 1280 720");
                        //writeSysfs("/sys/class/graphics/fb1/request2XScale", "16 1280 720");
			//writeSysfs("/sys/class/graphics/fb0/request2XScale", "16 1920 1080");
                        //writeSysfs("/sys/class/graphics/fb1/request2XScale", "16 1920 1080");
		}	
	}
	
	public void disableScaler()
        {
		Log.d(TAG, "******disableScaler");
                if(mSystemWriter != null)
                {
			mSystemWriter.writeSysfs("/sys/class/graphics/fb0/blank", "1");
			mSystemWriter.writeSysfs("/sys/class/ppmgr/ppscaler_rect", "x:0,y:0,w:1920,h:1080");
                        mSystemWriter.writeSysfs("/sys/class/graphics/fb0/free_scale", "1");
                        mSystemWriter.writeSysfs("/sys/class/vfm/map" ,"rm default");
                        mSystemWriter.writeSysfs("/sys/class/vfm/map" ,"add default decoder ppmgr deinterlace amvideo");
			mSystemWriter.writeSysfs("/sys/class/graphics/fb0/free_scale", "1");
                        mSystemWriter.writeSysfs("/sys/class/graphics/fb1/free_scale", "1");
                	mSystemWriter.writeSysfs("/sys/class/graphics/fb0/request2XScale", "2");
                        //mSystemWriter.writeSysfs("/sys/class/graphics/fb1/request2XScale", "2");
			mSystemWriter.writeSysfs("/sys/class/graphics/fb0/blank", "0");
		}
                else
                {
			writeSysfs("/sys/class/graphics/fb0/blank", "1");
                        writeSysfs("/sys/class/ppmgr/ppscaler_rect", "x:0,y:0,w:1920,h:1080");
                        writeSysfs("/sys/class/graphics/fb0/free_scale", "1");
			writeSysfs("/sys/class/vfm/map" ,"rm default");
                        writeSysfs("/sys/class/vfm/map" ,"add default decoder ppmgr deinterlace amvideo");
			writeSysfs("/sys/class/ppmgr/ppscaler_rect", "x:0,y:0,w:1920,h:1080");
			writeSysfs("/sys/class/graphics/fb0/free_scale", "1");
                        writeSysfs("/sys/class/graphics/fb1/free_scale", "1");
			//writeSysfs("/sys/class/ppmgr/ppscaler_rect", "x:0,y:0,w:1920,h:1080");
			//writeSysfs("/sys/class/graphics/fb0/free_scale", "1");
			writeSysfs("/sys/class/graphics/fb0/request2XScale", "2");
                        //writeSysfs("/sys/class/graphics/fb1/request2XScale", "2");
			writeSysfs("/sys/class/graphics/fb0/blank", "0");
                }
        }
}
