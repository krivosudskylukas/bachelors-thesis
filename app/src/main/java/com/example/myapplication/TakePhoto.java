package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePhoto extends AppCompatActivity {

    final int REQUEST_IMAGE_CAPTURE = 1;
    Button mCaptureBtn;
    ImageView mImageView;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        mCaptureBtn = findViewById(R.id.capture_img_btn);

        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText mikroName = findViewById(R.id.textView);
                String nameOfTheMicro = mikroName.getText().toString();
                if(nameOfTheMicro == null){
                    nameOfTheMicro = "";
                }
                takePicture(nameOfTheMicro);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != RESULT_CANCELED){
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                galleryAddPic();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }
        }
    }



    private File createImageFile(String name) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = name + "_" + timeStamp + "_";
        //Option to save photo to SD-card
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //Saving photo to gallery
        File storage = new File(Environment.getExternalStorageDirectory()+"/DCIM/Camera");

        File image = File.createTempFile(
                imageFileName,  /* prefix*/
                ".jpg",         /* suffix*/
                storage      /* directory*/
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
        //return storage;
    }



    private void takePicture(String name) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(name);
            } catch (IOException ex) {
                Toast.makeText(TakePhoto.this,"Cannot create file.",Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.provider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}

