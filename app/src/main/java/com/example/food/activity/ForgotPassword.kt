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

class ForgotPassword : AppCompatActivity() {
    lateinit var etFEmailAddress: EditText
    lateinit var etFMobile: EditText
    lateinit var btnFNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etFEmailAddress=findViewById(R.id.etFEmailAddress)
        etFMobile=findViewById(R.id.etFMobile)
        btnFNext=findViewById(R.id.btnFNext)

        btnFNext.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this@ForgotPassword)) {
                try {

                    val queue = Volley.newRequestQueue(this@ForgotPassword)

                    val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                    val params = JSONObject()
                    params.put("mobile_number", etFMobile.text.toString())
                    params.put("email", etFEmailAddress.text.toString())

                    val jsonrequest =
                        object :
                            JsonObjectRequest(Request.Method.POST, url, params, Response.Listener {
                                val obj = it.getJSONObject("data")
                                val success = obj.getBoolean("success")
                                if (success) {
                                    if(obj.getBoolean("first_try")){
                                        val intent =
                                            Intent(this@ForgotPassword, PasswordDetails::class.java)
                                        intent.putExtra("mobile",etFMobile.text.toString())
                                        startActivity(intent)
                                        Toast.makeText(this@ForgotPassword,"OTP sent to mail and valid for 24hrs",Toast.LENGTH_SHORT).show()
                                    }
                                 else{
                                        val intent =
                                            Intent(this@ForgotPassword, PasswordDetails::class.java)
                                        intent.putExtra("mobile",etFMobile.text.toString())
                                        startActivity(intent)
                                        Toast.makeText(this@ForgotPassword,"OTP already sent to mail and valid for 24hrs",Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        this@ForgotPassword,
                                        "Invalid Mobile Number or Email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@ForgotPassword,
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
                }
                catch (e: JSONException){
                    Toast.makeText(
                        this@ForgotPassword,
                        "JSON Exception has occurred",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else{
                val dialog= AlertDialog.Builder(this@ForgotPassword)
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
                    ActivityCompat.finishAffinity(this@ForgotPassword)
                }
                dialog.show()
                dialog.create()
            }
        }
    }
}
