package com.kilagee.onelove.ui.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kilagee.onelove.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val isLoading by viewModel.isLoading.collectAsState()
    val email by viewModel.email.collectAsState()
    
    // Collect one-time events
    LaunchedEffect(key1 = true) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(error)
        }
    }
    
    LaunchedEffect(key1 = true) {
        viewModel.resetPasswordSuccess.collectLatest {
            snackbarHostState.showSnackbar(
                message = "Password reset email sent successfully!"
            )
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.forgot_password)) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.reset_password),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = stringResource(id = R.string.password_reset_instructions),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.updateEmail(it) },
                    label = { Text(stringResource(id = R.string.email)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Reset Password Button
                Button(
                    onClick = { viewModel.resetPassword() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(
                            text = stringResource(id = R.string.reset_password),
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}
