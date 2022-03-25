package com.app.myapplication

import android.content.Intent
import android.graphics.drawable.Drawable

data class AppInfo(
    val label: CharSequence,
    var packageName: CharSequence,
    var icon: Drawable,
    val launchIntentForPackage: Intent?
)