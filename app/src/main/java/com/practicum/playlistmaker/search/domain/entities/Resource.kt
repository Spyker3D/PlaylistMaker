package com.practicum.playlistmaker.search.domain.entities


sealed class Resource<T>(val data: T? = null) {

    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(data: T? = null) : Resource<T>(data)
    class InternetConnectionError<T>(data: T? = null) : Resource<T>(data)

}
