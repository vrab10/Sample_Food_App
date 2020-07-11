package com.example.food.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.R
import com.example.food.data.MenuData
import com.example.food.data.Order
import java.text.SimpleDateFormat
import java.util.*

class OrderAdapter(val context: Context,
                   private val orderHistoryList: ArrayList<Order>
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): OrderViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.order_single_row, p0, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderHistoryList.size
    }

    override fun onBindViewHolder(p0: OrderViewHolder, p1: Int) {
        val orderHistory = orderHistoryList[p1]
        p0.txtResName.text = orderHistory.resName
        p0.txtDate.text = formatDates(orderHistory.orderDate)

        val foodItemsList = ArrayList<MenuData>()
        for (i in 0 until orderHistory.foodItem.length()) {
            val foodJson = orderHistory.foodItem.getJSONObject(i)
            foodItemsList.add(
                MenuData(
                    foodJson.getString("food_item_id").toInt(),
                    foodJson.getString("name"),
                    foodJson.getString("cost"))
            )
        }

        val cartItemAdapter = CartAdapter(context, foodItemsList)
        val mLayoutManager = LinearLayoutManager(context)
        p0.recyclerResHistory.layoutManager = mLayoutManager
        p0.recyclerResHistory.itemAnimator = DefaultItemAnimator()
        p0.recyclerResHistory.adapter = cartItemAdapter

    }


    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResName: TextView = view.findViewById(R.id.txtResHistoryResName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val recyclerResHistory: RecyclerView = view.findViewById(R.id.recyclerResHistoryItems)
    }

    private fun formatDates(dateString: String): String? {
        val inputFormatter = SimpleDateFormat("dd-MM-yy HH:mm:ss", Locale.ENGLISH)
        val date: Date = inputFormatter.parse(dateString) as Date
        val outputFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        return outputFormatter.format(date)
    }

}