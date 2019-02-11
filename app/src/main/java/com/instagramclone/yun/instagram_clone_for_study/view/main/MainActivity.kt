package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.instagramclone.yun.instagram_clone_for_study.R
import com.instagramclone.yun.instagram_clone_for_study.util.myMakeText
import com.instagramclone.yun.instagram_clone_for_study.util.replace
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
    private val alertFragment: AlertFragment by lazy {
        AlertFragment()
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
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
                replace(R.id.main_content, alertFragment)
                return true
            }
            R.id.action_account -> {
                replace(R.id.main_content, userFragment)
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

        bottom_navigation.setOnNavigationItemSelectedListener ( this )

        ActivityCompat
                .requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
    }
}
