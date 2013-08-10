package com.kuhmu.mylib.libs;

import java.util.List;

import android.R;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ImageView.ScaleType;
 
public class BitmapAdapter extends ArrayAdapter<Bitmap> {
    private int resourceId;
    int height;
 
    public BitmapAdapter(Context context, int resource, List<Bitmap> objects, int height) {
        super(context, resource, objects);
        resourceId = resource;
        this.height = height;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, null);
        }
 
        ImageView view = (ImageView) convertView;
        view.setMinimumHeight(height);
        view.setImageBitmap(getItem(position));
        view.setScaleType(ScaleType.FIT_END);
 
        return view;
    }
    
 
}