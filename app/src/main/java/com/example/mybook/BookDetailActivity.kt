package com.example.mybook

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.mybook.extensions.htmlToString
import com.example.mybook.model.Item
import kotlinx.android.synthetic.main.activity_book_detail.*

class BookDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        val item = intent.extras?.getParcelable<Item>("item") ?: return
        setActionBar(item.title)
        bindBookData(item)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun setActionBar(title: String){
        setSupportActionBar(my_toolbar)
        with(supportActionBar){
            setTitle(title.htmlToString())
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun bindBookData(item: Item){
        Glide.with(this).load(item.image).into(iv_image)
        tv_title.text = item.title.htmlToString().toString()
        tv_author.text = item.author.htmlToString().toString()
        tv_publisher.text = item.publisher.htmlToString().toString()
        tv_pub_date.text = convertToDataType(item.pubdate)
        tv_price.text = item.price
        tv_discount.text = item.discount
        tv_description.text = item.description.htmlToString().toString()
    }

    private fun convertToDataType(pubDate: String): String {
        return if (pubDate.length < 6) {
            pubDate
        } else {
            pubDate.substring(0, 4) + "." + pubDate.substring(4, 6) + "." + pubDate.substring(6)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}