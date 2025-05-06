package com.kilagee.onelove.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.kilagee.onelove.data.model.Result
import com.kilagee.onelove.data.model.User
import com.kilagee.onelove.data.repository.AuthRepository
import com.kilagee.onelove.data.repository.UserRepository
import com.kilagee.onelove.util.AppError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Sealed class representing auth UI state
 */
sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    data class Error(val error: AppError, val message: String) : AuthUiState()
    data class Success<T>(val data: T) : AuthUiState()
}

/**
 * Auth operation types for tracking current operation
 */
enum class AuthOperation {
    NONE,
    REGISTER,
    LOGIN,
    GOOGLE_SIGN_IN,
    PHONE_VERIFICATION,
    RESET_PASSWORD,
    UPDATE_PROFILE,
    VERIFY_EMAIL,
    DELETE_ACCOUNT
}

/**
 * ViewModel for authentication operations
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    // Current auth UI state
    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()
    
    // Current auth operation being performed
    private val _currentOperation = MutableStateFlow(AuthOperation.NONE)
    val currentOperation: StateFlow<AuthOperation> = _currentOperation.asStateFlow()
    
    // Current user authentication state
    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()
    
    // User profile data
    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()
    
    // Email verification state
    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified.asStateFlow()
    
    init {
        // Initialize current user
        _currentUser.value = authRepository.getCurrentUser()
        _isEmailVerified.value = authRepository.isEmailVerified()
        
        // Listen for auth state changes
        viewModelScope.launch {
            authRepository.getCurrentUserFlow()
                .catch { e ->
                    Timber.e(e, "Error in auth state flow")
                }
                .collect { user ->
                    _currentUser.value = user
                    _isEmailVerified.value = user?.isEmailVerified ?: false
                    
                    // Get user data when authenticated
                    if (user != null) {
                        fetchUserData()
                    } else {
                        _userData.value = null
                    }
                }
        }
    }
    
    /**
     * Register a new user with email and password
     */
    fun registerWithEmailAndPassword(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _currentOperation.value = AuthOperation.REGISTER
            _authUiState.value = AuthUiState.Loading
            
            when (val result = authRepository.registerWithEmailAndPassword(email, password, displayName)) {
                is Result.Success -> {
                    _authUiState.value = AuthUiState.Success(result.data)
                    sendEmailVerification()
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(result.error, result.error.message)
                }
            }
            
            _currentOperation.value = AuthOperation.NONE
        }
    }
    
    /**
     * Login with email and password
     */
    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _currentOperation.value = AuthOperation.LOGIN
            _authUiState.value = AuthUiState.Loading
            
            when (val result = authRepository.loginWithEmailAndPassword(email, password)) {
                is Result.Success -> {
                    _authUiState.value = AuthUiState.Success(result.data)
                    _isEmailVerified.value = result.data.isEmailVerified
                    fetchUserData()
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(result.error, result.error.message)
                }
            }
            
            _currentOperation.value = AuthOperation.NONE
        }
    }
    
    /**
     * Sign in with credentials (Google, Facebook, etc.)
     */
    fun signInWithCredential(credential: AuthCredential) {
        viewModelScope.launch {
            _currentOperation.value = AuthOperation.GOOGLE_SIGN_IN
            _authUiState.value = AuthUiState.Loading
            
            when (val result = authRepository.signInWithCredential(credential)) {
                is Result.Success -> {
                    _authUiState.value = AuthUiState.Success(result.data)
                    _isEmailVerified.value = result.data.isEmailVerified
                    fetchUserData()
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(result.error, result.error.message)
                }
            }
            
            _currentOperation.value = AuthOperation.NONE
        }
    }
    
    /**
     * Send email verification
     */
    fun sendEmailVerification() {
        viewModelScope.launch {
            _currentOperation.value = AuthOperation.VERIFY_EMAIL
            _authUiState.value = AuthUiState.Loading
            
            when (val result = authRepository.sendEmailVerification()) {
                is Result.Success -> {
                    _authUiState.value = AuthUiState.Success(Unit)
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(result.error, result.error.message)
                }
            }
            
            _currentOperation.value = AuthOperation.NONE
        }
    }
    
    /**
     * Reset password by sending reset email
     */
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            _currentOperation.value = AuthOperation.RESET_PASSWORD
            _authUiState.value = AuthUiState.Loading
            
            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is Result.Success -> {
                    _authUiState.value = AuthUiState.Success(Unit)
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(result.error, result.error.message)
                }
            }
            
            _currentOperation.value = AuthOperation.NONE
        }
    }
    
    /**
     * Confirm password reset
     */
    fun confirmPasswordReset(code: String, newPassword: String) {
        viewModelScope.launch {
            _currentOperation.value = AuthOperation.RESET_PASSWORD
            _authUiState.value = AuthUiState.Loading
            
            when (val result = authRepository.confirmPasswordReset(code, newPassword)) {
                is Result.Success -> {
                    _authUiState.value = AuthUiState.Success(Unit)
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(result.error, result.error.message)
                }
            }
            
            _currentOperation.value = AuthOperation.NONE
        }
    }
    
    /**
     * Update user password
     */
    fun updatePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _currentOperation.value = AuthOperation.UPDATE_PROFILE
            _authUiState.value = AuthUiState.Loading
            
            when (val result = authRepository.updatePassword(currentPassword, newPassword)) {
                is Result.Success -> {
                    _authUiState.value = AuthUiState.Success(Unit)
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(result.error, result.error.message)
                }
            }
            
            _currentOperation.value = AuthOperation.NONE
        }
    }
    
    /**
     * Update user email
     */
    fun updateEmail(newEmail: String, password: String) {
        viewModelScope.launch {
            _currentOperation.value = AuthOperation.UPDATE_PROFILE
            _authUiState.value = AuthUiState.Loading
            
            when (val result = authRepository.updateEmail(newEmail, password)) {
                is Result.Success -> {
                    _authUiState.value = AuthUiState.Success(Unit)
                    sendEmailVerification()
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(result.error, result.error.message)
                }
            }
            
            _currentOperation.value = AuthOperation.NONE
        }
    }
    
    /**
     * Sign out the current user
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _currentUser.value = null
            _userData.value = null
            _isEmailVerified.value = false
            _authUiState.value = AuthUiState.Initial
        }
    }
    
    /**
     * Delete user account
     */
    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _currentOperation.value = AuthOperation.DELETE_ACCOUNT
            _authUiState.value = AuthUiState.Loading
            
            when (val result = authRepository.deleteAccount(password)) {
                is Result.Success -> {
                    _authUiState.value = AuthUiState.Success(Unit)
                    _currentUser.value = null
                    _userData.value = null
                    _isEmailVerified.value = false
                }
                is Result.Error -> {
                    _authUiState.value = AuthUiState.Error(result.error, result.error.message)
                }
            }
            
            _currentOperation.value = AuthOperation.NONE
        }
    }
    
    /**
     * Refresh the user data from repository
     */
    fun fetchUserData() {
        if (authRepository.isLoggedIn()) {
            viewModelScope.launch {
                when (val result = authRepository.getUserData()) {
                    is Result.Success -> {
                        _userData.value = result.data
                    }
                    is Result.Error -> {
                        Timber.e(result.error.throwable, "Error fetching user data")
                    }
                }
            }
        }
    }
    
    /**
     * Check if email is verified and refresh if needed
     */
    fun refreshEmailVerificationStatus() {
        val user = authRepository.getCurrentUser()
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _isEmailVerified.value = user.isEmailVerified
            }
        }
    }
    
    /**
     * Reset auth UI state to initial
     */
    fun resetAuthState() {
        _authUiState.value = AuthUiState.Initial
        _currentOperation.value = AuthOperation.NONE
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}