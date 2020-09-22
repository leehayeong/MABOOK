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
import com.example.mybook.model.BookListResponse
import com.example.mybook.retrofit.NaverApi
import kotlinx.android.synthetic.main.fragment_search.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(R.layout.fragment_search) {

    private val api = NaverApi.createRetrofit()
    private var query = ""
    private val display = 10
    private var start = 1
    private var total = 0

    private val bookAdapter: BookAdapter by lazy {
        BookAdapter(mutableListOf()).apply {
            onItemClick = { _, position ->
                val nextFragment = BookDetailFragment()
                val bundle = Bundle()
                bundle.putParcelable("item", bookAdapter.getItem(position))
                nextFragment.arguments = bundle
                fragmentManager?.replaceFragment(R.id.fl_fragment_view, nextFragment, true)
            }
        }
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
        val resultSearchBook = api.searchBook(query, display, start)
        resultSearchBook.enqueue(object : Callback<BookListResponse> {
            override fun onResponse(call: Call<BookListResponse>, response: Response<BookListResponse>) {
                val itemList = response.body()?.items ?: emptyList()
                bookAdapter.addItem(itemList)
                total = response.body()?.total ?: 0
                setSearchOutputField(query, total)
                Log.i("호출 성공", "${response.body()}")
            }

            override fun onFailure(call: Call<BookListResponse>, t: Throwable) {
                Log.e("호출 실패", "$t")
            }
        })
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

                if (start <= 1000) {
                    val lastVisibleItem =
                        (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    if (lastVisibleItem != -1 && lastVisibleItem >= layoutManager.itemCount - 1) {
                        start += display
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
}