package com.multimedia.room;

import java.util.HashMap;
import java.util.Map;

public class GroupInfo {

	public static Map<String, String> sGroupIdMap = new HashMap<String, String>();
	public static Map<String, String> sAddressMap = new HashMap<String, String>();
	private static int ADDRESS_COUNT = 225;
	private static int GROUP_COUNT = 100;
	private static final String sGroupPrefix = "230.1.3.";
	static {
		for (int i = 1; i < GROUP_COUNT; i++) {

			sGroupIdMap.put("g" + i, sGroupPrefix + i);
		}
	}

	public static String getGroupId(String group) {
		if (sGroupIdMap.containsKey(group)) {
			return sGroupIdMap.get(group);
		} else {
			String tempId = sGroupPrefix + GROUP_COUNT;
			sGroupIdMap.put(group, tempId);
			GROUP_COUNT++;
			return tempId;
		}
	}

	static {
		int ip = 254;
			for (char m = 'A'; m <= 'A' + 15; m++) {
				for (int n = 1; n <= 15; n++) {
					sAddressMap.put(m + "" + n, ip-- + "");
				}
			}
	}

}
