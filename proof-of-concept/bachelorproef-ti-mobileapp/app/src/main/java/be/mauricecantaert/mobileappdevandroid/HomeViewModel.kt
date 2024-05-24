package be.mauricecantaert.mobileappdevandroid

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import be.mauricecantaert.mobileappdevandroid.api.Application
import be.mauricecantaert.mobileappdevandroid.model.ApiState
import be.mauricecantaert.mobileappdevandroid.network.RetrofitService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class HomeViewModel(
    private val retrofitService: RetrofitService,
) : ViewModel() {
    var apiState: ApiState by mutableStateOf(ApiState.Start)

    private val _uiState = MutableStateFlow("")
    val uiState = _uiState.asStateFlow()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                val service =
                    application.container.retrofitService
                HomeViewModel(
                    retrofitService = service,
                )
            }
        }
    }

    fun getSuggestion(uri: Uri, context: Context) {
        apiState = ApiState.Loading
        val input = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "fileUpload")
        val output = FileOutputStream(file)
        input?.copyTo(output)
        input?.close()
        output.close()

        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())

        viewModelScope.launch {
            apiState = try {
                val response = retrofitService.getSuggestion(requestBody)
                _uiState.update { response.text }
                ApiState.Success
            } catch (e: Exception) {
                Log.e("HomeViewModel Exception", e.message ?: e.toString())
                ApiState.Error
            }
        }
    }
}