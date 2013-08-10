/** 
 * �Q�l http://y-anz-m.blogspot.jp/2011/01/android-sqline-database.html
 *  
 **/

package com.kuhmu.mylib.libs;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class SQLiteHelper extends SQLiteOpenHelper {

	// The Android �̃f�t�H���g�ł̃f�[�^�x�[�X�p�X
	private static String DB_PATH;

	private static String DB_NAME = "booktable";
	private static String DB_NAME_ASSET = "booktable.db";

	static final String CREATE_TABLE = "create table booktable ( _id text primary key null, title text not null, genre integer not null, date integer not null, imgUrl text not null, isRead integer not null);";
	private SQLiteDatabase mDataBase;

	private final Context context;

	public SQLiteHelper(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
		DB_PATH = Environment.getDataDirectory().getPath() + "/data/"
				+ context.getPackageName() + "/databases/database.db";
	}

	/**
	 * asset �Ɋi�[�����f�[�^�x�[�X���R�s�[���邽�߂̋�̃f�[�^�x�[�X���쐬����
	 * 
	 **/
	public void createEmptyDataBase() throws IOException {
		boolean dbExist = checkDataBaseExists();
		if (dbExist) {
		} else {
			this.getReadableDatabase();

			try {
				copyDataBaseFromAsset();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
	}

	/**
	 * �ăR�s�[��h�~���邽�߂ɁA���łɃf�[�^�x�[�X�����邩�ǂ������肷��
	 * 
	 * @return ���݂��Ă���ꍇ {@code true}
	 */
	private boolean checkDataBaseExists() {
		SQLiteDatabase checkDb = null;

		try {
			String dbPath = DB_PATH + DB_NAME;
			checkDb = SQLiteDatabase.openDatabase(dbPath, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
		}

		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null ? true : false;
	}

	/**
	 * asset �Ɋi�[�����f�[���x�[�X���f�t�H���g�� �f�[�^�x�[�X�p�X�ɍ쐬��������̃f�[�^�x�[�X�ɃR�s�[����
	 * */
	private void copyDataBaseFromAsset() throws IOException {
		InputStream mInput = context.getAssets().open(DB_NAME_ASSET);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream mOutput = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int size;
		while ((size = mInput.read(buffer)) > 0) {
			mOutput.write(buffer, 0, size);
		}
		mOutput.flush();
		mOutput.close();
		mInput.close();
	}

	public SQLiteDatabase openDataBase() throws SQLException {
		// Open the database
		String myPath = DB_PATH + DB_NAME;
		mDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);
		return mDataBase;
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		arg0.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	public synchronized void close() {
		if (mDataBase != null)
			mDataBase.close();

		super.close();
	}
}
