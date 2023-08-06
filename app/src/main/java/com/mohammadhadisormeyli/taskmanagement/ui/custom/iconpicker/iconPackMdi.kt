@file:JvmName("IconPackMdi")

package com.mohammadhadisormeyli.taskmanagement.ui.custom.iconpicker

import com.maltaisn.icondialog.pack.IconPackLoader
import com.mohammadhadisormeyli.taskmanagement.R
import java.util.*


fun createMaterialDesignIconPack(loader: IconPackLoader) =
    loader.load(
        R.xml.iconpack_mdi_icons, R.xml.iconpack_mdi_tags,
        listOf(Locale("en"))
    )
