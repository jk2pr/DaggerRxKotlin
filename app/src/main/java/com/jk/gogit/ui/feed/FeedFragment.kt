package com.jk.gogit.ui.feed

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.jk.gogit.R
import com.jk.gogit.extensions.hide
import com.jk.gogit.extensions.show
import com.jk.gogit.ui.adapters.FeedAdapter
import com.jk.gogit.ui.constants.ARGUMENTS
import com.jk.gogit.ui.feed.viewmodels.FeedViewModel
import com.jk.gogit.ui.login.data.response.Resource
import com.jk.gogit.ui.view.BaseActivity
import com.jk.gogit.ui.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_feed.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@AndroidEntryPoint
class FeedFragment : Fragment(R.layout.fragment_feed), FeedAdapter.OnViewSelectedListener {
    var lastPage: Int = 1
    var lastVisibleItem: Int = 0
    var totalItemCount: Int = 0
    var loading = false
    var pageNumber = 1

    @ExperimentalCoroutinesApi
    private val feedViewModel: FeedViewModel by activityViewModels()
    //(R.id.nav) { defaultViewModelProviderFactory }

    private lateinit var activity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }


    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        setUpToolbar()
        setUpUI()
        setupObserver()
    }

    private fun setUpToolbar() {
        val navController = findNavController()
        setupWithNavController(activity.collapsing_toolbar, activity.toolbar, navController)
        activity.toolbar?.let {
            it.elevation = 0.0f
            it.overflowIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_overflow_white)
            activity.setSupportActionBar(it)
        }
    }


    @ExperimentalCoroutinesApi
    private fun setUpUI() {
        swipeRefresh?.setColorSchemeColors(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_bright),
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_light),
                ContextCompat.getColor(requireContext(), android.R.color.holo_orange_light),
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_light))

        swipeRefresh?.setOnRefreshListener {
            feedViewModel.setState(FeedViewModel.Action.Refresh)
            // loadPage()
        }

        feed_recyclerView.apply {
            setHasFixedSize(true)
            val lManager = androidx.recyclerview.widget.LinearLayoutManager(context)
            layoutManager = lManager
            feed_recyclerView.adapter = FeedAdapter(requestManager = activity.requestManager, viewActions = this@FeedFragment)
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
            Toast.makeText(activity, it.id.toString(), Toast.LENGTH_SHORT).show()
            feedViewModel.setState(FeedViewModel.Action.Refresh)
        }
    }

    @InternalCoroutinesApi
    @ExperimentalCoroutinesApi
    private fun setupObserver() {
        feedViewModel.feedDataLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    val feeds = it.data
                    (feed_recyclerView.adapter as FeedAdapter).addItems(feeds)
                    showLoader(false, isError = false)
                }
                is Resource.Loading -> {
                    showLoader(true, isError = false)
                }
                is Resource.Error -> {
                    if (swipeRefresh.isRefreshing)
                        swipeRefresh.isRefreshing = false

                    showLoader(false, isError = true)
                    Toast.makeText(activity, it.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

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

    override fun onFeedItemClick(textView: View, owner: String) {
        // NavUtils.redirectToRepoDetails(requireActivity(), owner)
    }

    override fun onActorImageClick(imageView: View, loginId: String) {
        findNavController().navigate(R.id.action_fragment_feed_to_userProfileFragment, bundleOf(Pair(ARGUMENTS.KEY_USER_ID, loginId)))
        // NavUtils.redirectToProfile(requireActivity(), loginId)
    }


}