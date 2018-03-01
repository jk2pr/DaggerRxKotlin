package com.jk.gogit.ui.view.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.jk.gogit.R
import com.jk.gogit.model.Users
import com.jk.gogit.ui.adapters.UserAdapter
import kotlinx.android.synthetic.main.fragment_following.*


/**
 * A simple [Fragment] subclass.
 */
class FollowingFragment : Fragment(), UserAdapter.OnViewSelectedListener {
    override fun onItemSelected(url: String?) {

        print("On ITEm Click in Follwores Fragment")
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView_following.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)

        }
        recyclerView_following.adapter = UserAdapter(this)
    }

    fun updateAdapter(data: List<Users>) {
        (recyclerView_following.adapter as UserAdapter).addItems(data)
    }
}// Required empty public constructor