package com.kilagee.onelove.ui.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kilagee.onelove.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegistrationSuccess: () -> Unit,
    viewModel: AuthViewModel
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    val isLoading by viewModel.isLoading.collectAsState()
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val firstName by viewModel.firstName.collectAsState()
    val lastName by viewModel.lastName.collectAsState()
    val username by viewModel.username.collectAsState()
    val age by viewModel.age.collectAsState()
    val country by viewModel.country.collectAsState()
    val location by viewModel.location.collectAsState()
    val gender by viewModel.gender.collectAsState()
    val ageVerification by viewModel.ageVerification.collectAsState()
    
    // Country dropdown
    var expandedCountry by remember { mutableStateOf(false) }
    val countries = listOf("United States", "Canada", "United Kingdom", "Australia", "Germany", "France", "Japan", "India", "Brazil", "Other")
    
    // Gender dropdown
    var expandedGender by remember { mutableStateOf(false) }
    val genders = listOf("Male", "Female", "Non-binary", "Prefer not to say")
    
    // Collect one-time events
    LaunchedEffect(key1 = true) {
        viewModel.error.collectLatest { error ->
            snackbarHostState.showSnackbar(error)
        }
    }
    
    LaunchedEffect(key1 = true) {
        viewModel.registerSuccess.collectLatest {
            onRegistrationSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.signup)) },
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
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Create your OneLove Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                
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
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // First Name & Last Name
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { viewModel.updateFirstName(it) },
                        label = { Text(stringResource(id = R.string.first_name)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "First Name"
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { viewModel.updateLastName(it) },
                        label = { Text(stringResource(id = R.string.last_name)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Last Name"
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = { viewModel.updateUsername(it) },
                    label = { Text(stringResource(id = R.string.username)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Username"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = { Text(stringResource(id = R.string.password)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { viewModel.updateConfirmPassword(it) },
                    label = { Text(stringResource(id = R.string.confirm_password)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirm Password"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Age
                OutlinedTextField(
                    value = if (age > 0) age.toString() else "",
                    onValueChange = { 
                        viewModel.updateAge(it.toIntOrNull() ?: 0)
                    },
                    label = { Text("Age") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Cake,
                            contentDescription = "Age"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Country
                ExposedDropdownMenuBox(
                    expanded = expandedCountry,
                    onExpandedChange = { expandedCountry = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = country,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.select_country)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = "Country"
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCountry)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedCountry,
                        onDismissRequest = { expandedCountry = false }
                    ) {
                        countries.forEach { countryOption ->
                            DropdownMenuItem(
                                text = { Text(countryOption) },
                                onClick = {
                                    viewModel.updateCountry(countryOption)
                                    expandedCountry = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Location
                OutlinedTextField(
                    value = location,
                    onValueChange = { viewModel.updateLocation(it) },
                    label = { Text(stringResource(id = R.string.enter_location)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = "Location"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Gender
                ExposedDropdownMenuBox(
                    expanded = expandedGender,
                    onExpandedChange = { expandedGender = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.select_gender)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Gender"
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedGender,
                        onDismissRequest = { expandedGender = false }
                    ) {
                        genders.forEach { genderOption ->
                            DropdownMenuItem(
                                text = { Text(genderOption) },
                                onClick = {
                                    viewModel.updateGender(genderOption)
                                    expandedGender = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Age verification
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = ageVerification,
                        onCheckedChange = { viewModel.updateAgeVerification(it) }
                    )
                    
                    Text(
                        text = stringResource(id = R.string.age_verification),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Register Button
                Button(
                    onClick = { viewModel.register() },
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
                            text = stringResource(id = R.string.signup),
                            fontSize = 18.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Already have an account
                TextButton(
                    onClick = onNavigateToLogin
                ) {
                    Text(
                        text = "Already have an account? ${stringResource(id = R.string.login)}",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
