package com.sc.aizuanshi.utils;

import net.slidingmenu.tools.AdManager;
import net.slidingmenu.tools.os.OffersManager;
import net.slidingmenu.tools.os.PointsChangeNotify;
import net.slidingmenu.tools.os.PointsManager;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.newqm.sdkoffer.QuMiConnect;
import com.newqm.sdkoffer.QuMiNotifier;
import com.sc.aizuanshi.R;
import com.umeng.message.PushAgent;
import com.xxsmoneyx.DevInit;

public class AdvertUtils {
	private static final String TAG = "AdvertUtils";
	private Context context;
	private QumiPoints qumiPoints;
	private SharedPreferences preferences;
	private Editor editor;
	private static final String QUMI = "qumi";

	public AdvertUtils(Context context) {
		super();
		this.context = context;
		initAdvert();
		initConfig();
	}

	public void initAdvert() {
		String qumi_id = context.getResources().getString(R.string.qumi_id);
		String qumi_key = context.getResources().getString(R.string.qumi_key);
		QuMiConnect.ConnectQuMi(context, qumi_id, qumi_key);
		QuMiConnect.getQumiConnectInstance(context).initOfferAd(context);

		String youmi_id = context.getResources().getString(R.string.youmi_id);
		String youmi_key = context.getResources().getString(R.string.youmi_key);
		AdManager.getInstance(context).init(youmi_id, youmi_key, false);
		OffersManager.getInstance(context).onAppLaunch();
		

		DevInit.initGoogleContext((Activity) context, "c637cdec71059668ab9962e243152f57");
		DevInit.setCurrentUserID(context, "123456789");

		// ����
		PushAgent mPushAgent = PushAgent.getInstance(context);
		mPushAgent.enable();
		PushAgent.getInstance(context).onAppStart();
	}

	public void initPointsPlafrom() {
		youmiPoints = new YoumiPoints();
		qumiPoints = new QumiPoints();

		PointsManager.getInstance(context).registerNotify(youmiPoints);
		QuMiConnect.getQumiConnectInstance().showpoints(qumiPoints);
	}

	private void initConfig() {
		preferences = context.getSharedPreferences("Points", Context.MODE_PRIVATE);
		editor = preferences.edit();
	}

	public QumiPoints getQuMiNotifier() {
		return qumiPoints;
	}

	private void setQumi(int points) {
		editor.putInt(QUMI, points);
		editor.commit();
	}

	public int getQumi() {
		return preferences.getInt(QUMI, 0);
	}

	private int totalPoints() {
		int qumi = getQumi();
		int youmi = PointsManager.getInstance(context).queryPoints();
		Log.d(TAG, "趣米:" + qumi + "  有米:" + youmi);
		return qumi + youmi;
	}

	public boolean pointsAdd(int points) {
		return PointsManager.getInstance(context).awardPoints(points);
	}

	public void pointsLessen(int points) {

	}

	class QumiPoints implements QuMiNotifier {

		public void earnedPoints(int pointTotal, int earnedpoint) {
			getPoints(pointTotal + earnedpoint);
		}

		public void getPoints(int pointTotal) {
			Log.d(TAG, "趣米回调,积分发生变化");
			setQumi(pointTotal);
			if (listener != null) {
				listener.checkPoints(totalPoints());
			}
		}

		public void getPointsFailed(String errmsg) {

		}

	}

	class YoumiPoints implements PointsChangeNotify {

		public void onPointBalanceChange(int pointsBalance) {
			Log.d(TAG, "有米回调,积分发生变化");
			listener.checkPoints(totalPoints());
		}
	}

	public PointsListener listener;
	private YoumiPoints youmiPoints;

	public void setPointsListener(PointsListener listener) {
		this.listener = listener;
	}

	public interface PointsListener {
		public void checkPoints(int points);
	}
}
