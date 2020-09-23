package com.example.mybook

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.mybook.adapter.BookAdapter
import com.example.mybook.extensions.replaceFragment
import com.example.mybook.retrofit.NaverApi
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val api = NaverApi.createRetrofit()
    private var query = ""
    private var start = 1
    private var total = 0
    private val compositeDisposable = CompositeDisposable()
    private val bookAdapter: BookAdapter by lazy {
        BookAdapter(mutableListOf()).apply {
            onItemClick = { _, position ->
                val nextFragment = BookDetailFragment()
                val bundle = Bundle()
                bundle.putParcelable(BookDetailFragment.ITEM_KEY, bookAdapter.getItem(position))
                nextFragment.arguments = bundle
                fragmentManager?.replaceFragment(R.id.fl_fragment_view, nextFragment, true)
            }
        }
    }

    companion object {
        private const val RESULT_DISPLAY_SIZE = 10
        private const val MAX_START_PAGE = 1000
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_book_list.adapter = bookAdapter
        rv_book_list.addItemDecoration(DividerItemDecoration(activity, VERTICAL))
        setSearchOutputField(query, total)

        initScrollListener()
        initSearchClickListener()
        initEnterListener()
        initFabListener()
    }

    private fun clickSearch() {
        bookAdapter.clearItem()
        if (et_query.text.isBlank()) {
            clearSearchField()
            return
        }
        query = et_query.text.toString()
        start = 1
        hideKeyboard()
        searchBook()
    }

    private fun searchBook() {
        val resultSearchBook = api.searchBookRx(query, RESULT_DISPLAY_SIZE, start)
        resultSearchBook
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                total = response.total
                bookAdapter.addItem(response.items)
                setSearchOutputField(query, total)
                Log.i("호출성공", "$response")
            }, {
                Log.e("호출실패", "$it")
            }).addTo(compositeDisposable)
    }

    private fun clearSearchField() {
        Toast.makeText(activity, getString(R.string.search_info), Toast.LENGTH_SHORT).show()
        setSearchOutputField("", 0)
    }

    private fun setSearchOutputField(query: String, total: Int) {
        tv_total.text = getString(R.string.search_total, query, total)
    }

    private fun initScrollListener() {
        rv_book_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager

                if (start <= MAX_START_PAGE) {
                    val lastVisibleItem =
                        (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    if (lastVisibleItem != -1 && lastVisibleItem >= layoutManager.itemCount - 1) {
                        start += RESULT_DISPLAY_SIZE
                        searchBook()
                    }
                }
            }
        })
    }

    private fun initSearchClickListener() {
        btn_search.setOnClickListener {
            clickSearch()
        }
    }

    private fun initEnterListener() {
        et_query.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    clickSearch()
                    true
                }
                else -> false
            }
        }
    }

    private fun initFabListener() {
        fab_up.setOnClickListener {
            rv_book_list.smoothScrollToPosition(0)
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_query.windowToken, 0)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}