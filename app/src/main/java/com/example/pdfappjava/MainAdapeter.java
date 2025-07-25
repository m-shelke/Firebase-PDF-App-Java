package com.example.pdfappjava;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.io.File;

public class MainAdapeter extends FirebaseRecyclerAdapter<FileInfo,MainAdapeter.MainViewHolder> {

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public MainAdapeter(@NonNull FirebaseRecyclerOptions<FileInfo> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MainViewHolder holder, int position, @NonNull FileInfo model) {

        holder.title.setText(model.getFileName());
        holder.nov.setText(String.valueOf(model.getNov()));
        holder.nol.setText(String.valueOf(model.getNol()));
        holder.nod.setText(String.valueOf(model.getNod()));

        holder.pdfImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.pdfImg.getContext(),PADViewActivity.class);
                intent.putExtra("fileName",model.getFileName());
                intent.putExtra("fileUrl",model.getFileUrl());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.pdfImg.getContext().startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview,parent,false);
        return new MainViewHolder(view);
    }

    public class MainViewHolder extends RecyclerView.ViewHolder{

        ImageView pdfImg;
        TextView title,nov,nol,nod;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            pdfImg = itemView.findViewById(R.id.item_pdf);
            title = itemView.findViewById(R.id.item_title);
            nov = itemView.findViewById(R.id.nov);
            nol = itemView.findViewById(R.id.nol);
            nod = itemView.findViewById(R.id.nod);
        }
    }
}
