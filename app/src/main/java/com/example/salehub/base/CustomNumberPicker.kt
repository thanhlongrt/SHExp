package com.example.salehub.base

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import androidx.core.content.ContextCompat
import com.example.salehub.R


/**
 * Created by Thanh Long Nguyen on 6/18/2021
 */
class CustomNumberPicker : NumberPicker {

    constructor(context: Context) : super(context) {
        hideDividers()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        hideDividers()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        hideDividers()
    }

    override fun addView(child: View?) {
        super.addView(child)
        updateView(child)
    }

    override fun addView(child: View?, index: Int) {
        super.addView(child, index)
        updateView(child)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
        updateView(child)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        updateView(child)
    }

    private fun updateView(view: View?) {
        if (view != null && view is EditText) {
            with(view) {
                textSize = 20f
                setTextColor(ContextCompat.getColor(context, R.color.colorBlackText))
                isEnabled = false
            }
        }
    }

    private fun hideDividers() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            selectionDividerHeight = 0
        } else {
            val numberPickerClass = Class.forName("android.widget.NumberPicker")
            val fields: Array<java.lang.reflect.Field> = numberPickerClass.declaredFields
            for (field in fields) {
                if (field.name == "mSelectionDividerHeight") {
                    field.isAccessible = true
                    try {

                        field.set(this, 0)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    break
                }
                if (field.name == "mSelectionDivider") {
                    field.isAccessible = true
                    try {

                        field.set(this, null)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    break
                }
            }
        }
    }
}