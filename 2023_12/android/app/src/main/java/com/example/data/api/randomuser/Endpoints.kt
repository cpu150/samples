package com.example.data.api.randomuser

import com.example.data.api.randomuser.model.GetRandomUsersDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface Endpoints {
    @Headers("Content-Type:application/json; Accept:application/json")
    @GET("?format=json")
    fun getRandomUsers(@Query("results") numberOfUsers: Int): Response<GetRandomUsersDTO>
}
