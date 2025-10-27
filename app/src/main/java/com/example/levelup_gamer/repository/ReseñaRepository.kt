package com.example.levelup_gamer.repository


import com.example.levelup_gamer.model.Reseñas
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ResenaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val resenasCollection = db.collection("resenas")

    suspend fun addResena(resena: Reseñas): Result<String> {
        return try {
            val document = resenasCollection.add(resena).await()
            Result.success(document.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getResenas(): List<Reseñas> {
        return try {
            val snapshot = resenasCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            snapshot.documents.map { document ->
                document.toObject(Reseñas::class.java)!!.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getResenasPorJuego(juego: String): List<Reseñas> {
        return try {
            val snapshot = resenasCollection
                .whereEqualTo("juego", juego)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            snapshot.documents.map { document ->
                document.toObject(Reseñas::class.java)!!.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteResena(resenaId: String): Result<Unit> {
        return try {
            resenasCollection.document(resenaId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}