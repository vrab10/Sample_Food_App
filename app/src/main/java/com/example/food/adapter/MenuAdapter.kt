package com.example.food.adapter

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.food.R
import com.example.food.data.MenuData
import com.example.food.database.CartDatabase
import com.example.food.database.CartEntity
import com.example.food.fragments.MenuFragment


class MenuAdapter(val context: Context, val list: ArrayList<MenuData>,val listener: OnItemClickListener
) :RecyclerView.Adapter<MenuAdapter.MenuViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rest_menu_single_row,parent,false)
        return MenuViewHolder(view)

    }

    override fun getItemCount(): Int {
     return list.size
    }


    interface OnItemClickListener{
        fun onAddItemClick(foodItem: MenuData)
        fun onRemoveItemClick(foodItem: MenuData)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val men=list[position]
        val sum=men.cost
        holder.number.text=men.number.toString()
        holder.itemame.text=men.name
        holder.itemcost.text="Rs $sum"

        holder.btnCart.setOnClickListener {
            if(holder.btnCart.text=="Add") {
                holder.btnCart.text = "Remove"
                val favColor = ContextCompat.getColor(context, R.color.yellow)
                holder.btnCart.setBackgroundColor(favColor)
                listener.onAddItemClick(men)
            }
            else{
                holder.btnCart.text = "Add"
                val favColor = ContextCompat.getColor(context, R.color.colorPrimary)
                holder.btnCart.setBackgroundColor(favColor)
                listener.onRemoveItemClick(men)
            }

        }
    }


    class MenuViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val number: TextView=view.findViewById(R.id.number)
        val itemame:TextView=view.findViewById(R.id.itemname)
        val itemcost:TextView=view.findViewById(R.id.itemcost)
        val btnCart: Button=view.findViewById(R.id.btnCart)
    }

}