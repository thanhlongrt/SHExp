package com.example.salehub

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentCoroutineBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class CoroutineFragment : BaseViewBindingFragment<FragmentCoroutineBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentCoroutineBinding? {
        return FragmentCoroutineBinding.inflate(inflater, container, false)
    }

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e("exceptionHandler", "$coroutineContext: $throwable")
    }

    override fun initView() {
        binding?.run {
            btnExceptionHandler.setOnClickListener {
                lifecycleScope.launch(exceptionHandler) {
                    func1()
                    func2()
                    func3()
                }
            }
            btnSupervisorScope.setOnClickListener {
                lifecycleScope.launch {
                    supervisorScope {
                        func1()
                        func2()
                        func3()
                    }
                }
            }
        }
    }

    private suspend fun func1() {
        delay(500)
        Log.e("TAG", "func1: end")
    }

    private suspend fun func2() {
        delay(1000)
        Log.e("TAG", "func2: end")
        throw NullPointerException()
    }

    private suspend fun func3() {
        delay(1500)
        Log.e("TAG", "func3: end")
    }
}
