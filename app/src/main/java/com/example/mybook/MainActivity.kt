package com.example.mybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mybook.model.Book
import com.example.mybook.retrofit.NaverApi
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val api = NaverApi.createRetrofit()
    private val query = "책"
    private val display = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchBook()
    }

    private fun searchBook() {
        val resultSearchBook = api.searchBook(query, display)
        resultSearchBook.enqueue(object : Callback<Book> {
            override fun onResponse(call: Call<Book>, response: Response<Book>) {
                val total = response.body()?.total ?: 0
                val itemList = response.body()?.items ?: emptyList()

                setText(total)

                Log.i("호출 성공", "${response.body()}")
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                Log.e("호출 실패", "$t")
            }
        })
    }

    private fun setText(total: Int){
        tv_total.text = "$query 검색 결과 총 $total 건"
    }
}