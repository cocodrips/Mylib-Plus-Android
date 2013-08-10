package com.kuhmu.mylib.libs;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class SqlCommand {
	SQLiteDatabase db;
	String dbName = "booktable";
	public SqlCommand(SQLiteDatabase db){
		this.db = db;
	}
	
	public boolean insertSql(String id, String title, int genre, int date, String imgUrl, int isRead){
		ContentValues values = new ContentValues();  
        values.put("_id", id);  
        values.put("title", title);  
        values.put("genre", genre);
        values.put("imgUrl", imgUrl);
        values.put("isRead", isRead);
        
        long ret = 0;
        try {
			ret = db.insert(dbName, null, values);
		} catch (Exception e) {
			
		}
        
        if (ret == -1) {
        	 Log.v(title, "データベース insert失敗");
		}
		return false;
	}
	
	public Cursor searchSql(){
		Cursor cursor = db.query(dbName, new String[] {"_id", "data"}, null, null, null, null, "_id DESC");
		return cursor;
	}
	
	public Cursor latestBooks(){
		Cursor cursor = db.query(dbName, new String[] {"_id", "title"}, null, null, null, null, "_id DESC");
		return cursor;
	}
	
	
}
