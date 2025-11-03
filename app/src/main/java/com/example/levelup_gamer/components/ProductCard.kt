@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.levelup_gamer.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.levelup_gamer.model.Producto
import com.example.levelup_gamer.ui.theme.*

@Composable
fun ProductCard(
    producto: Producto,
    onAgregar: () -> Unit,
    onEliminar: () -> Unit,
    cantidadEnCarrito: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var isHovered by remember { mutableStateOf(false) }

    val cardColor = if (isHovered) {
        Brush.linearGradient(colors = listOf(Color(0xFF001533), Color(0xFF003366)))
    } else {
        SolidColor(CyberBlack)
    }

    val borderColor = if (isHovered) CyberBlue else CyberBlue.copy(alpha = 0.3f)
    val shadowColor = if (isHovered) CyberBlue else CyberBlue.copy(alpha = 0.5f)
    val shadowElevation = if (isHovered) 20.dp else 8.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .onHover { isHovered = it } // <-- Efecto hover
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(16.dp),
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .clickable { onClick() }
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .background(cardColor)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = producto.imagenUrl,
                    contentDescription = producto.nombre,
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, CyberBlue, RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = producto.nombre,
                    color = CyberBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (producto.descripcion.isNotEmpty()) {
                    Text(
                        text = producto.descripcion,
                        color = CyberGray,
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = "\$${producto.precio}",
                    color = CyberBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Stock: ${producto.stock}",
                        color = CyberGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    if (cantidadEnCarrito > 0) {
                        CyberQuantityControls(
                            cantidad = cantidadEnCarrito,
                            onAdd = onAgregar,
                            onRemove = onEliminar,
                            modifier = Modifier.weight(1f, false)
                        )
                    } else {
                        CyberButton(
                            onClick = onAgregar,
                            text = "AGREGAR",
                            modifier = Modifier.weight(1f, false)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CyberQuantityControls(
    cantidad: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(36.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp), spotColor = CyberBlue, ambientColor = CyberBlue)
                .border(1.dp, CyberBlue, RoundedCornerShape(8.dp))
                .background(CyberBlack, RoundedCornerShape(8.dp))
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Eliminar", tint = CyberGreen, modifier = Modifier.size(16.dp))
        }

        Text(
            text = cantidad.toString(),
            color = CyberWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        IconButton(
            onClick = onAdd,
            modifier = Modifier
                .size(36.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp), spotColor = CyberGreen, ambientColor = CyberGreen)
                .border(1.dp, CyberGreen, RoundedCornerShape(8.dp))
                .background(CyberBlack, RoundedCornerShape(8.dp))
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar", tint = CyberGreen, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun CyberButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(36.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(10.dp),
                ambientColor = CyberGreen,
                spotColor = CyberGreen
            ),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) CyberGreen else CyberGreen.copy(alpha = 0.3f),
            contentColor = CyberBlack
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, CyberGreen),
        enabled = enabled
    ) {
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 0.5.sp)
    }
}
