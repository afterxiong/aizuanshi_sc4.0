package com.sc.aizuanshi;

import net.slidingmenu.tools.os.OffersManager;
import net.slidingmenu.tools.os.PointsChangeNotify;
import net.slidingmenu.tools.os.PointsManager;
import net.slidingmenu.tools.video.VideoAdManager;
import net.slidingmenu.tools.video.listener.VideoAdListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.newqm.sdkoffer.QuMiConnect;
import com.newqm.sdkoffer.QuMiNotifier;
import com.sc.aizuanshi.utils.Parameters;
import com.sc.aizuanshi.utils.Points;
import com.xxsmoneyx.DevInit;
import com.xxsmoneyx.GetTotalMoneyListener;

public class ScoreActivity extends BaseActivity {
	private TextView gameTitle, gold;
	private int id;
	private String name;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String goldNumber = getResources().getString(R.string.gold_tip);
			goldNumber = String.format(goldNumber, p.totalPoints());
			gold.setText(goldNumber);
		}

	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score);
		initialiseData();
		initaliseView();
		initalisePlatfrom();
		pointsLook();
	}

	public void initialiseData() {
		Intent intent = getIntent();
		id = intent.getIntExtra("ids", 0);
		name = intent.getStringExtra("names");
		p = new Points(this);
	}

	public void initaliseView() {
		gold = (TextView) findViewById(R.id.gold);
		gameTitle = (TextView) findViewById(R.id.game_title);
		gameTitle.setText(name);
		Drawable drawable = getResources().getDrawable(Parameters.iconId[id]);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		gameTitle.setCompoundDrawables(drawable, null, null, null);

		findViewById(R.id.follow).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialogFollow();
			}
		});

	}

	public void initalisePlatfrom() {
		OffersManager.getInstance(this).onAppLaunch();
		VideoAdManager.getInstance(this).requestVideoAd();
		youmiPoints = new YoumiPoints();
		PointsManager.getInstance(this).registerNotify(youmiPoints);
		DianleMoney dianleMoney = new DianleMoney();
		DevInit.getTotalMoney(this, dianleMoney);
		QumiPoints qumiPoints = new QumiPoints();
		System.out.println("qumiPoints" + qumiPoints);
		QuMiConnect.getQumiConnectInstance().showpoints(qumiPoints);

	}

	public void pointsLook() {
		setPointsListener(new PointsListener() {

			public void qumi(int points) {
				p.setQumi(points);
				System.out.println("qumit定义回调:" + points);
				handler.sendEmptyMessage(1);
			}

			public void dianle(int points) {
				p.setDianle(points);
				System.out.println("dianle自定义回调:" + points);
				handler.sendEmptyMessage(2);
			}

			public void youmi(int points) {
				p.setYoumi(points);
				System.out.println("youmi自定义回调:" + points);
				handler.sendEmptyMessage(3);
			}

		});
	}

	public void finishActivity(View view) {
		this.finish();
	}

	public void dianle(View view) {
		DevInit.showOffers(this);
	}

	public void pass2(View view) {
		OffersManager.getInstance(this).showOffersWall();
	}

	public void pass3(View view) {
		QuMiConnect.getQumiConnectInstance(this).initOfferAd(this);
		QuMiConnect.getQumiConnectInstance().showOffers(new QumiPoints());
	}

	public void pass4(View view) {
		Toast.makeText(this, "建议在WIFI情况下使用该通道...", Toast.LENGTH_LONG).show();
		VideoAdManager.getInstance(this).showVideo(this, new VideoAdListener() {

			public void onVideoPlayFail() {
			}

			public void onVideoPlayComplete() {
				PointsManager.getInstance(ScoreActivity.this).awardPoints(5);
				Toast.makeText(ScoreActivity.this, "您获得了5个金币的奖励", Toast.LENGTH_SHORT).show();
			}

			public void onVideoCallback(boolean callback) {
			}

			public void onVideoPlayInterrupt() {
			}

			public void onDownloadComplete(String id) {
			}

			public void onNewApkDownloadStart() {
			}

			public void onVideoLoadComplete() {

			}
		});
	}

	public void promote(View view) {
		if (p.totalPoints() > 50) {
			config.setRank(config.getrRank() + 1);
			Toast.makeText(this, "升级成功...", Toast.LENGTH_SHORT).show();
			p.setMoney();
			handler.sendEmptyMessage(4);
		} else {
			final AlertDialog.Builder dialog = new Builder(this);
			dialog.setTitle("温馨提示");
			dialog.setMessage("你升级的金币数量不够，无法升级。请通过金币免费获取通道来获取金币");
			dialog.setNegativeButton("选择其他的通道", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			dialog.show();
		}
	}

	public void feedback(View view) {
		dialogFollow();
	}

	protected void onDestroy() {
		super.onDestroy();
		OffersManager.getInstance(this).onAppExit();
		PointsManager.getInstance(this).unRegisterNotify(youmiPoints);
	}

	class DianleMoney implements GetTotalMoneyListener {

		public void getTotalMoneyFailed(String arg0) {

		}

		public void getTotalMoneySuccessed(String arg0, long arg01) {
			System.out.println("点乐回调:" + arg01);
			listener.dianle((int) arg01);
		}

	}

	class QumiPoints implements QuMiNotifier {

		public void earnedPoints(int arg0, int arg1) {
			getPoints(arg0 + arg1);
		}

		public void getPoints(int arg0) {
			System.out.println("趣米回调:" + arg0);
			listener.qumi(arg0);
		}

		public void getPointsFailed(String arg0) {

		}
	}

	class YoumiPoints implements PointsChangeNotify {

		public void onPointBalanceChange(int arg0) {
			listener.youmi(arg0);
		}

	}

	public void setPointsListener(PointsListener listener) {
		this.listener = listener;
	}

	public PointsListener listener;
	private Points p;
	private YoumiPoints youmiPoints;

	public interface PointsListener {
		void qumi(int points);

		void dianle(int points);

		void youmi(int points);

	}

}
