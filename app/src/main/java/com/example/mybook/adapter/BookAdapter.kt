package com.example.mybook.adapter

import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mybook.R
import com.example.mybook.model.Item
import kotlinx.android.synthetic.main.item_book.view.*

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
            view.tv_book_title.text = htmlToString(item.title)
            view.tv_book_author.text = htmlToString(item.author)
            view.tv_book_publisher.text = htmlToString(item.publisher)
            view.tv_book_price.text = item.price
            view.tv_book_discount.text = item.discount
        }

        private fun htmlToString(string: String): Spanned {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(string)
            }
        }
    }
}