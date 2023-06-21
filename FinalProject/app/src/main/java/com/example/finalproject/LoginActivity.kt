package com.example.finalproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalproject.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "회원 가입 및 로그인"

        binding.signUpButton.setOnClickListener {
            Log.d("회원가입", "회원가입버튼")
            val email = binding.idEditText.text.toString()
            val password = binding.pwEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase에 사용자 생성 요청
            Firebase.auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입에 성공했습니다. 로그인해주세요.", Toast.LENGTH_SHORT).show()
                        // 회원가입 성공
                    } else {
                        // 회원가입 실패
                        Toast.makeText(this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.loginButton.setOnClickListener {
            Log.d("로그인", "로그인 버튼")

            val email = binding.idEditText.text.toString()
            val password = binding.pwEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "이메일 또는 패스워드가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase에 로그인 요청
            Firebase.auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val nickname = binding.nicknameEditText.text.toString()
                        Log.d("nickname", nickname)
                        val intent = Intent(this, ShowScheduleActivity::class.java)
                        intent.putExtra("nickname", nickname)
                        startActivity(intent)
                        finish()
                        // 로그인 성공
                    } else {
                        Log.e("LoginActivity", task.exception.toString())
                        Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        // 로그인 실패
                    }
                }
        }
    }
}