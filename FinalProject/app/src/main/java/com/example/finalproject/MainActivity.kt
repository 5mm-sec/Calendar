package com.example.finalproject

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG

import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.example.finalproject.databinding.ActivityMainBinding
import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.messaging.FirebaseMessaging

import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.messaging.ktx.remoteMessage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : AppCompatActivity() {

    val TAG : String = "안녕"

    lateinit var binding: ActivityMainBinding
    // ActivityMainBinding 변수를 나중에 초기화할 것임을 나타내는 lateinit 예약어 사용

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        // 메인 액티비티에서 등록 토큰 가져오기
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                // Firebase Cloud Messaging(FMC) 등록 토큰 가져오기 실패 시 로그 출력
                return@OnCompleteListener
            } else {
                Log.d("실패실패", "실패실패")
            }

            // FCM 등록 토큰 가져오기 성공

            // 새로운 FCM 등록 토큰 받아오기
            val token = task.result

            // 로그 및 토스트 메시지 출력
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg) // FCM 등록 토큰 출력
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })

        val currentUser = Firebase.auth.currentUser // 현재 사용자 가져오기
        val example = 1
        if (example == 1) {
            startActivity(Intent(this, LoginActivity::class.java)) // example 값이 1인 경우 LoginActivity로 이동
        }
    }
}






