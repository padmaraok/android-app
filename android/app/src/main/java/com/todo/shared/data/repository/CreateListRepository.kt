package com.todo.shared.data.repository

import com.todo.shared.data.model.CreateListResponse
import com.todo.shared.data.model.RotateKeyResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface CreateListApi {
    @POST("/createList")
    suspend fun createList(): Response<CreateListResponse>

    @POST("/rotateKey")
    suspend fun rotateKey(@Query("shortId") shortId: String): Response<RotateKeyResponse>
}

class CreateListRepository(
    private val api: CreateListApi
) {
    suspend fun createList(): Result<CreateListResponse> {
        return try {
            val response = api.createList()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rotateKey(shortId: String): Result<RotateKeyResponse> {
        return try {
            val response = api.rotateKey(shortId)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
