package com.sc.aizuanshi.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Points {
	private SharedPreferences pref;
	private Editor editor;
	private static final String QUMI = "qumi";
	private static final String DIANLE = "dianle";
	private static final String YOUMI = "youmi";
	private static final String MONEY = "money";

	public Points(Context context) {
		super();
		pref = context.getSharedPreferences("Points", Context.MODE_PRIVATE);
		editor = pref.edit();
	}

	public void setQumi(int p) {
		editor.putInt(QUMI, p);
		editor.commit();
	}

	public void setDianle(int p) {
		editor.putInt(DIANLE, p);
		editor.commit();
	}

	public void setYoumi(int p) {
		editor.putInt(YOUMI, p);
		editor.commit();
	}

	public int totalPoints() {
		int qumi = pref.getInt(QUMI, 0);
		int dianle = pref.getInt(DIANLE, 0);
		int yoimi = pref.getInt(YOUMI, 0);
		return qumi + dianle + yoimi-getMoney();
	}

	public void setMoney() {
		int m = getMoney() + 50;
		editor.putInt(MONEY, m);
		editor.commit();
	}

	public int getMoney() {
		return pref.getInt(MONEY, 0);
	}
}
