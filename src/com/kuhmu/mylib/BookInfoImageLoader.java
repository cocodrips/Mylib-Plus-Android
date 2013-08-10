package com.kuhmu.mylib;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BookInfoImageLoader extends AsyncTask<String, String, Bitmap> {

	private ImageView imageView;

	public BookInfoImageLoader(ImageView imageView) {
		this.imageView = imageView;
	}

	@Override
	protected Bitmap doInBackground(String... arg0) {
		Bitmap bitmap = null;
		try {
			URL url = new URL(arg0[0]);
			InputStream input = url.openStream();
			bitmap = BitmapFactory.decodeStream(input);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return bitmap;
	}

	protected void onPostExecute(Bitmap res) {
		this.imageView.setImageBitmap(res);
	}
}
