package com.jk.gogit

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jk.gogit.utils.NavUtils
import kotlinx.android.synthetic.main.activity_no_login_main.*

class NoLoginFragment : Fragment(R.layout.activity_no_login_main) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        btn_login.setOnClickListener {
            NavUtils.redirectToLogin(requireActivity())
        }
    }

}
