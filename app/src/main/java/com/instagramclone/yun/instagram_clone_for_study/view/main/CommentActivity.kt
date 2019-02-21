package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.instagramclone.yun.instagram_clone_for_study.R
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {

    private var uid: String? = null
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        val userFragment = UserFragment()

        if(userFragment.arguments != null) {
            println("이프문 들어왔어요")
            uid = userFragment.arguments!!.getString("destinationUid")
        }
        back_btn.setOnClickListener {
            finish()
        }

    }

    override fun onResume() {
        super.onResume()
        getProfileImage()
    }

    fun getProfileImage() {
        uid?.let {
            firestore.collection("profileImages").document(it).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot == null) return@addSnapshotListener
                if(documentSnapshot.data != null) {
                    val url = documentSnapshot.data["image"]
                    Glide.with(this).load(url).apply( RequestOptions().circleCrop() ).into(comment_imageview)
                }
            }
        }
    }
    inner class CommentRecyclerview : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_comment,parent, false)
            //커스텀 뷰 홀더 만들어 주는거는 메모리 누수를 막아주기 위함
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int = 3

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {

        }

    }
}
