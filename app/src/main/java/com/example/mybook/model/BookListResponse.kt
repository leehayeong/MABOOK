package com.example.mybook.model

data class BookListResponse (
    val total: Int = 0,
    val items: List<Item>
)