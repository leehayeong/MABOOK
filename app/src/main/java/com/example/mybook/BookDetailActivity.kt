package com.example.mybook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mybook.model.Item
import kotlinx.android.synthetic.main.activity_book_detail.*

class BookDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val item = intent.extras?.getParcelable<Item>("item") ?: return
        tv_title.text = item.title
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }
}