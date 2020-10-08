package com.example.mybook.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.mybook.model.Item
import com.example.mybook.retrofit.NaverApi
import com.example.mybook.util.SupportOptional
import com.example.mybook.util.emptyOptional
import com.example.mybook.util.optionalOf
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject


class SearchViewModel(private val api: NaverApi) : ViewModel() {

    val searchResult: BehaviorSubject<SupportOptional<List<Item>>> =
        BehaviorSubject.createDefault(emptyOptional())
    private var bookItems: MutableList<Item> = mutableListOf()
    var query = ""
    var start = 1
    var total = 0

    fun searchBook(): Disposable =
        api.searchBookRx(query, RESULT_DISPLAY_SIZE, start)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter {!(start != 1 && it.total == 0) }
            .subscribe({ response ->
                total = response.total
                bookItems.addAll(response.items)
                searchResult.onNext(optionalOf(bookItems))
                Log.i("호출성공", "$response.items")
            }) {
                Log.e("호출실패", "$it")
            }

    fun clearSearchRequestData(query: String, start: Int) {
        this.query = query
        this.start = start
        bookItems.clear()
    }

    fun clearSearchResult() {
        total = 0
        searchResult.onNext(emptyOptional())
    }

    fun addNextPage() {
        start += RESULT_DISPLAY_SIZE
    }

    companion object {
        const val RESULT_DISPLAY_SIZE = 10
        const val MAX_START_PAGE = 1000
    }
}