package com.jk.gogit.utils

import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.jk.gogit.*
import com.jk.gogit.model.UserProfile
import com.jk.gogit.ui.view.*
import org.jetbrains.anko.*

object NavUtils {

    fun redirectToLogin(context: Activity) {
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        context.startActivity(context.intentFor<LoginActivity>())
    }

    fun redirectToHome(context: Activity, user: FirebaseUser?) {
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        if (user != null) {
            context.startActivity(context.intentFor<MainActivity>().clearTop().clearTask().newTask())
        }

    }

    fun redirectToIssueDetails(context: Activity, owner: String?, repo: String?, issue: Int) {

        context.startActivity(context.intentFor<IssueDetailActivity>
        (("owner" to owner),
                ("repoName" to repo),
                ("issueNumber" to issue)))
    }


   /* fun redirectToNoLoginMainActivity(context: Activity) {
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        context.startActivity(context.intentFor<NoLoginMainActivity>())
        context.finish()
    }*/

    fun redirectToProfile(context: Activity, id: String?) {
        // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        context.startActivity(context.intentFor<UserProfileActivity>(("id" to id)))

    }

    fun redirectToEditProfile(context: Activity, userProfile: UserProfile?) {
        // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        context.startActivityForResult(context.intentFor<EditProfileActivity>(("UserData" to Gson().toJson(userProfile))), 0)

    }

    fun redirectToRepoDetails(context: Activity, owner: String?) {
        // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        context.startActivity(context.intentFor<RepoDetailsActivity>(("repoOwner" to owner)))
    }

    fun redirectToOrganisation(context: Activity, id: String?) {
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        context.startActivity(context.intentFor<OrgProfileActivity>(("id" to id)))
    }

    fun redirectToAboutRepo(context: Activity, repoDetail: String) {
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        context.startActivity(context.intentFor<AboutRepoActivity>(("repoDetail" to repoDetail)))
    }


    fun redirectToSplash(context: Activity) {
        // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        context.startActivity(context.intentFor<SplashActivity>().clearTop().clearTask().newTask().noHistory())
        context.finishAndRemoveTask()
    }

    fun redirectToNotification(context: Activity) {
        // overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        context.startActivity(context.intentFor<NotificationActivity>())
    }

    fun redirectToPrivacyPolicy(context: Activity) {
        context.startActivity(context.intentFor<PpActivity>())
    }

}