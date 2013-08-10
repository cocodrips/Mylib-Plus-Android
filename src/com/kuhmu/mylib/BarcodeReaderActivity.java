package com.kuhmu.mylib;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

public class BarcodeReaderActivity extends Activity implements
		SurfaceHolder.Callback, Camera.PreviewCallback,
		Camera.AutoFocusCallback {
	private static final String TAG = "test";

	// PREVIEWの範囲定義(値適当)
	private static final int MIN_PREVIEW_PIXCELS = 470 * 320;
	private static final int MAX_PREVIEW_PIXCELS = 1920 * 1080;

	private Camera myCamera;
	private SurfaceView surfaceView;

	private Boolean hasSurface;
	private Boolean initialized;
	private Boolean AutofocusStart;

	private Point screenPoint;
	private Point previewPoint;

	private int count;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// タスクバー削除,タイトルバー削除
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		hasSurface = false;
		initialized = false;
		AutofocusStart = false;
		count = 5;

		setContentView(R.layout.barcodereader_layout);
	}

	@Override
	protected void onResume() {
		super.onResume();

		surfaceView = (SurfaceView) findViewById(R.id.preview);
		SurfaceHolder holder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(holder);
		} else {
			holder.addCallback(this);
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		}
	}

	@Override
	protected void onPause() {
		closeCamera();
		if (!hasSurface) {
			SurfaceHolder holder = surfaceView.getHolder();
			holder.removeCallback(this);
		}
		super.onPause();
	}

	// surface関連
	// surface生成
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	// surface破棄
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	// surface変更
	// カメラ縦用(未記入)
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	// @param holder
	private void initCamera(SurfaceHolder holder) {
		try {
			openCamera(holder);
		} catch (Exception e) {
			Log.w(TAG, e);
		}
	}

	// @param holder
	private void openCamera(SurfaceHolder holder) throws IOException {
		if (myCamera == null) {
			myCamera = Camera.open();
			if (myCamera == null) {
				throw new IOException();
			}
		}
		myCamera.setPreviewDisplay(holder);

		if (!initialized) {
			initialized = true;
			initFromCameraParameters(myCamera);
		}

		setCameraParameters(myCamera);
		myCamera.startPreview();
	}

	private void closeCamera() {
		if (myCamera != null) {
			myCamera.stopPreview();
			myCamera.release();
			myCamera = null;
		}
	}

	// @param camera
	private void setCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		// setPrevieSize使わないほうがいい？
		parameters.setPreviewSize(previewPoint.x, previewPoint.y);

		camera.setParameters(parameters);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		Log.d(TAG, "onPreviewFrame");
		View BarcodeView = (View) findViewById(R.id.barcode_view);

		int read_x = BarcodeView.getLeft() * previewPoint.x / screenPoint.x;
		int read_y = BarcodeView.getTop() * previewPoint.y / screenPoint.y;
		int width = BarcodeView.getWidth() * previewPoint.x / screenPoint.x;
		int height = BarcodeView.getHeight() * previewPoint.y / screenPoint.y;

		Log.d(TAG, "Barcode.left = " + BarcodeView.getLeft());
		Log.d(TAG, "Barcode.Top = " + BarcodeView.getTop());
		Log.d(TAG, "Barcode_width = " + BarcodeView.getWidth());
		Log.d(TAG, "Barcode_height = " + BarcodeView.getHeight());

		Log.d(TAG, "read_x = " + read_x);
		Log.d(TAG, "read_y = " + read_y);
		Log.d(TAG, "width = " + width);
		Log.d(TAG, "height = " + height);

		// 読み取り位置指定
		PlanarYUVLuminanceSource source = new PlanarYUVLuminanceSource(data,
				previewPoint.x, previewPoint.y, read_x, read_y, width, height,
				false);

		// 以下バーコード読み取り
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Log.d("focus", "focus w =" + w);
		Log.d("focus", "focus h =" + h);
		MultiFormatReader reader = new MultiFormatReader();
		try {
			Result result = reader.decode(bitmap);
			if (result.getText() != null) {
				// Toast.makeText(this, result.getText(),
				// Toast.LENGTH_LONG).show();

				// ここでintent BookInfoActivity.javaにStringText
				Intent intent = new Intent(BarcodeReaderActivity.this,
						BookInfoActivity.class);
				intent.putExtra("isbn", result.getText());
				startActivity(intent);
				finish();

			}
		} catch (Exception e) {
			// Toast.makeText(this, "error: " + e.getMessage(),
			// Toast.LENGTH_LONG).show();
			count--;
			Autofocus_loop();
			bitmap = null;

		}
	}

	// @param camera
	private void initFromCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		/*
		 * Display display = getWindowManager().getDefaultDisplay(); Point size
		 * = new Point(); display.getSize(size); int width=size.x; int
		 * height=size.y;
		 */

		WindowManager manager = (WindowManager) getApplication()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		if (width < height) {
			int tmp = width;
			width = height;
			height = tmp;
		}

		screenPoint = new Point(width, height);
		Log.d(TAG, "screenPoint = " + screenPoint);
		previewPoint = findPreviewPoint(parameters, screenPoint, false);
		Log.d(TAG, "previewPoint = " + previewPoint);
	}

	// @param camera parameters
	// @param screenPoint
	// @param portrait
	// @return previewpoint
	private Point findPreviewPoint(Camera.Parameters parameters,
			Point screenPoint, boolean portrait) {
		Point previewPoint = null;
		int diff = Integer.MAX_VALUE;

		for (Camera.Size supportPreviewSize : parameters
				.getSupportedPreviewSizes()) {
			int pixels = supportPreviewSize.width * supportPreviewSize.height;
			if (pixels < MIN_PREVIEW_PIXCELS || pixels > MAX_PREVIEW_PIXCELS) {
				continue;
			}

			int supportedWidth = portrait ? supportPreviewSize.height
					: supportPreviewSize.width;
			int supportedHeight = portrait ? supportPreviewSize.width
					: supportPreviewSize.height;
			int newDiff = Math.abs(screenPoint.x * supportedHeight
					- supportedWidth * screenPoint.y);

			if (newDiff == 0) {
				previewPoint = new Point(supportedWidth, supportedHeight);
				break;
			}

			if (newDiff < diff) {
				previewPoint = new Point(supportedWidth, supportedHeight);
				diff = newDiff;
			}
		}
		if (previewPoint == null) {
			Camera.Size defaultPreviewSize = parameters.getPreviewSize();
			previewPoint = new Point(defaultPreviewSize.width,
					defaultPreviewSize.height);
		}

		return previewPoint;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (myCamera != null && !AutofocusStart) {
			AutofocusStart = true;
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				Camera.Parameters parameters = myCamera.getParameters();
				if (!parameters.getFocusMode().equals(
						Camera.Parameters.FOCUS_MODE_FIXED)) {
					Autofocus_loop();
				}
			}
		}
		return true;
	}

	public void Autofocus_loop() {
		if (count == 0) {
			count = 5;
			myCamera.autoFocus(this);
		} else {
			myCamera.setOneShotPreviewCallback(this);
		}

	}

	@Override
	public void onAutoFocus(boolean success, Camera camera) {
		camera.setOneShotPreviewCallback(this);
	}

}
