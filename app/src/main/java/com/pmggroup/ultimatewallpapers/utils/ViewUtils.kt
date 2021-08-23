package com.pmggroup.ultimatewallpapers.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.pmggroup.ultimatewallpapers.application.UltimateWallpapersApplication
import com.google.android.material.snackbar.Snackbar


@SuppressLint("ShowToast")
fun Context.toast(message: String) {
    // for (count in 0..1)
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun isNetworkAvailable(): Boolean {

    val connectivityManager =
        UltimateWallpapersApplication.context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activateNetworkInfo = connectivityManager.activeNetworkInfo
    return activateNetworkInfo != null && activateNetworkInfo.isConnected
}


fun showLog(message: String) {
//    if (BuildConfig.DEBUG)
    Log.e("UltimateWallpaper", message)

}


fun View.snackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.hideKeyboard(activity: Activity) {
    val imm =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}