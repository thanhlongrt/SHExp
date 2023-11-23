package com.example.salehub.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import java.lang.reflect.ParameterizedType

abstract class BaseDataBindingFragment<B : ViewDataBinding> : BaseFragment() {

    private lateinit var mViewBinding: B

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return setupView(inflater, container, getLayoutId())
    }

    fun setupView(inflater: LayoutInflater, container: ViewGroup?, layoutId: Int): View {
        mViewBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)

//        mViewBinding.setVariable(BR.viewModel, mViewModel)
//        mViewBinding.setVariable(BR.view, this)

        setupExtraVariable(mViewBinding)
        mViewBinding.lifecycleOwner = this
        return mViewBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    open fun setupExtraVariable(mViewBinding: B) {
    }

    private fun getGenericType(clazz: Class<*>): Class<*> {
        val type = clazz.genericSuperclass
        val paramType = type as ParameterizedType
        return paramType.actualTypeArguments[0] as Class<*>
    }
}