package com.todo.shared.data.model

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Keep
@Parcelize
data class TodoItem(
    @DocumentId
    val id: String = "",
    val text: String = "",
    val done: Boolean = false,
    val position: Int = 0,
    @ServerTimestamp
    val createdAt: Date? = null
) : Parcelable

@Keep
data class TodoList(
    val shortId: String = "",
    val title: String? = null,
    val publicRead: Boolean = true,
    val createdAt: Date? = null
)

@Keep
data class WriteKey(
    val writeKey: String = ""
)

data class CreateListResponse(
    val shortId: String = "",
    val writeKey: String = "",
    val readLink: String = "",
    val editLink: String = ""
)

data class RotateKeyResponse(
    val writeKey: String = "",
    val editLink: String = ""
)
