package com.jk.gogit.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.jk.gogit.ui.feed.FeedFragment
import com.jk.gogit.ui.profile.fragments.UserProfileFragment
import javax.inject.Inject

class MainFragmentFactory
@Inject constructor() : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {

            FeedFragment::class.java.name -> {
                val fragment = FeedFragment()
                fragment
            }
            UserProfileFragment::class.java.name -> {
                val fragment = UserProfileFragment()
                fragment
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}