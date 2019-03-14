package com.instagramclone.yun.instagram_clone_for_study.model

data class PushDTO(
        var to: String? = null,
        var notification: Notification? = Notification()
){
    data class Notification(
            var body: String? = null,
            var title: String? = null
    )
}