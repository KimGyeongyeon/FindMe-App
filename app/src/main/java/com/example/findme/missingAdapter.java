package com.example.findme;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class missingAdapter extends BaseAdapter {
    Context context;
    missingContainer flags[];
    LayoutInflater inflater;
    private FirebaseStorage storage;
    private StorageReference storageReference;

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
        return flags[position];
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
        
        storageReference.child(flags[i].image_url).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                Toast.makeText(context, "spinner_icon_Failure", Toast.LENGTH_SHORT).show();
            }
        });
        name.setText(flags[i].name);
        return view;
    }
}
