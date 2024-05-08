package com.example.gallery;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> imagePaths;

    public ImageAdapter(Context context) {
        this.context = context;
        this.imagePaths = new ArrayList<>();
    }

    public void setImagePaths(ArrayList<String> imagePaths) {
        this.imagePaths = imagePaths;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return imagePaths.size();
    }

    @Override
    public Object getItem(int position) {
        return imagePaths.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Uri imageUri = Uri.parse(imagePaths.get(position));

        // Set the image URI to the ImageView
        viewHolder.imageView.setImageURI(imageUri);
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
    }
}
