package com.example.food.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.example.food.R
import com.example.food.fragments.MenuFragment

class RestMenu : AppCompatActivity() {

    lateinit var menuTool:Toolbar
    lateinit var menuFrame:FrameLayout
    var id:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rest_menu)

        menuTool=findViewById(R.id.menuTool)
        menuFrame=findViewById(R.id.menuFrame)

        var name: String?=""

        if(intent!=null){
             id=intent.getIntExtra("id",0)
            name=intent.getStringExtra("name")
        }

        setSupportActionBar(menuTool)
        supportActionBar?.title=name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction().replace(
                R.id.menuFrame,
                MenuFragment()
            ).commit()

        val frag= MenuFragment()  //this is used for sending data between fragment to fragment
        val bundle = Bundle()
        bundle.putInt("id", id)
        bundle.putString("name",name)
        frag.arguments = bundle

        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.menuFrame,frag)       //we can use add also link in stackoverflow
        transaction.commit()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId
        if(id==android.R.id.home){
            val intent= Intent(this@RestMenu, MainActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onStop() {
        super.onStop()
        finish()
    }

}