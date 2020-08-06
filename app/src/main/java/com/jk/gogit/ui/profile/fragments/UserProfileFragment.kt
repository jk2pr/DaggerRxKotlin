package com.jk.gogit.ui.profile.fragments

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jk.gogit.R
import com.jk.gogit.extensions.loadingA
import com.jk.gogit.model.UserProfile
import com.jk.gogit.ui.constants.ARGUMENTS
import com.jk.gogit.ui.login.data.response.Resource
import com.jk.gogit.ui.profile.viewmodels.UserProfileViewModel
import com.jk.gogit.ui.view.MainActivity
import com.jk.gogit.ui.view.fragments.OrgFragment
import com.jk.gogit.ui.view.fragments.RepoFragment
import com.jk.gogit.utils.DialogUtils
import com.jk.gogit.utils.NavUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.collapsing_toolbar
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.include_profile_header.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.email

@AndroidEntryPoint
class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private val userProfileViewModel: UserProfileViewModel by navGraphViewModels(R.id.nav) { defaultViewModelProviderFactory }
    private lateinit var activity: MainActivity
    private val selectedUser by lazy {
        arguments?.get(ARGUMENTS.KEY_USER_ID) as String
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        setupObserver()

        userProfileViewModel.setLoginId(selectedUser)
        userProfileViewModel.setState(UserProfileViewModel.MainState.UserProfileEvent)
    }


    @ExperimentalCoroutinesApi
    fun setUpUI() {

        val navController = findNavController()
        NavigationUI.setupWithNavController(activity.collapsing_toolbar, activity.toolbar, navController)
        container?.adapter = PageAdapter(childFragmentManager, selectedUser)
        tabs?.setupWithViewPager(container)
        container.offscreenPageLimit = tabs.tabCount
        float_filter_repo.setOnClickListener {
            //  if (it.isShown)
            //openPopUp()
        }
        container.addOnPageChangeListener(onPageListener)


    }


    private fun setupObserver() {
        userProfileViewModel.finalDataLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    updateProfile(it.data)

                    //showLoader(false)
                }
                is Resource.Loading -> {
                    //  showLoader(true)
                }
                is Resource.Error -> {
                    // showLoader(false)
                    Toast.makeText(activity, it.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    private fun updateProfile(userProfile: UserProfile) {

        val name = userProfile.name
        profile?.let {
            it.setTag(R.id.profile, name)
            it.loadingA(activity.requestManager, userProfile.avatarUrl, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    val bitmap = (resource as BitmapDrawable).bitmap
                    androidx.palette.graphics.Palette.from(bitmap).generate { p ->
                        run {
                            it.setOnClickListener { _ ->
                                val c = p!!.getVibrantColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                                DialogUtils.showImageDialog(activity, it.getTag(R.id.profile)?.toString(), userProfile.avatarUrl, c)
                            }
                        }


                    }

                    return false
                }
            })

        }


        btn_edit.setOnClickListener {
            NavUtils.redirectToEditProfile(activity, userProfile)
        }
        // enableHomeInToolBar(name, true)
        //  txt_login.startAnimation(fadeIn)
        txt_login.visibility = View.VISIBLE
        txt_login.text = userProfile.login

        if (userProfile.bio.isNullOrEmpty()) txt_bio?.visibility = View.GONE else {
            txt_bio.apply {
                // startAnimation(fadeIn)
                visibility = View.VISIBLE
                text = userProfile.bio
            }
        }
        if (userProfile.company.isNullOrEmpty()) txt_company.visibility = View.GONE else {
            txt_company?.apply {
                // startAnimation(fadeIn)
                visibility = View.VISIBLE
                text = userProfile.company
            }
        }
        if (userProfile.location.isNullOrEmpty()) txt_location.visibility = View.GONE else {
            txt_location?.apply {
                // startAnimation(fadeIn)
                visibility = View.VISIBLE
                text = userProfile.location
            }
        }
        if (userProfile.email.isNullOrEmpty()) txt_email.visibility = View.GONE else {
            txt_email?.apply {
                // startAnimation(fadeIn)
                visibility = View.VISIBLE
                //text = userProfile.email
                val span = Spannable.Factory.getInstance().newSpannable(userProfile.email)
                span.setSpan(object : ClickableSpan() {
                    override fun onClick(v: View) {
                        try {
                            activity.email(userProfile.email, "Send via GoGit Android", "")
                        } catch (e: Exception) {
                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        // ds.color = ContextCompat.getColor(img_actor.context, R.color.colorPrimaryDark)
                        ds.isUnderlineText = true
                        ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                }, 0, userProfile.email.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                text = span
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
        if (userProfile.blog.isNullOrEmpty()) txt_blog.visibility = View.GONE else {
            txt_blog?.apply {
                // // startAnimation(fadeIn)
                visibility = View.VISIBLE
                //text = userProfile.blog
                val span = Spannable.Factory.getInstance().newSpannable(userProfile.blog)
                span.setSpan(object : ClickableSpan() {
                    override fun onClick(v: View) {
                        try {
                            var up = userProfile.blog
                            if (!up.startsWith("http")) {
                                up = "http://".plus(up)
                            }
                            // browse(up)
                        } catch (e: Exception) {
                        }
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        // ds.color = ContextCompat.getColor(img_actor.context, R.color.colorPrimaryDark)
                        ds.isUnderlineText = true
                        ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                    }
                }, 0, userProfile.blog.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                text = span
                movementMethod = LinkMovementMethod.getInstance()
            }
        }
        container.post {
            if (container.currentItem == 0)
                onPageListener.onPageSelected(0)
        }
    }

    private val onPageListener = object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            when (position) {
                0 ->
                    showFab(true)
                else ->
                    showFab(false)
            }
        }
    }

    private fun showFab(isShown: Boolean) {
        if (isShown) float_filter_repo.show() else float_filter_repo.hide()
    }

    inner class PageAdapter(fm: androidx.fragment.app.FragmentManager, val selectedUser: String) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {
        var registeredFragments = SparseArray<Fragment>()
        override fun getItem(position: Int): Fragment {
            val bundle = bundleOf(Pair("id", selectedUser))
            when (position) {
                0 -> {
                    val fragment = RepoFragment()
                    bundle.putInt("index", 0)
                    fragment.arguments = bundle
                    return fragment
                }
                /* 1 -> {
                     val fragment = FollowersFragment()
                     bundle.putInt("index", 1)
                     fragment.arguments = bundle
                     return fragment
                 }
                 2 -> {
                     val fragment = FollowersFragment()
                     bundle.putInt("index", 2)
                     fragment.arguments = bundle
                     return fragment
                 }
                 3 -> {
                     val fragment = RepoFragment()
                     bundle.putInt("index", 3)
                     fragment.arguments = bundle
                     return fragment
                 }*/
                //4
                else -> {
                    val fragment = OrgFragment()
                    fragment.arguments = bundle
                    return fragment
                }


            }

        }


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = super.instantiateItem(container, position) as Fragment
            registeredFragments.put(position, fragment)
            return fragment
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
            registeredFragments.remove(position)

        }

        override fun getCount(): Int {
            return 1
        }

        override fun getItemPosition(int: Any): Int {
            return androidx.viewpager.widget.PagerAdapter.POSITION_NONE
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 ->
                    "Repositories"
                1 ->
                    "Followers"
                2 ->
                    "Followings"
                3 ->
                    "Stars"
                4 ->
                    "Organizations"
                else ->
                    null
            }
        }

    }

}



