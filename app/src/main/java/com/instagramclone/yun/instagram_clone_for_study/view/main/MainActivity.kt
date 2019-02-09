package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.instagramclone.yun.instagram_clone_for_study.R
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
                println("1")
                replace(R.id.main_content, detailViewFragment)
                return true
            }
            R.id.action_search -> {
                println("2")
                replace(R.id.main_content, gridFragment)
                return true
            }
            R.id.action_gallery -> {
                println("3")
                return true
            }
            R.id.action_favorite -> {
                println("4")
                replace(R.id.main_content, alertFragment)
                return true
            }
            R.id.action_account -> {
                println("5")
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
    }
}
