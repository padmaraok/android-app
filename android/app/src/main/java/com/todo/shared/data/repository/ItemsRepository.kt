package com.todo.shared.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.todo.shared.data.model.TodoItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ItemsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun observeItems(shortId: String): Flow<List<TodoItem>> = callbackFlow {
        val listener = firestore
            .collection("lists")
            .document(shortId)
            .collection("items")
            .orderBy("position", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(TodoItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addItem(shortId: String, text: String, writeKey: String): Result<String> {
        return try {
            val item = mapOf(
                "text" to text,
                "done" to false,
                "position" to getNextPosition(shortId),
                "createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp(),
                "k" to writeKey // Write key for authorization, not persisted
            )

            val docRef = firestore
                .collection("lists")
                .document(shortId)
                .collection("items")
                .document()

            docRef.set(item).await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItem(shortId: String, itemId: String, text: String, done: Boolean, writeKey: String): Result<Unit> {
        return try {
            val updates = mapOf(
                "text" to text,
                "done" to done,
                "k" to writeKey // Write key for authorization, not persisted
            )

            firestore
                .collection("lists")
                .document(shortId)
                .collection("items")
                .document(itemId)
                .update(updates)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteItem(shortId: String, itemId: String, writeKey: String): Result<Unit> {
        return try {
            // Firestore security rules require write key in request.resource.data.k
            // So we update instead of delete to include the key
            val deleteData = mapOf(
                "text" to "",
                "done" to true,
                "k" to writeKey
            )

            firestore
                .collection("lists")
                .document(shortId)
                .collection("items")
                .document(itemId)
                .update(deleteData)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getNextPosition(shortId: String): Int {
        return try {
            val snapshot = firestore
                .collection("lists")
                .document(shortId)
                .collection("items")
                .orderBy("position", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            snapshot.documents.firstOrNull()
                ?.getLong("position")?.toInt()?.plus(1) ?: 0
        } catch (e: Exception) {
            0
        }
    }
}
