package com.jk.gogit.ui.view

import android.app.Activity
import android.app.Dialog
import android.content.Intent
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
import android.view.Menu
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.AppBarLayout
import com.google.gson.Gson
import com.jk.gogit.EditProfileActivity
import com.jk.gogit.R
import com.jk.gogit.R.id.*
import com.jk.gogit.callbacks.OnFilterSelectedListener
import com.jk.gogit.exception.FollowerException
import com.jk.gogit.exception.NotFollowerException
import com.jk.gogit.extensions.loadingA
import com.jk.gogit.model.Repo
import com.jk.gogit.model.UserProfile
import com.jk.gogit.ui.view.fragments.FollowersFragment
import com.jk.gogit.ui.view.fragments.OrgFragment
import com.jk.gogit.ui.view.fragments.RepoFragment
import com.jk.gogit.utils.DialogUtils
import com.jk.gogit.utils.NavUtils
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.include_profile_header.*
import org.jetbrains.anko.*
import java.util.*
import javax.inject.Inject

class UserProfileActivity : BaseActivity(), AnkoLogger, AppBarLayout.OnOffsetChangedListener, Observer<UserProfile> {
    override fun onChanged(t: UserProfile?) {
        updateProfile(t!!)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        swipeRefresh.isEnabled = verticalOffset == 0
    }

    var data: MutableMap<UserProfile, List<Repo>> = mutableMapOf()
    var selectedUser: String = "N/A"
    var navHistory = Stack<String>()

    @Inject
    lateinit var requestManager: RequestManager
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_user_profile
    }

    private val dialog by lazy {
        val dialogLayout = View.inflate(this@UserProfileActivity, R.layout.filter_repo, null)
        AlertDialog.Builder(this).apply {
            setView(dialogLayout)
        }.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHomeInToolBar(null, true)
        selectedUser = intent.getStringExtra("id")
        container?.adapter = PageAdapter(supportFragmentManager)
        tabs?.setupWithViewPager(container)
        container.offscreenPageLimit = tabs.tabCount
        float_filter_repo.setOnClickListener {
            if (it.isShown)
                openPopUp()
        }
        container.addOnPageChangeListener(onPageListener)
        model.liveData.observe(this, this)

        btn_follow?.setOnClickListener { v ->
            run {
                val ob: Completable = if ((v as Button).text.toString().contentEquals(StringBuffer(getString(R.string.follow)))) {
                    model.followUser(selectedUser)
                } else {
                    model.unFollowUser(selectedUser)
                }
                subscriptions.add(ob.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) .subscribe(
                        {

                        },
                        {
                            handleFollowingStuffs(it, true)
                        })
                )
            }
        }


        btn_follow?.visibility = GONE
        if (isProfileSameAsLogin(selectedUser)) {
            /*it means it is login user*/
            selectedUser = "N/A"
        } else

        updateUser()
        navHistory.push(selectedUser)

        swipeRefresh?.setColorSchemeColors(ContextCompat.getColor(this, android.R.color.holo_blue_bright),
                ContextCompat.getColor(this, android.R.color.holo_green_light),
                ContextCompat.getColor(this, android.R.color.holo_orange_light),
                ContextCompat.getColor(this, android.R.color.holo_red_light))

        swipeRefresh?.setOnRefreshListener {
            updateUser()
        }
        txt_internet.setOnClickListener {
            updateUser()
        }



    }


    private fun openPopUp() {
        if (dialog.isShowing)
            return
        dialog.show()
        val privateRadio = dialog.find<RadioButton>(radio_private)
        val publicRadio = dialog.find<RadioButton>(radio_public)
        if (isProfileSameAsLogin(selectedUser)) {
            privateRadio.visibility = View.VISIBLE
            publicRadio.visibility = View.VISIBLE
        } else {
            privateRadio.visibility = GONE
            publicRadio.visibility = GONE
        }
        val radioGroup = dialog.find<RadioGroup>(R.id.radio_1)
        for (i in 0 until radioGroup.childCount) {
            val item = (radioGroup.getChildAt(i) as RadioButton)
            if (item.text.toString().contentEquals(getSelectedFilterType().toString())) {
                item.isChecked = true
            }
            item.setOnCheckedChangeListener { buttonView, isChecked ->
                onRadioSelected(dialog, buttonView, isChecked)
            }
        }
    }

    fun getSelectedFilterType(): StringBuffer? {
        val radioGroup = dialog.findOptional<RadioGroup>(R.id.radio_1)
        if (radioGroup != null)
            for (i in 0 until radioGroup.childCount) {
                val item = radioGroup.getChildAt(i) as RadioButton
                if (item.isChecked) {
                    return StringBuffer(item.text.toString().toLowerCase().trim())
                }
            }
        return StringBuffer("all")
    }

    private fun onRadioSelected(d: Dialog, item: View, isCheck: Boolean) {

        val a = (container.adapter as PageAdapter).registeredFragments[container.currentItem]
        if (a is OnFilterSelectedListener)
            if (isCheck) {
                when (item.id) {
                    radio_all ->
                        a.onFilterSelected("all")
                    radio_public ->
                        a.onFilterSelected("public")
                    radio_private ->
                        a.onFilterSelected("private")
                    radio_owner ->
                        a.onFilterSelected("owner")
                    radio_member ->
                        a.onFilterSelected("member")
                    else -> {
                    }
                }
                item.postDelayed({ d.cancel() }, 50)
            }
    }


    fun showFab(isShown: Boolean) {
        if (isShown) float_filter_repo.show() else float_filter_repo.hide()
    }

    private fun removeAnimationIfAny() {
        txt_login.clearAnimation()
        txt_bio.clearAnimation()
        txt_company.clearAnimation()
        txt_location.clearAnimation()
        txt_email.clearAnimation()
        txt_blog.clearAnimation()
        btn_follow?.clearAnimation()
    }

    private fun createAnimation(): Animation {
        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = 200
        fadeIn.fillAfter = true
        //fadeIn.repeatCount = 1
        return fadeIn
    }

    private fun updateProfile(userProfile: UserProfile) {

        removeAnimationIfAny()
        val fadeIn = createAnimation()
        val name = userProfile.name
        profile?.let {
            it.setTag(R.id.profile, name)
            it.loadingA(requestManager, userProfile.avatarUrl, object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    val bitmap = (resource as BitmapDrawable).bitmap
                    androidx.palette.graphics.Palette.from(bitmap).generate { p ->
                        run {
                            it.setOnClickListener { _ ->
                                val c = p!!.getVibrantColor(ContextCompat.getColor(this@UserProfileActivity, R.color.colorPrimary))
                                DialogUtils.showImageDialog(this@UserProfileActivity, it.getTag(R.id.profile)?.toString(), userProfile.avatarUrl, c)
                            }
                        }


                    }

                    return false
                }
            })

        }


        btn_edit.setOnClickListener {
            NavUtils.redirectToEditProfile(this, userProfile)
        }
        enableHomeInToolBar(name, true)
        txt_login.startAnimation(fadeIn)
        txt_login.visibility = View.VISIBLE
        txt_login.text = userProfile.login

        if (userProfile.bio.isNullOrEmpty()) txt_bio?.visibility = GONE else {
            txt_bio.apply {
                startAnimation(fadeIn)
                visibility = View.VISIBLE
                text = userProfile.bio
            }
        }
        if (userProfile.company.isNullOrEmpty()) txt_company.visibility = GONE else {
            txt_company?.apply {
                startAnimation(fadeIn)
                visibility = View.VISIBLE
                text = userProfile.company
            }
        }
        if (userProfile.location.isNullOrEmpty()) txt_location.visibility = GONE else {
            txt_location?.apply {
                startAnimation(fadeIn)
                visibility = View.VISIBLE
                text = userProfile.location
            }
        }
        if (userProfile.email.isNullOrEmpty()) txt_email.visibility = GONE else {
            txt_email?.apply {
                startAnimation(fadeIn)
                visibility = View.VISIBLE
                //text = userProfile.email
                val span = Spannable.Factory.getInstance().newSpannable(userProfile.email)
                span.setSpan(object : ClickableSpan() {
                    override fun onClick(v: View) {
                        try {
                            email(userProfile.email, "Send via GoGit Android", "")
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
        if (userProfile.blog.isNullOrEmpty()) txt_blog.visibility = GONE else {
            txt_blog?.apply {
                startAnimation(fadeIn)
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
                            browse(up)
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
            // if (snack.isShown)
            //   snack.dismiss()
            when (position) {
                0 ->
                    showFab(true)
                else ->
                    showFab(false)
            }
        }
    }

    private fun updateUser() {
        invalidateOptionsMenu()
        resetDialog()
     /*   val u = model.getUserProfile(selectedUser).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) .subscribe({
            model.liveData.value = it
            // updateProfile(it)

            swipeRefresh.isRefreshing = false
        }, { it ->
            onError(it)
            swipeRefresh.isRefreshing = false
        }, {

        })
        subscriptions.add(u)*/

        if (getLoginData()?.login.isNullOrEmpty()) {
            //Not a logged in user
        } else {
            //logged in user
            if (!isProfileSameAsLogin(selectedUser)) {
                val f = model.ifIamFollowing(selectedUser).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()) .subscribe({},
                        {
                            handleFollowingStuffs(it, false)
                        })
                subscriptions.add(f)
            } //else
            //  btn_edit.visibility = View.VISIBLE
        }


        if (swipeRefresh.isRefreshing)
            container.adapter?.notifyDataSetChanged()
    }

    private fun resetDialog() {
        val radioGroup = dialog.findOptional<RadioGroup>(R.id.radio_1)
        radioGroup?.let {
            (it.getChildAt(0) as RadioButton).isChecked = true
        }
    }

    private fun handleFollowingStuffs(it: Throwable, isWriting: Boolean) {
        removeAnimationIfAny()
        btn_follow.animation = createAnimation()
        if (isProfileSameAsLogin(selectedUser)) {
            btn_follow.visibility = GONE
        } else {
            btn_follow?.visibility = View.VISIBLE
            when (it) {
                is FollowerException -> {
                    btn_follow.text = getString(R.string.unfollow)
                    if (isWriting) toast(getString(R.string.now_u_r_follower))
                }
                is NotFollowerException -> {
                    btn_follow.text = getString(R.string.follow)
                    if (isWriting) toast(getString(R.string.now_u_r_not_follower))
                }
                else -> {
                    onError(it)
                    btn_follow.visibility = GONE

                }
            }
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_your_profile)?.isVisible = !isProfileSameAsLogin(selectedUser)
        return super.onPrepareOptionsMenu(menu)
    }

    inner class PageAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {
        var registeredFragments = SparseArray<androidx.fragment.app.Fragment>()
        override fun getItem(position: Int): androidx.fragment.app.Fragment {
            val bundle = Bundle()
            bundle.putString("id", selectedUser)
            when (position) {
                0 -> {
                    val fragment = RepoFragment()
                    bundle.putInt("index", 0)
                    fragment.arguments = bundle
                    return fragment
                }
                1 -> {
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
                }
                //4
                else -> {
                    val fragment = OrgFragment()
                    fragment.arguments = bundle
                    return fragment
                }


            }

        }


        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = super.instantiateItem(container, position) as androidx.fragment.app.Fragment
            registeredFragments.put(position, fragment)
            return fragment
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            super.destroyItem(container, position, `object`)
            registeredFragments.remove(position)

        }

        override fun getCount(): Int {
            return 5
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

    override fun onResume() {
        super.onResume()
        appbar?.addOnOffsetChangedListener(this)
    }

    override fun onPause() {
        super.onPause()
        appbar?.removeOnOffsetChangedListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                if (!swipeRefresh.isRefreshing)
                    swipeRefresh.isRefreshing = true
                val eData = Gson().fromJson(data.getStringExtra("EditData"), EditProfileActivity.EditData::class.java)
                val u = model.saveProfile(/*name, email, blog, company, location, hireable, bio*/ eData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ pf ->
                            model.liveData.value = pf
                            print(it.toString())
                        }, { er ->
                            onError(er)
                            if (swipeRefresh.isRefreshing)
                                swipeRefresh.isRefreshing = false
                        }, {
                            if (swipeRefresh.isRefreshing)
                                swipeRefresh.isRefreshing = false
                        })
                subscriptions.add(u)

            }
        }
    }
}