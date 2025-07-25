package com.example.pdfappjava;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pdfappjava.databinding.ActivityPadviewBinding;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

public class PADViewActivity extends AppCompatActivity {

    ActivityPadviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityPadviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String fileName = getIntent().getStringExtra("fileName");
        String fileUrl = getIntent().getStringExtra("fileUrl");

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching... "+fileName);

        binding.pdfWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
            }
        });

        String url;

        try {
            url = URLEncoder.encode(fileUrl,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        assert fileUrl != null;
        binding.pdfWebView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
    }
}