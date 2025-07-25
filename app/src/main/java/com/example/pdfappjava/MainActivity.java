package com.example.pdfappjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pdfappjava.databinding.ActivityMainBinding;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    MainAdapeter mainAdapeter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UploadPDFActivity.class));
            }
        });

        FirebaseRecyclerOptions<FileInfo> options = new FirebaseRecyclerOptions.Builder<FileInfo>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("MyPDF"), FileInfo.class)
                .build();

        binding.mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mainAdapeter = new MainAdapeter(options);
        binding.mainRecyclerView.setAdapter(mainAdapeter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mainAdapeter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainAdapeter.stopListening();
    }
}