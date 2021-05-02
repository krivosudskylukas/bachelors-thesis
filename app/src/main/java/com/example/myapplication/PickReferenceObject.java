package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PickReferenceObject extends AppCompatActivity {
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

                //Imgproc.blur(ImageMat,ImageMat,new Size(1,1));

                Mat element  = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(15, 15), new Point(0, 0));

                Imgproc.morphologyEx(ImageMat, ImageMat, Imgproc.MORPH_TOPHAT, element, new Point(0, 0));



                Imgproc.threshold(ImageMat, ImageMat, 15, 255, Imgproc.THRESH_BINARY);

                /*Imgproc.erode(ImageMat, ImageMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2,2)));

                Imgproc.dilate(ImageMat, ImageMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));*/

                //
                //
                // dilate a erote
                // FAJNY KOD POTIALTO
                //
                //
                //
                //
                //          FUNGUJUCI KOD
                //
                //
                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(ImageMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);



                /*for(int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
                    MatOfPoint matOfPoint = contours.get(idx);
                    if(Imgproc.contourArea(matOfPoint) > 2000){
                        Rect rect = Imgproc.boundingRect(matOfPoint);
                        Imgproc.rectangle(ImageMat, rect.tl(), rect.br(), new Scalar(255, 0, 0));

                    }

                    //Imgproc.minEnclosingCircle();
                    //minEnclosingCircle( (Mat)contours_poly[i], center[i], radius[i]);
                }*/

                //
                //
                //          FUNGUJUCI KOD
                //
                //

                Mat cannyOutput = new Mat();
                Imgproc.Canny(ImageMat, cannyOutput, 100, 100 * 2);

                //Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
                Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
                double size = 0;
                for (int i = 0; i < contours.size(); i++) {
                    Scalar color = new Scalar(0, 255, 255);
                    if(Imgproc.contourArea(contours.get(i)) > 5000 && Imgproc.contourArea(contours.get(i)) < 500000) {
                        size += Imgproc.contourArea(contours.get(i));
                        Imgproc.drawContours(drawing, contours, i, color, 2, Core.LINE_8, hierarchy, 0, new Point());
                    }
                }
                Log.d("rataX", "onActivityResult: "+size);
                Imgproc.putText(drawing,String.valueOf(size),new Point(10,25),1,1,new Scalar(255,0,0));






                //Utils.matToBitmap(ImageMat, bmp32);
                Utils.matToBitmap(drawing, bmp32);


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
