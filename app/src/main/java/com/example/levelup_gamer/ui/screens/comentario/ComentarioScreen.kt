package com.example.levelup_gamer.ui.screens.comentario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // 👈 IMPORTAR ESTO
import androidx.compose.ui.focus.FocusRequester

@Composable
fun ComentarioScreen(
    productId: String,
    viewModel: com.example.levelup_gamer.viewmodel.ComentarioViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // 👈 VALOR POR DEFECTO
) {
    val comments by viewModel.comments.collectAsState() // 👈 ESTO DEBERÍA FUNCIONAR AHORA

    var commentText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Cargar comentarios al iniciar la pantalla
    LaunchedEffect(productId) {
        viewModel.fetchComments(productId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(comments) { comment ->
                CommentItem(comment)
            }
        }

        CommentInput(
            commentText = commentText,
            onCommentTextChanged = { commentText = it },
            onAddComment = {
                if (commentText.isNotBlank()) {
                    viewModel.addComment(
                        productId,
                        "userIdExample", // Aquí obtendrías el ID del usuario actual
                        "Usuario Anónimo", // Nombre de usuario
                        commentText
                    )
                    commentText = ""
                }
            }
        )
    }
}

@Composable
fun CommentInput(
    commentText: String,
    onCommentTextChanged: (String) -> Unit,
    onAddComment: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = commentText,
            onValueChange = onCommentTextChanged,
            modifier = Modifier.weight(1f),
            label = { Text("Escribe un comentario...") },
            placeholder = { Text("Escribe tu comentario...") } // 👈 AGREGADO
        )
        IconButton(
            onClick = onAddComment,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Enviar comentario"
            )
        }
    }
}

@Composable
fun CommentItem(comment: com.example.levelup_gamer.model.Comentario) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp)) // 👈 MEJORADO
            .padding(16.dp)
    ) {
        Text(
            text = comment.author,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = comment.text,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}