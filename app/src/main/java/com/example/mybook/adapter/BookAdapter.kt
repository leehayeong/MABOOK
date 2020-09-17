package com.example.mybook.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybook.R
import com.example.mybook.extensions.htmlToString
import com.example.mybook.model.Item
import kotlinx.android.synthetic.main.item_book.view.*
import java.text.DecimalFormat

class BookAdapter(private val itemList: MutableList<Item>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    var onItemClick: ((View, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        return BookViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false))
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(it, position)
        }

        holder.apply {
            bind(itemList[position])
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun clearItem() {
        this.itemList.clear()
        notifyDataSetChanged()
    }

    fun addItem(itemList: List<Item>) {
        this.itemList.addAll(itemList)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Item {
        return itemList[position]
    }

    class BookViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Item) {
            Glide.with(view).load(item.image).into(view.iv_book_image)
            view.tv_book_title.text = item.title.htmlToString()
            view.tv_book_author.text = item.author.htmlToString()
            view.tv_book_publisher.text = item.publisher.htmlToString()
            setPrice(item.price, item.discount)
        }

        private fun setPrice(price: String, discount: String){
            val dec = DecimalFormat("#,###")
            if(discount.isBlank()){
                view.tv_book_discount.text = view.resources.getString(R.string.price_won, dec.format(price.toInt()))
                view.tv_book_price.text = ""
                return
            }
            view.tv_book_price.text = view.resources.getString(R.string.price_won, dec.format(price.toInt()))
            view.tv_book_price.paintFlags = view.tv_book_price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            view.tv_book_discount.text = view.resources.getString(R.string.price_won, dec.format(discount.toInt()))
        }
    }
}