package com.jk.gogit

import android.content.ContentValues
import android.database.Cursor
import android.os.Bundle
import android.util.SparseArray
import android.view.Menu
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.jk.gogit.db.suggestions.DBCONSTANTS
import com.jk.gogit.ui.adapters.SearchSuggestionAdapter
import com.jk.gogit.ui.view.BaseActivity
import com.jk.gogit.ui.view.fragments.SearchRepoFragment
import com.jk.gogit.ui.view.fragments.SearchUserFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.toast
import java.util.concurrent.TimeUnit


/**
 * @author Jitendra Prajapati (jk2praj@gmail.com) on 12/03/2018 (MM/DD/YYYY )
 */

class SearchActivity : BaseActivity() {

    private var cursor: Cursor? = null
    private var searchView: SearchView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSearchView()
    }


    private lateinit var publishSubject: PublishSubject<String>
    private fun initSearchView() {

        enableHomeInToolBar(null, true)
        container?.adapter = MyPageAdapter(supportFragmentManager)
        tabs?.setupWithViewPager(container)

    }

    private fun doSearch(p0: String) {
        val pageAdapter = (container?.adapter as? MyPageAdapter)
        if (pageAdapter != null)
            if (pageAdapter.registeredFragments.size() != 0) {
                pageAdapter.apply {
                    val searchU = pageAdapter.registeredFragments[0] as? SearchUserFragment
                    searchU?.let {
                        (searchU.isAdded).let { _ ->
                            searchU.initSearchView(p0)
                        }
                    }
                    val searchRepo = pageAdapter.registeredFragments[1] as? SearchRepoFragment
                    searchRepo?.let {
                        (searchRepo.isAdded).let { _ ->
                            searchRepo.initSearchView(p0)
                        }
                    }
                }

                val contentValue = ContentValues()
                contentValue.put(DBCONSTANTS.COLUMN_NAME, p0)
                val uri = contentResolver.insert(SearchSuggestionsProvider.uri, contentValue)

                print(uri)

            } else
                toast("Sparse array is empty")

    }


    //  private val snackbar by lazy { Snackbar.make(container, "", Snackbar.LENGTH_SHORT) }

    /*   fun showSearchResultCount(count: String) {
           snackbar.setText("$count  Search results found")
           if (!snackbar.isShown)
               snackbar.show()
       }*/

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_search
    }

    override fun onStop() {
        super.onStop()
        cursor?.close()

    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        searchView?.apply {
            isIconified = false
            maxWidth = Integer.MAX_VALUE
            setOnCloseListener {
                onBackPressed()
                true
            }
            setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return true
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    val cursor = searchView?.suggestionsAdapter?.getItem(position) as Cursor
                    val q = cursor.getString(1)
                    searchView?.setQuery(q, true)
                    return true
                }

            })
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null)
                        if (query.length > 3)
                            doSearch(query.toString())

                    return true
                }


                override fun onQueryTextChange(p0: String?): Boolean {
                    if (p0 != null)
                        if (p0.length > 3) {
                            if (!::publishSubject.isInitialized) {
                                publishSubject = PublishSubject.create<String>()
                                subscriptions.add(publishSubject
                                        .debounce(300, TimeUnit.MILLISECONDS)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .distinctUntilChanged()
                                        .subscribe {
                                            val projection = arrayOf(DBCONSTANTS.COLUMN_ID, DBCONSTANTS.COLUMN_NAME)
                                            cursor = contentResolver.query(SearchSuggestionsProvider.uri,
                                                    projection,
                                                    "%$p0%",
                                                    null,
                                                    null)
                                            cursor?.let {
                                                val from = arrayOf(DBCONSTANTS.COLUMN_NAME)
                                                val to = intArrayOf(R.id.txt_s_name)
                                                val cursorAdapter = SearchSuggestionAdapter(this@SearchActivity, R.layout.item_suggestion, cursor, from, to, androidx.cursoradapter.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
                                                searchView?.suggestionsAdapter = cursorAdapter
                                            }
                                        })
                            }
                            publishSubject.onNext(p0.toString())
                        }
                    return true
                }

            })
        }
        val count = menu?.size()
        for (i in 0 until (count!!)) {
            menu.getItem(i).isVisible = menu.getItem(i).itemId == R.id.action_search_bar
        }
        searchView = menu.findItem(R.id.action_search_bar).actionView as SearchView

        searchView?.apply {
            isIconified = false
            maxWidth = Integer.MAX_VALUE
            setOnCloseListener {
                onBackPressed()
                true
            }
            setOnSuggestionListener(object : SearchView.OnSuggestionListener {
                override fun onSuggestionSelect(position: Int): Boolean {
                    return true
                }

                override fun onSuggestionClick(position: Int): Boolean {
                    cursor = searchView?.suggestionsAdapter?.getItem(position) as Cursor
                    val q = cursor?.getString(1)
                    searchView?.setQuery(q?.trim(), true)
                    return true
                }

            })
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null)
                        if (query.trim().length > 3)
                            doSearch(query.toString())

                    return true
                }


                override fun onQueryTextChange(p0: String?): Boolean {
                    if (p0 != null)
                        if (p0.trim().length > 3) {
                            if (!::publishSubject.isInitialized) {
                                publishSubject = PublishSubject.create<String>()
                                subscriptions.add(publishSubject
                                        .debounce(300, TimeUnit.MILLISECONDS)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .distinctUntilChanged()
                                        .subscribe {
                                            val projection = arrayOf(DBCONSTANTS.COLUMN_ID, DBCONSTANTS.COLUMN_NAME)
                                            cursor = contentResolver.query(SearchSuggestionsProvider.uri,
                                                    projection,
                                                    "%${it.trim()}%",
                                                    null,
                                                    null)
                                            cursor?.let { crs ->
                                                val from = arrayOf(DBCONSTANTS.COLUMN_NAME)
                                                val to = intArrayOf(R.id.txt_s_name)
                                                val cursorAdapter = SearchSuggestionAdapter(this@SearchActivity,
                                                        R.layout.item_suggestion, crs, from, to, androidx.cursoradapter.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
                                                searchView?.suggestionsAdapter = cursorAdapter
                                            }
                                        })
                            }
                            publishSubject.onNext(p0.toString())
                        } else
                            searchView?.suggestionsAdapter = null
                    return true
                }

            })
        }


        return true

    }

    inner class MyPageAdapter(fm: androidx.fragment.app.FragmentManager) : androidx.fragment.app.FragmentStatePagerAdapter(fm) {
        var registeredFragments = SparseArray<androidx.fragment.app.Fragment?>()
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 ->
                    SearchUserFragment()

                else ->
                    SearchRepoFragment()
            }
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fragment = super.instantiateItem(container, position) as androidx.fragment.app.Fragment
            registeredFragments.put(position, fragment)
            return fragment
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            super.destroyItem(container, position, obj)
            registeredFragments.remove(position)

        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 ->
                    "Users"
                1 ->
                    "Repositories"
                else ->
                    null
            }
        }
    }
}
