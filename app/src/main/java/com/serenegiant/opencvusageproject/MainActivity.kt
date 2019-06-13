package com.serenegiant.opencvusageproject

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.util.*
import kotlin.collections.ArrayList
import com.serenegiant.opencvusageproject.widget.ColorPickerDialog


class MainActivity : AppCompatActivity() {
    lateinit var dialog: ColorPickerDialog
    val front = -1
    val bg = 0
    var handler = Handler()

    class MyPoint constructor(x: Int, y: Int, label: Int) {
        var x: Int = x
        var y: Int = y
        var label: Int = label
    }

    private var pointLists: ArrayList<ArrayList<MyPoint>> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        OpenCVLoader.initDebug()
        color_tv.setBackgroundColor(image_view.chooseColor)
        color_btn.setOnClickListener(View.OnClickListener {
            dialog = ColorPickerDialog(this, image_view.chooseColor,
                    "选择颜色",
                    ColorPickerDialog.OnColorChangedListener { color ->
                        image_view.chooseColor = color
                        color_tv.setBackgroundColor(image_view.chooseColor)
                    })
            dialog.show()
        })

        image_view.post(Runnable {
            kotlin.run {
                dealPicture(R.drawable.picture1)
            }
        })


        choose_picture.setOnClickListener(View.OnClickListener {
            var intent = Intent()
            intent.setClass(this, ChoosePictureActivity::class.java)
            startActivityForResult(intent, 202)
        })
    }


    fun from(bmp: Bitmap) {
        val mat = Mat()
        Utils.bitmapToMat(bmp, mat)
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)
        var thresold = BinaryZation.ChooseMethod(mat,5)
        Imgproc.threshold(mat, mat, thresold.toDouble(), 255.0, Imgproc.CV_THRESH_BINARY)
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(3.0, 3.0))
        Imgproc.erode(mat, mat, kernel)
        Imgproc.dilate(mat, mat, kernel)
        val w = mat.cols()
        val h = mat.rows()
        val dataOld = ByteArray(w * h)
        mat.get(0, 0, dataOld)
        Utils.matToBitmap(mat, bmp)
        handler.post(Runnable {
            kotlin.run {
                image_view.setImageBitmap(bmp)
            }
        })
        val data = IntArray(w * h)
        var black = 0
        var white = 0
        for (i in dataOld.indices) {
            data[i] = dataOld[i].toInt()
            if (data[i] == -1) {
                data[i] = 0
                white++
            } else {
                data[i] = -1
                black++
            }
        }
        Log.i("blackwhite", black.toString() + " " + white)

        var label = 1
        val stack = Stack<Int>()
        val list = ArrayList<Int>()
        stack.push(0)
        stack.push(1)
        for (j in 1 until h-1) {
            for (i in 1 until w-1) {
                if (data[j * w + i] == 0) {
                    list.clear()
                    if (data[j * w + i - 1] >= 0) {
                        list.add(data[j * w + i - 1])
                    }
                    if (data[(j - 1) * w + i] >= 0) {
                        list.add(data[(j - 1) * w + i])
                    }
                    if (data[(j - 1) * w + i - 1] >= 0) {
                        list.add(data[(j - 1) * w + i - 1])
                    }
                    if (data[(j - 1) * w + i + 1] >= 0) {
                        list.add(data[(j - 1) * w + i + 1])
                    }

                    if (list.size == 0) {
                        stack.push(++label)
                        data[j * w + i] = label
                    } else {
                        sort(list)
                        val smallLabel = list[0]
                        data[j * w + i] = smallLabel


                        for (k in 1 until list.size) {
                            var anotherLabel = stack[list[k]]
                            if (smallLabel < anotherLabel) {
                                stack[anotherLabel] = smallLabel
                                anotherLabel = smallLabel
                            } else if (smallLabel > anotherLabel) {
                                stack[smallLabel] = anotherLabel
                            }
                        }
                    }
                }
            }
        }


        for (i in 2 until stack.size) {
            var curLabel = stack[i]
            var preLabel = stack[curLabel]
            while (preLabel != curLabel) {
                curLabel = preLabel
                preLabel = stack[preLabel]
            }
            stack[i] = curLabel
        }

        val tempList = ArrayList<Int>()
        for (i in 0 until w) {
            for (j in 0 until h) {

                if (data[j * w + i] > 0) {
                    val temp = data[j * w + i]
                    if (!tempList.contains(stack[temp])) {
                        tempList.add(stack[temp])
                    }
                    data[j * w + i] = stack[temp]
                }
            }
        }

        pointLists.clear()
        for (i in 0 until w) {
            for (j in 0 until h) {
                if (data[j * w + i] == front) {
                    continue
                }
                var flag = false
                var tempList: ArrayList<MyPoint> = ArrayList()
                for (m in pointLists.indices) {
                    if (pointLists.get(m).size > 0) {
                        if (pointLists.get(m).get(0).label == data[j * w + i]) {
                            flag = true
                            tempList = pointLists.get(m)
                            break
                        }
                    }
                }
                tempList.add(MyPoint(i, j, data[j * w + i]))
                if (!flag) {
                    pointLists.add(tempList)
                }
            }
        }
        image_view.pointLists = pointLists
        image_view.labels = data
        Log.i("listsize", tempList.size.toString() + "")
        Log.i("listsizes", pointLists.size.toString() + "")
    }


    private fun sort(data: ArrayList<Int>) {
        for (i in data.indices) {
            for (j in i until data.size) {
                if (data[j] < data[i]) {
                    val temp = data[i]
                    data[i] = data[j]
                    data[j] = temp

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 202 && resultCode == Activity.RESULT_OK) {
            var resourceId = data?.getIntExtra("resourceId", -1)
            if (resourceId != -1) {
                dealPicture(resourceId!!)
            }

        }
    }


    fun dealPicture(resourceId: Int) {
        var view = LayoutInflater.from(this).inflate(R.layout.dialog_wait, null);
        var dialog: AlertDialog = AlertDialog.Builder(this).setView(view).create()
        dialog.show()
        val bmp = BitmapFactory.decodeResource(resources, resourceId!!)
        val scale = Math.min(image_view.width / bmp.width.toDouble(), image_view.height / bmp.height.toDouble())
        var matrix = Matrix()
        matrix.setScale(scale.toFloat(), scale.toFloat())
        var newBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
        Thread(Runnable {
            kotlin.run {
                from(newBmp)
                handler.post(Runnable { kotlin.run {
                    dialog.dismiss()
                } })
            }
        }).start()

    }


}
