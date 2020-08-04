package com.jk.gogit

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.google.firebase.auth.GithubAuthProvider
import com.jk.gogit.ui.view.BaseActivity
import com.jk.gogit.utils.NavUtils
import com.jk.gogit.utils.NavUtils.redirectToHome
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.Credentials
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug


class LoginActivity : BaseActivity(), AnkoLogger {
    override fun getLayoutResourceId(): Int {
        return R.layout.activity_login
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressbar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(this, android.R.color.white), PorterDuff.Mode.SRC_IN);
        btn_login.setOnClickListener {
            readInputDoLogin()

        }
        handlePrivacyPolicy()

        //btn_skip.paintFlags = btn_skip.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        btn_skip.setOnClickListener {
            onBackPressed()
        }

        input_password.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                readInputDoLogin()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
            true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showDialog() {
        NavUtils.redirectToPrivacyPolicy(this)
    }

    private fun handlePrivacyPolicy() {
        val text = txt_terms.text
        val span = Spannable.Factory.getInstance().newSpannable(text)
        span.setSpan(object : ClickableSpan() {
            override fun onClick(v: View) {
                // browse("https://jk2pr.github.io/")
                showDialog()
            }

            override fun updateDrawState(ds: TextPaint) {
                // ds.color = ContextCompat.getColor(img_actor.context, R.color.colorPrimaryDark)
                ds.isUnderlineText = true
                ds.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
        }, text.indexOf("Privacy Policy"/*""Terms and Condition"*/), text!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        txt_terms.text = span
        txt_terms.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun readInputDoLogin() {
        val userName = input_email.text.toString()
        val password = input_password.text.toString()
        if (validate(userName, password)) {
            showLoader(true)
            val token = Credentials.basic(userName, password)
            //doLogin(token)
        }
    }

    private fun validate(userName: String, password: String): Boolean {
        var valid = true

        if (userName.isEmpty()) {
            input_layout_username.error = "Enter a valid github user name"
            valid = false
        } else {
            input_layout_username.error = null
        }

        if (password.isEmpty()) {
            input_layout_password.error = "Password should not be empty"
            valid = false
        } else {
            input_layout_password.error = null
        }

        return valid
    }

    fun showLoader(isLoading: Boolean) {
        if (isLoading) {
            login_layout_group?.visibility = View.GONE
            progressbar?.visibility = View.VISIBLE

        } else {
            login_layout_group?.visibility = View.VISIBLE
            progressbar?.visibility = View.GONE
        }

    }

   /* private fun doLogin(token: String) {
        pref.edit().putString("initToken", token).apply()
        subscriptions.add(loginApi.authorizations(AuthRequestModel().generate())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { accessToken ->
                            signInWithToken(accessToken.body()!!.token)
                        }, { e ->
                    onError(e)
                    showLoader(false)
                }))
    }*/

    private fun signInWithToken(token: String) {
        val credential = GithubAuthProvider.getCredential(token)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    debug("signInWithCredential:onComplete:" + task.isSuccessful)
                    if (!task.isSuccessful) {
                        task.exception?.printStackTrace()
                        showLoader(false)
                    } else {
                        pref.edit().putString("AccessToken", token).apply()
                        val user = task.result?.user
                        redirectToHome(this, user)

                    }
                }
    }


}
