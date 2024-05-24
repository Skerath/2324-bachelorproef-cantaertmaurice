package mauricecantaert.dto

data class SuggestionDto(
    val text: String,
)

fun String.toSuggestionDto() = SuggestionDto(text = this)