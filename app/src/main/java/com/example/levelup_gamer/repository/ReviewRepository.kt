package com.example.levelup_gamer.repository


import com.example.levelup_gamer.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val reviewsCollection = db.collection("reviews")

    suspend fun addReview(review: Review): Result<String> {
        return try {
            val document = reviewsCollection.add(review).await()
            Result.success(document.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReviews(productId: String? = null): List<Review> {
        return try {
            var query = reviewsCollection.orderBy("timestamp", Query.Direction.DESCENDING)

            productId?.let {
                query = query.whereEqualTo("productId", it)
            }

            val snapshot = query.get().await()
            snapshot.documents.map { document ->
                document.toObject(Review::class.java)!!.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUserReviews(userId: String): List<Review> {
        return try {
            val snapshot = reviewsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().await()
            snapshot.documents.map { document ->
                document.toObject(Review::class.java)!!.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deleteReview(reviewId: String): Result<Unit> {
        return try {
            reviewsCollection.document(reviewId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}