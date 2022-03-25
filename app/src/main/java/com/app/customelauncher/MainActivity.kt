package com.app.customelauncher

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.myapplication.AppInfo
import com.app.myapplication.AppInfoUtils
import com.app.myapplication.Result

class MainActivity : AppCompatActivity(), View.OnClickListener, TextWatcher {

    lateinit var mainActivityViewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val appInfoUtils = AppInfoUtils.getInstance(this)
        appInfoUtils?.getApplicationInfo()?.observe(this, {
            when(it){
                is Result.Success->{
                    val recyclerviewApplications = findViewById<RecyclerView>(R.id.recyclerview_applications)
                    if(recyclerviewApplications.adapter==null) {
                        val displayMetrics = DisplayMetrics()
                        windowManager.defaultDisplay.getMetrics(displayMetrics)
                        var width = (displayMetrics.widthPixels / 4)
                        recyclerviewApplications.adapter = AppsAdapter(it.data,this,width)
                        recyclerviewApplications.layoutManager = GridLayoutManager(this,4)
                    }else{
                        val appsAdapter : AppsAdapter = recyclerviewApplications.adapter as AppsAdapter
                        appsAdapter.notifyDataSetChanged(it.data)
                    }
                }
            }
        })
        appInfoUtils?.getStatusOfAppInstallUninstall()?.observe(this, {
            Toast.makeText(this,it,Toast.LENGTH_LONG).show()
        })
        mainActivityViewModel = MainActivityViewModel(application)
        findViewById<EditText>(R.id.editTextSearch).addTextChangedListener(this)

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.item_app->{
                val appInfo : AppInfo = v.getTag(R.string.tag_item_application) as AppInfo
                startActivity(appInfo.launchIntentForPackage)
            }
        }
    }

    override fun onBackPressed() {}

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        val recyclerviewApplications = findViewById<RecyclerView>(R.id.recyclerview_applications)
        if(recyclerviewApplications.adapter!=null) {
            val appsAdapter : AppsAdapter = recyclerviewApplications.adapter as AppsAdapter
            mainActivityViewModel.findAppsBasisOnQuery(s.toString().trim(),appsAdapter.appsList)
                .observe(this, Observer {
                    appsAdapter.notifySearchDataSetChanged(it)
                })
        }

    }

}