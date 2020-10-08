package com.example.mybook.ui.booklink

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.example.mybook.R
import kotlinx.android.synthetic.main.fragment_book_link.*

class BookLinkFragment : Fragment(R.layout.fragment_book_link) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val link = arguments?.getString(LINK_KEY) ?: return
        loadWebView(link)
        setBackKeyListener()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadWebView(link: String) {
        with(wb_book_link) {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
            loadUrl(link)
        }
    }

    private fun setBackKeyListener(){
        wb_book_link.setOnKeyListener { _, _, keyEvent ->
            if(keyEvent.keyCode == KeyEvent.KEYCODE_BACK && !wb_book_link.canGoBack()){
                false
            } else if (keyEvent.keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == MotionEvent.ACTION_UP){
                wb_book_link.goBack()
                true
            } else {
                true
            }
        }
    }

    companion object {
        const val LINK_KEY = "link"
    }
}