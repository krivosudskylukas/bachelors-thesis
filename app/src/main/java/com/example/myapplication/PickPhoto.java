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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

                /*Imgproc.cvtColor(ImageMat, ImageMat, Imgproc.COLOR_RGB2GRAY);

                Imgproc.blur(ImageMat,ImageMat,new Size(1,1));

                Mat element  = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(15, 15), new Point(0, 0));

                Imgproc.morphologyEx(ImageMat, ImageMat, Imgproc.MORPH_TOPHAT, element, new Point(0, 0));



                Imgproc.threshold(ImageMat, ImageMat, 15, 255, Imgproc.THRESH_BINARY);*/

                // Prekonvertovanie farebného obrázku do odtieňov šedej
                Imgproc.cvtColor(ImageMat, ImageMat, Imgproc.COLOR_RGB2GRAY);

                Mat kernelGradient = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(10,10));

                // Rozostrenie obrázku aby neboli brané do úvahy malé čiastočky
                Imgproc.blur(ImageMat,ImageMat,new Size(12,12));
                Imgproc.GaussianBlur(ImageMat,ImageMat,new Size(5,5),0);
                

                Imgproc.morphologyEx(ImageMat,ImageMat,Imgproc.MORPH_GRADIENT,kernelGradient, new Point(0,0),3);

                Imgproc.dilate(ImageMat, ImageMat, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(10, 10)),new Point(0,0),2);

                Imgproc.threshold(ImageMat, ImageMat, 20, 255, Imgproc.THRESH_BINARY);


                Imgproc.morphologyEx(ImageMat,ImageMat,Imgproc.MORPH_CLOSE,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(15, 15)),new Point(0,0),5);

                Imgproc.morphologyEx(ImageMat,ImageMat,Imgproc.MORPH_OPEN,kernelGradient,new Point(0,0),2);
                Imgproc.erode(ImageMat, ImageMat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(15,15)),new Point(0,0),2);


                Imgproc.Canny(ImageMat, ImageMat, 5, 5 );
                //
                //
                //
                /*Imgproc.morphologyEx(ImageMat,ImageMat,Imgproc.MORPH_CLOSE,kernel);*/




                //
                //
                // dilate a erote
                // FAJNY KOD POTIALTO
                //
                //
                //








                //
                //
                //          FUNGUJUCI KOD
                //
                //
                List<MatOfPoint> contours = new ArrayList<>();
                Mat hierarchy = new Mat();
                Imgproc.findContours(ImageMat, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);



                //
                //
                //          FUNGUJUCI KOD
                //
                //


                Mat cannyOutput = new Mat();
                Imgproc.Canny(ImageMat, cannyOutput, 10, 20 );

                //Imgproc.findContours(cannyOutput, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
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
                /*area = Imgproc.contourArea(contours.get(4));
                size = Imgproc.contourArea(contours.get(2));
                Imgproc.drawContours(drawing, contours, 2, new Scalar(255, 0, 0), 2, Core.LINE_8, hierarchy, 0, new Point());
                Imgproc.drawContours(drawing, contours, 3, new Scalar(255, 0, 255), 2, Core.LINE_8, hierarchy, 0, new Point());
                Imgproc.drawContours(drawing, contours, 4, new Scalar(255, 255, 0), 2, Core.LINE_8, hierarchy, 0, new Point());
                Imgproc.drawContours(drawing, contours, 5, new Scalar(2, 0, 255), 2, Core.LINE_8, hierarchy, 0, new Point());*/
                //Imgproc.fillPoly(ImageMat,contours,new Scalar(255,0,0));




                double petriArea = Math.PI * 8.8 * 8.8;


                double referencePixelCount = size / petriArea;

                double dishSize = size / referencePixelCount;


                Log.d("poect pixelov", " misky : "+ size);
                Log.d("pocet pixelov", "mikroorg : "+ area);

                Log.d("rozmer misky", "Rozmer misky : "+ dishSize);
                Log.d("petri ", "area: "+petriArea);

                Log.d("pomer", "pomer je: "+roundTwoDecimals(referencePixelCount));

                double result = area / referencePixelCount;
                Log.d("VYSLEDOK", "OBSAH "+roundTwoDecimals(result/3.5));



                /*Log.d("najvacsi", "najvacsi is: "+size);
                Log.d("dokopy", "dokopy is: "+area);
                Log.d("rataX", "ratio is: "+(size/petriArea));
                Imgproc.putText(drawing,String.valueOf(158),new Point(5,2),1,1,new Scalar(255,0,0));///////////////////////////////////////////////////////////////////*/

                /*int i = 0;
                for(MatOfPoint cont : contours) {
                   String s = String.valueOf(Imgproc.contourArea(cont));
                    Log.d("rata", "onActivityResult: "+s);
                    //Log.d("rata", "onActivityResult: "+cont.width());
                    //Imgproc.drawContours(ImageMat,contours, i,new Scalar(255,0,0),2,Imgproc.LINE_4);
                    i++;
                }*/

                //double i = Imgproc.contourArea(contours.get(0));
                //Toast.makeText(this, "Something went wrong", Integer.parseInt(String.valueOf(i))).show();


                /*double maxArea = 0;
                float[] radius = new float[1];
                Point center = new Point();
                for (int i = 0; i < contours.size(); i++) {
                    MatOfPoint c = contours.get(i);
                    if (Imgproc.contourArea(c) > maxArea) {
                        MatOfPoint2f c2f = new MatOfPoint2f(c.toArray());
                        Imgproc.minEnclosingCircle(c2f, center, radius);
                    }
                }*/
                /*  vector<vector<Point> > contours_poly( contours.size() );
                        vector<Rect> boundRect( contours.size() );
                    vector<Point2f>center( contours.size() );*/


                /*Vector<Point> center = new Vector<>(contours.size());
                Vector<Integer> radius = new Vector<>(contours.size());

                for( int i = 0; i< contours.size(); i++ )
                {
                    Scalar color = new Scalar( 255, 255, 255 );
                    Imgproc.drawContours(ImageMat,contours,i,color);
                    Imgproc.circle(ImageMat, center.get(i), radius.get(i),color);
                    /*drawContours( drawing, contours_poly, i, color, 1, 8, vector<Vec4i>(), 0, Point() );
                    rectangle( drawing, boundRect[i].tl(), boundRect[i].br(), color, 2, 8, 0 );
                    circle( drawing, center[i], (int)radius[i], color, 2, 8, 0 );
                }*/
                /*Mat canny_output = new Mat();
                Imgproc.Canny(ImageMat,canny_output,100,200,3);*/


                ////////////////////////////////////////////////////////////
                //Utils.matToBitmap(ImageMat, bmp32);
                Utils.matToBitmap(drawing, bmp32);
                ////////////////////////////////////////////////////////////
                image_view.setImageBitmap(bmp32);
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
