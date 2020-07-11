package com.example.food.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.food.R
import com.example.food.data.MenuData
import com.example.food.data.Rest

class CartAdapter(val context: Context, val list: ArrayList<MenuData>): RecyclerView.Adapter<CartAdapter.CartViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_single_row,parent,false)
        return CartAdapter.CartViewHolder(view)
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val data=list[position]
        val a=data.cost

        holder.txtItem.text=data.name
        holder.txtPrice.text="Rs $a"
    }


    class CartViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val txtItem:TextView=view.findViewById(R.id.txtItem)
        val txtPrice:TextView=view.findViewById(R.id.txtPrice)

    }
}