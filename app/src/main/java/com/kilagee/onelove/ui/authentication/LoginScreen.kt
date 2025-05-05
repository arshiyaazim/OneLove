package com.kilagee.onelove.ui.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kilagee.onelove.R
import com.kilagee.onelove.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.login)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Text(
                text = "OneLove",
                style = MaterialTheme.typography.headlineLarge
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Email Field
            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password Field
            OutlinedTextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Forgot Password Link
            TextButton(
                onClick = { navController.navigate(Screen.ForgotPassword.route) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = stringResource(R.string.forgot_password))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Login Button
            Button(
                onClick = {
                    // Implement login logic here
                    isLoading.value = true
                    // Simulate login process
                    // In a real app, this would call a ViewModel method
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading.value && email.value.isNotEmpty() && password.value.isNotEmpty()
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(text = stringResource(R.string.login))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Error Message
            errorMessage.value?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Register Link
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Don't have an account?")
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                    Text(text = stringResource(R.string.register))
                }
            }
        }
    }
}