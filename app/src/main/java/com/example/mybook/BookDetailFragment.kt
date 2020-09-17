package com.example.mybook

import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mybook.extensions.htmlToString
import com.example.mybook.model.Item
import kotlinx.android.synthetic.main.fragment_book_detail.*

class BookDetailFragment : Fragment(R.layout.fragment_book_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = arguments?.getParcelable<Item>("item") ?: return
        setActionBar(item.title)
        bindBookData(item)
    }

    private fun setActionBar(title: String) {
        val activity = (activity as AppCompatActivity).apply {
            setSupportActionBar(my_toolbar)
        }
        with(activity.supportActionBar) {
            this?.title = title.htmlToString()
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun bindBookData(item: Item) {
        Glide.with(this).load(item.image).into(iv_image)
        tv_title.text = item.title.htmlToString().toString()
        tv_author.text = item.author.htmlToString().toString()
        tv_publisher.text = item.publisher.htmlToString().toString()
        tv_pub_date.text = convertToDataType(item.pubdate)
        tv_description.text = when (item.description.isBlank()) {
            true -> getString(R.string.not_found_description)
            else -> item.description.htmlToString().toString()
        }
        if (item.discount.isBlank()) {
            tv_discount.text = item.price
            tv_price.text = ""
            return
        }
        tv_price.text = item.price
        tv_price.paintFlags = tv_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        tv_discount.text = item.discount
        tv_discount_rate.text = calDiscountRate(item.price, item.discount)
    }

    private fun convertToDataType(pubDate: String): String {
        return if (pubDate.length < 6) {
            pubDate
        } else {
            pubDate.substring(0, 4) + "." + pubDate.substring(4, 6) + "." + pubDate.substring(6)
        }
    }

    private fun calDiscountRate(discount: String, price: String): String {
        return "${(100 - (Integer.parseInt(discount).toDouble() / Integer.parseInt(price)
            .toDouble() * 100).toInt())}%"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}