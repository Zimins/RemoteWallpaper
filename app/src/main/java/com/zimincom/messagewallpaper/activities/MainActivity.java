package com.zimincom.messagewallpaper.activities;

import android.app.WallpaperManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zimincom.messagewallpaper.R;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Context context = this;

    // TODO: 2017. 7. 1. connect to server
    // TODO: 2017. 7. 1. upload image file to server
    // TODO: 2017. 7. 1. image notify when file uploaded
    // TODO: 2017. 7. 1. file load in phone


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);

        try {
            wallpaperManager.setResource(R.drawable.eatman);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
