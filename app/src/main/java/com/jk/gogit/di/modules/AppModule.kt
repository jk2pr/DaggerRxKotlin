package com.jk.gogit.di.modules

import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.jk.gogit.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@Module
@InstallIn(ApplicationComponent::class)
class AppModule() {


    @Provides
    @Singleton
    fun providePreference(@ApplicationContext
                          application: Context): SharedPreferences {
        return application.getSharedPreferences(application.getString(R.string.app_name), Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideFirebaseAppAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAnswers(@ApplicationContext
                       application: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(application)
    }

    @Provides
    @Singleton
    fun provideGlide(
            @ApplicationContext
            application: Context
    ): RequestManager {
        return Glide.with(application)
                .setDefaultRequestOptions(RequestOptions())
    }
}