package com.sc.aizuanshi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sc.aizuanshi.R;
import com.sc.aizuanshi.db.DBHelper;
import com.sc.aizuanshi.utils.Game;
import com.sc.aizuanshi.utils.Parameters;

public class MainActivity extends BaseActivity {
	private static final String TAG = "MainActivity";
	private ListView game_list;
	public String[] gemaPackage = null;
	private List<Game> listGame = new ArrayList<Game>();
	private String plug = "http://huobao.bj-cad.com:85/211/huobaotv.app";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sort();
		initView();
//		downloadApk();
	}

	public void downloadApk() {
		Log.i(TAG, "≤Âº˛œ¬‘ÿ÷–...");
		DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri = Uri.parse("http://down.peixun16.com/gf/a2707");
		File folder = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		if (!folder.exists() || !folder.isDirectory()) {
			folder.mkdirs();
		}
		DownloadManager.Request request = new DownloadManager.Request(uri);
		request.setAllowedNetworkTypes(Request.NETWORK_MOBILE
				| Request.NETWORK_WIFI);
		request.setDestinationInExternalPublicDir(
				Environment.DIRECTORY_DOWNLOADS, "new_version.apk");
		request.setVisibleInDownloadsUi(true);
		long downloadId = dm.enqueue(request);
	}

	protected void onResume() {
		super.onResume();
		DBHelper helper = new DBHelper(this);
		List<Game> temp = initData();
		PackageManager pm = this.getPackageManager();
		for (int j = 0; j < temp.size(); j++) {
			Game game = temp.get(j);
			List<PackageInfo> info = pm.getInstalledPackages(0);
			for (PackageInfo p : info) {
				if (game.getPackages().equals(p.packageName)) {
					helper.updateSate(game.getId());
				}
			}
		}
	}

	public void initView() {
		game_list = (ListView) findViewById(R.id.game_list);
		game_list.setAdapter(new GameAdapter());
	}

	public void finishActivity(View view) {
		this.finish();
	}

	public List<Game> sort() {
		List<Game> temp = initData();
		for (int i = 0; i < temp.size(); i++) {
			Game game = temp.get(i);
			if (game.getExist() == 1) {
				listGame.add(game);
			}
		}

		for (int i = 0; i < temp.size(); i++) {
			Game game = temp.get(i);
			if (game.getExist() != 1) {
				listGame.add(game);
			}

		}
		return listGame;
	}

	class GameAdapter extends BaseAdapter {
		private ViewHolder vh;

		public int getCount() {
			return listGame.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Game game = listGame.get(position);
			if (convertView == null) {
				vh = new ViewHolder();
				convertView = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.game_item, null);
				convertView.setTag(vh);
			} else {
				vh = (ViewHolder) convertView.getTag();
			}
			vh.install = (ImageView) convertView.findViewById(R.id.install);
			vh.tip = (TextView) convertView.findViewById(R.id.game_tip);
			vh.tip.setText(game.getName());
			Drawable drawable = getResources().getDrawable(
					Parameters.iconId[game.getId()]);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			vh.tip.setCompoundDrawables(drawable, null, null, null);
			if (game.getExist() == 1) {
				vh.install.setVisibility(View.VISIBLE);
			} else {
				vh.install.setVisibility(View.GONE);
			}
			convertView.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					if (game.getExist() == 1) {
						Intent intent = new Intent(MainActivity.this,
								DeslActivity.class);
						intent.putExtra("ids", game.getId());
						intent.putExtra("names", game.getName());
						startActivity(intent);
					} else {
						View layout = getLayoutInflater().inflate(
								R.layout.toast, null);
						TextView textarning = (TextView) layout
								.findViewById(R.id.warning);
						textarning.getBackground().setAlpha(150);
						Toast toast = new Toast(getApplicationContext());
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.setDuration(Toast.LENGTH_SHORT);
						toast.setView(layout);
						toast.show();
					}
				}
			});
			return convertView;
		}
	}

	class ViewHolder {
		TextView tip;
		ImageView install;
	}

}
