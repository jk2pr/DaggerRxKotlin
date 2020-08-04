package com.jk.gogit.di.modules

import android.content.Context
import androidx.room.Room
import com.jk.gogit.db.AppDatabase
import com.jk.gogit.db.suggestions.DBCONSTANTS
import com.jk.gogit.db.suggestions.SuggestionDB
import com.jk.gogit.db.suggestions.SuggestionDB.Companion.MIGRATION1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

/**
 * Created by Jitendra on 08/11/2017.
 */

@Module
@InstallIn(ApplicationComponent::class)
class DBModule() {
    @Provides
    @Singleton
    fun getAppDB(@ApplicationContext
                 application: Context): AppDatabase {
        return Room.databaseBuilder(application,
                AppDatabase::class.java, "database-name").build()

    }

    @Provides
    @Singleton
    fun getSuggestionDB(@ApplicationContext
                        application: Context): SuggestionDB {
        return Room.databaseBuilder(application,
                SuggestionDB::class.java, DBCONSTANTS.DB_NAME).addMigrations(MIGRATION1_2).build()

    }
}