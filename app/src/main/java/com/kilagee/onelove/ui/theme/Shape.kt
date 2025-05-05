package com.kilagee.onelove.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(20.dp)
)

// Additional custom shapes
val CardShape = RoundedCornerShape(16.dp)
val ButtonShape = RoundedCornerShape(50.dp) // Pill shape
val InputFieldShape = RoundedCornerShape(12.dp)
val ProfileImageShape = RoundedCornerShape(percent = 50) // Circle
