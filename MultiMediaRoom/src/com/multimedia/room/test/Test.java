package com.multimedia.room.test;

import android.test.AndroidTestCase;
import android.util.Log;

import com.multimedia.room.CommonUtil;
import com.multimedia.room.FilesEntity;
import com.multimedia.room.GroupInfo;
import org.apache.http.client.ClientProtocolException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Test extends AndroidTestCase {

	public void testGetFilesFromXml() throws ClientProtocolException,
			IOException {
		String url = "http://192.168.1.5:8080/list.xml";
		String result = CommonUtil.getFileData(url);
		Log.d("result", result);
		List<FilesEntity> files = CommonUtil
				.getFilesEntityFromXML(new ByteArrayInputStream(result
						.getBytes()));
		for (FilesEntity file : files) {
			System.out.print(file.toString());
		}

	}

	public void formatMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		String msg = "sender:A1,command:set_seat,param:A1";
		String[] params = msg.split(",");
		for (int i = 0; i < params.length; i++) {
			String[] keyValue = params[i].split(":");
			String key = keyValue[0];
			String value = keyValue[1];
			sb.append("\"").append(key).append("\"").append(":").append("\"")
					.append(value).append("\"").append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(",")).append("}");
		Log.d("message", sb.toString());
	}

	public void getGroupId() {
		String group = "g257";
		Log.d(group, GroupInfo.getGroupId(group));
	}

	public void testHashMap() {
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		hm.put(1, "value1");
		hm.put(2, "value2");
		Log.d("result", "value = " + hm.get(3));
	}

}
