package com.practicum.playlistmaker.search.domain.consumer

sealed class ConsumerData<T>(val value: T? = null) {

    class Data<T>(value: T) : ConsumerData<T>(value)
    class Error<T>(value: T? = null): ConsumerData<T>(value)
    class InternetConnectionError<T>(value: T? = null) : ConsumerData<T>(value)
}