package be.helmo.myscout.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Phase (
    @PrimaryKey
    val id: UUID,

    var name: String?,

    var description: String?,

    @NonNull
    var duration: Long,

    var notice: String?,

    @NonNull
    var favorite: Boolean
)