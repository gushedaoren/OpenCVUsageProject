//
// Created by Administrator on 2018/5/30 0030.
//
#include <com_serenegiant_opencvusageproject_OpenCVHelper.h>
#include <stdio.h>
#include <stdlib.h>
#include <opencv2/opencv.hpp>
#include <android/bitmap.h>
#include <android/log.h>

using namespace cv;
#define TAG "myDemo-jni" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型

extern "C" {

JNIEXPORT jintArray JNICALL Java_com_serenegiant_opencvusageproject_OpenCVHelper_gray(
        JNIEnv *env, jclass obj, jintArray buf, int w, int h);

JNIEXPORT void JNICALL Java_com_serenegiant_opencvusageproject_OpenCVHelper_convertMat
  (JNIEnv *, jclass, jlong, jint, jint);

JNIEXPORT jintArray JNICALL Java_com_serenegiant_opencvusageproject_OpenCVHelper_gray(
        JNIEnv *env, jclass obj, jintArray buf, int w, int h) {

    AndroidBitmapInfo info;
    jint *cbuf;
    cbuf = env->GetIntArrayElements(buf, JNI_FALSE );
    if (cbuf == NULL) {
        return 0;
    }

    Mat imgData(h, w, CV_8UC4, (unsigned char *) cbuf);

    uchar* ptr = imgData.ptr(0);
    for(int i = 0; i < w*h; i ++){
        //计算公式：Y(亮度) = 0.299*R + 0.587*G + 0.114*B
        //对于一个int四字节，其彩色值存储方式为：BGRA
        int grayScale = (int)(ptr[4*i+2]*0.299 + ptr[4*i+1]*0.587 + ptr[4*i+0]*0.114);
        ptr[4*i+1] = grayScale;
        ptr[4*i+2] = grayScale;
        ptr[4*i+0] = grayScale;
    }

    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, cbuf);
    env->ReleaseIntArrayElements(buf, cbuf, 0);
    return result;
}


JNIEXPORT void JNICALL Java_com_serenegiant_opencvusageproject_OpenCVHelper_convertMat
  (JNIEnv * env, jclass obj, jlong matinfo, jint w, jint h){
   Mat mat = *(Mat *)matinfo;
   mat.at<uchar>(0,0) = 20;

  }

}

