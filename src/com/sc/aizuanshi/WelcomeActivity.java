package com.sc.aizuanshi;

import java.util.List;

import net.slidingmenu.tools.AdManager;
import net.slidingmenu.tools.st.SpotManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.dlnetwork.DevInit;
import com.newqm.sdkoffer.QuMiConnect;
import com.sc.aizuanshi.db.DBHelper;
import com.sc.aizuanshi.utils.Game;
import com.sc.aizuanshi.utils.Parameters;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

public class WelcomeActivity extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this, MainActivity.class));
		this.finish();
		checkApks();
		initAd();
	}

	private void initAd() {
		/** 有米 */
		AdManager.getInstance(this).init(Parameters.YOU_MI_ID,
				Parameters.YOU_MI_KEY);
		// 启动广告缓存
		SpotManager.getInstance(this).loadSpotAds();
		// 竖屏动画
		SpotManager.getInstance(this).setSpotOrientation(
				SpotManager.ORIENTATION_PORTRAIT);
		/** 趣米 */
		QuMiConnect.ConnectQuMi(this, Parameters.QU_MI_APP_ID,
				Parameters.QU_MI_KEY);
		// 点乐
		// DevInit.initGoogleContext(this, Parameters.DIAN_LE_ID);
		// 友盟
		PushAgent mPushAgent = PushAgent.getInstance(this);
		mPushAgent.enable();
		PushAgent.getInstance(this).onAppStart();
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
	}

	private void checkApks() {
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
}
