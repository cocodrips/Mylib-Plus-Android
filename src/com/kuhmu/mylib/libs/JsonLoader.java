package com.kuhmu.mylib.libs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import com.kuhmu.mylib.libs.MyAsyncTask.AsyncTaskCallback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.ImageView;

public abstract class JsonLoader extends AsyncTask<String, String, String> {
    private AsyncTaskCallback callback;
    TextView textView;


	public JsonLoader(AsyncTaskCallback callback) {
        this.callback = callback;
	}

	@Override
	protected String doInBackground(String... arg0) {

		// バックグラウンド処理

		String string = null;
		try {

			URL url = new URL(arg0[0]);
			InputStream is = url.openConnection().getInputStream();

			// JSON形式で結果が返るためパースのためにStringに変換する
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line;
			while (null != (line = reader.readLine())) {
				sb.append(line);
			}
			string = new String(sb);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return string;
	}

	protected void onPostExecute(String res) {
//		this.textView.setText(res);
        this.callback.onTaskEnd(res, textView);

	}

	public interface AsyncTaskCallback {
        public void onTaskEnd(String str, TextView textView);
        
	}
}
