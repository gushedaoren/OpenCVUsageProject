package com.serenegiant.opencvusageproject.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.serenegiant.opencvusageproject.ChoosePictureActivity
import com.serenegiant.opencvusageproject.OnItemClickListener
import com.serenegiant.opencvusageproject.R

class PictureAdapter(context: Context,list: ArrayList<Int>,listener:OnItemClickListener ):RecyclerView.Adapter<PictureAdapter.ViewHolder>(){
    var context = context
    var list = list
    var listener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view  = LayoutInflater.from(context).inflate(R.layout.item_picture,null)
        var holder = ViewHolder(view)
        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.image.setImageResource(list.get(position))

        holder.itemView.setOnClickListener(View.OnClickListener {
            listener.itemClick(position)
        })
    }


    class ViewHolder:RecyclerView.ViewHolder{
        constructor(itemView:View):super(itemView){
            image = itemView.findViewById(R.id.image_view)
        }
        var image:ImageView
    }


}