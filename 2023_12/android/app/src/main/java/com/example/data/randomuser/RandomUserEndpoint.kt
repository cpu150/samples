package com.example.data.randomuser

import com.example.data.randomuser.model.GetRandomUsersDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface RandomUserEndpoint {
    @Headers("Content-Type:application/json; Accept:application/json")
    @GET("?format=json")
    fun getRandomUsers(@Query("results") numberOfUser: Int): Call<GetRandomUsersDTO>
}
