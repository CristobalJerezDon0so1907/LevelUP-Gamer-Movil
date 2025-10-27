package com.example.levelup_gamer.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.levelup_gamer.R

@Composable
fun RatingBar(
    rating: Float,
    onRatingChange: (Float) -> Unit = {},
    editable: Boolean = false,
    starSize: Int = 24
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            IconButton(
                onClick = { if (editable) onRatingChange(i.toFloat()) },
                enabled = editable
            ) {
                Icon(
                    painter = painterResource(
                        id = if (i <= rating) R.drawable.ic_star_filled
                        else R.drawable.ic_star_outline
                    ),
                    contentDescription = "Estrella $i",
                    tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray
                )
            }
            if (i < 5) Spacer(modifier = Modifier.width(4.dp))
        }
    }
}