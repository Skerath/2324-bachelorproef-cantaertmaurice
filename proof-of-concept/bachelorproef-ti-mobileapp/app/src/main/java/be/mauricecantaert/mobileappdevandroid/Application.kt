package be.mauricecantaert.mobileappdevandroid

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.mauricecantaert.mobileappdevandroid.model.ApiState
import be.mauricecantaert.mobileappdevandroid.ui.common.ErrorIndicator
import be.mauricecantaert.mobileappdevandroid.ui.common.LoadingIndicator
import dev.jeziellago.compose.markdowntext.MarkdownText

@Composable
fun Application() {

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { homeViewModel.getSuggestion(it, context) }
    }

    Scaffold { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {

            when (homeViewModel.apiState) {
                is ApiState.Start -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Button(onClick = { launcher.launch("image/*") }) {
                            Text("Select File")
                        }
                    }
                }

                is ApiState.Loading -> {
                    LoadingIndicator()
                }

                is ApiState.Success -> {
                    val state by homeViewModel.uiState.collectAsState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        MarkdownText(markdown = state)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { launcher.launch("image/*") }) {
                            Text("Select File")
                        }
                    }
                }
                ApiState.Error -> ErrorIndicator("Error occurred!") { launcher.launch("image/*") }
            }
        }
    }
}