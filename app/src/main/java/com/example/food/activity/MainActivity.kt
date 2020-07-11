package com.example.food.activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.widget.Toolbar  //added files
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.food.R
import com.example.food.fragments.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var mtoolBar:Toolbar
    lateinit var drawerLayout:DrawerLayout
    lateinit var navBar: NavigationView

    var PreviousMenuItem:MenuItem?=null

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences=getSharedPreferences(getString(R.string.shared), Context.MODE_PRIVATE)


        mtoolBar=findViewById(R.id.mtooBar)
        drawerLayout=findViewById(R.id.drawerLayout)
        navBar=findViewById(R.id.navBar)

        setUpActionBar()
        home()

        val actionBarDrawerToggle=ActionBarDrawerToggle(this@MainActivity,drawerLayout,
            R.string.opendrawer,
            R.string.closedrawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navBar.setNavigationItemSelectedListener {

            if(PreviousMenuItem!=null){
                PreviousMenuItem?.isChecked=false
            }

            it.isCheckable=true
            it.isChecked=true
            PreviousMenuItem=it


            when(it.itemId){
                R.id.Rhome -> {
                    home()
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        ProfileFragment()
                    ).commit()

                    val frag= ProfileFragment()  //this is used for sending data between fragment to fragment
                    val bundle = Bundle()
                    bundle.putString("name", sharedPreferences.getString("name",""))
                    bundle.putString("email",sharedPreferences.getString("email",""))
                    bundle.putString("mobile",sharedPreferences.getString("mobile",""))
                    bundle.putString("address",sharedPreferences.getString("address",""))
                    bundle.putString("u_id",sharedPreferences.getString("u_id",""))
                    frag.arguments = bundle

                    val transaction=supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frameLayout,frag)
                    transaction.commit()

                    supportActionBar?.title="My Profile"

                    drawerLayout.closeDrawers()
                }
                R.id.fav -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        FavFragment()
                    ).commit()

                    supportActionBar?.title="Favourite Restaurants"

                    drawerLayout.closeDrawers()
                }

                R.id.history -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        HistoryFragment()
                    ).commit()

                    val frag= HistoryFragment()  //this is used for sending data between fragment to fragment
                    val bundle = Bundle()
                    bundle.putString("name", sharedPreferences.getString("name",""))
                    bundle.putString("email",sharedPreferences.getString("email",""))
                    bundle.putString("mobile",sharedPreferences.getString("mobile",""))
                    bundle.putString("address",sharedPreferences.getString("address",""))
                    bundle.putString("u_id",sharedPreferences.getString("u_id",""))
                    frag.arguments = bundle

                    val transaction=supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.frameLayout,frag)
                    transaction.commit()

                    supportActionBar?.title="My Previous Orders"
                    drawerLayout.closeDrawers()
                }

                R.id.faq -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frameLayout,
                        FaqFragment()
                    ).commit()

                    supportActionBar?.title="FAQs"

                    drawerLayout.closeDrawers()
                }

                R.id.logout -> {

                    val dialog= AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to exit")

                    dialog.setPositiveButton("Yes"){
                            text,listener ->
                        val intent= Intent(this@MainActivity,
                            LoginActivity::class.java)

                        startActivity(intent)
                        finish()
                    }

                    dialog.setNegativeButton("No"){
                            text,listener ->
                    }
                    dialog.show()
                    dialog.create()

                    drawerLayout.closeDrawers()
                }
            }

            return@setNavigationItemSelectedListener true
        }

//using of layout filter for intializing values in it.

        val view = LayoutInflater.from(this@MainActivity).inflate(R.layout.header,null)
        val name: TextView =view.findViewById(R.id.txtName)
        val number:TextView=view.findViewById(R.id.txtPhone)
        name.text=sharedPreferences.getString("name","")
        number.text=sharedPreferences.getString("mobile","")
        navBar.addHeaderView(view)

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }


    fun setUpActionBar(){
        setSupportActionBar(mtoolBar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun home(){
        supportFragmentManager.beginTransaction().replace(
            R.id.frameLayout,
            RestaurantFragment()
        ).commit()
        supportActionBar?.title="All Restaurants"
        navBar.setCheckedItem(R.id.Rhome)
    }

    override fun onBackPressed() {
        val a = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when (a) {
            !is RestaurantFragment -> home()

            else -> super.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

}
