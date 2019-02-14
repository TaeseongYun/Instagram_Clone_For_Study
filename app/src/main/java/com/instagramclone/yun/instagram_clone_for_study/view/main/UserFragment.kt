package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.BoringLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.instagramclone.yun.instagram_clone_for_study.R
import com.instagramclone.yun.instagram_clone_for_study.model.ContentDTO
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment() {


    val uid = FirebaseAuth.getInstance().currentUser?.uid
    companion object {
        private val PICK_PROFILE_FROM_ALBUM = 10

        //팩토리 함수로 호출
        fun callPICK_PROFILE_FROM_ALBUM() = PICK_PROFILE_FROM_ALBUM
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(inflater.context).inflate(R.layout.fragment_user,container,false)
                    .apply {
                        account_iv_profile.setOnClickListener {
                            Intent(Intent.ACTION_PICK).apply {
                                type = "image/*"
                                activity?.startActivityForResult(this, PICK_PROFILE_FROM_ALBUM)
                            }
                        }
                        account_recyclerview.adapter = UserFragmentRecyclerview()
                        account_recyclerview.layoutManager = GridLayoutManager(activity, 3)
                    }

    override fun onResume() {
        super.onResume()
        getProfileImage()
    }
    fun getProfileImage() {
        uid?.let { FirebaseFirestore.getInstance().collection("profileImages").document(it).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
            if(documentSnapshot.data != null) {
                val url = documentSnapshot.data["image"]
                Glide.with(activity).load(url).apply ( RequestOptions().circleCrop() ).into(account_iv_profile)
            }
            }
        }
    }
    inner class UserFragmentRecyclerview : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val contnetDTO: ArrayList<ContentDTO>
        init {
            contnetDTO = ArrayList()
            FirebaseFirestore.getInstance().collection("image").whereEqualTo("uid", uid).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                for(snapshot in querySnapshot.documents) {
                    contnetDTO.add(snapshot.toObject(ContentDTO::class.java))
                }

            }
        }
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3
            val imageview = ImageView(parent?.context).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(width, width)

            }
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview)

        override fun getItemCount(): Int = contnetDTO.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            (holder as CustomViewHolder).imageview.apply {
                Glide.with(holder.itemView.context).load(contnetDTO[position].imageUri)
                        .into(this)
            }

        }

    }

}