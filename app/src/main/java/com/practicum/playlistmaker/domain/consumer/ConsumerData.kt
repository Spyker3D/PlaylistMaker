package com.practicum.playlistmaker.domain.consumer

sealed interface ConsumerData<T> {

    data class Data<T>(val value: T) : ConsumerData<T>
    class Error<T>: ConsumerData<T>
    class InternetConnectionError<T> : ConsumerData<T>
}