package com.practicum.playlistmaker.data.network

import android.util.Log
import com.practicum.playlistmaker.data.dto.NetworkResponse
import com.practicum.playlistmaker.data.dto.TrackSearchRequest
import com.practicum.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.Exception

const val ITUNES_URL = "https://itunes.apple.com"

class RetrofitNetworkClient : NetworkClient {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ITUNES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val iTunesApiService: ItunesApiService by lazy {
        retrofit.create(ItunesApiService::class.java)
    }

    override fun doRequest(dto: Any?): NetworkResponse {
        return try {
            if (dto is TrackSearchRequest) {
                val response = iTunesApiService.searchTrack(dto.request).execute()
                val body: NetworkResponse = response.body() ?: NetworkResponse()
                body.apply { resultCode = response.code() }
            } else {
                NetworkResponse().apply { resultCode = 400 }
            }
        } catch (ex: IOException) {
            Log.e("SearchActivity", "Network error", ex)
            NetworkResponse().apply { resultCode = 0 }
        }
    }
}