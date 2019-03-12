package com.instagramclone.yun.instagram_clone_for_study.model

data class FollowDTO(
        var followerCount: Int = 0,
        //제 3자가 나를 팔로잉 하는지
        var follwers: MutableMap<String, Boolean> = HashMap(),

        var followingCount: Int = 0,
        //내가 누구를 follwing 하는지
        var follwings: MutableMap<String, Boolean> = HashMap()
)