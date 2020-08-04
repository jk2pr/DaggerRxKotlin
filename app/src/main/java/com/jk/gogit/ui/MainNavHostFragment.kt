package com.jk.gogit.ui

import android.content.Context
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jk.gogit.ui.MainFragmentFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainNavHostFragment : NavHostFragment() {



    @Inject
    lateinit var mainFragmentFactory: MainFragmentFactory
    override fun onAttach(context: Context) {
        super.onAttach(context)
        childFragmentManager.fragmentFactory = mainFragmentFactory
    }
}