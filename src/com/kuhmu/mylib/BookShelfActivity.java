package com.kuhmu.mylib;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;

import com.kuhmu.mylib.libs.JsonLibs;
import com.kuhmu.mylib.libs.MyLibActivity;

public class BookShelfActivity extends MyLibActivity {
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mPlanetTitles;
	int currentPos;

	public BookShelfActivity() {
		this.currentActivity = this;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bookshelf);

		// TopPageをはじめ表示
		Intent intent = new Intent(BookShelfActivity.this, MainActivity.class);
		intent.putExtra("message", "Hello SecondActivity.");
		startActivity(intent);

		// actinbar
		mTitle = mDrawerTitle = getTitle();
		mPlanetTitles = getResources().getStringArray(R.array.planets_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mPlanetTitles));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle("");

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}

	}

	// actionbar
	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.cameraBtn:
			moveActivity(BarcodeReaderActivity.class);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	public void flickItem(boolean bool) {
		if (bool) {
			if (currentPos < 5) {
				selectItem(currentPos + 1);
			}
		} else {
			if (currentPos > 0) {
				selectItem(currentPos - 1);
			}
		}

	}

	private void selectItem(int position) {
		currentPos = position;
		// update the main content by replacing fragments
		Context context = getApplicationContext();

		Fragment fragment = new PlanetFragment(context);
		// view.setOnTouchListener((PlanetFragment) fragment);

		Bundle args = new Bundle();
		args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(mPlanetTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		getActionBar().setTitle("");
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * サンプルそのまま利用 Fragment that appears in the "content_frame", shows a planet
	 */
	public static class PlanetFragment extends Fragment implements
			OnTouchListener {
		public static final String ARG_PLANET_NUMBER = "planet_number";
		Context context;
		public final int bookMaxNum = 12;
		String[] isbnCode = new String[bookMaxNum];
		JSONObject[] bookData = new JSONObject[bookMaxNum];

		public PlanetFragment(Context context) {
			this.context = context;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			int num = getArguments().getInt(ARG_PLANET_NUMBER);
			View rootView;
			if (num < 5) {
				rootView = inflater.inflate(R.layout.book_shelf, container,
						false);
				imgLayout(rootView, num);
				View view = (View) rootView.findViewById(R.id.shelfTouchView);
				view.setOnTouchListener(this);

			} else {
				rootView = inflater.inflate(R.layout.search_layout, container,
						false);

			}

			return rootView;
		}

		// もう無理やりタッチ座標とるわ
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int downX = 0;
			int downY = 0;
			int upX = 0;

			int[] bookPosX = new int[] { 100, 325, 550 };
			int[] bookPosY = new int[] { 50, 280, 510, 740 };

			Log.d("TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) event.getX();
				downY = (int) event.getY();
				break;
			case MotionEvent.ACTION_UP:
				upX = (int) event.getX();
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			}

			// フリック
			if (Math.abs(downX - upX) > 800) {
				Log.d("flick", "flick!");
			} else {

				for (int i = 0; i < bookPosX.length; i++) {
					for (int j = 0; j < bookPosY.length; j++) {
						if (bookPosX[i] < downX && downX < bookPosX[i] + 150) {
							if (bookPosY[j] < downY
									&& downY < bookPosY[j] + 180) {
								int number = i + 3 * j;
								Intent intent = new Intent(context,
										BookInfoActivity.class);
								intent.putExtra("isbn", isbnCode[number]);
								startActivity(intent);
							}

						}
					}
				}
			}

			return true;
		}

		public void search(View view) {
			Button searchBtn = (Button) view.findViewById(R.id.searchBtn);
			EditText searchText = (EditText) view
					.findViewById(R.id.editSearchText);
			String str = searchText.getText().toString();

			searchBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
				}
			});

		}

		public void imgLayout(View view, int num) {

			GridView gridView = (GridView) view.findViewById(R.id.grid);
			gridView.setMinimumWidth(view.getWidth() / 2);

			int pLeft = view.getWidth() / 10;
			// gridView.setPadding(pLeft, 0, pLeft, 0);
			// gridView.setColumnWidth(pLeft * 2);

			JsonLibs jsonLibs = new JsonLibs();
			String fileName = "genre" + num + ".json";

			JSONObject json = jsonLibs.jsonFileParse(fileName, getResources()
					.getAssets());
			String[] imgUrls = null;

			try {
				JSONObject bookInfo = json;
				JSONArray jsonArray = bookInfo.getJSONArray("Items");
				int bookNum = Math.min(jsonArray.length(), bookMaxNum);

				imgUrls = new String[bookNum];
				for (int i = 0; i < bookNum; i++) {
					bookData[i] = jsonArray.getJSONObject(i);
					JSONObject item = bookData[i].getJSONObject("Item");
					String imgUrl = item.getString("largeImageUrl");
					imgUrls[i] = imgUrl;
					isbnCode[i] = item.getString("isbn");
				}

				ImageLoader imageLoader = new ImageLoader(gridView, context,
						view.getHeight());
				imageLoader.execute(imgUrls);

			} catch (Exception e) {

			}

		}
	}

}