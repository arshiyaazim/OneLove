package com.kilagee.onelove.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp)
)

// Additional custom shapes
val BottomSheetShape = RoundedCornerShape(
    topStart = 20.dp,
    topEnd = 20.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

val CardShape = RoundedCornerShape(12.dp)
val ButtonShape = RoundedCornerShape(50.dp)  // Pill shape
val ProfileImageShape = RoundedCornerShape(percent = 50)  // Circle