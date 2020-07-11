package com.example.food.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.example.food.R
import com.example.food.adapter.MenuAdapter
import com.example.food.adapter.OrderAdapter
import com.example.food.data.MenuData
import com.example.food.data.Order
import com.example.food.util.ConnectionManager
import org.json.JSONException

/**
 * A simple [Fragment] subclass.
 */
class HistoryFragment : Fragment() {
    lateinit var recylerHistory:RecyclerView
    lateinit var layout:RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderAdapter
    var orderHistoryList = ArrayList<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_history, container, false)

        recylerHistory=view.findViewById(R.id.recyclerOrderHistory)
        layout=LinearLayoutManager(activity as Context)

        val uid=arguments?.getString("u_id")

        val url= "http://13.235.250.119/v2/orders/fetch_result/"+uid

        val queue= Volley.newRequestQueue(activity as Context)
        if(ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonRequest = object :
                JsonObjectRequest(
                    Request.Method.GET, url, null, Response.Listener {
                        try {
                            val obj = it.getJSONObject("data")
                            val success = obj.getBoolean("success")
                            if (success) {
                                val array = obj.getJSONArray("data")
                                for (i in 0 until array.length()) {
                                    val orderObject = array.getJSONObject(i)
                                    val foodItems = orderObject.getJSONArray("food_items")
                                    val order = Order(
                                        orderObject.getInt("order_id"),
                                        orderObject.getString("restaurant_name"),
                                        orderObject.getString("order_placed_at"),
                                        foodItems
                                    )

                                    orderHistoryList.add(order)
                                    recyclerAdapter =
                                        OrderAdapter(activity as Context, orderHistoryList)
                                    recylerHistory.adapter = recyclerAdapter
                                    recylerHistory.layoutManager = layout
                                }
                            } else {
                                Toast.makeText(
                                    activity as Context, "Invalid Password or Number",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
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
}