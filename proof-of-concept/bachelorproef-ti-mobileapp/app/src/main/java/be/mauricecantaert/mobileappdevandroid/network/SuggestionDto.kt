package be.mauricecantaert.mobileappdevandroid.network

import kotlinx.serialization.Serializable

@Serializable
data class SuggestionDto(
    val text: String,
)
