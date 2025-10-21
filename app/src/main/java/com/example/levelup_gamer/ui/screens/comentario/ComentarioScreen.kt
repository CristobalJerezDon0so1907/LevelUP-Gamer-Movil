package com.example.levelup_gamer.ui.screens.comentario

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import com.example.levelup_gamer.model.Comentario
import com.example.levelup_gamer.viewmodel.ComentarioViewModel

@Composable
fun ComentarioScreen(productId: String, viewModel: ComentarioViewModel = ComentarioViewModel()) {
    val comments by viewModel.comments.collectAsState()
    val commentText = remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequesterester() }

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
            commentText = commentText.value,
            onCommentTextChanged = { commentText.value = it },
            onAddComment = {
                if (commentText.value.isNotBlank()) {
                    viewModel.addComment(
                        productId,
                        "userIdExample", // Aquí obtendrías el ID del usuario actual
                        "Usuario Anónimo", // Nombre de usuario
                        commentText.value
                    )
                    commentText.value = ""
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
            label = { Text("Escribe un comentario...") }
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
fun CommentItem(comment: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.LightGray, RoundedCornerShape(8.dp))
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
