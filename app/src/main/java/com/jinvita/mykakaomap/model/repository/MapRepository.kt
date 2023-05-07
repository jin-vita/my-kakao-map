package com.jinvita.mykakaomap.model.repository

import com.jinvita.mykakaomap.model.data.CategorySearchData
import com.jinvita.mykakaomap.model.retrofit.KakaoApiClient
import retrofit2.Response

class MapRepository {
    suspend fun searchCategory(
        category: String,
        x: String,
        y: String,
        radius: Int,
        sort: String,
        page: Int,
        size: Int
    ): Response<CategorySearchData> {
        return KakaoApiClient.apiService.getSearchCategory(
            KakaoApiClient.API_KEY,
            category,
            x,
            y,
            radius,
            sort,
            page,
            size
        )
    }
}