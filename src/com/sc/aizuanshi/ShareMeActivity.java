package com.sc.aizuanshi;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.sc.aizuanshi.R;
import com.sc.aizuanshi.utils.Config;
import com.sc.aizuanshi.utils.Parameters;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

public class ShareMeActivity extends BaseActivity implements OnClickListener {
	protected static final String TAG = null;
	private String summary = "���������APP̫ţ��,��ÿ�춼�ܹ���ȡ��ǧ����ʯ������";
	private String title = "��������ʯ";
	private TextView qq, wechats, qzones, wechatmom, gameTitle, before_number;
	private int id;
	private String name;
	private int number;
	private Tencent tencent;
	private IWXAPI api;
	private BroadcastReceiver broadcastReceiver;
	public static final String REFURBISH_DATA = "com.metek.ShareMeActivity.REFURBISH";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		Intent intent = getIntent();
		id = intent.getIntExtra("ids", 0);
		name = intent.getStringExtra("names");
		initView();
		configPlatforms();
		refurbish();

	}

	private void configPlatforms() {
		String qq_id = getResources().getString(R.string.qq_id);
		String weixin_id = getResources().getString(R.string.weixin_id);
		tencent = Tencent.createInstance(qq_id, this.getApplicationContext());
		api = WXAPIFactory.createWXAPI(this, weixin_id);
		api.registerApp(weixin_id);
	}

	public void initView() {
		number = config.getQq() + config.getQzones() + config.getWechats() + config.getWechatmom();
		qq = (TextView) findViewById(R.id.tencen_qq);
		qq.setOnClickListener(this);
		wechats = (TextView) findViewById(R.id.wechats);
		wechats.setOnClickListener(this);
		qzones = (TextView) findViewById(R.id.qzones);
		qzones.setOnClickListener(this);
		wechatmom = (TextView) findViewById(R.id.wechatmom);
		wechatmom.setOnClickListener(this);

		before_number = (TextView) findViewById(R.id.before_number);
		String before = getResources().getString(R.string.before);
		before = String.format(before, number);
		before_number.setText(before);

		gameTitle = (TextView) findViewById(R.id.game_title);
		Drawable drawable = getResources().getDrawable(Parameters.iconId[id]);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		gameTitle.setCompoundDrawables(drawable, null, null, null);
		gameTitle.setText(name);

		String numberStr = getResources().getString(R.string.share_number);
		qq.setText(String.format(numberStr, new Object[] { config.getQq(), Config.QQ_MAX }));
		qzones.setText(String.format(numberStr, new Object[] { config.getQzones(), Config.QZONES_MAX }));
		wechats.setText(String.format(numberStr, new Object[] { config.getWechats(), Config.WECHATS_MAX }));
		wechatmom.setText(String.format(numberStr, new Object[] { config.getWechatmom(), Config.WECHATMOM_MAX }));

		findViewById(R.id.follow).setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				contacts();
			}
		});

	}

	public void raise(View view) {
		number = config.getQq() + config.getQzones() + config.getWechats() + config.getWechatmom();
		if (number >= 10) {
			config.setRank(2);
			startActivity(new Intent(ShareMeActivity.this, DeslActivity.class));
			this.finish();
		} else {
			Toast.makeText(this, "����������û�����...", Toast.LENGTH_LONG).show();
		}
	}

	public void finishActivity(View view) {
		this.finish();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tencen_qq:
			shareQQ();
			break;
		case R.id.wechats:
			shareWechats();
			break;
		case R.id.qzones:
			shareQzones();
			break;
		case R.id.wechatmom:
			shareWechatmom();
			break;

		default:
			break;
		}
		Toast.makeText(this, "���ڷ������Ե�....", Toast.LENGTH_LONG).show();
	}

	public void shareQQ() {
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, summary);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, Parameters.TARGET_URL);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, Parameters.IMAGE_URL);
		tencent.shareToQQ(this, params, new BaseUiListener());
		Parameters.SHARETYPE = 1;
	}

	public void shareQzones() {
		final Bundle params = new Bundle();
		params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
		params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
		params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);
		params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, Parameters.TARGET_URL);
		ArrayList<String> list = new ArrayList<String>();
		list.add(Parameters.IMAGE_URL);
		params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list);
		tencent.shareToQzone(this, params, new BaseUiListener());
		Parameters.SHARETYPE = 2;
	}

	public void shareWechats() {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(this, "����δ��װ΢�ſͻ���", Toast.LENGTH_SHORT).show();
			return;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = Parameters.TARGET_URL;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = summary;
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		msg.thumbData = thumb.getNinePatchChunk();
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
		Parameters.SHARETYPE = 3;
	}

	public void shareWechatmom() {
		if (!api.isWXAppInstalled()) {
			Toast.makeText(this, "����δ��װ΢�ſͻ���", Toast.LENGTH_SHORT).show();
			return;
		}
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = Parameters.TARGET_URL;
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = title;
		msg.description = summary;
		Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		msg.thumbData = thumb.getNinePatchChunk();
		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.message = msg;
		req.scene = SendMessageToWX.Req.WXSceneTimeline;
		api.sendReq(req);
		Parameters.SHARETYPE = 4;
	}

	class BaseUiListener implements IUiListener {

		public void onCancel() {
			Toast.makeText(ShareMeActivity.this, "ȡ������...", Toast.LENGTH_LONG).show();
		}

		public void onComplete(Object obj) {
			Toast.makeText(ShareMeActivity.this, "����ɹ�...", Toast.LENGTH_LONG).show();
			if (Parameters.SHARETYPE == 1) {
				config.setQq();
			} else if (Parameters.SHARETYPE == 2) {
				config.setQzones();
			}
			initView();
		}

		public void onError(UiError error) {
			Toast.makeText(ShareMeActivity.this, "����ܾ�...", Toast.LENGTH_LONG).show();
		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		tencent.onActivityResult(requestCode, resultCode, data);
		initView();
	}

	public void refurbish() {
		broadcastReceiver = new BroadcastReceiver() {

			public void onReceive(Context context, Intent intent) {
				initView();
				System.out.println("�㲥ˢ������");
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(REFURBISH_DATA);
		registerReceiver(broadcastReceiver, filter);
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

}
