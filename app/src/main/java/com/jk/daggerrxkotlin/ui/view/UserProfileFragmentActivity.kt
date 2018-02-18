package com.jk.daggerrxkotlin.ui.view

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.firebase.analytics.FirebaseAnalytics
import com.jk.daggerrxkotlin.application.MyApplication
import com.jk.daggerrxkotlin.db.AppDatabase
import com.jk.daggerrxkotlin.extensions.loading
import com.jk.daggerrxkotlin.model.Repo
import com.jk.daggerrxkotlin.model.UserProfile
import com.jk.daggerrxkotlin.ui.adapters.RepoAdapter
import com.jk.daggerrxkotlin.ui.viewmodel.UserViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_user_profile.*
import org.jetbrains.anko.AnkoLogger
import javax.inject.Inject
import kotlin.jk.com.dagger.R
import android.arch.lifecycle.ViewModelProviders


class UserProfileActivity : BaseActivity(), AnkoLogger {


    @Inject
    lateinit var appDatabase: AppDatabase
    @Inject
    lateinit var mFirebaseAnalytics: FirebaseAnalytics
    var subscriptions = CompositeDisposable()

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_user_profile
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MyApplication.appComponent.inject(this)
        showLoader(true)
        recyclerView_repo.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = RepoAdapter(null)

        }
        val model = ViewModelProviders.of(this).get(UserViewModel::class.java)
        subscriptions.add(model.getUser().subscribe({
            updateUI(it)
        }, { e ->
            e.printStackTrace()
        }, {
            print("ONComplete")
        })
        )

    }

    fun updateUI(data: MutableMap<UserProfile, List<Repo>>) {
        showLoader(false)
        for ((key, value) in data) {
            txt_displayname.text = key.name
            txt_email.text = key.email
            profile?.loading(key.avatarUrl)
            followers_count.text= "Following ${key.followers.toString()}"
            followering_count.text= "Followers ${key.following.toString()}"
            repo_count.text= "Repositories ${key.publicRepos.toString()}"
            if (recyclerView_repo?.adapter != null) {
                val adapter = recyclerView_repo?.adapter as RepoAdapter
                with(adapter) {
                    clearItems()
                    addItems(value)
                    showLoader(false)
                }
            }
        }
    }


    fun showLoader(isLoading: Boolean) {
        when (isLoading) {
            true -> {
                progressbar.visibility = View.VISIBLE
                parentConstraintUsrProfile.visibility = View.GONE
            }
            false -> {
                progressbar.visibility = View.GONE
                parentConstraintUsrProfile.visibility = View.VISIBLE
            }
        }

    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
        subscriptions.dispose()
    }
}
