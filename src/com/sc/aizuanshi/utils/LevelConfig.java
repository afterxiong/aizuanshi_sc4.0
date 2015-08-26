package com.sc.aizuanshi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LevelConfig {

	public static String Level[] = {};
	private static SharedPreferences preferences;
	private static Editor editor;
	private static final String LEVELS = "Levels";

	public static void initLevel(Context context) {
		preferences = context.getSharedPreferences("Level", Context.MODE_PRIVATE);
		editor = preferences.edit();
	}

	public static int getLevel() {
		if (preferences == null) {
			return -1;
		}
		
		int max = preferences.getInt(LEVELS, 0);
		if (max >= Level.length) {
			max = Level.length;
		}
		return max;

	}

	public static void setLevel() {
		if (editor == null) {
			return;
		}
		int r = getLevel();
		editor.putInt(LEVELS, r);
	}
}
