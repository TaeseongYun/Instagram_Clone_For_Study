package com.instagramclone.yun.instagram_clone_for_study.model

data class PushDTO(
        val to: String? = null,
        val notification: Notification? = Notification()
){
    data class Notification(
            val body: String? = null,
            val title: String? = null
    )
}