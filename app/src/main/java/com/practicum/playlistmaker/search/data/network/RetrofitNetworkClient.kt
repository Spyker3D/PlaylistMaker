package com.practicum.playlistmaker.search.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.practicum.playlistmaker.search.data.dto.NetworkResponse
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ITUNES_URL = "https://itunes.apple.com"

class RetrofitNetworkClient(
    private val context: Context,
    private val iTunesApiService: ItunesApiService,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : NetworkClient {

    override suspend fun doRequest(dto: Any?): NetworkResponse {
        if (!isConnected()) {
            return NetworkResponse().apply { resultCode = -1 }
        }
        if (dto !is TrackSearchRequest) {
            return NetworkResponse().apply { resultCode = 400 }
        }

        return withContext(dispatcherIO) {
            try {
                val response = iTunesApiService.searchTrack(dto.request)
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                NetworkResponse().apply { resultCode = 500 }
            }
        }
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