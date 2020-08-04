package com.jk.gogit.ui.view

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.jk.gogit.R
import dagger.hilt.android.AndroidEntryPoint
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import javax.inject.Inject


@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_splash)

    }
    private val splashDelay: Long = 3000 //3 seconds
    private val mDelayHandler = Handler()

    @Inject
    lateinit var mAuth: FirebaseAuth

    private val mRunnable = Runnable {
        if (!isFinishing) {
            startActivity(intentFor<MainActivity>().clearTop().clearTask().newTask())
           /* when (*//*intent.dataString != null -> {
                        //Handle
                        val dataString = intent?.dataString?.removeSuffix("/")?.toLowerCase(Locale.getDefault())
                        val baseUrl = arrayOf(
                                "github.com/",
                                "http://github.com/",
                                "https://github.com/",
                                "http://github.com",
                                "https://github.com")
                        if (baseUrl.contains(dataString)) {
                            // url is not have any relative url
                            redirectToNoLoginMainActivity(this)
                        } else
                            if (dataString!=null) {
                                if (Utils.isUserUrl(dataString)) {
                                    //Redirect to USer    \
                                    val uName = Utils.getUserFromUrl(dataString)
                                    if (!uName.isNullOrBlank())
                                        redirectToProfile(this, uName)
                                } else if (Utils.isRepoUrl(dataString)) {
                                    //Redirect to Repo
                                    val repo = Utils.getRepoFullNameFromUrl(dataString)
                                    if (!repo.isNullOrBlank())
                                        redirectToRepoDetails(this, repo)
                                } else if (Utils.isIssueUrl(dataString)) {
                                    val dataArray = Utils.getIssuePath(dataString)
                                    redirectToIssueDetails(this, dataArray[0], dataArray[1], dataArray[2].toInt())
                                } else {
                                    analytics.logEvent(dataString, null)
                                    toast("url is Invalid, can,t open now")
                                }
                            }
                        finish()
                    }*//*
                mAuth.currentUser) {
                null -> NavUtils.redirectToLogin()
                else -> NavUtils.red().navigate(R.id.fragment_main)
            }*/

        }
    }



    override fun onResume() {
        super.onResume()
        mDelayHandler.postDelayed(mRunnable, splashDelay)
    }

    override fun onPause() {
        super.onPause()
        mDelayHandler.removeCallbacks(mRunnable)
    }

}
