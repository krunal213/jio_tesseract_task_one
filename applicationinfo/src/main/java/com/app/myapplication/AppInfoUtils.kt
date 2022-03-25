package com.app.myapplication

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import java.lang.Exception
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.pm.ApplicationInfo
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.*

class AppInfoUtils {

    private val appCompatActivity : AppCompatActivity

    private constructor(appCompatActivity: AppCompatActivity) {
        this.appCompatActivity = appCompatActivity
    }

    companion object {
        private var appInfoUtils : AppInfoUtils? = null
        fun getInstance(appCompatActivity: AppCompatActivity) : AppInfoUtils?{
            synchronized(Any()){
                if(appInfoUtils==null){
                    appInfoUtils = AppInfoUtils(appCompatActivity)
                }
                return appInfoUtils
            }
        }
    }

    private val applicationListLiveData = MutableLiveData<Result<ArrayList<AppInfo>>>()

    fun getApplicationInfo(): LiveData<Result<ArrayList<AppInfo>>> {
        setApplicationInfoResult()
        return applicationListLiveData
    }

    private fun setApplicationInfoResult() {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                applicationListLiveData.postValue(Result.Loading)
                applicationListLiveData.postValue(getApplictions())
            }
        }
    }

    fun getStatusOfAppInstallUninstall(): LiveData<String> {
        val statusLiveData = MutableLiveData<String>()
        class InstallReceiver : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val pendingResult = goAsync()
                setApplicationInfoResult()
                GlobalScope.launch {
                    withContext(Dispatchers.IO) {
                        statusLiveData.postValue(
                            getApplicationName(context, intent).plus(" ")
                                .plus(appCompatActivity.resources.getString(R.string.installed))
                        )
                        pendingResult.finish()
                    }
                }
            }
        }
        class UnInstallReceiver : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val pendingResult = goAsync()
                setApplicationInfoResult()
                statusLiveData.postValue(appCompatActivity.resources.getString(R.string.uninstall_successful))
                pendingResult.finish()
            }
        }
        appCompatActivity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                val intentFilter = IntentFilter()
                intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED)
                intentFilter.addAction(Intent.ACTION_PACKAGE_INSTALL)
                intentFilter.addDataScheme("package")
                appCompatActivity.registerReceiver(InstallReceiver(), intentFilter)

                val uninstallIntentFilter = IntentFilter()
                uninstallIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED)
                uninstallIntentFilter.addDataScheme("package")
                appCompatActivity.registerReceiver(UnInstallReceiver(), uninstallIntentFilter)
            }
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                try {
                    appCompatActivity.unregisterReceiver(InstallReceiver())
                    appCompatActivity.unregisterReceiver(UnInstallReceiver())
                } catch (ex: Exception) {
                }
            }
        })
        return statusLiveData
    }

    private fun getApplictions(): Result<ArrayList<AppInfo>> {
        val pManager: PackageManager = appCompatActivity.packageManager
        val i = Intent(Intent.ACTION_MAIN, null)
        i.addCategory(Intent.CATEGORY_LAUNCHER)
        var appsList = pManager.queryIntentActivities(i, 0)
            .sortedWith(compareBy { it.loadLabel(pManager).toString() })
            .map {
                val label = it.loadLabel(pManager)
                val packageName = it.activityInfo.packageName
                val icon = it.activityInfo.loadIcon(pManager)
                AppInfo(label, packageName, icon,pManager.getLaunchIntentForPackage(packageName))
            } as ArrayList<AppInfo>
        return if (appsList.size > 0) {
            Result.Success(appsList)
        } else {
            Result.Error(Exception(appCompatActivity.resources.getString(R.string.error_no_application_found)))
        }
    }

    private fun getApplicationName(
        context: Context,
        data: Intent,
    ): String {
        val packageManager = context.packageManager
        var applicationInfo: ApplicationInfo? = null
        try {
            val uri: Uri? = data.data
            val installedPackageName: String? = uri?.encodedSchemeSpecificPart
            applicationInfo = installedPackageName?.let { packageManager.getApplicationInfo(it, 0) }
        } catch (e: PackageManager.NameNotFoundException) { }
        return (if (applicationInfo != null) packageManager.getApplicationLabel(applicationInfo) else "") as String
    }

}