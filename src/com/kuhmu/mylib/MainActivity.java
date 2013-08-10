package com.kuhmu.mylib;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private final Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
		handler.postDelayed(new transrateBookShelfActivity(), 3000);
	}

	class transrateBookShelfActivity implements Runnable {
		public void run() {
			finish();
			overridePendingTransition(R.anim.vanish_in, R.anim.vanish_out);

		}
	}

}
