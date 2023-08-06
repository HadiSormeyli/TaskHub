package com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.data.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.FileType
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.PAGE_SIZE

class FilesRepository(
    private val application: Application
) {

    fun getFiles(
        fileType: FileType = FileType.IMAGE,
    ) = Pager(
        PagingConfig(
            pageSize = PAGE_SIZE,
            enablePlaceholders = true,
            initialLoadSize = PAGE_SIZE,
        )
    ) {
        FilesPagingSource(
            application = application,
            fileType = fileType
        )
    }.flow
}
