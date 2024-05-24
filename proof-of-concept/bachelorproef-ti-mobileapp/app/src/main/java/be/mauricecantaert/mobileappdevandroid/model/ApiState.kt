package be.mauricecantaert.mobileappdevandroid.model

sealed interface ApiState {

    data object Start : ApiState
    data object Success : ApiState
    data object Error : ApiState
    data object Loading : ApiState
}
