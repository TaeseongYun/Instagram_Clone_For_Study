package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.instagramclone.yun.instagram_clone_for_study.R
import kotlinx.android.synthetic.main.fragment_alert.view.*

class AlertFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(inflater.context).inflate(R.layout.fragment_alert,container,false)
                    .apply {
                        alarmfragment_recyclerview.adapter = AlarmRecyclerViewAdapter()
                        alarmfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
                    }

    inner class AlarmRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_comment, parent, false)

            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {

        }


        override fun getItemCount(): Int = 3

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        }

    }
}