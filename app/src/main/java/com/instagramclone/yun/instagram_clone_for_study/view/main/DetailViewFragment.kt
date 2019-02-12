package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.instagramclone.yun.instagram_clone_for_study.R
import kotlinx.android.synthetic.main.fragment_detail.view.*

class DetailViewFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(inflater.context).inflate(R.layout.fragment_detail,container,false)
                    .apply {
                        detailviewfragment_recyclerview.adapter = DetailRecyclerviewAdapter()
                        detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
                    }
    inner class DetailRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent?.context).inflate(R.layout.item_detail,parent,false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view)


        override fun getItemCount(): Int = 3

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            //아이템 뷰 쓴 이유는?

        }

    }
}