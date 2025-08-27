package com.example.going.model.util

data class DataFetchState(
    var isLoading: Boolean = false,
    var isError: String? = null,
    var isSuccess: String? = null
)