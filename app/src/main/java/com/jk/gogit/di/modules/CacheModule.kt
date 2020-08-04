package com.jk.gogit.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import java.io.File
import javax.inject.Singleton

/**
 * Created by Jitendra on 08/11/2017.
 */

@Module
@InstallIn(ApplicationComponent::class)
class CacheModule() {
    @Provides
    @Singleton
    fun getCacheDir(@ApplicationContext
                    application: Context): Cache {
        val httpCacheDirectory = File(application.cacheDir, "responses")
        return Cache(httpCacheDirectory, 10 * 1024 * 1024)


    }
}
