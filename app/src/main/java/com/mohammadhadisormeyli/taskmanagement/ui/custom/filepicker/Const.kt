package com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker
import java.io.Serializable

enum class ListDirection : Serializable {
    LTR,
    RTL
}

enum class FileType : Serializable {
    VIDEO,
    IMAGE,
    AUDIO,
    ALL,
}

const val PAGE_SIZE = 10