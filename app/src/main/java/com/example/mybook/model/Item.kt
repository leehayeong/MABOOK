package com.example.mybook.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    val title: String,
    val image: String,
    val author: String,
    val price: String,
    val discount: String,
    val publisher: String,
    val description: String,
    val pubdate: String,
    val link: String
) : Parcelable