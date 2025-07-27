package com.example.pdfappjava;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pdfappjava.databinding.ActivityUploadMultiplePdfactivityBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UploadMultiplePDFActivity extends AppCompatActivity {

    com.example.pdfappjava.databinding.ActivityUploadMultiplePdfactivityBinding binding;

    StorageReference storageReference;
    List<String> filesList, statusList;
    MultiplePDFAdapter multiplePDFAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUploadMultiplePdfactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        storageReference = FirebaseStorage.getInstance().getReference();

        filesList = new ArrayList<>();
        statusList = new ArrayList<>();

        binding.multilRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        multiplePDFAdapter = new MultiplePDFAdapter(filesList, statusList);
        binding.multilRecyclerView.setAdapter(multiplePDFAdapter);


        binding.uploadMultipleBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Android 11+ (API 30)
                    if (!Environment.isExternalStorageManager()) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivityForResult(intent, 110);
                    } else {
                        proceedWithStorageAccess();
                    }
                } else {
                    // Android 6 - 10 (API 23 to 29)
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    }
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK){

            if (data.getClipData() != null){

                for (int i =0;i<data.getClipData().getItemCount();i++){

                    Uri fileUri = data.getClipData().getItemAt(i).getUri();

                    String fileName = getFileNameFromUri(fileUri);

                    filesList.add(fileName);
                    statusList.add("Loading..");

                    multiplePDFAdapter.notifyDataSetChanged();

                    final int index = i;
                    StorageReference reference = storageReference.child("MultiPlePDF").child(fileName);
                    reference.putFile(fileUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    statusList.remove(index);
                                    statusList.add(index,"Done");
                                    multiplePDFAdapter.notifyDataSetChanged();
                                }
                            });

                }
            }
        }
    }

    @SuppressLint("Range")
    private String getFileNameFromUri(Uri fileUri) {

        String result = null;

        if (Objects.equals(fileUri.getScheme(), "content")){
            Cursor cursor = getContentResolver().query(fileUri,null,null,null,null,null);

            try {
                if (cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                assert cursor != null;
                cursor.close();
            }
        }

        if (result == null){
            result = fileUri.getPath();
            int cut = result.lastIndexOf('/');

            if (cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void proceedWithStorageAccess() {

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File: "), 1);
    }
}