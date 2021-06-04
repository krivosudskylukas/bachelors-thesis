package com.example.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PickPhoto extends AppCompatActivity {
    private static int RESULT_LOAD_IMAGE = 1;
    ImageView image_view;
    final int divisonConstant = 6500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pick_from_gallery);


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

                Bitmap bmp32 = selectedImage.copy(Bitmap.Config.ARGB_8888, true);
                OpenCVLoader.initDebug();
                Mat ImageMat = new Mat();
                Utils.bitmapToMat(bmp32, ImageMat);

                // Prekonvertovanie farebného obrázku do odtieňov šedej
                Imgproc.cvtColor(ImageMat, ImageMat, Imgproc.COLOR_RGB2GRAY);

                // Rozostrenie obrázku aby neboli brané do úvahy malé čiastočky
                Imgproc.blur(ImageMat,ImageMat,new Size(12,12));
                Imgproc.GaussianBlur(ImageMat,ImageMat,new Size(5,5),0);


                //Vytvorenie kernelu pre morfologické operácie
                Mat kernelGradient = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(10,10));

                //Morfologická operácia Gradient
                Imgproc.morphologyEx(ImageMat,ImageMat,Imgproc.MORPH_GRADIENT,kernelGradient, new Point(0,0),3);
                //Morfologická operácia dilate
                Imgproc.dilate(ImageMat, ImageMat, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(10, 10)),new Point(0,0),2);
                //Tresholdová operácia
                Imgproc.threshold(ImageMat, ImageMat, 20, 255, Imgproc.THRESH_BINARY);
                //Morfologická operácia close
                Imgproc.morphologyEx(ImageMat,ImageMat,Imgproc.MORPH_CLOSE,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(15, 15)),new Point(0,0),5);
                //Morfologická operácia open
                Imgproc.morphologyEx(ImageMat,ImageMat,Imgproc.MORPH_OPEN,kernelGradient,new Point(0,0),2);
                //Morfologická operácia erode
                Imgproc.erode(ImageMat, ImageMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15,15)),new Point(0,0),2);
                //Algoritmus na hľadanie hrán
                Imgproc.Canny(ImageMat, ImageMat, 5, 10 );

                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(ImageMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);


                Mat cannyOutput = new Mat();
                Imgproc.Canny(ImageMat, cannyOutput, 10, 20 );

                Mat drawing = Mat.zeros(cannyOutput.size(), CvType.CV_8UC3);
                int idx = 0;
                double size = 0;
                for (int i = 0; i < contours.size(); i++) {
                    Scalar color = new Scalar(255, 0, 255);
                    if(Imgproc.contourArea(contours.get(i)) > size) {
                        size = Imgproc.contourArea(contours.get(i));
                        idx = i;
                    }
                }


                int j = 0;
                double area = 0;
                for (int i = 0; i < contours.size(); i++) {
                    /*if(Imgproc.contourArea(contours.get(i)) < 1500){
                        continue;
                    }*/
                    Scalar color = new Scalar(255, 0, 255);
                    double[] contourInfo = hierarchy.get(0,i);
                    int q = (int)contourInfo[2]; // this gives next sibling

                    if(q<=0){
                        Imgproc.drawContours(drawing, contours, i, new Scalar(10 + i * 20, 10 + i * 25, 0), 2, Core.LINE_8, hierarchy, 0, new Point());
                        area += Imgproc.contourArea(contours.get(i));
                    }


                }




                double petriArea = Math.PI * 4.4 * 4.4;


                double referencePixelCount = (size / petriArea)-divisonConstant;

                double dishSize = size / referencePixelCount;


                /*Log.d("poect pixelov", " misky : "+ size);
                Log.d("pocet pixelov", "mikroorg : "+ area);

                Log.d("rozmer misky", "Rozmer misky : "+ dishSize);
                Log.d("petri ", "area: "+petriArea);

                Log.d("pomer", "pomer je: "+roundTwoDecimals(referencePixelCount));*/

                double result = area / referencePixelCount;

                //Log.d("VYSLEDOK", "OBSAH "+roundTwoDecimals(result));




                EditText t = findViewById(R.id.textView);
                EditText dayText = findViewById(R.id.measurementDay);
                String day = dayText.getText().toString();
                String micro = t.getText().toString();


                if((day!=null && micro != null) && (!day.trim().isEmpty() && !micro.trim().isEmpty())) {

                    Intent intent = new Intent(PickPhoto.this, AutoSaveInDatabase.class);

                    intent.putExtra("name", micro);
                    intent.putExtra("day", day);
                    intent.putExtra("area", String.valueOf(result));
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(PickPhoto.this, MainActivity.class);
                    startActivity(intent);
                }
                ////////////////////////////////////////////////////////////
                //Utils.matToBitmap(ImageMat, bmp32);
                Utils.matToBitmap(drawing, bmp32);
                ////////////////////////////////////////////////////////////
                //image_view.setImageBitmap(bmp32);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this,"You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }


    double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }



}
