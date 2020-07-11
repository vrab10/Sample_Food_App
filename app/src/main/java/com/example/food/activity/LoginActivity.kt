package com.example.food.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food.R
import com.example.food.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var btnLogin: Button
    lateinit var etMobile: EditText
    lateinit var etPassword: EditText
    lateinit var txtForgot: TextView
    lateinit var txtAccount: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        sharedPreferences=getSharedPreferences(getString(R.string.shared), Context.MODE_PRIVATE)

        btnLogin=findViewById(R.id.btnLogin)
        etMobile=findViewById(R.id.etMobile)
        etPassword=findViewById(R.id.etPassword)
        txtForgot=findViewById(R.id.txtForgot)
        txtAccount=findViewById(R.id.txtAccount)

        var s:String
        var p:String

        btnLogin.setOnClickListener {
            s=etMobile.text.toString()
            p=etPassword.text.toString()
            val url="http://13.235.250.119/v2/login/fetch_result/"

            if(p.length>=4 && s.length==10){

                val queue= Volley.newRequestQueue(this@LoginActivity)
                val jsonParams=JSONObject()
                jsonParams.put("mobile_number",s)
                jsonParams.put("password",p)

                if(ConnectionManager().checkConnectivity(this@LoginActivity)) {

                    try{

                    val jsonRequest=object :
                        JsonObjectRequest(Request.Method.POST,url,jsonParams,Response.Listener{
                            val obj=it.getJSONObject("data")
                            val success=obj.getBoolean("success")
                            if(success){
                                val data=obj.getJSONObject("data")
                                savePreferences(data.getString("name"), data.getString("email"),
                                    data.getString("mobile_number"),data.getString("address"),data.getString("user_id"))
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                            }
                            else{
                                Toast.makeText(this@LoginActivity,"Invalid Password or Number",Toast.LENGTH_SHORT).show()
                            }


                    },
                        Response.ErrorListener {
                            Toast.makeText(this@LoginActivity,"Volley error has occured",Toast.LENGTH_SHORT).show()
                        }){

                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "d57175f7bbf8e5"

                            return headers
                        }
                    }
                    queue.add(jsonRequest)
                }


                    catch(e: JSONException){
                        Toast.makeText(this@LoginActivity,"Json Exception Occured",Toast.LENGTH_SHORT).show()
                    }

            }
                else{
                    val dialog= AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")

                    dialog.setPositiveButton("Open Settings"){
                            text,listener ->
                        val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)  //implicit intent used to connect outside the app
                        startActivity(settingsIntent)
                        finish()
                    }

                    dialog.setNegativeButton("Exit"){
                            text,listener ->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }
                    dialog.show()
                    dialog.create()
                }

            }

            else{
                Toast.makeText(this@LoginActivity,"Invalid Password or Number",Toast.LENGTH_SHORT).show()
            }
        }


        txtForgot.setOnClickListener {
            val intent=Intent(this@LoginActivity,
                ForgotPassword::class.java)
             startActivity(intent)
        }

        txtAccount.setOnClickListener {
            val intent=Intent(this@LoginActivity, SignUp::class.java)
            startActivity(intent)
        }

    }

    fun savePreferences(name: String,email:String,pnumber: String,address: String,u_id:String){
        sharedPreferences.edit().putString("name",name).apply()
        sharedPreferences.edit().putString("email",email).apply()
        sharedPreferences.edit().putString("mobile",pnumber).apply()
        sharedPreferences.edit().putString("address",address).apply()
        sharedPreferences.edit().putBoolean("logged",true).apply()
        sharedPreferences.edit().putString("u_id",u_id).apply()
    }

}
