package com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.extension
/**
 * Is valid position in list
 *
 * @param position
 * @return
 */
internal fun List<*>.isValidPosition(position: Int): Boolean {
    return if (isNotEmpty()) position in 0 until size else position >= 0
}