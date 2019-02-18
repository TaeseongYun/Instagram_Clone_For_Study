package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
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
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.fragment_user.view.*

class UserFragment : Fragment() {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    //현재 나의 Uid
    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
    var uid: String? = null
    companion object {
        private val PICK_PROFILE_FROM_ALBUM = 10

        //팩토리 함수로 호출
        fun callPICK_PROFILE_FROM_ALBUM() = PICK_PROFILE_FROM_ALBUM
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            LayoutInflater.from(inflater.context).inflate(R.layout.fragment_user,container,false)
                    .apply {
                        if(arguments != null) {
                            uid = arguments?.getString("destinationUid")
                        }
                        account_iv_profile.setOnClickListener {
                            Intent(Intent.ACTION_PICK).apply {
                                type = "image/*"
                                activity?.startActivityForResult(this, PICK_PROFILE_FROM_ALBUM)
                            }
                        }
                        account_btn_follow_signout.seton
                        account_recyclerview.adapter = UserFragmentRecyclerview()
                        account_recyclerview.layoutManager = GridLayoutManager(activity, 3)
                    }

    fun requestFollow() {
        var tsDocFollowing = currentUserUid?.let {
            firestore.collection("user").document(it)
        }
        firestore.runTransaction {
            transaction ->
            var followDTO = tsDocFollowing?.let { transaction.get(it).toObject(FollowDTO::class.java) }
            if(followDTO == null) {
                //아무도 팔로잉 하지 않았을 경우
                followDTO = FollowDTO().apply {
                    followerCount = 1
                    follwings[uid] = true

                    tsDocFollowing?.let { tsDoc -> followDTO?.let { follow -> transaction.set(tsDoc, follow) } }
                }
                return@runTransaction
            }

            if(followDTO.follwings.containsKey(uid)) {
                //내 아이디가 제 3자를 이미 팔로잉 하고 있을 경우
                //follow를 취소하는 코드 (이미 팔로잉 하고있으니까) -> 제 3자가 나를 팔로워 취소한다.
                followDTO.followingCount -= 1
                followDTO.follwings.remove(uid)
            }else {
                //내가 제 3자 팔로잉 하지 않았을 경우 -> 제 3자가 나를 팔로워 한다.
                followDTO.followingCount += 1
                followDTO.follwings[uid] = true
            }
            tsDocFollowing?.let { transaction.set(it, followDTO!!) }
            return@runTransaction
        }
        var tsDocFollwer = uid?.let { firestore.collection("users").document(it) }
        firestore.runTransaction {
            transaction ->
            var followDTO = tsDocFollowing?.let { transaction.get(it).toObject(FollowDTO::class.java) }

            if(followDTO == null) {
                //아무도 팔로우 하지 않았을 경우
                followDTO = FollowDTO().apply {
                    followerCount = 1
                    follwers[currentUserUid] = true

                    tsDocFollwer?.let { tsDocFollwer -> followDTO?.let{ followDTO -> transaction.set(tsDocFollwer, followDTO) } }
                }
                return@runTransaction
            }
            //제 3자의 유저를 내가 필로잉 하고 있을 경우 -> 내가 제3자 팔로우를 취소한다.
            if(followDTO?.follwers?.containsKey(currentUserUid)!!) {
                followDTO!!.followerCount -= 1
                followDTO!!.follwers.remove(currentUserUid)

                tsDocFollwer?.let { tsDocFollwer -> followDTO?.let{ followDTO -> transaction.set(tsDocFollwer, followDTO) } }
            }else {
                //제 3자를 내가 팔로워 하지 않았을 경우 -> 내가 제 3자 팔로우를 하겠다.
                followDTO!!.followerCount += 1
                followDTO!!.follwers[currentUserUid] = true

            }
            tsDocFollwer?.let { tsDocFollwer -> followDTO?.let{ followDTO -> transaction.set(tsDocFollwer, followDTO) } }
            return@runTransaction
        }
    }
    override fun onResume() {
        super.onResume()
        getProfileImage()
    }
    fun getProfileImage() {
        currentUserUid?.let {
            firestore.collection("profileImages").document(it).addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                if(documentSnapshot.data != null) {
                    val url = documentSnapshot.data["image"]
                    Glide.with(activity).load(url).apply ( RequestOptions().circleCrop() ).into(account_iv_profile)
                }
            }
        }
    }
    inner class UserFragmentRecyclerview : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        val contnetDTO: ArrayList<ContentDTO>
        init {
            contnetDTO = ArrayList()
            firestore.collection("image").whereEqualTo("uid", currentUserUid).addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                for(snapshot in querySnapshot.documents) {
                    contnetDTO.add(snapshot.toObject(ContentDTO::class.java))
                }

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

        override fun getItemCount(): Int = contnetDTO.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
            (holder as CustomViewHolder).imageview.apply {
                Glide.with(holder.itemView.context).load(contnetDTO[position].imageUri)
                        .into(this)
            }

        }

    }

}