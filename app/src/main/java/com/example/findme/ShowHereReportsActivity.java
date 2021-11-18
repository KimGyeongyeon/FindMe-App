package com.example.findme;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.SnapshotParser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.Date;


public class ShowHereReportsActivity extends AppCompatActivity {

    private FirestoreRecyclerAdapter<HereReportCard, personHolder> adapter;
//    recyclerView.setLayoutManager(new LinearLayoutManager(this));

//    personAdapter adapter; // Create Object of the Adapter class
    public String petId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_here_reports);

        Intent intent = getIntent();
        petId = intent.getStringExtra("petId");

        RecyclerView recyclerView = findViewById(R.id.recycler1);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = FirebaseFirestore.getInstance().collection("pet").document(petId)
                .collection("here").orderBy("date", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<HereReportCard> options = new FirestoreRecyclerOptions.Builder<HereReportCard>()
                .setQuery(query, new SnapshotParser<HereReportCard>() {
                    @NonNull
                    @Override
                    public HereReportCard parseSnapshot(@NonNull DocumentSnapshot snapshot) {
                        return new HereReportCard(snapshot.getDate("date"), snapshot.getString("userMail"), snapshot.getString("img"));
                    }
                }).build();

        adapter = new FirestoreRecyclerAdapter<HereReportCard, personHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull personHolder holder, int position, @NonNull HereReportCard model) {
                holder.setDate(model.getDate());
                holder.setId(model.getUserMail());
                holder.setImage(model.getImgUrl());
            }

            @NonNull
            @Override
            public personHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.here_report_card, parent, false);
                return new personHolder(view);
            }
        };

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) { return; }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            adapter.notifyDataSetChanged();
                            break;
                        case MODIFIED:
                            adapter.notifyDataSetChanged();
                            break;
                        case REMOVED:
                            adapter.notifyDataSetChanged();
                            break;
                    }
                }
            }
        });

        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }



    private class personHolder extends RecyclerView.ViewHolder {
        private View view;

        public personHolder(View itemView){
            super(itemView);
            view = itemView;
        }

        void setDate(Date date){
            TextView textView = view.findViewById(R.id.here_report_card_date);
            textView.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(date));
        }

        void setId(String userMail){
            TextView textView = view.findViewById(R.id.here_report_card_id);
            String[] temp = userMail.split("@");
            textView.setText("by " + temp[0]);
        }

        void setImage(String imgUrl){
            ImageView imageView = view.findViewById(R.id.here_report_card_image);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference photoReference = storageReference.child(imgUrl);
            photoReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            });
        }

    }





}
