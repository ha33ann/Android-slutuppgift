package com.Hassan.chattingapp.base

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.Hassan.chattingapp.databinding.ActivitySplashBinding
import com.Hassan.chattingapp.utils.TinyDB

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    //här instansierar vi databasen och autentiseringen för firebase
    lateinit var binding: ActivitySplashBinding
    lateinit var tinyDB: TinyDB

    //här skapar vi en instans av tinyDB för att spara data lokalt
    override fun onCreate(savedInstanceState: Bundle?) {
        //sätter layouten till activity_splash
        super.onCreate(savedInstanceState)
        binding=ActivitySplashBinding.inflate(layoutInflater)
        val view:View=binding.root
        setContentView(view)
        tinyDB= TinyDB(this)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //här skapar vi en onCLickListener för att gå till login-sidan
        binding.goToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}