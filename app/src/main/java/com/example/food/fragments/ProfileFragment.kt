package com.example.food.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.food.R

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

 lateinit var username:TextView
    lateinit var useremail:TextView
    lateinit var userplace:TextView
    lateinit var usernumber:TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view=inflater.inflate(R.layout.fragment_profile, container, false)

        username=view.findViewById(R.id.username)
        useremail=view.findViewById(R.id.useremail)
        userplace=view.findViewById(R.id.userplace)
        usernumber=view.findViewById(R.id.usernumber)

       var name=arguments?.getString("name")
       var email=arguments?.getString("email")
       var mobile=arguments?.getString("mobile")
       var address=arguments?.getString("address")

        username.text=name
        usernumber.text=mobile
        userplace.text=address
        useremail.text=email


        return view
    }

}
