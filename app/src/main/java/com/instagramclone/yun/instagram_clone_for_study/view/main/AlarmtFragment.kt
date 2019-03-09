package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.instagramclone.yun.instagram_clone_for_study.R
import com.instagramclone.yun.instagram_clone_for_study.model.AlarmDTO
import kotlinx.android.synthetic.main.fragment_alert.view.*
import kotlinx.android.synthetic.main.item_comment.*
import kotlinx.android.synthetic.main.item_comment.view.*

class AlarmtFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(inflater.context).inflate(R.layout.fragment_alert,container,false)
                    .apply {
                        alarmfragment_recyclerview.adapter = AlarmRecyclerViewAdapter()
                        alarmfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
                    }

    inner class AlarmRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        val alarmDTOList = ArrayList<AlarmDTO>()

        init {
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            FirebaseFirestore.getInstance().collection("alarms").whereEqualTo("destinationUid",uid)
                    .orderBy("timeStamp").addSnapshotListener{querySnapshot, firebaseFirestoreException ->
                        alarmDTOList.clear()
                        if(querySnapshot == null) return@addSnapshotListener
                        for(snapshot in querySnapshot.documents) {
                            alarmDTOList.add(snapshot.toObject(AlarmDTO::class.java))
                        }

                        notifyDataSetChanged()
                    }
        }
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_comment, parent, false)

            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view) {

        }


        override fun getItemCount(): Int = alarmDTOList.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

            val commentTextview = holder?.itemView?.commentviewItem_textview_profile

            when(alarmDTOList[position].kind) {
                0 -> {
                    val str_0 = alarmDTOList[position].userId + getString(R.string.alarm_favorite)
                    commentTextview?.text = str_0
                }

                1 -> {
                    val str_1 = alarmDTOList[position].userId +
                            getString(R.string.alarm_who) +
                            alarmDTOList[position].message +
                            getString(R.string.alarm_comment)
                    commentTextview?.text = str_1
                }

                2 -> {
                    val str_2 = alarmDTOList[position].userId +
                            getString(R.string.alarm_follow)
                    commentTextview?.text = str_2
                }
            }

        }

    }
}