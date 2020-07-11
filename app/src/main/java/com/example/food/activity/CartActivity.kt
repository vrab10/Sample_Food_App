package com.example.food.activity

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar  //added files
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.food.R
import com.example.food.adapter.CartAdapter
import com.example.food.data.MenuData
import com.example.food.database.CartDatabase
import com.example.food.database.CartEntity
import com.example.food.fragments.MenuFragment
import com.example.food.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {
    var res_name: String?=""
    lateinit var cartTool:Toolbar
    var res_id :Int?=0
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtResName:TextView
    var orderList = ArrayList<MenuData>()
    lateinit var recyclerView:RecyclerView
    lateinit var layout:RecyclerView.LayoutManager
    lateinit var btnPlaceOrder:Button
    lateinit var rlCart: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        cartTool=findViewById(R.id.cartTool)
        recyclerView=findViewById(R.id.recylerCart)
        setSupportActionBar(cartTool)
        supportActionBar?.title="My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

       btnPlaceOrder=findViewById(R.id.btnPlaceOrder)
       rlCart = findViewById(R.id.rlCart)

        txtResName=findViewById(R.id.txtResName)
        sharedPreferences=getSharedPreferences(getString(R.string.shared), Context.MODE_PRIVATE)

        if(intent!=null){
            res_name=intent.getStringExtra("resName")
            var name=res_name

        res_id=intent.getIntExtra("resId",0)
    }
        txtResName.text=res_name
        val itemsdb=DBAsynctask(applicationContext).execute().get()
        layout=LinearLayoutManager(this@CartActivity)

        for (element in itemsdb) {
            orderList.addAll(Gson().fromJson(element.items, Array<MenuData>::class.java).asList())
        }

        recyclerView.adapter=CartAdapter(this@CartActivity,orderList)
        recyclerView.layoutManager=layout


        val uid=sharedPreferences.getString("u_id","")


        var sum = 0
        for (i in 0 until orderList.size) {
            sum += (orderList[i].cost).toInt()
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnPlaceOrder.text = total

        btnPlaceOrder.setOnClickListener {

            rlCart.visibility = View.INVISIBLE

            val url= "http://13.235.250.119/v2/place_order/fetch_result/"
            val params = JSONObject()
            params.put("user_id", uid)
            params.put("restaurant_id", res_id)
            params.put("total_cost",sum.toString())

            val foodArray = JSONArray()
            for (i in 0 until orderList.size) {
                val foodId = JSONObject()
                foodId.put("food_item_id", orderList[i].number)
                foodArray.put(i, foodId)
            }
            params.put("food", foodArray)


            val queue= Volley.newRequestQueue(this@CartActivity)
            if(ConnectionManager().checkConnectivity(this@CartActivity)) {
                try{
                    val jsonRequest=object :
                        JsonObjectRequest(
                            Request.Method.POST,url,params, Response.Listener{
                                val obj=it.getJSONObject("data")
                                val success=obj.getBoolean("success")
                                if(success) {
                                    val clear =
                                       ClearDBAsync(applicationContext, res_id.toString()).execute().get()
                                    val dialog = Dialog(
                                        this@CartActivity,
                                        android.R.style.Theme_Black_NoTitleBar_Fullscreen
                                    )
                                    dialog.setContentView(R.layout.order_placed)
                                    dialog.show()
                                    dialog.setCancelable(false)
                                    val btnOk = dialog.findViewById<Button>(R.id.btnOk)
                                    btnOk.setOnClickListener {
                                        dialog.dismiss()
                                        startActivity(Intent(this@CartActivity, MainActivity::class.java))
                                        ActivityCompat.finishAffinity(this@CartActivity)
                                    }

                                }
                                else{
                                    rlCart.visibility = View.VISIBLE
                                    Toast.makeText(this@CartActivity,"Invalid Password or Number",
                                        Toast.LENGTH_SHORT).show()
                                }

                            },
                            Response.ErrorListener {
                                rlCart.visibility = View.VISIBLE
                                Toast.makeText(this@CartActivity,"Volley error has occured", Toast.LENGTH_SHORT).show()
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
                    rlCart.visibility = View.VISIBLE
                    Toast.makeText(this@CartActivity,"Json Exception Occured", Toast.LENGTH_SHORT).show()
                }

            }
            else{
                val dialog= AlertDialog.Builder(this@CartActivity)
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
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.show()
                dialog.create()
            }

        }

    }

/*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            supportFragmentManager.beginTransaction().replace(
                R.id.menuFrame,
                MenuFragment()
            ).commit()

            val frag= MenuFragment()  //this is used for sending data between fragment to fragment
            val bundle = Bundle()
            bundle.putInt("id",id)
            bundle.putString("name",res_name)
            frag.arguments = bundle

            val transaction=supportFragmentManager.beginTransaction()
            transaction.replace(R.id.menuFrame,frag)       //we can use add like link in stackoverflow is provided
            transaction.commit()
        }

        return super.onOptionsItemSelected(item)
    }

*/
    
    override fun onSupportNavigateUp(): Boolean {
        val clear=
            ClearDBAsync(applicationContext, res_id.toString()).execute().get()
        onBackPressed()
        return true
    }


    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, CartDatabase::class.java, "res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.cartDao().deleteOrders(resId)
            db.close()
            return true
        }

    }

    class DBAsynctask(val context: Context): AsyncTask<Void, Void, List<CartEntity>>(){
        val db= Room.databaseBuilder(context, CartDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg params: Void?): List<CartEntity> {
            val a=db.cartDao().getAllCarts()
            db.close()
            return a
        }
    }

}