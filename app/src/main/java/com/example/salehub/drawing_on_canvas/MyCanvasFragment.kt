package com.example.salehub.drawing_on_canvas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentMyCanvasBinding
import kotlin.math.asin

class MyCanvasFragment : BaseViewBindingFragment<FragmentMyCanvasBinding>() {

    override fun initView() {
        binding?.btnClear?.setOnClickListener {
            binding?.canvasView?.clear()
        }
    }

    fun angle(doi: Double, huyen: Double): Double {
        return Math.toDegrees(asin(doi / huyen))
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentMyCanvasBinding? {
        return FragmentMyCanvasBinding.inflate(inflater, container, false)
    }

    fun screenShot(view: View): Bitmap? {
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}
