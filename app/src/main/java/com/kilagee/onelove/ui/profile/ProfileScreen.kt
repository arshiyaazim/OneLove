package com.kilagee.onelove.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kilagee.onelove.R
import com.kilagee.onelove.data.model.MembershipLevel
import com.kilagee.onelove.data.model.VerificationStatus
import com.kilagee.onelove.navigation.Screen
import com.kilagee.onelove.ui.theme.Verified
import com.kilagee.onelove.ui.theme.NotVerified
import com.kilagee.onelove.ui.theme.TempApproved

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // These would come from a ViewModel in a real app
    val profileImageUrl = remember { mutableStateOf<String?>(null) }
    val fullName = remember { mutableStateOf("John Doe") }
    val username = remember { mutableStateOf("johndoe") }
    val bio = remember { mutableStateOf("Software developer based in New York. Love traveling and meeting new people.") }
    val location = remember { mutableStateOf("New York, USA") }
    val education = remember { mutableStateOf("Computer Science, NYU") }
    val job = remember { mutableStateOf("Senior Developer at Tech Co.") }
    val membershipLevel = remember { mutableStateOf(MembershipLevel.PREMIUM) }
    val verificationStatus = remember { mutableStateOf(VerificationStatus.TEMPORARILY_APPROVED) }
    val points = remember { mutableStateOf(750) }
    val interests = remember { mutableStateOf(listOf("Travel", "Photography", "Coding", "Hiking", "Music")) }
    
    val context = LocalContext.current
    
    // These would be handled by a ViewModel in a real app
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Offers Sent", "Offers Received", "Offers Declined")
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.profile)) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUrl.value != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(profileImageUrl.value)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Edit Button
                IconButton(
                    onClick = { /* Navigate to edit profile */ },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Edit Profile",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                // Verification Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    VerificationBadge(status = verificationStatus.value)
                }
                
                // Membership Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    MembershipBadge(level = membershipLevel.value)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Profile Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = fullName.value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "@${username.value}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bio
                Text(
                    text = bio.value,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Info Cards Section
                InfoCard(
                    icon = Icons.Filled.LocationOn,
                    title = "Location",
                    content = location.value
                )
                
                InfoCard(
                    icon = Icons.Filled.School,
                    title = "Education",
                    content = education.value
                )
                
                InfoCard(
                    icon = Icons.Filled.Work,
                    title = "Job",
                    content = job.value
                )
                
                InfoCard(
                    icon = Icons.Filled.Star,
                    title = "Points",
                    content = "${points.value} points"
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Interests Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Interests",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        interests.value.forEach { interest ->
                            InterestChip(interest = interest)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Offers Tabs
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Offers",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }
                    
                    // Tab Content
                    when (selectedTab) {
                        0 -> OffersList(type = "sent", emptyList = true)
                        1 -> OffersList(type = "received", emptyList = false)
                        2 -> OffersList(type = "declined", emptyList = true)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun InfoCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, content: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun InterestChip(interest: String) {
    Surface(
        modifier = Modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = interest,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun VerificationBadge(status: VerificationStatus) {
    val (color, text) = when (status) {
        VerificationStatus.FULLY_VERIFIED -> Pair(Verified, "Verified")
        VerificationStatus.TEMPORARILY_APPROVED -> Pair(TempApproved, "Temp. Verified")
        VerificationStatus.PENDING -> Pair(Color.Gray, "Pending")
        else -> Pair(NotVerified, "Not Verified")
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (status == VerificationStatus.FULLY_VERIFIED) 
                    Icons.Filled.Verified else Icons.Filled.Info,
                contentDescription = "Verification Status",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

@Composable
fun MembershipBadge(level: MembershipLevel) {
    val (color, text) = when (level) {
        MembershipLevel.VIP -> Pair(Color(0xFFD4AF37), "VIP")
        MembershipLevel.PREMIUM -> Pair(Color(0xFF9E9E9E), "Premium")
        else -> Pair(Color(0xFFCD7F32), "Basic")
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Membership Level",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

@Composable
fun OffersList(type: String, emptyList: Boolean) {
    if (emptyList) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "No Offers",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "No $type offers found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        // This is just a placeholder for demonstration
        // In a real app, this would be a LazyColumn with actual offers data
        Column(modifier = Modifier.fillMaxWidth()) {
            repeat(2) { index ->
                OfferItem(
                    type = "Coffee Date",
                    person = "Emma Thompson",
                    date = "May 10, 2025 at 3:00 PM",
                    status = "Pending"
                )
            }
        }
    }
}

@Composable
fun OfferItem(type: String, person: String, date: String, status: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* Handle click */ },
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = type,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (status) {
                        "Accepted" -> Verified
                        "Declined" -> NotVerified
                        else -> Color.Gray
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "With: $person",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = "When: $date",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}