package com.jk.gogit.ui.view

import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.jk.gogit.R
import com.jk.gogit.SearchActivity
import com.jk.gogit.db.AppDatabase
import com.jk.gogit.exception.ApiRateLimitExceedException
import com.jk.gogit.exception.FileNotFoundException
import com.jk.gogit.exception.UserUnAuthorizedException
import com.jk.gogit.model.UserProfile
import com.jk.gogit.network.api.IApi
import com.jk.gogit.network.api.ILogin
import com.jk.gogit.ui.viewmodel.UserViewModel
import com.jk.gogit.utils.NavUtils.redirectToLogin
import com.jk.gogit.utils.NavUtils.redirectToNotification
import com.jk.gogit.utils.NavUtils.redirectToProfile
import com.jk.gogit.utils.NavUtils.redirectToSplash
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.Headers
import org.jetbrains.anko.find
import org.jetbrains.anko.intentFor
import java.net.UnknownHostException
import javax.inject.Inject
import javax.net.ssl.SSLHandshakeException


abstract class BaseActivity : AppCompatActivity() {
    @Inject
    lateinit var api: IApi
    @Inject
    lateinit var loginApi: ILogin
    @Inject
    protected lateinit var mAuth: FirebaseAuth
    @Inject
    protected lateinit var pref: SharedPreferences
    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var analytics: FirebaseAnalytics

    @Inject
    lateinit var firebaseAnalytics
            : FirebaseAnalytics
    private val snack by lazy { Snackbar.make(find(android.R.id.content), "There is no data that match!", Snackbar.LENGTH_LONG) }
    val model: UserViewModel by lazy { ViewModelProvider(this).get(UserViewModel::class.java) }
    var subscriptions = CompositeDisposable()

    companion object {

        private var sUser: UserProfile? = null
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        const val VISIBLE_THRESHOLD = 1 //5
        var sMaxRecord = 50
        var isNotificationAvailable = false

    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {

        if (mAuth.currentUser != null) {

            val menuItem = menu?.findItem(R.id.action_notification)
            val item = menuItem?.actionView?.find<ImageView>(R.id.img_notification)
            if (!isNotificationAvailable)
                item?.visibility = View.GONE
            else
                item?.visibility = View.VISIBLE
            menuItem?.isVisible = true
        } else {
            menu?.findItem(R.id.action_logout)?.title = getString(R.string.sign_in)
            menu?.findItem(R.id.action_your_profile)?.isVisible = false
            menu?.findItem(R.id.action_notification)?.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(getLayoutResourceId())
        setUpToolbar()
        //   setUpSearchBar()
    }

    fun getLoginData(): UserProfile? {
        if (sUser == null)
            sUser = Gson().fromJson(pref.getString("UserData", null), UserProfile::class.java)
        return sUser
    }

    fun save(user: UserProfile) {
        sUser = null
        pref.edit().putString("UserData", Gson().toJson(user)).apply()

    }


    abstract fun getLayoutResourceId(): Int


    private fun setUpToolbar() {
        if (toolbar != null) {
            val toolbar = toolbar as androidx.appcompat.widget.Toolbar
            toolbar.elevation = 0.0f
            //findOptional<AppBarLayout>(R.id.app_bar_layout)?.setPadding(0, getStatusBarHeight(), 0, 0)
            //toolbar.visibility = View.VISIBLE
            toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_overflow_white)
            //toolbar_title.text = getText(R.string.app_name)
            setSupportActionBar(toolbar)
        }
    }


    fun enableHomeInToolBar(title: String?, isUpEnable: Boolean) {
        supportActionBar?.apply {
            setHomeButtonEnabled(isUpEnable)
            setDisplayHomeAsUpEnabled(isUpEnable)
            toolbar_title.text = title
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)

        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val item = menu.findItem(R.id.action_notification)
        item.actionView?.setOnClickListener {
            redirectToNotification(this)

        }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                //   supportFinishAfterTransition()
                onBackPressed()
                true
            }
            R.id.action_search -> {
                //onSearchRequested()
                startActivity(intentFor<SearchActivity>())
                true
            }
            R.id.action_logout -> {
                if (item.title.contains(getString(R.string.logout))) {
                    doLogout()
                    redirectToSplash(this)
                } else {
                    redirectToLogin(this)
                }
                true
            }
            R.id.action_star -> {
                if (item.isChecked) {
                    item.isChecked = false
                    item.setIcon(R.drawable.ic_star_border_white)
                } else {
                    item.isChecked = true
                    item.setIcon(R.drawable.ic_star_white_fill)
                }
                true
            }
            R.id.action_watch -> {
                if (item.isEnabled) {

                }
                if (item.isChecked) {
                    item.isChecked = false
                    item.setIcon(R.drawable.ic_watching_off)
                } else {
                    item.isChecked = true
                    item.setIcon(R.drawable.ic_watching_on)
                }
                true
            }
            R.id.action_your_profile -> {
                if (item.isVisible)
                    redirectToProfile(this, getLoginData()?.login)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun isProfileSameAsLogin(selectedUser: String): Boolean {
        if (getLoginData()?.login.isNullOrEmpty())
            return false
        return selectedUser.contentEquals(getLoginData()?.login as String)
                || selectedUser.contentEquals("N/A")
    }


    fun handlePagination(headers: Headers?): Int {

        try {
            if (headers == null)
                return 1
            val list = headers.get("Link")?.split(",")
            val map = list?.associateBy(
                    {
                        it.split(";")[1].split("=")[1].replace("\"", "")

                    }, {
                it.split(";")[0]
            })
            val url = map?.get("last")?.trim()?.removeSurrounding("<", ">")
            //return url?.substring(url.lastIndexOf("=") + 1, url.length - 1)?.toInt() ?: 0

            url?.let {
                val uri = Uri.parse(it)
                return uri.getQueryParameter("page")!!.toInt()
            }
            return 1

        } catch (e: Exception) {
            return 1
        }
    }

    private val dialog by lazy {
        AlertDialog.Builder(this).apply {
            setCancelable(false)
            setTitle(getString(R.string.error))
            setPositiveButton("Go back") { _, _ -> onBackPressed() }
        }
    }

    fun onError(throwable: Throwable?) {
        when (throwable) {
            is UnknownHostException -> {
                //  dialog.apply {
                showSnackBar("You are not connected to internet")
                // (!create().isShowing).let {
                //      show()


            }
            is ApiRateLimitExceedException ->
                // dialog.apply {
                showSnackBar("Api Rate Limit Exceed for this user, Please retry")
            //    (!create().isShowing).let {
            //         show()


            is java.net.SocketTimeoutException,
            is SSLHandshakeException,
            is java.net.ConnectException ->
                //dialog.apply {
                showSnackBar("Internet is not accessible")
            //   (!create().isShowing).let {
            //         show()
            //    }
            // }


            is FileNotFoundException ->
                // dialog.apply {
                showSnackBar("Content not available")
            //    (!create().isShowing).let {
            //      show()
            //  }


            null ->
                //dialog.apply {
                showSnackBar("This type of file is not supported. Please download this from top menu")
            //  setNegativeButton("Manual Download") { _, _ ->
            //     (this@BaseActivity as? FullFileActivity)?.checkPermissionAndDownload()
            //  }
            // (!create().isShowing).let {
            //     show()
            // }


            is UserUnAuthorizedException -> {
                if (throwable.message.contains(resources.getString(R.string.invalid_credential))) {

                    showSnackBar(resources.getString(R.string.invalid_credential))
                    return
                }
                doLogout()
                dialog.apply {
                    setMessage("Seems U r now logged out")
                    setPositiveButton("Go back and re-login") { dialog, _ ->
                        redirectToSplash(this@BaseActivity)
                        dialog.dismiss()
                    }
                    setNegativeButton("Close and Exit") { _, _ ->
                        finishAffinity()
                        System.exit(0)
                    }
                    (!create().isShowing).run {
                        show()
                    }
                }
            }
        }
    }

    private fun showSnackBar(message: String) {

        snack.apply {
            val view = view as FrameLayout
            /* val params = view.getChildAt(0).layoutParams as FrameLayout.LayoutParams
             params.height = resources.getDimensionPixelSize(R.dimen.dp55)
             view.layoutParams = params*/
            view.minimumHeight = resources.getDimensionPixelSize(R.dimen.dp55)


            /* setAction("Dismiss", {
                 //snack.dismiss()
                 retrySubject?.onNext(2)
             })*/
            setActionTextColor(Color.GREEN)
            setText(message)
            if (!isShown)
                show()
        }.view.apply {
            setBackgroundColor(ContextCompat.getColor(this@BaseActivity, R.color.drkred))
            find<TextView>(R.id.snackbar_text).apply {
                setTextColor(Color.WHITE)
                //typeface= Typeface.BOLD_ITALIC
            }
        }
    }

    private fun doLogout() {
        mAuth.signOut()
        sUser = null
        pref.edit().clear().apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
        subscriptions.dispose()
        supportFinishAfterTransition()
    }

}
