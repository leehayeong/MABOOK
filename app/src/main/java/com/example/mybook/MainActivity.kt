package com.example.mybook

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.VERTICAL
import com.example.mybook.adapter.BookAdapter
import com.example.mybook.model.Book
import com.example.mybook.retrofit.NaverApi
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val api = NaverApi.createRetrofit()
    private var query = ""
    private val display = 10
    private var start = 1
    private var total = 0

    private val bookAdapter: BookAdapter by lazy {
        BookAdapter(mutableListOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_book_list.adapter = bookAdapter
        rv_book_list.addItemDecoration(DividerItemDecoration(applicationContext, VERTICAL))

        initScrollListener()
        initSearchClickListener()
        initEnterListener()
    }

    private fun clickSearch(){
        query = et_query.text.toString()
        et_query.text.clear()
        bookAdapter.clearItem()
        start = 1
        hideKeyboard()
        searchBook()
    }

    private fun searchBook() {
        val resultSearchBook = api.searchBook(query, display, start)
        resultSearchBook.enqueue(object : Callback<Book> {
            override fun onResponse(call: Call<Book>, response: Response<Book>) {
                val itemList = response.body()?.items ?: emptyList()
                total = response.body()?.total ?: 0

                bookAdapter.addItem(itemList)
                setTotal()

                Log.i("호출 성공", "${response.body()}")
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                Log.e("호출 실패", "$t")
            }
        })
    }

    private fun setTotal() {
        tv_total.text = getString(R.string.search_total, query, total)
    }

    private fun initScrollListener() {
        rv_book_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager

                // 페이징
                if (start <= 1000) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
                    if (lastVisibleItem != -1 && lastVisibleItem >= layoutManager.itemCount - 1) {
                        start += display
                        searchBook()
                    }
                }
            }
        })
    }

    private fun initSearchClickListener(){
        btn_search.setOnClickListener {
            clickSearch()
        }
    }

    private fun initEnterListener(){
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

    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_query.windowToken, 0)
    }
}