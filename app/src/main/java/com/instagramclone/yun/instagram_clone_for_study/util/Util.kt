package com.instagramclone.yun.instagram_clone_for_study.util

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

fun AppCompatActivity.replace(@IdRes fragmentId: Int, fragment: Fragment, tag: String? = null) =
        supportFragmentManager.beginTransaction().replace(fragmentId, fragment, tag).commit()