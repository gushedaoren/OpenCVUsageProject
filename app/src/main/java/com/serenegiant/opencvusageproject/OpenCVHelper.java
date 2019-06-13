package com.serenegiant.opencvusageproject;

public class OpenCVHelper {
    static {
        System.loadLibrary("OpenCV");
    }
    public static native int[] gray(int[] buf, int w, int h);
    public static native long convertMat(long matinfo, int w, int h);
}
