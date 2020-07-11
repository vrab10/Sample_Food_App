package com.example.food.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.example.food.R
import com.example.food.activity.CartActivity
import com.example.food.adapter.MenuAdapter
import com.example.food.data.MenuData
import com.example.food.database.CartDatabase
import com.example.food.database.CartEntity
import com.example.food.util.ConnectionManager
import com.google.gson.Gson
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class MenuFragment : Fragment() {

    lateinit var recylerview:RecyclerView
    lateinit var layout:RecyclerView.LayoutManager
    lateinit var recyclerAdapter: MenuAdapter
    lateinit var btnhide:Button
    val list= arrayListOf<MenuData>()
    var orderlist= arrayListOf<MenuData>()
    var resid:Int?=0
    var resName:String?=""

    var bookList= arrayListOf<CartEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view=inflater.inflate(R.layout.fragment_menu, container, false)

        recylerview=view.findViewById(R.id.menurecycler)

        btnhide=view.findViewById(R.id.btnHide)
        btnhide.visibility=View.GONE

        layout=LinearLayoutManager(activity)

        resid=arguments?.getInt("id",0)
        resName=arguments?.getString("name")

        btnhide.setOnClickListener {
             proceedToCart()
        }

        val url= "http://13.235.250.119/v2/restaurants/fetch_result/"
        val queue= Volley.newRequestQueue(activity as Context)
        if(ConnectionManager().checkConnectivity(activity as Context)) {
                val jsonRequest = object :
                    JsonObjectRequest(
                        Request.Method.GET, url + resid, null, Response.Listener {
                            try {
                                val obj = it.getJSONObject("data")
                                val success = obj.getBoolean("success")
                                if (success) {
                                    val array = obj.getJSONArray("data")
                                    for (i in 0 until array.length()) {
                                        val jsonObj = array.getJSONObject(i)
                                        val rest = MenuData(
                                            i + 1, jsonObj.getString("name"),
                                            jsonObj.getString("cost_for_one")
                                        )

                                        list.add(rest)

                                        recyclerAdapter = MenuAdapter(
                                            activity as Context,
                                            list,
                                            object : MenuAdapter.OnItemClickListener {
                                                override fun onAddItemClick(foodItem: MenuData) {
                                                    orderlist.add(foodItem)
                                                    if (orderlist.size > 0) {
                                                        btnhide.visibility = View.VISIBLE
                                                    }
                                                }

                                                override fun onRemoveItemClick(foodItem: MenuData) {
                                                    orderlist.remove(foodItem)
                                                    if (orderlist.isEmpty()) {
                                                        btnhide.visibility = View.GONE
                                                    }
                                                }
                                            })
                                        recylerview.adapter = recyclerAdapter
                                        recylerview.layoutManager = layout
                                    }

                                } else {
                                    Toast.makeText(
                                        activity as Context, "Invalid Password or Number",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }       catch (e: JSONException) {
                                Toast.makeText(activity as Context, "Json Exception Occured", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(
                                activity as Context,
                                "Volley error has occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "d57175f7bbf8e5"

                        return headers
                    }
                }
                queue.add(jsonRequest)

            }
        else{
            val dialog= AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")

            dialog.setPositiveButton("Open Settings"){
                    text,listener ->
                val settingsIntent= Intent(Settings.ACTION_WIRELESS_SETTINGS)  //implicit intent used to connect outside the app
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit"){
                    text,listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.show()
            dialog.create()
        }



        return view
    }

     fun proceedToCart() {

         val gson = Gson()

         val foodItems = gson.toJson(orderlist)

         val async = ItemsOfCart(activity as Context, resid.toString(), foodItems, 1).execute().get()

         if (async) {
             val intent = Intent(activity, CartActivity::class.java)
             intent.putExtra("resName", resName)
             val z=resName
             intent.putExtra("resId", resid as Int)
             startActivity(intent)
         } else {
             Toast.makeText((activity as Context), "Some unexpected error", Toast.LENGTH_SHORT)
                 .show()
         }
    }


    class ItemsOfCart(
        context: Context,
        val restaurantId: String,
        val foodItems: String,
        val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, CartDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    db.cartDao().insertCart(CartEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.cartDao().deleteCart(CartEntity(restaurantId, foodItems))
                    db.close()
                    return true
                }
            }

            return false
        }

    }

}