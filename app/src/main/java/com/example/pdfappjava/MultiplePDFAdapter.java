package com.example.pdfappjava;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MultiplePDFAdapter extends RecyclerView.Adapter<MultiplePDFAdapter.ViewHolder> {

    List<String> fileList,statusList;

    public MultiplePDFAdapter(List<String> fileList, List<String> statusList) {
        this.fileList = fileList;
        this.statusList = statusList;
    }

    @NonNull
    @Override
    public MultiplePDFAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multiple_pdf,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MultiplePDFAdapter.ViewHolder holder, int position) {

        String fileName = fileList.get(position);

        if (fileName.length() > 15)
            fileName = fileName.substring(0,15)+"...";

        holder.fileName.setText(fileName);

        String fileStatus = statusList.get(position);

        if (fileName.equals("Loading.."))
            holder.progressBar.setImageResource(R.drawable.baseline_wifi_protected_setup_24);
        else
            holder.progressBar.setImageResource(R.drawable.baseline_check_circle_24);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView fileIcon,progressBar;
        TextView fileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileIcon = itemView.findViewById(R.id.fileIcon);
            progressBar = itemView.findViewById(R.id.progressBar);
            fileName = itemView.findViewById(R.id.fileName);
        }
    }
}
