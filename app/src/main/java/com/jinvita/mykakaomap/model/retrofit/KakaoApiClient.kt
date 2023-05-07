package com.jinvita.mykakaomap.model.retrofit

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object KakaoApiClient {
    const val BASE_URL = "https://dapi.kakao.com/"

    const val API_KEY = "KakaoAK aea58e9057f35a2266511be81f92ee4a"

    //    const val API_KEY = "KakaoAK e1e12e4da395be426f23e300338049cd"
    private val retrofit: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            // 위의 RxJava2CallAdapterFactory를 사용하는 이유는 기존의 Retrofit의 Call이라는 Response Type을 Rx의 Single 또는 Observable로 변환하기 위함
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
    }

    val apiService: KakaoAPI by lazy {
        retrofit.build().create(KakaoAPI::class.java)
    }
}