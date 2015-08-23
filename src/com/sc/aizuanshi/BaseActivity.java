package com.sc.aizuanshi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.dlnetwork.GetTotalMoneyListener;
import com.newqm.sdkoffer.QuMiNotifier;
import com.sc.aizuanshi.db.DBHelper;
import com.sc.aizuanshi.utils.Config;
import com.sc.aizuanshi.utils.CopyData;
import com.sc.aizuanshi.utils.Game;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity implements QuMiNotifier,GetTotalMoneyListener {
	private int qumiNumber = 0;
	private int dianleNumber = 0;
	private SharedPreferences pref;
	private Editor editor;
	public Config config;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		CopyData.extractDatabase(this, "game_info.db");
		pref = getSharedPreferences("download", Context.MODE_PRIVATE);
		editor = pref.edit();
		config = Config.getSingle(this);
	}

	public void contacts() {
		dialogFollow();
	}

	public void plugDialog(final String plugUrl) {
		final AlertDialog.Builder dialog = new Builder(BaseActivity.this);
		dialog.setTitle("温馨提示");
		dialog.setMessage("未检测到补丁程序，为了保证顺利领取钻石，请下载补丁文件并安装");
		dialog.setNegativeButton("取消", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setPositiveButton("确定", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				Uri uri = Uri.parse(plugUrl);
				File folder = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				if (!folder.exists() || !folder.isDirectory()) {
					folder.mkdirs();
				}
				DownloadManager.Request request = new DownloadManager.Request(
						uri);
				request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
						| Request.NETWORK_WIFI);
				request.setDestinationInExternalPublicDir(
						Environment.DIRECTORY_DOWNLOADS, "new_version.apk");
				request.setVisibleInDownloadsUi(true);
				long downloadId = dm.enqueue(request);

				editor.putLong("download_id", downloadId);
				editor.commit();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * @return 返回版本信息
	 */
	public int getVersionCode() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);
			return pi.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 转换成字符串
	 * 
	 * @param in
	 * @return
	 */
	public String readFromStream(InputStream in) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[8192];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			if (in != null) {
				in.close();
			}
			if (baos != null) {
				baos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baos.toString();
	}

	/**
	 * 更新
	 * 
	 * @param describe
	 * @param urlpath
	 */
	private void showUpdateDialog(String describe, final String urlpath) {
		final AlertDialog.Builder dialog = new Builder(this);
		dialog.setCancelable(false);
		dialog.setTitle(this.getResources().getString(
				R.string.splashactivity_update_prompt));
		dialog.setMessage(describe);
		dialog.setNegativeButton("以后更新", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		dialog.setPositiveButton("马上更新", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
				System.out.println("更新的地址" + urlpath);
				Uri uri = Uri.parse(urlpath);
				File folder = Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
				if (!folder.exists() || !folder.isDirectory()) {
					folder.mkdirs();
				}
				DownloadManager.Request request = new DownloadManager.Request(
						uri);
				request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
						| Request.NETWORK_WIFI);
				request.setDestinationInExternalPublicDir(
						Environment.DIRECTORY_DOWNLOADS, "new_version.apk");
				request.setTitle("版本更新中....");
				request.setVisibleInDownloadsUi(true);
				long downloadId = dm.enqueue(request);
				editor.putLong("download_id", downloadId);
				editor.commit();
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	public void dialogFollow() {
		final AlertDialog.Builder dialog = new Builder(this);
		dialog.setTitle("联系客服");
		dialog.setMessage("请关注我们唯一的微信公众号heiying8,及时获取更多的资讯");
		dialog.setNegativeButton("复制公众号", new OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				ClipboardManager cm = (ClipboardManager) BaseActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData
						.newPlainText("simple text", "heiying8");
				cm.setPrimaryClip(clip);
				Toast.makeText(BaseActivity.this, "复制成功", Toast.LENGTH_SHORT)
						.show();
			}
		});
		dialog.show();
	}

	public List<Game> initData() {
		List<Game> temp = new ArrayList<Game>();
		DBHelper helper = new DBHelper(this);
		Cursor cursor = helper.query();
		while (cursor.moveToNext()) {
			Game game = new Game();
			game.setId(cursor.getInt(0));
			game.setName(cursor.getString(1));
			game.setPackages(cursor.getString(2));
			game.setExist(cursor.getInt(3));
			temp.add(game);
		}
		return temp;
	}

	public void earnedPoints(int pointTotal, int arg1) {

	}

	public void getPoints(int arg0) {
		qumiNumber = arg0;
	}

	public void getPointsFailed(String arg0) {

	}

	public int getQuMiNumber() {
		return qumiNumber;
	}

	public void getTotalMoneyFailed(String arg0) {
		
	}

	public void getTotalMoneySuccessed(String arg0, long arg1) {
		this.dianleNumber=(int) arg1;
	}
	public int getDianleNumber(){
		return dianleNumber;
	}
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
