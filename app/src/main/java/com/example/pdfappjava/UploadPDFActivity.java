package com.example.pdfappjava;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pdfappjava.databinding.ActivityUploadPdfactivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadPDFActivity extends AppCompatActivity {

    ActivityUploadPdfactivityBinding binding;
    Uri filePath;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityUploadPdfactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        binding.logoImg.setVisibility(View.INVISIBLE);
        binding.cancelCv.setVisibility(View.INVISIBLE);

        binding.cancelCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.logoImg.setVisibility(View.INVISIBLE);
                binding.cancelCv.setVisibility(View.INVISIBLE);
                binding.broseImg.setVisibility(View.VISIBLE);
            }
        });

        binding.broseImg.setOnClickListener(new View.OnClickListener() {
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

        binding.uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToFirebase(filePath);
            }
        });
    }

    private void uploadToFirebase(Uri filePath) {

        progressDialog.setMessage("File Uploading..");
        progressDialog.show();

       String randomValue = String.valueOf(databaseReference.push());

       //Successfully, We get Random value
        Log.e("randomValue",randomValue);

        Toast.makeText(this, randomValue, Toast.LENGTH_SHORT).show();

        StorageReference reference = storageReference.child("MyPDF").child(System.currentTimeMillis()+".pdf");
        reference.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        FileInfo fileInfo = new FileInfo(uri.toString(),binding.fileNameEt.getText().toString(),0,0,0);
                                        databaseReference.child("MyPDF").push().setValue(fileInfo);

                                        binding.logoImg.setVisibility(View.INVISIBLE);
                                        binding.cancelCv.setVisibility(View.INVISIBLE);
                                        binding.broseImg.setVisibility(View.VISIBLE);
                                        binding.fileNameEt.setText("");

                                        progressDialog.dismiss();
                                        Toast.makeText(UploadPDFActivity.this, "Successfully Save FileInfo In Database", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UploadPDFActivity.this, "Failed To Save FileInfo In Database", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        int percent = (int) ((100 * snapshot.getBytesTransferred()) /snapshot.getTotalByteCount());
                        progressDialog.setMessage("Progress: "+percent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadPDFActivity.this, "Failed To Save File Info", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK){

            assert data != null;
            filePath = data.getData();
            Log.e("TAG", String.valueOf(filePath));

            binding.logoImg.setVisibility(View.VISIBLE);
            binding.cancelCv.setVisibility(View.VISIBLE);
            binding.broseImg.setVisibility(View.INVISIBLE);

        }

        if (requestCode == 110) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // ✅ Permission granted
                    proceedWithStorageAccess();
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void proceedWithStorageAccess() {
        // Do the task that requires All Files Access permission
        Toast.makeText(this, "Permission granted! Proceeding...", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent,100);
    }
}