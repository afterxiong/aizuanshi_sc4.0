package com.sc.aizuanshi;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.sc.aizuanshi.db.DBHelper;
import com.sc.aizuanshi.receiver.AppCheckRecevier;
import com.sc.aizuanshi.receiver.AppCheckRecevier.OnCheckAppListener;
import com.sc.aizuanshi.utils.LevelConfig;
import com.sc.aizuanshi.utils.Play;
import com.sc.aizuanshi.utils.ShareConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseActivity {
	private ListView game_list;
	public String[] gemaPackage = null;
	private List<Play> result = new ArrayList<Play>();
	private DBHelper helper;
	private AppCheckRecevier appCheckRecevier;
	private PlayAdapter playAdapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		checkApp();
		LevelConfig.initLevel(this);
		ShareConfig.intiShareConfig(this);
	}

	public void init() {
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		helper = new DBHelper(this);
		game_list = (ListView) findViewById(R.id.game_list);
		playAdapter = new PlayAdapter(queryApp(), this);
		game_list.setAdapter(playAdapter);
	}

	private void checkApp() {

		appCheckRecevier = new AppCheckRecevier();
		appCheckRecevier.setOnCheckAppListener(new OnCheckAppListener() {

			public void check(String name) {
				queryApp();
				playAdapter.notifyDataSetChanged();
			}
		});
	}

	public List<Play> queryApp() {
		List<Play> temp = new ArrayList<Play>();
		Cursor cursor = helper.query();
		while (cursor.moveToNext()) {
			Play game = new Play();
			game.setId(cursor.getInt(0));
			game.setName(cursor.getString(1));
			game.setPackages(cursor.getString(2));
			game.setExist(cursor.getInt(3));
			temp.add(game);
		}

		PackageManager pm = this.getPackageManager();
		for (int j = 0; j < temp.size(); j++) {
			Play game = temp.get(j);
			List<PackageInfo> info = pm.getInstalledPackages(0);
			for (PackageInfo p : info) {
				if (game.getPackages().equals(p.packageName)) {
					helper.updateSate(game.getId(), 1);
					System.out.println(p.packageName);
				}
			}
		}

		for (int i = 0; i < temp.size(); i++) {
			Play play = temp.get(i);
			if (play.getExist() == 1) {
				result.add(play);
			}
		}

		for (int i = 0; i < temp.size(); i++) {
			Play play = temp.get(i);
			if (play.getExist() != 1) {
				result.add(play);
			}

		}

		return result;
	}

	public void finishActivity(View view) {
		this.finish();
	}

	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
