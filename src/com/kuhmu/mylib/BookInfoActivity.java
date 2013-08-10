package com.kuhmu.mylib;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.kuhmu.mylib.libs.JsonLoader;
import com.kuhmu.mylib.libs.MyLibActivity;

public class BookInfoActivity extends MyLibActivity {
	JsonLoader jsonLoader;
	String jsonResult;
	String appId = "楽天APIのappId";

	public BookInfoActivity() {
		this.currentActivity = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_info);

		Intent intent = getIntent();
		TextView textView = (TextView) findViewById(R.id.textView1);
		String isbn = intent.getStringExtra("isbn");
		apiAccess(isbn);
	}

	private void apiAccess(String isbn) {
		if (isbn.length() != 13 && isbn.length() != 10) {
			finish();
			overridePendingTransition(R.anim.vanish_in, R.anim.vanish_out);
		}

		jsonLoader = new JsonLoader(new JsonLoader.AsyncTaskCallback() {
			public void onTaskEnd(String str, TextView textView) {
				try {
					JSONObject json = new JSONObject(str);
					bookInfoLayout(json);

				} catch (Exception e) {
				}
			}
		}) {
		};
		jsonLoader
				.execute("https://app.rakuten.co.jp/services/api/BooksBook/Search/20130522?format=json&isbn="
						+ isbn + "&applicationId="+appId);
	}

	private void bookInfoLayout(JSONObject json) {
		ArrayList<String> list = new ArrayList<String>();
		String[] dataString = new String[] { "title", "author", "itemPrice",
				"salesDate", "itemCaption" };
		String[] dataName = new String[] { "タイトル", "著者", "値段", "発行日", "概要" };
		TextView caption = (TextView) findViewById(R.id.textView1);
		Display display = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);
		caption.setMaxWidth(p.x / 3);

		try {
			if (json.has("error")) {
				finish();
			}
			JSONArray jsonArray = json.getJSONArray("Items");
			JSONObject item = jsonArray.getJSONObject(0).getJSONObject("Item");
			String imgUrl = item.getString("largeImageUrl");
			imgLayout(imgUrl);

			for (int i = 0; i < dataName.length; i++) {
				if (dataString[i] == "itemCaption") {
					caption.setText(item.getString(dataString[i]));

				} else {
					list.add(dataName[i]);
					if (dataString[i] == "itemPrice") {
						list.add(item.getString(dataString[i]) + "円");
					} else {
						list.add(item.getString(dataString[i]));

					}
				}

			}
		} catch (Exception e) {

		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getApplicationContext(), R.layout.bookinfo_grid_layout, list);
		GridView gridView = (GridView) findViewById(R.id.bookInfoGrid);
		gridView.setAdapter(adapter);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.anim.vanish_in, R.anim.vanish_out);
			break;
		default:
			break;
		}
		return true;
	}

	private void imgLayout(String imgUrl) {
		Display display = getWindowManager().getDefaultDisplay();
		Point p = new Point();
		display.getSize(p);

		ImageView imageView = (ImageView) findViewById(R.id.bookInfoImageView);
		BookInfoImageLoader imageLoader = new BookInfoImageLoader(imageView);
		imageView.setMaxWidth(p.x / 3);
		imageLoader.execute(imgUrl);
		imageView.setScaleType(ScaleType.FIT_CENTER);

	}

}
