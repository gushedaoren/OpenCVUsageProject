package com.serenegiant.opencvusageproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCvActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cv);
        imageView = findViewById(R.id.image);
        Bitmap temp = BitmapFactory.decodeResource(getResources(),R.drawable.image);
        Log.i("bmpwin",temp.getWidth()+" "+temp.getHeight());
        Mat mat = new Mat();
        Utils.bitmapToMat(temp,mat);
        Imgproc.cvtColor(mat,mat,Imgproc.COLOR_BGR2GRAY);
        byte [] data = new byte[temp.getWidth()*temp.getHeight()];
        mat.get(0,0,data);
        Log.i("colorInfo",data[0]+" "+data[1]);
        OpenCVHelper.convertMat(mat.getNativeObjAddr(),mat.cols(),mat.rows());
        mat.get(0,0,data);
        Log.i("colorInfo",data[0]+" "+data[1]);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(
                        R.drawable.image)).getBitmap();
                int w = bitmap.getWidth(), h = bitmap.getHeight();
                int[] pix = new int[w * h];
                bitmap.getPixels(pix, 0, w, 0, 0, w, h);
                int [] resultPixes=OpenCVHelper.gray(pix,w,h);
                Bitmap result = Bitmap.createBitmap(w,h, Bitmap.Config.RGB_565);
                result.setPixels(resultPixes, 0, w, 0, 0,w, h);
                imageView.setImageBitmap(result);
            }
        });
    }
}
