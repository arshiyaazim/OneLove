package com.kilagee.onelove.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilagee.onelove.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableSharedFlow<String>()
    val error: SharedFlow<String> = _error.asSharedFlow()
    
    private val _isLoggedIn = MutableStateFlow(userRepository.isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()
    
    private val _loginSuccess = MutableSharedFlow<Unit>()
    val loginSuccess: SharedFlow<Unit> = _loginSuccess.asSharedFlow()
    
    private val _registerSuccess = MutableSharedFlow<Unit>()
    val registerSuccess: SharedFlow<Unit> = _registerSuccess.asSharedFlow()
    
    private val _resetPasswordSuccess = MutableSharedFlow<Unit>()
    val resetPasswordSuccess: SharedFlow<Unit> = _resetPasswordSuccess.asSharedFlow()
    
    // Form state
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")
    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val username = MutableStateFlow("")
    val age = MutableStateFlow(0)
    val country = MutableStateFlow("")
    val location = MutableStateFlow("")
    val gender = MutableStateFlow("")
    val ageVerification = MutableStateFlow(false)
    
    init {
        checkLoggedInStatus()
    }
    
    private fun checkLoggedInStatus() {
        _isLoggedIn.value = userRepository.isUserLoggedIn()
    }
    
    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = userRepository.signIn(email.value, password.value)
            if (result.isSuccess) {
                _isLoggedIn.value = true
                _loginSuccess.emit(Unit)
            } else {
                _error.emit(result.exceptionOrNull()?.message ?: "Login failed")
            }
            
            _isLoading.value = false
        }
    }
    
    fun register() {
        viewModelScope.launch {
            // Validate inputs
            if (!validateRegistration()) {
                return@launch
            }
            
            _isLoading.value = true
            
            val result = userRepository.signUp(
                email = email.value,
                password = password.value,
                firstName = firstName.value,
                lastName = lastName.value,
                username = username.value,
                age = age.value,
                country = country.value,
                location = location.value,
                gender = gender.value
            )
            
            if (result.isSuccess) {
                _isLoggedIn.value = true
                _registerSuccess.emit(Unit)
            } else {
                _error.emit(result.exceptionOrNull()?.message ?: "Registration failed")
            }
            
            _isLoading.value = false
        }
    }
    
    fun resetPassword() {
        viewModelScope.launch {
            if (email.value.isBlank()) {
                _error.emit("Please enter your email")
                return@launch
            }
            
            _isLoading.value = true
            
            val result = userRepository.resetPassword(email.value)
            if (result.isSuccess) {
                _resetPasswordSuccess.emit(Unit)
            } else {
                _error.emit(result.exceptionOrNull()?.message ?: "Password reset failed")
            }
            
            _isLoading.value = false
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
            _isLoggedIn.value = false
        }
    }
    
    private suspend fun validateRegistration(): Boolean {
        if (email.value.isBlank() || password.value.isBlank() || confirmPassword.value.isBlank() ||
            firstName.value.isBlank() || lastName.value.isBlank() || username.value.isBlank() ||
            age.value <= 0 || country.value.isBlank() || location.value.isBlank() || gender.value.isBlank()
        ) {
            _error.emit("All fields are required")
            return false
        }
        
        if (password.value != confirmPassword.value) {
            _error.emit("Passwords don't match")
            return false
        }
        
        if (age.value < 18) {
            _error.emit("You must be at least 18 years old")
            return false
        }
        
        if (!ageVerification.value) {
            _error.emit("You must confirm that you are 18 years or older")
            return false
        }
        
        return true
    }
    
    fun updateEmail(value: String) {
        email.value = value
    }
    
    fun updatePassword(value: String) {
        password.value = value
    }
    
    fun updateConfirmPassword(value: String) {
        confirmPassword.value = value
    }
    
    fun updateFirstName(value: String) {
        firstName.value = value
    }
    
    fun updateLastName(value: String) {
        lastName.value = value
    }
    
    fun updateUsername(value: String) {
        username.value = value
    }
    
    fun updateAge(value: Int) {
        age.value = value
    }
    
    fun updateCountry(value: String) {
        country.value = value
    }
    
    fun updateLocation(value: String) {
        location.value = value
    }
    
    fun updateGender(value: String) {
        gender.value = value
    }
    
    fun updateAgeVerification(value: Boolean) {
        ageVerification.value = value
    }
}
