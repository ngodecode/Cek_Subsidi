package com.fxlibs.common.data

sealed class LoadState<T> {
    class Loading<T> : LoadState<T>()
    data class Loaded<T>(val result:Result<T>): LoadState<T>()


    fun isLoading() = this is Loading
    fun data() : T? {
        return when (this) {
            is Loaded -> result.getOrNull()
            else -> null
        }
    }
}