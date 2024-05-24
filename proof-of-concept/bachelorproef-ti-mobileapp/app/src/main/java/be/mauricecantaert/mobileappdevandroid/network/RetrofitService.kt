package be.mauricecantaert.mobileappdevandroid.network

import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface RetrofitService {

    @Multipart
    @POST("/api")
    suspend fun getSuggestion(
        @Part("image") image: RequestBody
    ): SuggestionDto
}
