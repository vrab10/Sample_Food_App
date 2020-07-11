package com.example.food.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.widget.Toolbar  //added files
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
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

class SignUp : AppCompatActivity() {

    lateinit var etName: EditText
    lateinit var etEmailAddress: EditText
    lateinit var etMobile: EditText
    lateinit var etDelivery: EditText
    lateinit var etPass: EditText
    lateinit var etCPass: EditText
    lateinit var btnRegister: Button
    lateinit var toolBar: Toolbar
    lateinit var sharedPreferences:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        sharedPreferences=getSharedPreferences(getString(R.string.shared), Context.MODE_PRIVATE)



        etName = findViewById(R.id.etName)
        etEmailAddress = findViewById(R.id.etEmailAddress)
        etMobile = findViewById(R.id.etMobile)
        etDelivery = findViewById(R.id.etDelivery)
        etPass = findViewById(R.id.etPass)
        etCPass = findViewById(R.id.etCPass)//need to add functionality and relate with password
        btnRegister = findViewById(R.id.btnRegister)
        toolBar=findViewById(R.id.toolBar)

        setSupportActionBar(toolBar)
        supportActionBar?.title="Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var s: String
        var st: String
        var p: String
        var m: String
        var cp: String

        btnRegister.setOnClickListener {

            val url = "http://13.235.250.119/v2/register/fetch_result"

            s = etName.text.toString()
            p = etPass.text.toString()
            m = etMobile.text.toString()
            cp = etCPass.text.toString()
            if (s.length >= 3 && m.length == 10 && p.equals(cp)) {
            if(ConnectionManager().checkConnectivity(this@SignUp)) {

try {
    val queue = Volley.newRequestQueue(this@SignUp)
    val params = JSONObject()
    params.put("name", s)
    params.put("mobile_number", m)
    params.put("password", p)
    params.put("address", etDelivery.text.toString())
    params.put("email", etEmailAddress.text.toString())


    val jsonrequest =
        object :
            JsonObjectRequest(Request.Method.POST, url, params, Response.Listener {

                val obj = it.getJSONObject("data")
                val success = obj.getBoolean("success")
                if (success) {

                    val data=obj.getJSONObject("data")
                    savePreferences(data.getString("name"), data.getString("email"),
                        data.getString("mobile_number"),data.getString("address"),data.getString("user_id"))

                    val intent = Intent(this@SignUp, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@SignUp,
                        "Invalid Password or INumber",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }, Response.ErrorListener {
                Toast.makeText(
                    this@SignUp,
                    "Volley Error Occurred",
                    Toast.LENGTH_SHORT
                )
                    .show()

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
catch(e: JSONException){
    Toast.makeText(this@SignUp, "Error JsonException", Toast.LENGTH_SHORT)
        .show()
}
                }

            else{
                val dialog= AlertDialog.Builder(this@SignUp)
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
                    ActivityCompat.finishAffinity(this@SignUp)
                }
                dialog.show()
                dialog.create()
            }
            }
            else {
            Toast.makeText(this@SignUp, "Invalid Password or Number", Toast.LENGTH_SHORT)
                .show()
        }

            }

        }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            val intent=Intent(this@SignUp,
                LoginActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)

    }

    fun savePreferences(name: String,email:String,pnumber: String,address: String,u_id: String){
        sharedPreferences.edit().putString("name",name).apply()
        sharedPreferences.edit().putString("email",email).apply()
        sharedPreferences.edit().putString("mobile",pnumber).apply()
        sharedPreferences.edit().putString("address",address).apply()
        sharedPreferences.edit().putBoolean("logged",true).apply()
        sharedPreferences.edit().putString("u_id",u_id).apply()
    }


    override fun onPause() {
        super.onPause()
        finish()
    }

}

