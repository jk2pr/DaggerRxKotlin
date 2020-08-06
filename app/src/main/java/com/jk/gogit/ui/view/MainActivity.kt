package com.jk.gogit.ui.view

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.jk.gogit.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.Headers
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    @Inject
    lateinit var pref: SharedPreferences

    @Inject
    lateinit var mAuth: FirebaseAuth

    @Inject
    lateinit var requestManager: RequestManager

    companion object {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        const val VISIBLE_THRESHOLD = 1 //5
        var sMaxRecord = 50
        var isNotificationAvailable = false

    }

    private fun setUpToolbar() {
        if (toolbar != null) {
            val toolbar = toolbar as androidx.appcompat.widget.Toolbar
            toolbar.elevation = 0.0f
            toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_overflow_white)
            setSupportActionBar(toolbar)
        }
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

    fun enableHomeInToolBar(title: String?, isUpEnable: Boolean) {
        supportActionBar?.apply {
            setHomeButtonEnabled(isUpEnable)
            setDisplayHomeAsUpEnabled(isUpEnable)
            toolbar_title.text = title
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        my_nav_host_fragment.post {
            val navController = Navigation.findNavController(this, R.id.my_nav_host_fragment)
            val navGraph = navController.navInflater.inflate(R.navigation.nav)
            val startDestination = if (mAuth.currentUser == null) R.id.fragment_login else R.id.fragment_feed
            navGraph.startDestination = startDestination
            navController.graph = navGraph

            val appBarConfiguration = AppBarConfiguration(navController.graph)

            collapsing_toolbar.setupWithNavController(toolbar, navController, appBarConfiguration)
        }

        setUpToolbar()
        enableHomeInToolBar(resources.getString(R.string.app_name), false)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.my_nav_host_fragment).navigateUp()

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        /*   subscriptions.add(model.getNotifications(false, 0, sMaxRecord)
                   .subscribeOn(Schedulers.io())
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe({
                       isNotificationAvailable = it.body()!!.isEmpty() == false
                       super.onPrepareOptionsMenu(menu)
                   }, {
                       onError(it)
                       it.printStackTrace()
                   }))*/

        return true
    }

}
