@file:JvmName("IconPackFa")

package com.mohammadhadisormeyli.taskmanagement.ui.custom.iconpicker

import com.maltaisn.icondialog.pack.IconPackLoader
import com.mohammadhadisormeyli.taskmanagement.R
import java.util.*


fun createFontAwesomeIconPack(loader: IconPackLoader) =
    loader.load(
        R.xml.iconpack_fa_icons, R.xml.iconpack_fa_tags,
        listOf(Locale("en"))
    )
