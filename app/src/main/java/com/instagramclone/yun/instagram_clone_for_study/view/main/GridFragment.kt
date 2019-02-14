package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.instagramclone.yun.instagram_clone_for_study.R
import com.instagramclone.yun.instagram_clone_for_study.model.ContentDTO
import kotlinx.android.synthetic.main.fragment_grid.view.*


class GridFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(container?.context).inflate(R.layout.fragment_grid, container, false)
                    .apply {
                        gridfragment_recyclerview.adapter = GridFragmentRecyclerviewAdapter()
                        gridfragment_recyclerview.layoutManager = GridLayoutManager(activity, 3)
                    }

    inner class GridFragmentRecyclerviewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        var contentDTOs: ArrayList<ContentDTO>
        init {
            contentDTOs = ArrayList()
            FirebaseFirestore.getInstance().collection("image").orderBy("timeStamp").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                for(snapshot in querySnapshot.documents) {
                    contentDTOs.add(snapshot.toObject(ContentDTO::class.java))
                }
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            // 현재 보이는 화면넓이 길이를 3분의 1 해준 값
            val width = resources.displayMetrics.widthPixels / 3

            val imageView = ImageView(parent?.context).apply {
                //
                layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            }
            return CustomViewHolder(imageView)
        }

        private inner class CustomViewHolder(var imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun getItemCount(): Int = contentDTOs.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            (holder as CustomViewHolder).imageView
                    .apply {
                        Glide.with(holder.itemView.context)
                                .load(contentDTOs[position].imageUri)
                                .apply { RequestOptions().centerCrop() }
                                .into(this)
                    }
        }

    }
}