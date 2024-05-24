package be.mauricecantaert.mobileappdevandroid.data

import android.content.Context
import be.mauricecantaert.mobileappdevandroid.network.RetrofitService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

interface AppContainer {
    val retrofitService: RetrofitService
}

class DefaultAppContainer : AppContainer {
    private val restApiBaseUrl = "http://10.0.2.2:8080/"

    private val json = Json { ignoreUnknownKeys = true }

    private val logger =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logger)
        .readTimeout(60, TimeUnit.SECONDS)
        .connectTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofitClient = Retrofit.Builder()
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType()),
        )
        .baseUrl(restApiBaseUrl)
        .client(client)
        .build()

    override val retrofitService: RetrofitService by lazy {
        retrofitClient.create(RetrofitService::class.java)
    }
}
