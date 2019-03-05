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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.instagramclone.yun.instagram_clone_for_study.R
import com.instagramclone.yun.instagram_clone_for_study.model.ContentDTO
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {

    private lateinit var contentUid: String
    private var uid: String? = null
    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)


        back_btn.setOnClickListener {
            finish()
        }

        with(comment_recyclerview) {
            adapter = CommentRecyclerview()
            layoutManager = LinearLayoutManager(this@CommentActivity)
        }

        contentUid = intent.getStringExtra("contentUid")
        comment_btn_send.setOnClickListener {
            ContentDTO.Comment().apply {
                userId = FirebaseAuth.getInstance().currentUser?.email
                comment = comment_edit_message.text.toString()
                uid = FirebaseAuth.getInstance().currentUser?.uid

                //currnetTimeMillis()는 1970년 1월 1일을 시작점으로 두고 현재 년도 까지 의 시간
                timeStamp = System.currentTimeMillis()

               firestore.collection("image")
                       .document(contentUid)
                       .collection("comment")
                       .document()
                       .set(this)
            }

        }
    }

    override fun onResume() {
        super.onResume()
//        getProfileImage()
    }

//    fun getProfileImage() {
//        uid?.let {
//            firestore.collection("profileImages").document(it).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
//                if(documentSnapshot == null) return@addSnapshotListener
//                if(documentSnapshot.data != null) {
//                    val url = documentSnapshot.data["image"]
//                    Glide.with(this).load(url).apply( RequestOptions().circleCrop() ).into(comment_imageview)
//                }
//            }
//        }
//    }
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
