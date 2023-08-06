package com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.data.model

import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.FileType
import java.io.File
import java.io.Serializable

data class Media(
    val file: File,
    val type: FileType,
    var order: Int = 0,
    var isSelected: Boolean = false,
    val id: Int = file.path.hashCode()
) : Serializable
