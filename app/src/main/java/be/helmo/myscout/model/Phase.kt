package be.helmo.myscout.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import be.helmo.myscout.view.interfaces.IMeetingPresenter
import java.util.*

@Entity
data class Phase (
    @PrimaryKey
    val id: UUID,

    var num: Int,

    var name: String?,

    var description: String?,

    @NonNull
    var duration: Long,

    var notice: String?,

    @NonNull
    var favorite: Boolean
)