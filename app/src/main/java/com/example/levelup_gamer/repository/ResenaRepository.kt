package com.example.levelup_gamer.repository

import com.example.levelup_gamer.model.Resena
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ResenaRepository {

    private val db = FirebaseFirestore.getInstance()
    private val resenasCollection = db.collection("resenas")

    // Obtiene todas las reseñas de la base de datos, ordenadas por fecha
    suspend fun getAllResenas(): ResultWrapper<List<Resena>> {
        return try {
            val snapshot = resenasCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val resenas = snapshot.documents.mapNotNull { it.toObject(Resena::class.java)?.copy(id = it.id) }
            ResultWrapper.Success(resenas)
        } catch (e: Exception) {
            ResultWrapper.Error("Error al cargar las reseñas: ${e.message}")
        }
    }
    
    // Añade una nueva reseña a la base de datos
    suspend fun addResena(resena: Resena): ResultWrapper<Unit> {
        return try {
            // Firestore generará un ID automáticamente si no se especifica uno
            resenasCollection.add(resena).await()
            ResultWrapper.Success(Unit)
        } catch (e: Exception) {
            ResultWrapper.Error("Error al guardar la reseña: ${e.message}")
        }
    }
}
