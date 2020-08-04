package com.jk.gogit.ui.login.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
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
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GithubAuthProvider
import com.jk.gogit.R
import com.jk.gogit.extensions.hide
import com.jk.gogit.extensions.show
import com.jk.gogit.ui.login.viewmodels.LoginViewModel
import com.jk.gogit.ui.login.data.response.Resource
import com.jk.gogit.ui.view.MainActivity
import com.jk.gogit.utils.NavUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.Credentials
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val loginViewModel: LoginViewModel by navGraphViewModels(R.id.nav) { defaultViewModelProviderFactory }
    private lateinit var activity: MainActivity


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpUI()
        setupObserver()
    }


    @ExperimentalCoroutinesApi
    fun setUpUI() {
        val colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(requireContext(), android.R.color.white), BlendModeCompat.SRC_IN)
        progressbar.indeterminateDrawable.colorFilter = colorFilter
        btn_login.setOnClickListener { readInputDoLogin() }
        handlePrivacyPolicy()
        btn_skip.setOnClickListener { activity.onBackPressed() }
        input_password.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                readInputDoLogin()
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
            }
            true
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showDialog() {
        NavUtils.redirectToPrivacyPolicy(requireActivity())
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

    @ExperimentalCoroutinesApi
    private fun readInputDoLogin() {
        val userName = input_email.text.toString()
        val password = input_password.text.toString()
        if (validate(userName, password)) {
            showLoader(true)
            val authCredential = Credentials.basic(userName, password)
            activity.pref.edit().putString("initToken", authCredential).apply()
            loginViewModel.setState(LoginViewModel.MainState.LoginEvent)


        }
    }


    private fun validate(userName: String, password: String): Boolean {
        var valid = true

        if (userName.isEmpty()) {
            input_layout_username.error = "Enter a valid github user name"
            valid = false
        } else
            input_layout_username.error = null

        if (password.isEmpty()) {
            input_layout_password.error = "Password should not be empty"
            valid = false
        } else
            input_layout_password.error = null


        return valid
    }

    fun showLoader(isLoading: Boolean) {
        if (isLoading) {
            login_layout_group?.hide()
            progressbar?.show()

        } else {
            login_layout_group?.show()
            progressbar?.hide()
        }

    }

    private fun setupObserver() {

        loginViewModel.finalDataLiveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    activity.pref.edit().putString("AccessToken", it.data.token).apply()
                    // signInWithToken(it.data.token)
                    findNavController().navigate(R.id.action_fragment_login_to_fragment_feed)
                    showLoader(false)
                }
                is Resource.Loading -> {
                    showLoader(true)
                }
                is Resource.Error -> {
                    showLoader(false)
                    Toast.makeText(activity, it.exception?.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        })

    }
}


