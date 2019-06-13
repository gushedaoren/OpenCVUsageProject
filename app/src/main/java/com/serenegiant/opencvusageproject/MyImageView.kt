package com.serenegiant.opencvusageproject

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.imgproc.Imgproc
import java.util.ArrayList

class MyImageView :View{
    var scale:Double = 0.0
    var clickPost = Point(-1.0,-1.0)
    lateinit var pointLists:ArrayList<ArrayList<MainActivity.MyPoint>>
    lateinit var data:IntArray
    lateinit var labels:IntArray
    var bm: Bitmap? = null
    var chooseColor = Color.RED




    var bmpW = 0
    var bmpH = 0

    constructor(ctx:Context):super(ctx)

    constructor(ctx: Context,attr: AttributeSet):super(ctx,attr)


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                var px = event.x.toInt()
                var py = event.y.toInt()
                var label = labels[py*bmpW+px]
                for(i in pointLists.indices){
                    if(label == pointLists.get(i).get(0).label){
                        Log.i("labelinfo",label.toString())
                        var tempList = pointLists.get(i)
                        for(j in tempList.indices){
                            data[tempList.get(j).y*bmpW+tempList.get(j).x] = chooseColor
                        }
                        break
                    }
                }
                bm = Bitmap.createBitmap(data,0,bmpW,bmpW,bmpH,Bitmap.Config.ARGB_8888)
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        Log.i("dodrawable","dodrawable")
        var paint = Paint()
        paint.color = Color.BLACK
        if(bm != null){
            canvas?.drawBitmap(bm, Matrix(),paint)
        }
    }


    fun setImageBitmap(bm: Bitmap?) {
        this.bm = bm!!
        bmpW = bm!!.width
        bmpH = bm.height
        data = IntArray(bmpH*bmpW)
        bm.getPixels(data,0,bmpW,0,0,bmpW,bmpH)
        invalidate()
    }


}