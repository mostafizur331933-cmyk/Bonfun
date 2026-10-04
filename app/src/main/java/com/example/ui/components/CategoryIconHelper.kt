package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {
    fun getIconForCategory(categoryId: String): ImageVector {
        return when (categoryId) {
            "cat_ac" -> Icons.Default.AcUnit
            "cat_electrician" -> Icons.Default.Bolt
            "cat_plumber" -> Icons.Default.Plumbing
            "cat_refrigerator" -> Icons.Default.Kitchen
            "cat_carpenter" -> Icons.Default.Handyman
            "cat_painter" -> Icons.Default.FormatPaint
            "cat_appliance" -> Icons.Default.Microwave
            "cat_mason" -> Icons.Default.Foundation
            "cat_cleaner" -> Icons.Default.CleaningServices
            "cat_welder" -> Icons.Default.PrecisionManufacturing
            "cat_mechanic" -> Icons.Default.Build
            "cat_construction" -> Icons.Default.Engineering
            else -> Icons.Default.HomeRepairService
        }
    }
}
