package com.jk.gogit

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.jk.gogit.db.suggestions.DBCONSTANTS
import com.jk.gogit.db.suggestions.Suggestion
import com.jk.gogit.db.suggestions.SuggestionDB
import com.jk.gogit.db.suggestions.SuggestionDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchSuggestionsProvider : ContentProvider() {


    @Inject
    lateinit var suggestionDB: SuggestionDB
    private val suggestionDao: SuggestionDao by lazy {
        suggestionDB.suggestionDao()
    }
    private val matcher: UriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init {

        matcher.addURI(AUTHORITY, DBCONSTANTS.TABLE_NAME, CODE_SG)
    }

    companion object {
        private const val AUTHORITY = DBCONSTANTS.AUTHORITY
        val uri: Uri = Uri.parse("content://" + AUTHORITY + "/" + DBCONSTANTS.TABLE_NAME)
        const val CODE_SG = 1
    }


    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val code = matcher.match(uri)
        val result: Long
        when (code) {
            CODE_SG -> {
                result = runBlocking {
                    withContext(Dispatchers.Default) {
                        val v = values?.keySet()?.iterator()?.next()
                        val suggestion = Suggestion(0, values?.get(v) as String)
                        suggestionDao.insert(suggestion)
                    }
                }
            }
            else ->
                return null


        }
        return ContentUris.withAppendedId(uri, result)
    }

    override fun onCreate(): Boolean {
        // MyApplication.appComponent.inject(this)
        return true
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        val code = matcher.match(uri)
        var result: Cursor? = null
        if (code == CODE_SG) {
            runBlocking {

                result = withContext(Dispatchers.Default) {
                    if (selection == null)
                        suggestionDao.getAllSuggestion()
                    else
                        suggestionDao.getSuggestionList(selection)
                }
            }
        }
        return result
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        return 0
    }
}
