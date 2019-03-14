package com.instagramclone.yun.instagram_clone_for_study.view.main

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.instagramclone.yun.instagram_clone_for_study.model.PushDTO
import okhttp3.*
import java.io.IOException

class FcmPush() {
    val JSON = MediaType.parse("application/json; charset=utf-8")
    val url = "https://fcm.googleapis.com/fcm/send"
    private val SERVER_KEY = "AIzaSyBA5-jJkpmQUbuk7ScImM8mm_drCQen_WE"

    lateinit var okHttpClient: OkHttpClient
    lateinit var gson: Gson
    init {
        gson = Gson()
        okHttpClient = OkHttpClient()
    }

    fun sendMessage(destinationUid: String, title: String, message: String) {
        FirebaseFirestore.getInstance().collection("pushtokens").document(destinationUid)
                .get().addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        //성공한 결과값의 키값의 value 값을 저장 하겠다.
                        val token = task.result["pushtoken"].toString()

                        PushDTO().apply {
                            this.to = token
                            title.let { notification?.title = it }
                            message.let { notification?.body = it }


                            //이 코드는 원래
                            // to = 키값  notification.title = title  notification.body = message
                            //이런식으로 되어있는데 그것을 Json 형태로 바꾸어서 통신을 가능하게 만들어주는 코드 이다.
                            val body = RequestBody.create(JSON, gson.toJson(this))

                            //포스트맨(api사용 문서)를 보면 헤더 추가 해주는것이 있는데 그부분을 추가 해주는 코드드
                           val request = Request.Builder()
                                    .addHeader("Content-Type", "application/json")
                                    .addHeader("Authorization", "key=$SERVER_KEY")
                                   .url(url)
                                   .post(body)
                                   .build()

                            okHttpClient.newCall(request).enqueue(object : Callback{
                                override fun onFailure(call: Call, e: IOException) {
                                    //인터넷 연결 실패
                                }

                                override fun onResponse(call: Call, response: Response) {
                                    //인터넷 연결 성공

                                    println(response.body()?.string())
                                }

                            })
                        }
                    }
                }
    }
}