package com.jk.gogit.ui.feed

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.navGraphViewModels
import com.google.firebase.auth.FirebaseAuth
import com.jk.gogit.R
import com.jk.gogit.extensions.hide
import com.jk.gogit.extensions.show
import com.jk.gogit.ui.adapters.FeedAdapter
import com.jk.gogit.ui.feed.viewmodels.FeedViewModel
import com.jk.gogit.ui.login.data.response.Resource
import com.jk.gogit.ui.view.BaseActivity
import com.jk.gogit.ui.view.MainActivity
import com.jk.gogit.ui.view.SplashActivity
import com.jk.gogit.utils.NavUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed), FeedAdapter.OnViewSelectedListener {

    var lastPage: Int = 1
    var lastVisibleItem: Int = 0
    var totalItemCount: Int = 0
    var loading = false
    var pageNumber = 1
    private val feedViewModel: FeedViewModel by navGraphViewModels(R.id.nav) { defaultViewModelProviderFactory }

    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as MainActivity).app_bar_layout.show()
    }
    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            super.onViewCreated(view, savedInstanceState)
            setUpToolbar()
            setUpUI()
            setupObserver()
            feedViewModel.setState(FeedViewModel.MainState.FeedEvent)
        }
    private fun setUpToolbar() {
        toolbar ?.let {
            it.elevation = 0.0f
            //findOptional<AppBarLayout>(R.id.app_bar_layout)?.setPadding(0, getStatusBarHeight(), 0, 0)
            //toolbar.visibility = View.VISIBLE
            it.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_overflow_white)
            //toolbar_title.text = getText(R.string.app_name)
            (activity as SplashActivity).setSupportActionBar(it)
        }
    }


    private fun setUpUI()
    {
        swipeRefresh?.setColorSchemeColors(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_bright),
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_light),
                ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light),
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))

        swipeRefresh?.setOnRefreshListener {
           // loadPage()
        }

        feed_recyclerView.apply {
            setHasFixedSize(true)
            val lManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            layoutManager = lManager
            feed_recyclerView.adapter = FeedAdapter(this@FeedFragment)
            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView,
                                        dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    totalItemCount = feed_recyclerView.layoutManager!!.itemCount
                    lastVisibleItem = (feed_recyclerView.layoutManager as androidx.recyclerview.widget.LinearLayoutManager).findLastVisibleItemPosition()
                    if (!loading && totalItemCount <= lastVisibleItem + BaseActivity.VISIBLE_THRESHOLD) {
                        pageNumber++
                        loading = true
                       // loadPage()
                    }
                }
            })

        }
        txt_internet.setOnClickListener {
        //    doLoadingInit()
        }
    }

    private fun setupObserver()
    {
        feedViewModel.feedDataLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    val feeds=it.data
                    (feed_recyclerView.adapter as FeedAdapter).addItems(feeds)


                    showLoader(false,isError = false)
                }
                is Resource.Loading -> {
                    showLoader(true,isError = false)
                }
                is Resource.Error -> {
                    showLoader(false,isError = true)
                    Toast.makeText(activity, it.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

 /*  private val mainActivity by lazy { activity as MainActivity }
    private fun doLoadingInit() {
        showLoader(true, false)


       *//* mainActivity.subscriptions.add(mainActivity.api.getMyProfile()
                .flatMap {
                    mainActivity.save(it)
                    // loadPage()
                    mainActivity.api.getFeed(it.login, pageNumber, sMaxRecord)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    lastPage = mainActivity.handlePagination(it.headers())
                    loading = false
                    showLoader(false, false)
                    if (it.body()!!.isEmpty())
                        showEmptyView()
                    else
                        (feed_recyclerView.adapter as FeedAdapter).addItems(it.body()!!)
                }, {
                    showLoader(false, true)
                }))*//*
    }*/

   /* private fun loadPage() {
        if (pageNumber in 1..lastPage) {
            mainActivity.subscriptions.add(
                    mainActivity.api.getFeed(mainActivity.getLoginData()!!.login, pageNumber, sMaxRecord)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                //lastPage = handlePagination(it.headers())
                                loading = false
                                showLoader(false, false)
                                if (it.body()!!.isEmpty())
                                    showEmptyView()
                                else
                                    if (pageNumber == 1)
                                        (feed_recyclerView.adapter as FeedAdapter).clearItems()
                                (feed_recyclerView.adapter as FeedAdapter).addItems(it.body()!!)

                            }, {
                                onError(it)
                                showLoader(false, true)
                            }))

        }
    }*/

    private fun showEmptyView() {
        txt_internet.visibility = View.INVISIBLE
        progressbar?.visibility = View.INVISIBLE
        txt_empty.show()
    }

    fun showLoader(isLoading: Boolean, isError: Boolean) {
        txt_empty.visibility = View.INVISIBLE
        if (isError) {
            txt_internet.show()
            main_content?.visibility = View.INVISIBLE
            progressbar?.visibility = View.INVISIBLE
            return
        } else
            txt_internet.hide()
        if (isLoading) {
            main_content?.visibility = View.INVISIBLE
            progressbar?.show()

        } else {
            swipeRefresh.isRefreshing = false
            main_content?.show()
            progressbar?.visibility = View.INVISIBLE
        }

    }

    override fun onRepNameClicked(textView: TextView, owner: String) {
        NavUtils.redirectToRepoDetails(requireActivity(), owner)
    }

    override fun onActorNameClicked(imageView: ImageView, loginId: String) {
        NavUtils.redirectToProfile(requireActivity(), loginId)
    }


}