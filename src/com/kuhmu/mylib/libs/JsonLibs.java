package com.kuhmu.mylib.libs;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONObject;

import android.content.res.AssetManager;

public class JsonLibs {
	public JSONObject jsonFileParse(String filename, AssetManager as) {
		JSONObject json = null;
		// jsonÉtÉ@ÉCÉãì«Ç›çûÇ›
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = as.open(filename);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str;
            while((str = br.readLine()) != null){
                sb.append(str +"\n");
            }
            
            json = new JSONObject(new String(sb));     
		} catch (Exception e) {

		}
		return json;
	}
}
