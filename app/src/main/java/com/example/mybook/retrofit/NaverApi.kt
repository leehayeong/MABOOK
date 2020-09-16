package com.example.mybook.retrofit

import com.example.mybook.model.Book
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverApi {

    @GET("v1/search/book.json")
    fun searchBook(
        @Query("query") query: String? = null,
        @Query("display") display: Int? = null,
        @Query("start") start: Int? = null
    ): Call<Book>

    companion object {
        // 1. 변수 선언
        private const val NAVER_BASE_URL = "https://openapi.naver.com/"
        private const val CLIENT_ID = "MhMuAFyuM43SKGe2hOos"
        private const val CLIENT_SECRET = "rxnRXe5O_l"

        // 2. 레트로핏 객체 생성 함수 정의
        fun createRetrofit(): NaverApi {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            val headerInterceptor = Interceptor {
                val request = it.request()
                    .newBuilder()
                    .addHeader("X-Naver-Client-Id", CLIENT_ID)
                    .addHeader("X-Naver-Client-Secret", CLIENT_SECRET)
                    .build()
                return@Interceptor it.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(headerInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(NAVER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NaverApi::class.java)
        }
    }
}