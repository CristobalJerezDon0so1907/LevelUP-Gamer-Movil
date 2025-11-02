package com.example.levelup_gamer.repository

import com.example.levelup_gamer.model.Resena
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ResenaRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getResenas(): List<Resena> {
        return try {
            db.collection("resenas")
                .get()
                .await()
                .map { it.toObject(Resena::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addResena(resena: Resena): Result<Unit> {
        return try {
            db.collection("resenas")
                .add(resena)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
