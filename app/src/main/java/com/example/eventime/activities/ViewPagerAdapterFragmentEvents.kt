package com.example.eventime.activities

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ViewPagerAdapterFragmentEvents(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var fragments: ArrayList<Fragment> = ArrayList()
    private var fragmentsTitles: ArrayList<String> = ArrayList()

    fun addFragment(fragmento: Fragment, titulo: String) {
        fragments.add(fragmento)
        fragmentsTitles.add(titulo)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentsTitles[position]
    }
}