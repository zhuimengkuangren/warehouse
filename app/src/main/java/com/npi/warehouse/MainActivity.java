package com.npi.warehouse;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.npi.warehouse.downloadutil.ApkInstallDownloads;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.bt_download).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //下载链接url
                String url = "";
                ApkInstallDownloads.newInstance(url).installProcess(MainActivity.this);
            }
        });
    }
}
