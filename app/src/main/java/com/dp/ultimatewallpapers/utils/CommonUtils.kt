package com.dp.ultimatewallpapers.utils


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.view.View
import android.view.inputmethod.InputMethodManager




    fun showKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    public fun closeKeyboard(context: Context) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(
            InputMethodManager.HIDE_IMPLICIT_ONLY,
            0
        )
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    fun showHide(context: Context, view: View, show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime: Int =
                context.resources.getInteger(android.R.integer.config_shortAnimTime)
            view.animate().setDuration(shortAnimTime.toLong()).alpha(
                if (show) 1F else 0F
            ).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            view.visibility = if (show) View.VISIBLE else View.GONE
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    fun showInvisible(context: Context, view: View, show: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val shortAnimTime: Int =
                context.resources.getInteger(android.R.integer.config_shortAnimTime)
            view.animate().setDuration(shortAnimTime.toLong()).alpha(
                if (show) 1F else 0F
            ).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = if (show) View.VISIBLE else View.INVISIBLE
                }
            })
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            view.setVisibility(if (show) View.VISIBLE else View.INVISIBLE)
        }
    }

