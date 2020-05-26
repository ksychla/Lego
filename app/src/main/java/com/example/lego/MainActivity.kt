package com.example.lego

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var db: DataBase? = null
    private var archivedShown = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        db = DataBase(this, null, null, 1)
        refresh()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_CANCELED){
            refresh()
        }
    }

    private fun refresh(){
        buttonsLayout.removeAllViews()
        val inventories = db!!.getAllInventories()
        addButtons(inventories)
    }

    fun plusButtonClick(v: View){
        val intent = Intent(this, ProjectAdding::class.java)
        startActivityForResult(intent, Activity.RESULT_CANCELED)
    }

    private fun addButtons(inventory: MutableList<Inventory>){
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val archived = sharedPreferences.getBoolean("attachment", false)
        for(inv in inventory){
            if(inv.active!! > 0){
                val lay = LinearLayout(this)
                val bt = Button(this)
                val arch = Button(this)
                bt.text = inv.invName
                arch.text = "arch"
                bt.setOnClickListener{ changeToInventory(inv) }
                arch.setOnClickListener{ archiveInventory(inv) }
                lay.addView(bt)
                lay.addView(arch)

                buttonsLayout.addView(lay)
            } else if (archived){
                val lay = LinearLayout(this)
                val bt = Button(this)
                bt.text = inv.invName
                bt.setTextColor(Color.parseColor("#888888"))
                bt.setOnClickListener{ changeToInventory(inv) }
                lay.addView(bt)

                buttonsLayout.addView(lay)
            }
        }
    }

    private fun changeToInventory(inv: Inventory){
        val intent = Intent(this, ProjectActivity::class.java)
        intent.putExtra("inventoryNo", inv.id)
        startActivityForResult(intent, Activity.RESULT_CANCELED)
    }

    private fun archiveInventory(inv: Inventory){
        db?.archiveInventory(inv.id!!)
        refresh()
    }

    fun changeToSettings(v: View){
        val intent = Intent(this, SettingsActivity::class.java)
        startActivityForResult(intent, Activity.RESULT_CANCELED)
    }

}
