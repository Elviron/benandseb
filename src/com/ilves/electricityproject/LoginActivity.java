package com.ilves.electricityproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_screen);
	}
	
	public void onLogin(View v) {
		Intent intent = this.getIntent();
		intent.putExtra("isLoggedIn", true);
		this.setResult(RESULT_OK, intent);
		finish();
	}

}
