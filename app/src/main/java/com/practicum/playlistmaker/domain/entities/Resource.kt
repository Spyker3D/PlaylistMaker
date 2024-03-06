package com.practicum.playlistmaker.domain.entities

sealed interface Resource<T> {

    data class Success<T>(val data: T) : Resource<T>
    class Error<T> : Resource<T>
    class InternetConnectionError<T> : Resource<T>
}