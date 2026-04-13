package com.herovault.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.herovault.ui.fragments.InventoryFragment
import com.herovault.ui.fragments.QuestFragment
import com.herovault.ui.fragments.StatsFragment

class TabPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> StatsFragment()
        1 -> QuestFragment()
        2 -> InventoryFragment()
        else -> StatsFragment()
    }
}