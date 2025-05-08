package com.kilagee.onelove.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kilagee.onelove.ui.theme.OneLoveTheme
import com.kilagee.onelove.ui.theme.PremiumColor
import com.kilagee.onelove.ui.theme.StarColor
import com.kilagee.onelove.ui.theme.VerifiedBadgeColor

/**
 * Verification badge component
 */
@Composable
fun VerifiedBadge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(1.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = "Verified User",
            tint = VerifiedBadgeColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Premium badge component
 */
@Composable
fun PremiumBadge(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(PremiumColor)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Premium User",
            tint = Color.White,
            modifier = Modifier.size(12.dp)
        )
    }
}

/**
 * Rating badge component
 */
@Composable
fun RatingBadge(
    rating: Float,
    modifier: Modifier = Modifier
) {
    val color = when {
        rating >= 4.5f -> VerifiedBadgeColor
        rating >= 3.5f -> StarColor
        else -> Color.Gray
    }
    
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = color
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rating.toString(),
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Status indicator component
 */
@Composable
fun StatusIndicator(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(if (isOnline) VerifiedBadgeColor else Color.Gray)
    )
}

@Preview(showBackground = true)
@Composable
fun BadgesPreview() {
    OneLoveTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            VerifiedBadge(Modifier.size(24.dp))
        }
    }
}