package com.app.customelauncher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.app.myapplication.AppInfo

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    fun findAppsBasisOnQuery(query: String, appsList: ArrayList<AppInfo>?) =
        liveData<ArrayList<AppInfo>?> {
            if(query==null || query.isEmpty()){
                emit(null)
            }else{
                emit(appsList?.filter {
                    it.label.toString().trim().contains(query, true)
                } as ArrayList<AppInfo>)
            }
        }


}