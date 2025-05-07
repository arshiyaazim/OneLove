package com.kilagee.onelove.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kilagee.onelove.R
import com.kilagee.onelove.ui.LocalSnackbarHostState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Login screen for the app
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()
    val isPasswordValid by viewModel.isPasswordValid.collectAsState()
    
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()
    
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> {
                    onLoginSuccess()
                }
                is LoginEvent.NavigateToRegister -> {
                    onNavigateToRegister()
                }
                is LoginEvent.NavigateToForgotPassword -> {
                    onNavigateToForgotPassword()
                }
                is LoginEvent.ValidationError -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }
    
    // Show errors in snackbar
    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Error) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar((uiState as LoginUiState.Error).message)
                viewModel.clearErrors()
            }
        }
    }
    
    LoginScreenContent(
        email = email,
        password = password,
        rememberMe = rememberMe,
        isEmailValid = isEmailValid,
        isPasswordValid = isPasswordValid,
        isLoading = uiState is LoginUiState.Loading,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onRememberMeChange = { viewModel.toggleRememberMe() },
        onLoginClick = { viewModel.login() },
        onGoogleLoginClick = { /* Implement Google Sign-In flow */ },
        onFacebookLoginClick = { /* Implement Facebook Sign-In flow */ },
        onGuestLoginClick = { viewModel.loginAsGuest() },
        onRegisterClick = { onNavigateToRegister() },
        onForgotPasswordClick = { onNavigateToForgotPassword() }
    )
}

@Composable
fun LoginScreenContent(
    email: String,
    password: String,
    rememberMe: Boolean,
    isEmailValid: Boolean,
    isPasswordValid: Boolean,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRememberMeChange: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onFacebookLoginClick: () -> Unit,
    onGuestLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var showPassword by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        snackbarHost = { SnackbarHost(LocalSnackbarHostState.current) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "OneLove Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 16.dp)
                )
                
                // App Name
                Text(
                    text = "OneLove",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Tagline
                Text(
                    text = "Find your perfect match",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Login Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 4.dp
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Email field
                        OutlinedTextField(
                            value = email,
                            onValueChange = onEmailChange,
                            label = { Text("Email") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Email,
                                    contentDescription = "Email"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isEmailValid,
                            supportingText = {
                                if (!isEmailValid) {
                                    Text("Please enter a valid email address")
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Password field
                        OutlinedTextField(
                            value = password,
                            onValueChange = onPasswordChange,
                            label = { Text("Password") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Password"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (showPassword) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isPasswordValid,
                            supportingText = {
                                if (!isPasswordValid) {
                                    Text("Password must be at least 6 characters")
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    onLoginClick()
                                }
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Remember me and Forgot password
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = rememberMe,
                                    onCheckedChange = { onRememberMeChange() }
                                )
                                Text(
                                    text = "Remember me",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            
                            TextButton(
                                onClick = onForgotPasswordClick
                            ) {
                                Text(
                                    text = "Forgot password?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Login button
                        Button(
                            onClick = onLoginClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = !isLoading,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Login",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Divider with "OR" text
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            Text(
                                text = "OR",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Divider(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Social login buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Google button
                            OutlinedButton(
                                onClick = onGoogleLoginClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFEA4335)
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_google),
                                    contentDescription = "Google",
                                    tint = Color(0xFFEA4335)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Google")
                            }
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            // Facebook button
                            OutlinedButton(
                                onClick = onFacebookLoginClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF1877F2)
                                )
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_facebook),
                                    contentDescription = "Facebook",
                                    tint = Color(0xFF1877F2)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Facebook")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Continue as guest button
                        TextButton(
                            onClick = onGuestLoginClick,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Continue as guest",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Register link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don't have an account?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(
                        onClick = onRegisterClick
                    ) {
                        Text(
                            text = "Register",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Terms and privacy policy
                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}