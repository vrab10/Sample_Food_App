package com.example.food.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.food.R
import com.example.food.activity.RestMenu
import com.example.food.database.CartDatabase
import com.example.food.database.RestData
import com.squareup.picasso.Picasso

class FavAdapter(val context: Context,val list:List<RestData>): RecyclerView.Adapter<FavAdapter.AllViewHolder>() {


    class AllViewHolder(val view: View): RecyclerView.ViewHolder(view){

        val imgpic: ImageView =view.findViewById(R.id.imgpic)
        val txtRname: TextView =view.findViewById(R.id.txtRname)
        val txtRprice: TextView =view.findViewById(R.id.txtRprice)
        val txtRrating: TextView =view.findViewById(R.id.txtRrating)
        val layout: LinearLayout =view.findViewById(R.id.layoutFile)
        val image: ImageView =view.findViewById(R.id.imgfav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.all_single_row,parent,false)
        return FavAdapter.AllViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: AllViewHolder, position: Int) {
        val res=list[position]
        var a=res.cost_for_one
        holder.txtRname.text=res.name
        holder.txtRprice.text="Rs $a"
        holder.txtRrating.text=res.rating
        Picasso.get().load(res.image_url).error(R.drawable.ic_launcher_foreground).into(holder.imgpic)


        val on=RestData(res.rest_id,res.name,res.rating,res.cost_for_one,res.image_url)
        if(FavAdapter.DBAsynctask(context, on, 1).execute().get()){
            holder.image.setImageResource(R.drawable.ic_color_fav)
        }
        else{
            holder.image.setImageResource(R.drawable.ic_c_fav)
        }

        var rest=on
        holder.image.setOnClickListener {
            rest=RestData(res.rest_id,res.name,res.rating,res.cost_for_one,res.image_url)
            if(FavAdapter.DBAsynctask(context, rest, 1).execute().get()){
                FavAdapter.DBAsynctask(context, rest, 3).execute().get()
                holder.image.setImageResource(R.drawable.ic_c_fav)
            }
            else{
                if(FavAdapter.DBAsynctask(context, rest, 2).execute().get()) {
                    holder.image.setImageResource(R.drawable.ic_color_fav)
                }
                else{
                    Toast.makeText(context,"Some Error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.layout.setOnClickListener {
            val intent= Intent(context, RestMenu::class.java)
            intent.putExtra("id",res.rest_id.toInt())
            intent.putExtra("name",res.name)
            context.startActivity(intent)
        }

    }


    class DBAsynctask(val context: Context,val restEntity: RestData,val mode: Int): AsyncTask<Void, Void, Boolean>(){

        val db= Room.databaseBuilder(context, CartDatabase::class.java,"fav-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1 ->{
                    val book : RestData?=db.restDao().getFavById(restEntity.rest_id)
                    db.close()
                    return book!=null
                }
                2->{
                    db.restDao().addToFav(restEntity)
                    db.close()
                    return true

                }
                3->{
                    db.restDao().addToDelete(restEntity)
                    db.close()
                    return true
                }
            }

            return false
        }

    }



}

