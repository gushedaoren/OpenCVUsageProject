package com.serenegiant.opencvusageproject

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.StaggeredGridLayoutManager
import com.serenegiant.opencvusageproject.adapter.PictureAdapter
import kotlinx.android.synthetic.main.activity_choose_picture.*

class ChoosePictureActivity : AppCompatActivity() , OnItemClickListener {
    override fun itemClick(position:Int) {
        var intent = Intent()
        intent.putExtra("resourceId",list.get(position))
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    lateinit var adapter:PictureAdapter
    var array:IntArray = intArrayOf(R.drawable.picture1,R.drawable.picture5,R.drawable.picture7,
            R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4)
    var list:ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_picture)

        for(i in array.indices){
            list.add(array[i])
        }
        adapter = PictureAdapter(this,list,this)
        recyclerview.adapter = adapter
        recyclerview.layoutManager = StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL)
    }

}
