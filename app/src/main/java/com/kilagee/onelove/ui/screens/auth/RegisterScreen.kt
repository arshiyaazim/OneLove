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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
 * Registration screen for the app
 */
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val name by viewModel.name.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val acceptedTerms by viewModel.acceptedTerms.collectAsState()
    
    val isNameValid by viewModel.isNameValid.collectAsState()
    val isEmailValid by viewModel.isEmailValid.collectAsState()
    val isPasswordValid by viewModel.isPasswordValid.collectAsState()
    val isConfirmPasswordValid by viewModel.isConfirmPasswordValid.collectAsState()
    val isPhoneNumberValid by viewModel.isPhoneNumberValid.collectAsState()
    
    val snackbarHostState = LocalSnackbarHostState.current
    val coroutineScope = rememberCoroutineScope()
    
    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is RegisterEvent.NavigateToHome -> {
                    onRegisterSuccess()
                }
                is RegisterEvent.NavigateToLogin -> {
                    onNavigateToLogin()
                }
                is RegisterEvent.ValidationError -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }
            }
        }
    }
    
    // Show errors in snackbar
    LaunchedEffect(uiState) {
        if (uiState is RegisterUiState.Error) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar((uiState as RegisterUiState.Error).message)
                viewModel.clearErrors()
            }
        }
    }
    
    RegisterScreenContent(
        name = name,
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        phoneNumber = phoneNumber,
        acceptedTerms = acceptedTerms,
        isNameValid = isNameValid,
        isEmailValid = isEmailValid,
        isPasswordValid = isPasswordValid,
        isConfirmPasswordValid = isConfirmPasswordValid,
        isPhoneNumberValid = isPhoneNumberValid,
        isLoading = uiState is RegisterUiState.Loading,
        onNameChange = viewModel::updateName,
        onEmailChange = viewModel::updateEmail,
        onPasswordChange = viewModel::updatePassword,
        onConfirmPasswordChange = viewModel::updateConfirmPassword,
        onPhoneNumberChange = viewModel::updatePhoneNumber,
        onTermsChange = { viewModel.toggleTermsAcceptance() },
        onRegisterClick = { viewModel.register() },
        onGoogleRegisterClick = { /* Implement Google Sign-In flow */ },
        onFacebookRegisterClick = { /* Implement Facebook Sign-In flow */ },
        onNavigateToLogin = onNavigateToLogin
    )
}

@Composable
fun RegisterScreenContent(
    name: String,
    email: String,
    password: String,
    confirmPassword: String,
    phoneNumber: String,
    acceptedTerms: Boolean,
    isNameValid: Boolean,
    isEmailValid: Boolean,
    isPasswordValid: Boolean,
    isConfirmPasswordValid: Boolean,
    isPhoneNumberValid: Boolean,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onTermsChange: () -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleRegisterClick: () -> Unit,
    onFacebookRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        topBar = {
            TopAppBar(
                title = { Text("Create Account") },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "OneLove Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 16.dp)
                )
                
                // Registration form card
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
                        // Name field
                        OutlinedTextField(
                            value = name,
                            onValueChange = onNameChange,
                            label = { Text("Full Name") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Name"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isNameValid,
                            supportingText = {
                                if (!isNameValid) {
                                    Text("Name must be at least 2 characters")
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
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
                                    Text("Password must be at least 8 characters with a letter and number")
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Confirm Password field
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = onConfirmPasswordChange,
                            label = { Text("Confirm Password") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Confirm Password"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                    Icon(
                                        if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (showConfirmPassword) "Hide password" else "Show password"
                                    )
                                }
                            },
                            visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isConfirmPasswordValid,
                            supportingText = {
                                if (!isConfirmPasswordValid) {
                                    Text("Passwords do not match")
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Phone Number field (optional)
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = onPhoneNumberChange,
                            label = { Text("Phone Number (Optional)") },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Phone,
                                    contentDescription = "Phone Number"
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            isError = !isPhoneNumberValid,
                            supportingText = {
                                if (!isPhoneNumberValid) {
                                    Text("Please enter a valid phone number")
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                }
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Terms and conditions checkbox
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = acceptedTerms,
                                onCheckedChange = { onTermsChange() }
                            )
                            Text(
                                text = "I agree to the Terms of Service and Privacy Policy",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Register button
                        Button(
                            onClick = onRegisterClick,
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
                                    text = "Create Account",
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
                        
                        // Social registration buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Google button
                            OutlinedButton(
                                onClick = onGoogleRegisterClick,
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
                                onClick = onFacebookRegisterClick,
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
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Login link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(
                        onClick = onNavigateToLogin
                    ) {
                        Text(
                            text = "Login",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Terms and privacy policy
                Text(
                    text = "By creating an account, you agree to our Terms of Service and Privacy Policy",
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