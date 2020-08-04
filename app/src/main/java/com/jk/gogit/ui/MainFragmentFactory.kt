package com.jk.gogit.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.jk.gogit.ui.feed.FeedFragment
import javax.inject.Inject

class MainFragmentFactory
@Inject constructor() : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {

            FeedFragment::class.java.name -> {
                val fragment = FeedFragment()
                fragment
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}