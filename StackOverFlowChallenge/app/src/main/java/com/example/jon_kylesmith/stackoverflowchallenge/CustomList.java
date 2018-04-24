package com.example.jon_kylesmith.stackoverflowchallenge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomList extends ArrayAdapter<String>{
    private static final String TAG = CustomList.class.getSimpleName();

    private final Activity context;
    private final ArrayList<String> web;
    private final ArrayList<Bitmap> imageId;
    private final  ArrayList<String> badges;

    CustomList(Activity context, ArrayList<String> web, ArrayList<Bitmap> imageId,ArrayList<String> badges) {
        super(context, R.layout.list_single, web);
        this.context = context;
        this.web = web;
        this.imageId = imageId;
        this.badges = badges;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint({"ViewHolder", "InflateParams"})
        View rowView= inflater.inflate(R.layout.list_single, null, true);

        TextView txtTitle = rowView.findViewById(R.id.user);
        ImageView imageView = rowView.findViewById(R.id.profile);
        TextView txtBadge = rowView.findViewById(R.id.badge) ;
        txtTitle.setText(web.get(position));

        try {
            imageView.setImageBitmap(imageId.get(position));
        }
        catch (Exception e){
            Log.e(TAG, "Exception thrown while setting Image Bitmap", e);
        }

        String badge = txtBadge.getText().toString() + badges.get(position);
        txtBadge.setText(badge);
        return rowView;
    }
}
