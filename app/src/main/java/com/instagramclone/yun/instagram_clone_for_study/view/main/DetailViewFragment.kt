package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import com.instagramclone.yun.instagram_clone_for_study.R
import com.instagramclone.yun.instagram_clone_for_study.model.AlarmDTO
import com.instagramclone.yun.instagram_clone_for_study.model.ContentDTO
import com.instagramclone.yun.instagram_clone_for_study.model.FollowDTO
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_detail.view.*
import kotlinx.android.synthetic.main.item_detail.view.*

class DetailViewFragment : Fragment() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var user: FirebaseAuth
    private lateinit var fcmPush: FcmPush
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(inflater.context).inflate(R.layout.fragment_detail,container,false)
                    .apply {
                        firestore = FirebaseFirestore.getInstance()
                        user = FirebaseAuth.getInstance()
                        fcmPush = FcmPush()
                        detailviewfragment_recyclerview.adapter = DetailRecyclerviewAdapter()
                        detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)
                    }
    inner class DetailRecyclerviewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        //        이것은 image파일을 담아주는 배열
        val contentDTO: ArrayList<ContentDTO>
        //        이것은 image 파일 안에 있는 내용을 담아주는 배열
        val contentUidList: ArrayList<String>

        init {
            contentDTO = ArrayList()
            contentUidList = ArrayList()
            //orderBy("이름") 해놓으면 시간대별로 정렬된다.

//            현재 로그인된 유저의 UID(유저 주민등록번호)
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            uid?.let { firestore.collection("user").document( it ).get().addOnCompleteListener {
                task ->
                    if(task.isSuccessful) {
                    //결과를 FollowDTO로 캐스팅 하겠다
                        val userDTO = task.result.toObject(FollowDTO::class.java)

                        if(userDTO?.follwings != null) {
                            getContents(userDTO.follwings)
                        }
                     }
                }
            }
        }

        fun getContents(follower: MutableMap<String, Boolean>?) {
            firestore.collection("image").orderBy("timeStamp").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                //                새로고침 해주는 코드가 snapshotListener 안에 있어야하는 이유가 바뀔때 마다 for문이 돌기 떄문에 내부에 있어야한다.

                //새로고침 될때마다 누적이 되기 때문에 clear를 해주어야 누적 되지 않는다.
                contentDTO.clear()
                contentUidList.clear()
                if(querySnapshot == null) return@addSnapshotListener
                for(snapshot in querySnapshot.documents) {
                    val item = snapshot.toObject(ContentDTO::class.java)

                    //item.uid 라는 것은 ContentDTO로 캐스팅 한것에서 uid 값을 부르는것 following에 하나라도 있으면 가져 오겠다.
                    if(follower?.keys?.contains(item.uid)!!){
                        contentDTO.add(item)
                        //contentUidList는 collection 값 담아두는 리스트 즉  컬렉션 id 값
                        contentUidList.add(snapshot.id)
                    }
                }
                //            새로고침
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_detail,parent,false)
            return CustomViewHolder(view)
        }

        private inner class CustomViewHolder(view: View?) : RecyclerView.ViewHolder(view)

        //        위의 firestore for문 돌때 contentsDTO.add() 추가된 갯수만큼 보여주는것
        override fun getItemCount(): Int = contentDTO.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            //holder가 각기 하나의 아이템들
            (holder as CustomViewHolder).itemView.apply {
                contentDTO[position].uid?.let { firestore.collection("profileImages").document(it).get().addOnCompleteListener {
                    task -> val url = task.result["image"]
                    Glide.with(activity).load(url).apply(RequestOptions().circleCrop())
                            .into(detailviewitem_profile_image)
                    }
                }
                //                유저 id
                detailviewitem_profile_textview.text = contentDTO[position].userId?.substringBefore('@')
//                이미지
                Glide.with(holder.itemView.context).load(contentDTO[position].imageUri).into(detailviewitem_imageview_content)
//                설명 택스트
                detailviewitem_explain_textview.text = contentDTO[position].explain

//                좋아요 카운터 설정
                detailviewitem_favoritecounter_textview.text = "좋아요 ${contentDTO[position].favoriteCount}개"

                //좋아요를 클릭 햇을 경우  containsKey()는 유저 아이디 값이 포함되어있냐 라고 물어보는것
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                detailviewitem_favorite_imageview.setOnClickListener {
                    favoriteEvent(position)
                }

                if(contentDTO[position].favorites.containsKey(uid)) {
                    detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite)
                }else {
                    detailviewitem_favorite_imageview.setImageResource(R.drawable.ic_favorite_border)
                }
                //이부분 공부
                detailviewitem_profile_image.setOnClickListener {
                    val fragment = UserFragment()
                    val bundle = Bundle().apply {
                        putString("destinationUid",contentDTO[position].uid)//destinationUid에 contentDTO[position].uid 값을 넣겠다
                        putString("userId",contentDTO[position].userId)
                    }
                    with(fragment) {
                        arguments = bundle
                    }
                    activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment)
                            .commit()
                }
                //이부분공부
                detailviewitem_profile_textview.setOnClickListener {
                    val fragment = UserFragment()
                    val bundle = Bundle().apply {
                        putString("destinationUid", contentDTO[position].uid)//destinationUid에 contentDTO[position].uid 값을 넣겠다
                        putString("userId", contentDTO[position].userId)
                    }
                    with(fragment) {
                        arguments = bundle
                    }
                    activity!!.supportFragmentManager.beginTransaction().replace(R.id.main_content,fragment)
                            .commit()
                }


                //액티비티는 intent로 putExtra하여 해당되는 유저 id를 넣어준다.
                //댓글 다는 곳에서 글쓴이와 글쓴 내용을 담아두기 위해서 putExtra에 유저 id 를 담아두었음
                detailviewitem_comment_imageview.setOnClickListener {
                    Intent(it.context, CommentActivity::class.java).apply {
                        putExtra("contentUid", contentUidList[position])
                        putExtra("contentDestinationUid", contentDTO[position].uid)
                        putExtra("destinationUid", contentDTO[position].uid)
                        startActivity(this)
                    }
                }
            }
        }
        private fun favoriteEvent(position: Int) {
            val tsDoc = firestore.collection("image").document(contentUidList[position])

//            이부분 강의 다시 들어보기
            firestore.runTransaction {
                transaction: Transaction ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val contentDTO = transaction.get(tsDoc).toObject(ContentDTO::class.java)
                //좋아요를 누른 상태 -> 누르지 않은 상태
                if(contentDTO.favorites.containsKey(uid)) {
                    contentDTO.favoriteCount -= 1
                    contentDTO.favorites.remove(uid)


                }else {
                    //좋아요 누르지 않은 상태 -> 누르는 상태
                    contentDTO.favorites[uid] = true
                    contentDTO.favoriteCount += 1

                    this.contentDTO[position].uid?.let { favoriteAlarm(it) }
                }
                transaction.set(tsDoc, contentDTO)
            }
        }

        fun favoriteAlarm(destinationUid: String) {
            AlarmDTO().apply {
                this.destinationUid = destinationUid
                userId = user.currentUser?.email
                uid = user.currentUser?.uid
                kind = 0
                timeStamp = System.currentTimeMillis()

                firestore.collection("alarms").document().set(this)
            }

            val message = user.currentUser?.email + getString(R.string.alarm_favorite)
            fcmPush.sendMessage(destinationUid, "알림 메세지 입니다.", message)
        }
    }

}