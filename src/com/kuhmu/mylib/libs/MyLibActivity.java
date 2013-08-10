package com.kuhmu.mylib.libs;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import com.kuhmu.mylib.R;

public class MyLibActivity extends Activity {
	protected Activity currentActivity;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_shelf);
		settingActionBar();

		
	}

	// アクションバーのロゴ
	void settingActionBar() {
		ActionBar bar = getActionBar();
		bar.setTitle("");
		getActionBar().setHomeButtonEnabled(true);
		bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.navbar));

	}

	// アクションバーのアクション
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	protected void moveActivity(Class<?> destination) {
		Intent intent = new Intent(currentActivity, destination);
		intent.putExtra("message", "Hello SecondActivity.");
		startActivity(intent);
		overridePendingTransition(R.anim.vanish_in, R.anim.vanish_out);
	}

}
