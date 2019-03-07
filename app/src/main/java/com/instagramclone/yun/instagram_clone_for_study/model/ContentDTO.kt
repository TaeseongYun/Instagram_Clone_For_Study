package com.instagramclone.yun.instagram_clone_for_study.model

import android.net.Uri

data class ContentDTO(var explain: String? = null,
                      var imageUri: String? = null,
                      var uid: String? = null, //유저에 대한 주민등록 번호와 같은것(고유코드라고 생각)
                      var userId: String? = null, //uid가 있지만 유저 이메일이 있는게 누가 올렸는지 확인
                      var timeStamp: Long? = null,
                      var favoriteCount: Int = 0,
                      var favorites: MutableMap<String?, Boolean> = HashMap()) {
    data class Comment(var uid: String? = null,
                       var userId: String? = null,
                       var comment: String? = null,
                       var timeStamp: Long? = null
                       )
} //누가 좋아요 했는지 담는 부분


