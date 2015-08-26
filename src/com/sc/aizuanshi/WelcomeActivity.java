package com.sc.aizuanshi;

import android.content.Intent;
import android.os.Bundle;

public class WelcomeActivity extends BaseActivity {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startActivity(new Intent(this, MainActivity.class));
		this.finish();
	}
}
