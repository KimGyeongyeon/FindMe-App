package com.example.findme.post;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.findme.R;
import com.example.findme.domain.model.MissingContainer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class missingAdapter extends BaseAdapter {
    Context context;
//    missingContainer flags[];
    private ArrayList<MissingContainer> newflags;
    LayoutInflater inflater;
    private FirebaseStorage storage;
    private StorageReference storageReference;

//    public missingAdapter(Context applicationContext, missingContainer[] flags) {
//        this.context = applicationContext;
//        this.flags = flags;
//        inflater = (LayoutInflater.from(applicationContext));
//    }

    public missingAdapter(Context applicationContext, ArrayList<MissingContainer> newflags) {
        this.context = applicationContext;
        this.newflags = newflags;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
//        return flags.length;
        if(newflags == null) return 0;
        return newflags.size();
    }

    @Override
    public Object getItem(int position) {
//        return flags[position];
        return newflags.get(position);
    }

    public String getItemName(int position) {
//        return flags[position];
        return newflags.get(position).getName();
    }

    public String getItemDocId(int position) {
//        return flags[position];
        return newflags.get(position).getDocId();
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
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

//        Toast.makeText(context, newflags.get(i).image_url, Toast.LENGTH_SHORT).show();
        storageReference.child(newflags.get(i).getImage_url()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(icon);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //이미지 로드 실패시
                Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        name.setText(newflags.get(i).getName());
        return view;
    }
}
