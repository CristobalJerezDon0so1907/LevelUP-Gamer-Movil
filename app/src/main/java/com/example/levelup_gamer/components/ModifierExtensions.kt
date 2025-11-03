package com.example.levelup_gamer.components

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

/**
 * Una función de extensión de Modifier que detecta cuándo el cursor del ratón
 * pasa por encima del componente.
 *
 * @param action Un lambda que se invoca con `true` si el cursor está encima y `false` en caso contrario.
 */
fun Modifier.onHover(action: (Boolean) -> Unit) = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    LaunchedEffect(isHovered) {
        action(isHovered)
    }

    this.hoverable(interactionSource = interactionSource, enabled = true)
}
