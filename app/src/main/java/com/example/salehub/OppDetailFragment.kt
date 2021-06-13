package com.example.salehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

/**
 * Created by Thanh Long Nguyen on 6/10/2021
 */
class OppDetailFragment: Fragment() {

    private lateinit var tabLayout: TabLayout

    private lateinit var viewPager: ViewPager

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_opp_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tabLayout = view.findViewById(R.id.tabLayout)
        viewPager = view.findViewById(R.id.viewPager)

        viewPagerAdapter = ViewPagerAdapter(parentFragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)

        view.findViewById<TextView>(R.id.close_opp_button).setOnClickListener {
            getFragmentNavController(R.id.nav_host_fragment)?.navigate(R.id.action_oppDetailFragment_to_lostOppFragment)
        }

    }
}