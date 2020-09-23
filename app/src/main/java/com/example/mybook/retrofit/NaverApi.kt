package com.example.mybook.retrofit

import com.example.mybook.model.BookListResponse
import io.reactivex.rxjava3.core.Single
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NaverApi {

    @GET("v1/search/book.json")
    fun searchBookRx(
        @Query("query") query: String,
        @Query("display") display: Int? = null,
        @Query("start") start: Int? = null
    ): Single<BookListResponse>

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
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NaverApi::class.java)
        }
    }
}