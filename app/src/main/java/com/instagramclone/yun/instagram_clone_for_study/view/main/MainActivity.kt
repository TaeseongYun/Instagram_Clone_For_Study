package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.instagramclone.yun.instagram_clone_for_study.R
import com.instagramclone.yun.instagram_clone_for_study.util.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val detailViewFragment: DetailViewFragment by lazy {
        DetailViewFragment()
    }
    private val gridFragment: GridFragment by lazy {
        GridFragment()
    }
    private val userFragment: UserFragment by lazy {
        UserFragment()
    }
    private val alarmtFragment: AlarmtFragment by lazy {
        AlarmtFragment()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setToolbarDefault()
        when (item.itemId) {
            R.id.action_home -> {
                replace(R.id.main_content, detailViewFragment)
                return true
            }
            R.id.action_search -> {
                replace(R.id.main_content, gridFragment)
                return true
            }
            R.id.action_gallery -> {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    startActivity(Intent(this, AddPhotoActivity::class.java))
                } else {
                    myMakeText(this, R.string.not_grant, Toast.LENGTH_LONG)
                }

                return true
            }
            R.id.action_favorite -> {
                replace(R.id.main_content, alarmtFragment)
                return true
            }
            R.id.action_account -> {
                replace(R.id.main_content, userFragment)
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val userFragment = UserFragment()
                val bundle = Bundle().apply {
                    putString("destinationUid", uid)
                }
                userFragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.main_content, userFragment).commit()

                return true
            }
        }
        return false
    }

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        with(bottom_navigation) {
            setOnNavigationItemSelectedListener(this@MainActivity)
            selectedItemId = R.id.action_home
        }

        ActivityCompat
                .requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.ACCESS_NETWORK_STATE),1)
        registerPushToken()
    }

    fun registerPushToken() {
        val pushToken = FirebaseInstanceId.getInstance().token
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        var map = mutableMapOf<String, Any>()
        pushToken?.let { map["pushtoken"] = it }
        uid?.let { FirebaseFirestore.getInstance().collection("pushtokens").document(it)
                .set(map)
        }
    }
    override fun onResume() {
        super.onResume()
        setToolbarDefault()
    }
    fun setToolbarDefault() {
        toolbar_title_image.visibility = View.VISIBLE
        toolbar_btn_back.visibility = View.GONE
        toolbar_userName.visibility = View.GONE
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == UserFragment.callPICK_PROFILE_FROM_ALBUM() && resultCode == Activity.RESULT_OK) {
            val imageUri = data?.data
            val auth = FirebaseAuth.getInstance().currentUser?.uid
            auth?.let {auth ->
                imageUri?.let { it1 ->
                    FirebaseStorage.getInstance().reference.child("userProfileImages").child(auth)
                            .putFile(it1).addOnCompleteListener{
                                task ->
                                val url = task.result.downloadUrl.toString()
                                val map = HashMap<String, Any>()

                                //파이어베이스 데이터베이스는 map으로 되어있음
                                map["image"] = url
                                FirebaseFirestore.getInstance().collection("profileImages")
                                        .document(auth).set(map)
                            }
                }
            }
        }
    }
}
