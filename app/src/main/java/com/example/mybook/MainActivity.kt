package com.example.mybook

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
    private var isNewQuery = true
    private var backKeyPressedTime: Long = 0

    private val bookAdapter: BookAdapter by lazy {
        BookAdapter(mutableListOf()).apply{
            onItemClick = {_, position ->
                val nextIntent = Intent(this@MainActivity, BookDetailActivity::class.java)
                val item = bookAdapter.getItem(position)
                nextIntent.putExtra("item", item)
                nextIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                startActivity(nextIntent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv_book_list.adapter = bookAdapter
        rv_book_list.addItemDecoration(DividerItemDecoration(applicationContext, VERTICAL))

        initScrollListener()
        initSearchClickListener()
        initEnterListener()
        initFabListener()
    }

    private fun clickSearch() {
        query = et_query.text.toString()
        et_query.text.clear()
        bookAdapter.clearItem()
        start = 1
        isNewQuery = true
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

                if (isNewQuery) {
                    setTotal()
                    isNewQuery = false
                }

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

    private fun initFabListener(){
        fab_up.setOnClickListener {
            rv_book_list.smoothScrollToPosition(0)
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(et_query.windowToken, 0)
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - backKeyPressedTime >= 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, R.string.finish_info, Toast.LENGTH_SHORT).show()
            return
        }
        finish()
    }
}