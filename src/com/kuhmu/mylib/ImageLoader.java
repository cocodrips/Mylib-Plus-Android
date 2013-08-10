package com.kuhmu.mylib;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.GridView;

import com.kuhmu.mylib.libs.BitmapAdapter;

public class ImageLoader extends AsyncTask<String, String, ArrayList<Bitmap>> {
	ArrayList<Bitmap> list = new ArrayList<Bitmap>();
	private GridView gridView;
	int imageHeight;
	BitmapAdapter adapter;
	Context context;
	ArrayList<String> list1 = new ArrayList<String>();

	public ImageLoader(GridView gridView, Context context, int height) {
		this.gridView = gridView;
		this.context = context;
		this.imageHeight = (int) ((double) height / 6);
	}

	@Override
	protected ArrayList<Bitmap> doInBackground(String... arg0) {
		Bitmap bitmap = null;
		for (int i = 0; i < arg0.length; i++) {
			try {
				URL url = new URL(arg0[i]);
				InputStream input = url.openStream();
				bitmap = BitmapFactory.decodeStream(input);

			} catch (Exception e) {
				e.printStackTrace();
			}
			list.add(bitmap);
			publishProgress("ok");
		}

		return list;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		adapter = new BitmapAdapter(context, R.layout.list_item, list,
				imageHeight);
		gridView.setAdapter(adapter);
	}

	protected void onPostExecute(ArrayList<Bitmap> list) {
		adapter = new BitmapAdapter(context, R.layout.list_item, list,
				imageHeight);
		gridView.setAdapter(adapter);
	}

}
