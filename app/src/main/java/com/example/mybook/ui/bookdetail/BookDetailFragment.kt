package com.example.mybook.ui.bookdetail

import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.mybook.ui.booklink.BookLinkFragment
import com.example.mybook.R
import com.example.mybook.extensions.htmlToSpanned
import com.example.mybook.extensions.replaceFragment
import com.example.mybook.model.Item
import kotlinx.android.synthetic.main.fragment_book_detail.*
import java.text.DecimalFormat

class BookDetailFragment : Fragment(R.layout.fragment_book_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = arguments?.getParcelable<Item>(ITEM_KEY) ?: return
        setActionBar(item.title)
        bindBookData(item)
        initLinkClickListener(item.link)
    }

    private fun setActionBar(title: String) {
        setHasOptionsMenu(true)
        val activity = (activity as AppCompatActivity).apply {
            setSupportActionBar(my_toolbar)
        }
        with(activity.supportActionBar) {
            this?.title = title.htmlToSpanned()
            this?.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun bindBookData(item: Item) {
        Glide.with(this).load(item.image).into(iv_image)
        tv_title.text = item.title.htmlToSpanned().toString()
        tv_author.text = item.author.htmlToSpanned().toString()
        tv_publisher.text = item.publisher.htmlToSpanned().toString()
        tv_pub_date.text = convertToDataType(item.pubdate)
        tv_description.text = when (item.description.isBlank()) {
            true -> getString(R.string.not_found_description)
            else -> item.description.htmlToSpanned().toString()
        }
        setPrice(item.price, item.discount)
    }

    private fun setPrice(price: String, discount: String){
        if (price.isBlank()) {
            tv_discount.text = "-"
            return
        }

        val dec = DecimalFormat("#,###")
        if (discount.isBlank()) {
            tv_discount.text = getString(R.string.price_won, dec.format(price.toInt()))
            return
        }
        tv_price.text = getString(R.string.price_won, dec.format(price.toInt()))
        tv_price.paintFlags = tv_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        tv_discount.text = getString(R.string.price_won, dec.format(discount.toInt()))
        tv_discount_rate.text = calDiscountRate(discount, price)
    }

    private fun convertToDataType(pubDate: String): String {
        return if (pubDate.length < 6) {
            pubDate
        } else {
            pubDate.substring(0, 4) + "." + pubDate.substring(4, 6) + "." + pubDate.substring(6)
        }
    }

    private fun calDiscountRate(discount: String, price: String): String {
        return if (price == "0") "100%"
        else "${(100 - (discount.toDouble() / price.toDouble() * 100)).toInt()}%"
    }

    private fun initLinkClickListener(link: String) {
        btn_link.setOnClickListener {
            val nextFragment = BookLinkFragment()
            val bundle = Bundle()
            bundle.putString(BookLinkFragment.LINK_KEY, link)
            nextFragment.arguments = bundle
            activity?.supportFragmentManager?.replaceFragment(R.id.fl_fragment_view, nextFragment, true)
        }
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

    companion object {
        const val ITEM_KEY = "item"
    }
}