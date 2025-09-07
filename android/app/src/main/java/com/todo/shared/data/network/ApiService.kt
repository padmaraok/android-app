package com.todo.shared.data.network

import com.todo.shared.data.model.CreateListResponse
import com.todo.shared.data.model.RotateKeyResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/createList")
    suspend fun createList(): Response<CreateListResponse>

    @POST("/rotateKey")
    suspend fun rotateKey(@Query("shortId") shortId: String): Response<RotateKeyResponse>
}
