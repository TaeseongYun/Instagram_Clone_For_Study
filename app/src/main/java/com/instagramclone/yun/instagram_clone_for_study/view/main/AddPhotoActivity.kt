package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.instagramclone.yun.instagram_clone_for_study.R
import com.instagramclone.yun.instagram_clone_for_study.model.ContentDTO
import com.instagramclone.yun.instagram_clone_for_study.util.myMakeText
import kotlinx.android.synthetic.main.activity_add_photo.*
import com.instagramclone.yun.instagram_clone_for_study.util.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    private val PICK_IMAGE_FROM_ALBUM = 0
    private lateinit var storage: FirebaseStorage
    var photoUri: Uri? = null
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            startActivityForResult(this, PICK_IMAGE_FROM_ALBUM)
        }


        //사진을 클릭 했을때 다시 앨범이 나오게금 하는 코드
       add_photo_image.setOnClickListener {
            Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                startActivityForResult(this, PICK_IMAGE_FROM_ALBUM)
            }
        }
        addphoto_btn_upload.setOnClickListener {
            contentUpload()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //왠만한 모든 결과값이 여기에
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM) {
            if(resultCode == Activity.RESULT_OK){
                with(add_photo_image) {
                    photoUri = data?.data
                    setImageURI(data?.data)
                }
            }
            else {
                finish()
            }
        }
    }
    fun contentUpload() {
        progress_bar.visibility()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_.png"
        val storageRef = storage.reference.child("images").child(imageFileName)

        photoUri?.let {
            storageRef.putFile(it).addOnSuccessListener { taskSnapshot ->
                myMakeText(this, R.string.upload_success, Toast.LENGTH_LONG)

                //uri 은 파일 이름 Url은 http주소 uri는 Url을 포괄 하는것
                var uri  = taskSnapshot.downloadUrl

                var contentDTO = ContentDTO()
                contentDTO.imageUri = uri.toString()

                contentDTO.uid = auth.currentUser?.uid

                contentDTO.userId = auth.currentUser?.email

                contentDTO.explain = addphoto_edit_explain.text.toString()


                //게시물 업로드 시간
                contentDTO.timeStamp = System.currentTimeMillis()

                firestore.collection("image").document().set(contentDTO)

                setResult(Activity.RESULT_OK)

                finish()
            }.addOnFailureListener {
                progress_bar.unvisibility()

                myMakeText(this, R.string.upload_fail, Toast.LENGTH_LONG)
            }
        }
    }
}
