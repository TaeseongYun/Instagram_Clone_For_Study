package com.instagramclone.yun.instagram_clone_for_study.view.main

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.instagramclone.yun.instagram_clone_for_study.R
import kotlinx.android.synthetic.main.activity_login.*
import com.instagramclone.yun.instagram_clone_for_study.util.*


class LoginActivity : AppCompatActivity() {

    val GOOGLE = 9001
    private var auth: FirebaseAuth? = null
    lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()

        signIn_button.setOnClickListener {
            createUserAndLogin()
        }
        googleLogin_button.setOnClickListener {
            googleLogin()
        }
//        facebookLogin_button.setOnClickListener {  }




    }


    fun createUserAndLogin() {
        auth?.createUserWithEmailAndPassword(editText_email.text.toString(), editText_password.text.toString())
                ?.addOnCompleteListener {
                    if(it.isSuccessful) {
                        movePage(auth?.currentUser)
                        myMakeText(this, R.string.signup_complete, Toast.LENGTH_LONG)
                    } else if(it.exception?.message.isNullOrEmpty()) {
                        Toast.makeText(this, it.exception?.message.toString(), Toast.LENGTH_LONG)
                                .show()
                    } else {
                        emailLogin()
                    }
                }
    }

    fun movePage(user: FirebaseUser?) {
        user?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun emailLogin() {
        auth?.signInWithEmailAndPassword(editText_email.text.toString(), editText_password.text.toString())
                ?.addOnCompleteListener {
                    if(it.isSuccessful) {
                        movePage(auth?.currentUser)
                    } else {
                       Toast.makeText(this, "로그인 실패"+it.exception?.message.toString(),Toast.LENGTH_LONG)
                               .show()
                        Toast.makeText(this, editText_email.toString(), Toast.LENGTH_LONG)
                                .show()
                        println("editText에 들어간 아이디 값 -> ${editText_email.text}")
                    }
                }
    }
    fun googleLogin() {
        val googleSignInIntent = googleSignInClient.signInIntent
        startActivityForResult(googleSignInIntent, GOOGLE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            if(requestCode == GOOGLE) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if(result.isSuccess) {
                    val account = result.signInAccount
                    account?.let { googleLoginCridential(it) }
                }
            }
        }
    }

    fun googleLoginCridential(account: GoogleSignInAccount?) {
        val cridential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(cridential)?.addOnCompleteListener {
            if(it.isSuccessful) {
                myMakeText(this, R.string.signin_complete, Toast.LENGTH_LONG)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        movePage(auth?.currentUser)
    }
}
