package com.example.salehub.account

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salehub.R
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentSaleHubAccountBinding
import com.example.salehub.utils.Utils
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

class SaleHubAccountFragment : BaseViewBindingFragment<FragmentSaleHubAccountBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentSaleHubAccountBinding? {
        return FragmentSaleHubAccountBinding.inflate(inflater, container, false)
    }

    private lateinit var mAccountAdapter: MyAccountAdapter

    override fun initView() {
        setMarginTop()
        setupAccountList()
        initToolbarAndCoordinatorLayout()
        setupOnScroll()
    }

    private fun setupOnScroll() {
        binding?.appBar?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val total = appBarLayout?.totalScrollRange?.toFloat() ?: 1f
            val current = abs(verticalOffset).toFloat()
            val percent = current / total
            when (appBarLayout.totalScrollRange) {
                abs(verticalOffset) -> {
                }
                else -> {
                    // decorate swipe refresh layout
                }
            }
            binding?.ivBanner?.alpha = 1 - percent

            binding?.imgPerson?.alpha = 1 - percent
            binding?.cvSearch?.alpha = 1 - percent

            binding?.cvSearch?.translationY = percent *
                    ((binding?.toolBar?.height
                        ?: 0) + resources.getDimensionPixelSize(R.dimen._16_am_dp))


            val params = binding?.rvMyAccounts?.layoutParams as? CoordinatorLayout.LayoutParams
            params?.let {
                val defaultMarginTop = resources.getDimensionPixelSize(R.dimen._48dp)
                val scrollMarginTop = resources.getDimensionPixelSize(R.dimen._54dp)
                params.topMargin = defaultMarginTop + (scrollMarginTop * percent).toInt()
                binding?.rvMyAccounts?.layoutParams = it
            }

        })
    }

    private fun setupAccountList() {
        mAccountAdapter = MyAccountAdapter()
        val data = mutableListOf<String>()
        (1..99).forEach {
            data.add("Account $it$it$it")
        }
        mAccountAdapter.setDataList(data)
        binding?.rvMyAccounts?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAccountAdapter
        }
    }

    private fun initToolbarAndCoordinatorLayout() {
//        binding?.coordinatorLayout?.let { ViewCompat.requestApplyInsets(it) }
    }

    fun setMarginTop() {
        val param = binding?.toolBar?.layoutParams as? FrameLayout.LayoutParams
        param?.apply {
            topMargin = Utils.getStatusBarHeight()
            binding?.toolBar?.layoutParams = this
        }
    }

}