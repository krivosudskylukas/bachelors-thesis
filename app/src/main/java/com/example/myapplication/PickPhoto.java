package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PickPhoto extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    ImageView image_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_photo);


        Button button = (Button) findViewById(R.id.pickPhoto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_view = findViewById(R.id.image_view1);
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
            }
        });


    }


    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                Log.d("tagicel",imageUri.getPath());


                Bitmap bmp32 = selectedImage.copy(Bitmap.Config.ARGB_8888, true);

                Mat ImageMat = new Mat();
                Utils.bitmapToMat(bmp32, ImageMat);

                Imgproc.cvtColor(ImageMat, ImageMat, Imgproc.COLOR_RGB2GRAY);
               // Bitmap bmp = BitmapFactory.decodeStream(imageStream);

                Utils.matToBitmap(ImageMat, bmp32);


                /*Mat binary = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0));
                Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY_INV);
                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchey = new Mat();
                Imgproc.findContours(binary, contours, hierarchey, Imgproc.RETR_TREE,
                        Imgproc.CHAIN_APPROX_SIMPLE);
                Mat draw = Mat.zeros(src.size(), CvType.CV_8UC3);
                for (int i = 0; i < contours.size(); i++) {
                    Scalar color = new Scalar(0, 0, 255);
                    //Calculating the area
                    double cont_area = Imgproc.contourArea(contours.get(i));
                    System.out.println(cont_area);
                    if(cont_area>5000.0){
                        Imgproc.drawContours(draw, contours, i, color, 2,
                                Imgproc.LINE_8, hierarchey, 2, new Point() ) ;
                    } else {
                        color = new Scalar(255, 255, 255);
                        Imgproc.drawContours(draw, contours, i, color, 2, Imgproc.LINE_8,
                                hierarchey, 2, new Point() ) ;
                    }
                }*/

               /* HighGui.imshow("Contours operation", draw);
                HighGui.waitKey();*/

                image_view.setImageBitmap(bmp32);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this,"You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }




}
