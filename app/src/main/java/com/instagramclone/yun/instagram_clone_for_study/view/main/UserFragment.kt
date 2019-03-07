package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.instagramclone.yun.instagram_clone_for_study.R
import com.instagramclone.yun.instagram_clone_for_study.model.ContentDTO
import com.instagramclone.yun.instagram_clone_for_study.model.FollowDTO
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment() {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    //현재 나의 Uid
    private lateinit var currentUserUid: String
    var uid: String? = null
    private lateinit var auth: FirebaseAuth
    lateinit var fragmentView: View

    companion object {
        private val PICK_PROFILE_FROM_ALBUM = 10



        //팩토리 함수로 호출
        fun callPICK_PROFILE_FROM_ALBUM() = PICK_PROFILE_FROM_ALBUM
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.fragment_user, container, false)

        auth = FirebaseAuth.getInstance()
        currentUserUid = auth.currentUser?.uid!!

        if(arguments != null) {
            uid = arguments!!.getString("destinationUid")

            if(uid != null && uid == currentUserUid) {
                with(fragmentView) {
                    account_btn_follow_signout.text = getString(R.string.signout)
                    account_btn_follow_signout.setOnClickListener {
                        startActivity(Intent(activity, LoginActivity::class.java))
                        activity?.finish()
                        auth.signOut()
                    }
                }
            } else {
                with(fragmentView) {
                    account_btn_follow_signout.text = getString(R.string.follow)
                    account_btn_follow_signout.setOnClickListener {
                        requestFollow()
                    }
                }
                (activity as MainActivity).apply {
                    toolbar_btn_back.visibility = View.VISIBLE
                    toolbar_userName.visibility = View.VISIBLE
                    toolbar_title_image.visibility = View.GONE

                    toolbar_userName.text = arguments!!.getString("userId")
                    toolbar_btn_back.setOnClickListener { bottom_navigation.selectedItemId = R.id.action_home }
                }
            }
        }

        if(uid == currentUserUid) {
            fragmentView.account_iv_profile.setOnClickListener {
                if(ContextCompat.checkSelfPermission(activity!!, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    val photoPickerIntent = Intent(Intent.ACTION_PICK).apply {
                        type = "image/*"
                    }
                    activity!!.startActivityForResult(photoPickerIntent, PICK_PROFILE_FROM_ALBUM)
                }
            }
        }


        with(fragmentView) {
            account_recyclerview.layoutManager = GridLayoutManager(activity!!, 3)
            account_recyclerview.adapter = UserFragmentRecyclerview()
        }

        getFollowing()
        getFollower()
        return fragmentView
    }
    fun requestFollow() {
        val tsDocFollowing = currentUserUid.let {
            firestore.collection("user").document(it)
        }
        firestore.runTransaction {
            transaction ->
            var followDTO = tsDocFollowing.let { transaction.get(it).toObject(FollowDTO::class.java) }
            if(followDTO == null) {
                //아무도 팔로잉 하지 않았을 경우
                followDTO = FollowDTO().apply {
                    followingCount = 1
                    follwings[uid] = true
                }
                tsDocFollowing.let { tsDoc -> followDTO?.let { follow -> transaction.set(tsDoc, follow) } }
                return@runTransaction
            }

            if(followDTO.follwings.containsKey(uid)) {
                //내 아이디가 제 3자를 이미 팔로잉 하고 있을 경우
                //follow를 취소하는 코드 (이미 팔로잉 하고있으니까) -> 제 3자가 나를 팔로워 취소한다.
                with(followDTO) {
                    followingCount -= 1
                    follwings.remove(uid)
                }
            }else {
                //내가 제 3자 팔로잉 하지 않았을 경우 -> 제 3자가 나를 팔로워 한다.
                with(followDTO) {
                    followingCount += 1
                    follwings[uid] = true
                }
            }
            tsDocFollowing.let { transaction.set(it, followDTO) }
            return@runTransaction
        }
        val tsDocFollower = uid?.let { firestore.collection("user").document(it) }
        firestore.runTransaction {
            transaction ->
            var followDTO = tsDocFollower.let { transaction.get(it!!).toObject(FollowDTO::class.java) }

            if(followDTO == null) {
                //아무도 팔로우 하지 않았을 경우
                followDTO = FollowDTO().apply {
                    followerCount = 1
                    follwers[currentUserUid] = true
                }
                tsDocFollower?.let { tsDocFollower -> followDTO?.let{ followDTO -> transaction.set(tsDocFollower, followDTO) } }
                return@runTransaction
            }
            //제 3자의 유저를 내가 필로잉 하고 있을 경우 -> 내가 제3자 팔로우를 취소한다.
            if(followDTO?.follwers?.containsKey(currentUserUid)!!) {
                with(followDTO) {
                    followerCount -= 1
                    follwers.remove(currentUserUid)
                }
            }else {
                //제 3자를 내가 팔로워 하지 않았을 경우 -> 내가 제 3자 팔로우를 하겠다.
                with(followDTO) {
                    followerCount += 1
                    follwers[currentUserUid] = true
                }
            }
            tsDocFollower?.let { tsDocFollower -> followDTO?.let{ followDTO -> transaction.set(tsDocFollower, followDTO) } }
            return@runTransaction
        }
    }
    override fun onResume() {
        super.onResume()
        getProfileImage()
    }
    fun getProfileImage() {
        uid?.let {
            firestore.collection("profileImages").document(it).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                //로그아웃후 뷰가 사라졌는데 스냅샷이 남아 있는 경우가 있어서 null 일경우 리스너를 리턴 시킨다
                if(documentSnapshot == null) return@addSnapshotListener
                if(documentSnapshot.data != null) {
                    val url = documentSnapshot.data["image"]
                    Glide.with(activity).load(url).apply ( RequestOptions().circleCrop() ).into(account_iv_profile)
                }
            }
        }
    }

    fun getFollower() {
        uid?.let {
            firestore.collection("user").document(it).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val followDTO = documentSnapshot.toObject(FollowDTO::class.java)
                if(followDTO == null) return@addSnapshotListener
                fragmentView.account_tv_follower_count.text = followDTO.followerCount.toString()
                if( followDTO.follwers.containsKey(currentUserUid) ) {

                    with(fragmentView) {
                        account_btn_follow_signout.text = getString(R.string.follow_cancel)
                        activity?.let { myActivity -> ContextCompat.getColor(myActivity, R.color.colorLightGray) }
                                ?.let { myGetColor ->
                            account_btn_follow_signout.background
                                    .setColorFilter(myGetColor
                                            , PorterDuff.Mode.MULTIPLY)
                        }
                    }


                }else {
                    if( uid != currentUserUid ) {

                        with(fragmentView) {
                            account_btn_follow_signout.text = getString(R.string.follow)
                            account_btn_follow_signout.background.colorFilter = null
                        }

                    }
                }
            }
        }
    }


    fun getFollowing() {
        uid?.let {
            firestore.collection("user").document(it).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                val followDTO = documentSnapshot.toObject(FollowDTO::class.java)
                if(followDTO == null) return@addSnapshotListener
                fragmentView.account_tv_following_count.text = followDTO.followingCount.toString()
            }
        }
    }

    inner class UserFragmentRecyclerview : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val contentDTO: ArrayList<ContentDTO>
        init {
            contentDTO = ArrayList()
            //처음에 whereEqualTo("uid", currentUserUid)로 헀었는데 그러니까 현재 유저 사진만 그려진다.
            //"uid", uid 로 바꿔주니까 계정에 따라서 올렸던 사진이 변함
            firestore.collection("image").whereEqualTo("uid", uid).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(querySnapshot == null) return@addSnapshotListener
                for(snapshot in querySnapshot.documents) {
                    contentDTO.add(snapshot.toObject(ContentDTO::class.java))
                }
                fragmentView.account_tv_post_count.text = contentDTO.size.toString()
                notifyDataSetChanged()
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3
            val imageview = ImageView(parent?.context).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(width, width)

            }
            return CustomViewHolder(imageview)
        }

        inner class CustomViewHolder(var imageview: ImageView) : RecyclerView.ViewHolder(imageview)

        override fun getItemCount(): Int = contentDTO.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            (holder as CustomViewHolder).imageview.apply {
                Glide.with(holder.itemView.context).load(contentDTO[position].imageUri)
                        .into(this)
            }

        }

    }

}