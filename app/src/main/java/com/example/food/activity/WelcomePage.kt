package com.example.food.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.food.R

class WelcomePage : AppCompatActivity() {
lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.page_splash)


        sharedPreferences=getSharedPreferences(getString(R.string.shared), Context.MODE_PRIVATE)
        val a=sharedPreferences.getBoolean("logged",false)

        if(a){
            val intent=Intent(this@WelcomePage,
                MainActivity::class.java)
            startActivity(intent)
        }
        else{
            Handler().postDelayed({
                val intent=Intent(this@WelcomePage,
                    LoginActivity::class.java)
                startActivity(intent)
            },1000)

        }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }

}
