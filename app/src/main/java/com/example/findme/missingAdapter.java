package com.example.findme;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class missingAdapter extends BaseAdapter {
    Context context;
    missingContainer flags[];
    LayoutInflater inflater;

    public missingAdapter(Context applicationContext, missingContainer[] flags) {
        this.context = applicationContext;
        this.flags = flags;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return flags.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.spinner, null);
        ImageView icon = view.findViewById(R.id.image);
        TextView name = view.findViewById(R.id.name);
        icon.setImageBitmap(flags[i].image);
        name.setText(flags[i].name);
        return view;
    }
}
