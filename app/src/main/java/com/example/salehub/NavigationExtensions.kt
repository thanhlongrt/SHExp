package com.example.salehub

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation

/**
 * Created by Thanh Long Nguyen on 6/10/2021
 */

fun Fragment.getFragmentNavController(@IdRes id: Int) = activity?.let {
    return@let Navigation.findNavController(it, id)
}