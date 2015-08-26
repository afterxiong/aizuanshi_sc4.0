package com.sc.aizuanshi;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sc.aizuanshi.utils.Play;
import com.sc.aizuanshi.utils.Parameters;

public class PlayAdapter extends BaseAdapter {

	private List<Play> arr;
	private ViewHolder vh;
	private Context context;

	public PlayAdapter(List<Play> arr, Context context) {
		super();
		this.arr = arr;
		this.context = context;
	}

	public int getCount() {
		return arr.size();
	}

	public Object getItem(int position) {
		return arr.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final Play game = arr.get(position);
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.game_item, null);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.install = (ImageView) convertView.findViewById(R.id.install);
		vh.tip = (TextView) convertView.findViewById(R.id.game_tip);
		vh.tip.setText(game.getName());

		Drawable drawable = context.getResources().getDrawable(Parameters.iconId[game.getId()]);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		vh.tip.setCompoundDrawables(drawable, null, null, null);

		if (game.getExist() == 1) {
			vh.install.setVisibility(View.VISIBLE);
		} else {
			vh.install.setVisibility(View.GONE);
		}

		convertView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (game.getExist() == 1) {
					Intent intent = new Intent(context, DeslActivity.class);
					intent.putExtra("ids", game.getId());
					intent.putExtra("names", game.getName());
					context.startActivity(intent);
				} else {
					View layout = ((Activity) context).getLayoutInflater().inflate(R.layout.toast, null);
					TextView textarning = (TextView) layout.findViewById(R.id.warning);
					textarning.getBackground().setAlpha(150);
					Toast toast = new Toast(context);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.setDuration(Toast.LENGTH_SHORT);
					toast.setView(layout);
					toast.show();
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView tip;
		ImageView install;
	}

}
