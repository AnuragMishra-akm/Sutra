package com.example.sutra.model

data class SutraModel(
    val sutra: String = "",
    val images: List<String> = emptyList(),
    val userId: String = "",
    val timeStamp: String = "",
)
