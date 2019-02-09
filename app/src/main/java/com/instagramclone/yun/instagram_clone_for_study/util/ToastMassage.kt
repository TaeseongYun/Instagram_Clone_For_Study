package com.instagramclone.yun.instagram_clone_for_study.util

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.widget.Toast

fun AppCompatActivity.myMakeText(context: Context? , message: Int, duration: Int) =
        Toast.makeText(context, message, duration).show()