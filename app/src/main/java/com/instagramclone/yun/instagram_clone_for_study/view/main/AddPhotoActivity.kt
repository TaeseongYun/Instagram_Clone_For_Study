package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.instagramclone.yun.instagram_clone_for_study.R
import kotlinx.android.synthetic.main.activity_add_photo.*

class AddPhotoActivity : AppCompatActivity() {

    private val PICK_IMAGE_FROM_ALBUM = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        var photoPickerIntent = Intent(Intent.ACTION_PICK).apply {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //왠만한 모든 결과값이 여기에
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM) {
            if(resultCode == Activity.RESULT_OK){
                with(add_photo_image) {
                    setImageURI(data?.data)
                }
            }
            else {
                finish()
            }
        }
    }
}
