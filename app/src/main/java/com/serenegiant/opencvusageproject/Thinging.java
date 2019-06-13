package com.serenegiant.opencvusageproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.Arrays;

public class Thinging {
    private int fcolor = 0;


    public Bitmap start(int[] pixels, int[] flagmap, int width, int height){
        // 距离变化
        boolean stop = false;
        int times = 0;
        while(!stop) {
            // step one
            boolean s1 = step1Scan(pixels, flagmap, width, height);
            deletewithFlag(pixels, flagmap);
            Arrays.fill(flagmap, 0);
            // step two
            boolean s2 = step2Scan(pixels, flagmap, width, height);
            deletewithFlag(pixels, flagmap);
            Arrays.fill(flagmap, 0);
            if(s1 && s2) {
                Log.i("s1s2",s1+" "+s2);
                stop = true;
            }
        }


        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                int temp = pixels[j*width+i];
                int R = (temp << 16) & 0x00FF0000;
                int G = (temp << 8) & 0x0000FF00;
                int B = temp & 0x000000FF;
                pixels[j*width+i] = 0xFF000000|R|G|B;
            }
        }

        Mat mat = new Mat(width,height, CvType.CV_8U);

        byte[] bytes = new byte[width*height];
        for(int i=0;i<pixels.length;i++){
            if(pixels[i] == 255){
                bytes[i] = -1;
            }else{
                bytes[i] = 0;
            }
        }
        Bitmap bmp = Bitmap.createBitmap(pixels,width,height, Bitmap.Config.ARGB_8888);
//        for(int i=0;i<width;i++){
//            for(int j=0;j<height;j++){
//                mat.put(i,j,bytes[j*width+i]);
//            }
//        }
//
//        Utils.matToBitmap(mat,bmp);
        return bmp;
    }

    private boolean step1Scan(int[] input, int[] flagmap, int width, int height) {
        boolean stop = true;
        int bc = 255 - fcolor;
        int p1=0, p2=0, p3=0;
        int p4=0, p5=0, p6=0;
        int p7=0, p8=0, p9=0;
        int offset = 0;
        for(int row=1; row<height-1; row++) {
            offset = row*width;
            for(int col=1; col<width-1; col++) {
                p1 = (input[offset+col]);
                if(p1 == bc) continue;
                p2 = (input[offset-width+col]);
                p3 = (input[offset-width+col+1]);
                p4 = (input[offset+col+1]);
                p5 = (input[offset+width+col+1]);
                p6 = (input[offset+width+col]);
                p7 = (input[offset+width+col-1]);
                p8 = (input[offset+col-1]);
                p9 = (input[offset-width+col-1]);
                // match 1 - 前景像素  0 - 背景像素
                p1 = (p1 == fcolor) ? 1 : 0;
                p2 = (p2 == fcolor) ? 1 : 0;
                p3 = (p3 == fcolor) ? 1 : 0;
                p4 = (p4 == fcolor) ? 1 : 0;
                p5 = (p5 == fcolor) ? 1 : 0;
                p6 = (p6 == fcolor) ? 1 : 0;
                p7 = (p7 == fcolor) ? 1 : 0;
                p8 = (p8 == fcolor) ? 1 : 0;
                p9 = (p9 == fcolor) ? 1 : 0;

                int con1 = p2+p3+p4+p5+p6+p7+p8+p9;
                String sequence = "" + String.valueOf(p2) + String.valueOf(p3) + String.valueOf(p4) + String.valueOf(p5) +
                        String.valueOf(p6) + String.valueOf(p7) + String.valueOf(p8) + String.valueOf(p9) + String.valueOf(p2);
                int index1 = sequence.indexOf("01");
                int index2 = sequence.lastIndexOf("01");

                int con3 = p2*p4*p6;
                int con4 = p4*p6*p8;

                if((con1 >= 2 && con1 <= 6) && (index1 == index2) && con3 == 0 && con4 == 0) {
                    flagmap[offset+col] = 1;
                    stop = false;
                }

            }
        }
        return stop;
    }


    private boolean step2Scan(int[] input, int[] flagmap, int width, int height) {
        boolean stop = true;
        int bc = 255 - fcolor;
        int p1=0, p2=0, p3=0;
        int p4=0, p5=0, p6=0;
        int p7=0, p8=0, p9=0;
        int offset = 0;
        for(int row=1; row<height-1; row++) {
            offset = row*width;
            for(int col=1; col<width-1; col++) {
                p1 = (input[offset+col]);
                if(p1 == bc) continue;
                p2 = (input[offset-width+col]);
                p3 = (input[offset-width+col+1]);
                p4 = (input[offset+col+1]);
                p5 = (input[offset+width+col+1]);
                p6 = (input[offset+width+col]);
                p7 = (input[offset+width+col-1]);
                p8 = (input[offset+col-1]);
                p9 = (input[offset-width+col-1]);
                // match 1 - 前景像素  0 - 背景像素
                p1 = (p1 == fcolor) ? 1 : 0;
                p2 = (p2 == fcolor) ? 1 : 0;
                p3 = (p3 == fcolor) ? 1 : 0;
                p4 = (p4 == fcolor) ? 1 : 0;
                p5 = (p5 == fcolor) ? 1 : 0;
                p6 = (p6 == fcolor) ? 1 : 0;
                p7 = (p7 == fcolor) ? 1 : 0;
                p8 = (p8 == fcolor) ? 1 : 0;
                p9 = (p9 == fcolor) ? 1 : 0;

                int con1 = p2+p3+p4+p5+p6+p7+p8+p9;
                String sequence = "" + String.valueOf(p2) + String.valueOf(p3) + String.valueOf(p4) + String.valueOf(p5) +
                        String.valueOf(p6) + String.valueOf(p7) + String.valueOf(p8) + String.valueOf(p9) + String.valueOf(p2);
                int index1 = sequence.indexOf("01");
                int index2 = sequence.lastIndexOf("01");

                int con3 = p2*p4*p8;
                int con4 = p2*p6*p8;

                if((con1 >= 2 && con1 <= 6) && (index1 == index2) && con3 == 0 && con4 == 0) {
                    flagmap[offset+col] = 1;
                    stop = false;
                }

            }
        }
        return stop;
    }


    private void deletewithFlag(int[] pixels, int[] flagmap) {
        int bc = 255 - fcolor;
        for(int i=0; i<pixels.length; i++) {
            if(flagmap[i] == 1) {
                pixels[i] = 255;
            }
        }

    }
}
