package com.mohammadhadisormeyli.taskmanagement.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date


@Entity(
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
class Task(
    @field:ColumnInfo(index = true) var categoryId: Long?,
    var category: Category?,
    var title: String,
    var description: String,
    var attachments: List<String>,
    var startDate: Date?,
    var endDate: Date?
) : java.io.Serializable, Cloneable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}



