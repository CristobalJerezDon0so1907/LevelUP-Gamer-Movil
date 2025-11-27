package com.example.levelup_gamer.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

fun subirFotoAdmin(uri: Uri, onResult: (String?) -> Unit) {
    val storage = FirebaseStorage.getInstance()
    val db = FirebaseFirestore.getInstance()

    val ref = storage.reference.child("admin/perfil.jpg")

    ref.putFile(uri)
        .addOnSuccessListener {
            ref.downloadUrl
                .addOnSuccessListener { downloadUri ->
                    val url = downloadUri.toString()

                    db.collection("admin")
                        .document("perfil")
                        .set(mapOf("fotoUrl" to url), SetOptions.merge())
                        .addOnSuccessListener { onResult(url) }
                        .addOnFailureListener { onResult(null) }
                }
                .addOnFailureListener { onResult(null) }
        }
        .addOnFailureListener { onResult(null) }
}

