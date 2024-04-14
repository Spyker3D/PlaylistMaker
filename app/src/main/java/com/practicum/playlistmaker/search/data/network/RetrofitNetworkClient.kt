package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.practicum.playlistmaker.search.data.dto.NetworkResponse
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ITUNES_URL = "https://itunes.apple.com"

class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesApiService: ItunesApiService,
) : NetworkClient {

    override fun doRequest(dto: Any?): NetworkResponse {
        if (!isConnected()) {
            return NetworkResponse().apply { resultCode = -1 }
        }
        if (dto !is TrackSearchRequest) {
            return NetworkResponse().apply { resultCode = 400 }
        }
        val response = iTunesApiService.searchTrack(dto.request).execute()
        val body = response.body()
        return body?.apply { resultCode = response.code() }
            ?: NetworkResponse().apply { resultCode = response.code() }
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }
}