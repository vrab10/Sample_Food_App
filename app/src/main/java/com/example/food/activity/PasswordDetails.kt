package com.example.food.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
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


class PasswordDetails : AppCompatActivity() {

    lateinit var etPpassword:EditText
    lateinit var etcpPassword:EditText
    lateinit var etOtp:EditText
    lateinit var btnpNext:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_details)

        etPpassword=findViewById(R.id.etPpassword)
        etcpPassword=findViewById(R.id.etcpPassword)
        btnpNext=findViewById(R.id.btnpNext)
        etOtp=findViewById(R.id.etOtp)



        var mob:String?=""

        if(intent!=null) {
        mob=intent.getStringExtra("mobile")
        }

        val mobile=mob

        mob=etPpassword.text.toString()
        var mobs=etcpPassword.text.toString()
        btnpNext.setOnClickListener {

            if (mob == mobs) {

                if (ConnectionManager().checkConnectivity(this@PasswordDetails)) {
                    try {

                        val queue = Volley.newRequestQueue(this@PasswordDetails)

                        val url = " http://13.235.250.119/v2/reset_password/fetch_result"

                        val params = JSONObject()
                        params.put("mobile_number", mobile)
                        params.put("password", etPpassword.text.toString())
                        params.put("otp", etOtp.text.toString())


                        val jsonrequest =
                            object :
                                JsonObjectRequest(
                                    Request.Method.POST,
                                    url,
                                    params,
                                    Response.Listener {
                                        val obj = it.getJSONObject("data")
                                        val success = obj.getBoolean("success")
                                        val msg = obj.getString("successMessage")
                                        if (success) {
                                            val intent = Intent(
                                                this@PasswordDetails,
                                                LoginActivity::class.java
                                            )
                                            startActivity(intent)
                                            Toast.makeText(
                                                this@PasswordDetails,
                                                msg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Toast.makeText(
                                                this@PasswordDetails,
                                                "Invalidd Credentials",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    Response.ErrorListener {
                                        Toast.makeText(
                                            this@PasswordDetails,
                                            "Volley Error Occurred",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }) {

                                override fun getHeaders(): MutableMap<String, String> {
                                    val header = HashMap<String, String>()
                                    header["Content-type"] = "application/json"
                                    header["token"] = "d57175f7bbf8e5"
                                    return header
                                }
                            }

                        queue.add(jsonrequest)
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@PasswordDetails,
                            "JSON Exception has occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val dialog = AlertDialog.Builder(this@PasswordDetails)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")

                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingsIntent =
                            Intent(Settings.ACTION_WIRELESS_SETTINGS)  //implicit intent used to connect outside the app
                        startActivity(settingsIntent)
                        finish()
                    }

                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@PasswordDetails)
                    }
                    dialog.show()
                    dialog.create()
                }
            }else{
                Toast.makeText(
                    this@PasswordDetails,
                    "Invalid Credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }
}
