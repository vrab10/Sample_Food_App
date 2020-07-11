package com.example.food.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
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
import com.example.food.adapter.AllAdapter
import com.example.food.data.Rest
import com.example.food.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

/**
 * A simple [Fragment] subclass.
 */
class RestaurantFragment : Fragment() {

    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var recyclerView: RecyclerView
    lateinit var layout:RecyclerView.LayoutManager
    lateinit var recyclerAdapter: AllAdapter
    val list= arrayListOf<Rest>()

    var ratingComparator= Comparator<Rest> { rest1, rest2 ->

        if(rest1.rating.compareTo(rest2.rating,true)==0)
        {
            rest1.rating.compareTo(rest2.rating,true)
        }

        else{
            rest1.rating.compareTo(rest2.rating,true)
        }
    }

    var costComparator= Comparator<Rest> { rest1, rest2 ->

        if(rest1.cost_for_one.compareTo(rest2.cost_for_one,true)==0)
        {
            rest1.cost_for_one.compareTo(rest2.cost_for_one,true)
        }

        else{
            rest1.cost_for_one.compareTo(rest2.cost_for_one,true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_restaurant, container, false)

        progressBar=view.findViewById(R.id.progressBar)
        progressLayout=view.findViewById(R.id.progressLayout)
        recyclerView=view.findViewById(R.id.recylerView)
        layout= LinearLayoutManager(activity)

        progressLayout.visibility=View.VISIBLE
        progressBar.visibility=View.VISIBLE

        setHasOptionsMenu(true)

        val url= "http://13.235.250.119/v2/restaurants/fetch_result/"
        val queue= Volley.newRequestQueue(activity as Context)
        if(ConnectionManager().checkConnectivity(activity as Context)) {
                val jsonRequest=object :
                    JsonObjectRequest(
                        Request.Method.GET,url,null, Response.Listener{
                            try{
                            val obj=it.getJSONObject("data")
                            val success=obj.getBoolean("success")
                            if(success) {
                                progressBar.visibility=View.GONE
                                progressLayout.visibility=View.GONE
                            val array=obj.getJSONArray("data")
                                for(i in 0 until array.length()){
                                    val jsonObj=array.getJSONObject(i)
                                    val rest=Rest(jsonObj.getString("id"),jsonObj.getString("name"),
                                            jsonObj.getString("rating"),jsonObj.getString("cost_for_one"),
                                            jsonObj.getString("image_url"))

                                    list.add(rest)
                                    if (activity != null) {
                                        recyclerAdapter = AllAdapter(activity as Context, list)
                                        recyclerView.adapter = recyclerAdapter
                                        recyclerView.layoutManager = layout
                                    }
                                }

                            }
                            else{
                                Toast.makeText(activity as Context,"Invalid Password or Number",
                                    Toast.LENGTH_SHORT).show()
                            }

                        }catch(e: JSONException){
                                Toast.makeText(activity as Context,"Json Exception Occured", Toast.LENGTH_SHORT).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(activity as Context,"Volley error has occured", Toast.LENGTH_SHORT).show()
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort,menu)   //here menu will store the inflated file
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==R.id.sort){
            val dialog= android.app.AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By?")
            val items=arrayOf("Cost(Low to High)","Cost(High to Low)","Rating")

                dialog.setSingleChoiceItems(
                    items,
                    1,
                    DialogInterface.OnClickListener() { dialogInterface: DialogInterface, i: Int ->

                        when (i) {
                            0 -> {
                                Collections.sort(list, costComparator)
                            }
                            1 -> {
                                Collections.sort(list, costComparator)
                                list.reverse()
                            }
                            2 -> {
                                Collections.sort(list, ratingComparator)
                                list.reverse()
                            }

                        }
                    })
            dialog.setPositiveButton("Ok") { text, listener ->
                recyclerAdapter.notifyDataSetChanged()
        }

            dialog.setNegativeButton("Cancel"){
                    text,listener ->
            }

            dialog.show()
            dialog.create()
        }

        return super.onOptionsItemSelected(item)
    }

}