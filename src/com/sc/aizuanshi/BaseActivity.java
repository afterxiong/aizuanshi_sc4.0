package com.sc.aizuanshi;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.sc.aizuanshi.utils.AdvertUtils;
import com.sc.aizuanshi.utils.Config;
import com.sc.aizuanshi.utils.Copydb;

public class BaseActivity extends Activity {
	public Config config;
	public AdvertUtils advertUtils;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Copydb.extractDatabase(this, "game_info.db");
		config = Config.getSingle(this);
		initPlatfrom();
	}

	public void initPlatfrom() {
		advertUtils = new AdvertUtils(this);
	}

	public void contacts() {
		final AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle(getResources().getString(R.string.contacts));
		dialog.setMessage(getResources().getColor(R.string.message_tip));
		dialog.setNegativeButton(getResources().getString(R.string.copy_wechat), new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				ClipboardManager cm = (ClipboardManager) BaseActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("simple text", "heiying8");
				cm.setPrimaryClip(clip);
				Toast.makeText(BaseActivity.this, getResources().getString(R.string.copy_success), Toast.LENGTH_SHORT)
						.show();
			}
		});
		dialog.show();
	}


}
