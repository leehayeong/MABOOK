package com.example.mybook.ui.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.mybook.R
import com.example.mybook.adapter.BookAdapter
import com.example.mybook.extensions.replaceFragment
import com.example.mybook.retrofit.NaverApi
import com.example.mybook.rx.AutoClearedDisposable
import com.example.mybook.ui.bookdetail.BookDetailFragment
import com.example.mybook.ui.search.SearchViewModel.Companion.MAX_START_PAGE
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var disposable: AutoClearedDisposable

    private val viewModelFactory by lazy {
        SearchViewModelFactory(NaverApi.createRetrofit())
    }

    private val viewModel by viewModels<SearchViewModel>({ this }, { viewModelFactory })

    private val bookAdapter: BookAdapter by lazy {
        BookAdapter(mutableListOf()).apply {
            onItemClick = { _, position ->
                val nextFragment = BookDetailFragment()
                val bundle = Bundle()
                bundle.putParcelable(BookDetailFragment.ITEM_KEY, bookAdapter.getItem(position))
                nextFragment.arguments = bundle
                activity?.supportFragmentManager?.replaceFragment(R.id.fl_fragment_view, nextFragment, true)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        disposable = AutoClearedDisposable(viewLifecycleOwner)

        initScrollListener()
        initSearchClickListener()
        initEnterListener()
        initFabListener()

        with(rv_book_list) {
            addItemDecoration(DividerItemDecoration(activity, VERTICAL))
            adapter = bookAdapter
        }

        lifecycle.addObserver(disposable)
        disposable.add(
            viewModel.searchResult
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext { setSearchOutputField(viewModel.query, viewModel.total) }
                .subscribe { items ->
                    try {
                        bookAdapter.setItems(items.value)
                    } catch (e: IllegalStateException) {
                        bookAdapter.setItems(emptyList())
                    }
                }
        )
    }

    private fun clickSearch() {
        with(viewModel) {
            clearSearchRequestData(et_search.text.toString(), 1)
            if (et_search.text.toString().isBlank()) {
                clearSearchResult()
                showToastMsg(getString(R.string.search_info_msg))
            } else {
                searchBook()
            }
        }
        hideKeyboard()    }

    private fun setSearchOutputField(query: String, total: Int) {
        tv_total.text = getString(R.string.search_total, query, total)
    }

    private fun showToastMsg(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    private fun initSearchClickListener() {
        btn_search.setOnClickListener {
            clickSearch()
        }
    }

    private fun initEnterListener() {
        et_search.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH -> {
                    clickSearch()
                    true
                }
                else -> false
            }
        }
    }

    private fun initScrollListener() {
        rv_book_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager

                if (viewModel.start <= MAX_START_PAGE) {
                    val lastVisibleItem =
                        (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    if (lastVisibleItem != -1 && lastVisibleItem >= layoutManager.itemCount - 1) {
                        viewModel.addNextPage()
                        viewModel.searchBook()
                    }
                }
            }
        })
    }

    private fun initFabListener() {
        fab_up.setOnClickListener {
            rv_book_list.smoothScrollToPosition(0)
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_search.windowToken, 0)
    }
}