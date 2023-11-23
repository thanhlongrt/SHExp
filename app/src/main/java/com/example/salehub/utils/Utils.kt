package com.example.salehub.utils

import com.example.salehub.SHApp

object Utils {

    fun getStatusBarHeight(): Int {
        var sttBarHeight = 0
        var resId =
            SHApp.getAppContext().resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            sttBarHeight = SHApp.getAppContext().resources.getDimensionPixelSize(resId)
        }
        return sttBarHeight
    }

    val Any.TAG: String
        get() = this.javaClass.simpleName
}
