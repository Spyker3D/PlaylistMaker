package com.practicum.playlistmaker.search.domain.entities


sealed class Resource<T>(val data: T? = null) {

    class Success<T>(data: T) : Resource<T>(data)
    class Error<T> : Resource<T>()
    class InternetConnectionError<T> : Resource<T>()

}
