package com.zimincom.messagewallpaper.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zimincom.messagewallpaper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zimincom.messagewallpaper.R.id.imageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static int REQUEST_LOAD_IMAGE = 100;
    private static int REQUEST_READ_STORAGE = 200;

    Context context = this;


    // TODO: 2017. 7. 1. connect to server
    // TODO: 2017. 7. 1. upload image file to server
    // TODO: 2017. 7. 1. image notify when file uploaded

    @BindView(R.id.loadImageButton) Button loadImageButton;
    @BindView(imageView) ImageView loadedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
//
//        try {
//            wallpaperManager.setResource(R.drawable.eatman);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        loadImageButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.loadImageButton:
                loadImageFromGallery();
                break;
        }
    }

    private void loadImageFromGallery() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_LOAD_IMAGE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_LOAD_IMAGE) {
                // TODO: 2017. 7. 2. check READ_EXTERNAL_STORAGE

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                loadedImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.length > 0) {
                //permission granted
            } else {
                // notify you can't use service
            }
        }
    }
}
