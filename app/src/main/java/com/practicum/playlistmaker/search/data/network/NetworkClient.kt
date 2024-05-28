package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.NetworkResponse

interface NetworkClient {

    suspend fun doRequest(dto: Any?): NetworkResponse
}