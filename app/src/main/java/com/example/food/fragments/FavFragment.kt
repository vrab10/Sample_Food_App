package com.example.food.fragments

import android.content.AbstractThreadedSyncAdapter
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.example.food.R
import com.example.food.adapter.FavAdapter
import com.example.food.database.CartDatabase
import com.example.food.database.RestData

/**
 * A simple [Fragment] subclass.
 */
class FavFragment : Fragment() {
lateinit var recyclerView: RecyclerView
lateinit var layout:RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
         val view=inflater.inflate(R.layout.fragment_fav, container, false)

        layout=LinearLayoutManager(activity)
        recyclerView=view.findViewById(R.id.recylerFav)

        val async=DBAsynctask(activity as Context)
        recyclerView.adapter=FavAdapter(activity as Context,async.execute().get())
        recyclerView.layoutManager=layout

        return view
    }


    class DBAsynctask(val context: Context): AsyncTask<Void, Void, List<RestData>>(){

        val db= Room.databaseBuilder(context, CartDatabase::class.java,"fav-db").build()

        override fun doInBackground(vararg params: Void?): List<RestData> {
                    val a=db.restDao().getAllFav()
                    db.close()
                    return a
            }

        }

}
