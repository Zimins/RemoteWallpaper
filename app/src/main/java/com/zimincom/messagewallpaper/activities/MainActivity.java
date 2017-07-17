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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.zimincom.messagewallpaper.R;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zimincom.messagewallpaper.R.id.imageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 11;
    private static final int REQUEST_LOAD_IMAGE = 100;
    private static final int REQUEST_READ_STORAGE = 200;

    Context context = this;
    @BindView(R.id.loadImageButton) Button loadImageButton;
    @BindView(R.id.logout) Button logoutButton;
    @BindView(imageView) ImageView loadedImage;
    private FirebaseAuth firebaseAuth;

    // TODO: 2017. 7. 1. image notify when file uploaded
    // TODO: 2017. 7. 10. config firebase 
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseStorage firebaseStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();


//        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
//
//        try {
//            wallpaperManager.setResource(R.drawable.eatman);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d("main","user logined");
                    // TODO: 2017. 7. 13. load some state data
                } else {
                    Log.d("main", "user is null");
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setTheme(R.style.MainAuthTheme)
                            .setLogo(R.drawable.androidvictory)
                            .setAvailableProviders(
                                    Arrays.asList(
                                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                                    ))
                            .build(),RC_SIGN_IN);
                }
            }
        };
        loadImageButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }


    //    private void onSignedInInitialized(String displayName) {
//        mUsername = displayName;
//        attachDataBaseListener();
//    }
//
//    private void onSignedOutCleanUp() {
//        mUsername = ANONYMOUS;
//        mMessageAdapter.clear();
//        detachDataBaseListener();
//    }

    private void loadImageFromGallery() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);
        } else {
            showImageGallery();
        }
    }

    private void showImageGallery() {
        Intent intent =
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_LOAD_IMAGE);
    }
    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.loadImageButton:
                loadImageFromGallery();
                break;
            case R.id.logout:
                AuthUI.getInstance().signOut(this);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(context, "welcome", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(context, "login canceled", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == REQUEST_LOAD_IMAGE) {
            // TODO: 2017. 7. 2. check READ_EXTERNAL_STORAGE
            if (resultCode == RESULT_OK) {
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
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                showImageGallery();
            } else {
                // notify you can't use service
                Toast.makeText(MainActivity.this, "권한 없이 실행 불가합니다", Toast.LENGTH_SHORT).show();
                // make notify message

            }
        }
    }
}
