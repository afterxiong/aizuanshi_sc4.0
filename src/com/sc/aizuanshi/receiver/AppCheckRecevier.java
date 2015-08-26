package com.sc.aizuanshi.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppCheckRecevier extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		String name = intent.getData().getSchemeSpecificPart();
		listener.check(name);
	}

	public OnCheckAppListener listener;

	public void setOnCheckAppListener(OnCheckAppListener listener) {
		this.listener = listener;
	}

	public interface OnCheckAppListener {
		void check(String name);
	}
}
