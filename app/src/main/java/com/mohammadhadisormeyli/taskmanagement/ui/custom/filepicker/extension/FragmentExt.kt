import androidx.annotation.FloatRange
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.FilePicker
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.FileType
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.ListDirection
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.data.model.Media
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.extension.getStorageFiles
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.extension.hasPermission

/**
 * check has runtime permission
 *
 * @param permission permission name ex: Manifest.permission.READ_EXTERNAL_STORAGE
 * @return if has permission return true otherwise false
 */
internal fun Fragment.hasPermission(
    permission: String
): Boolean = requireContext().hasPermission(permission)

/**
 * Get storage files path
 *
 * @return list of file path, ex: /storage/0/emulated/download/image.jpg
 */
internal fun Fragment.getStorageFiles(
    fileType: FileType = FileType.IMAGE
) = requireContext().getStorageFiles(fileType = fileType)

/**
 * Show file picker
 *
 * @param title
 * @param titleTextColor
 * @param submitText
 * @param submitTextColor
 * @param accentColor
 * @param fileType
 * @param listDirection
 * @param cancellable
 * @param gridSpanCount
 * @param limitItemSelection
 * @param selectedFiles
 * @param onSubmitClickListener
 * @param onItemClickListener
 */
fun Fragment.showFilePicker(
    title: String = FilePicker.DEFAULT_TITLE,
    titleTextColor: Int = FilePicker.DEFAULT_TITLE_TEXT_COLOR,
    submitText: String = FilePicker.DEFAULT_SUBMIT_TEXT,
    submitTextColor: Int = FilePicker.DEFAULT_SUBMIT_TEXT_COLOR,
    accentColor: Int = FilePicker.DEFAULT_ACCENT_COLOR,
    fileType: FileType = FilePicker.DEFAULT_FILE_TYPE,
    listDirection: ListDirection = FilePicker.DEFAULT_LIST_DIRECTION,
    cancellable: Boolean = FilePicker.DEFAULT_CANCELABLE,
    gridSpanCount: Int = FilePicker.DEFAULT_SPAN_COUNT,
    limitItemSelection: Int = FilePicker.DEFAULT_LIMIT_COUNT,
    selectedFiles: ArrayList<Media> = arrayListOf(),
    @FloatRange(from = 0.0, to = 1.0)
    overlayAlpha: Float = FilePicker.DEFAULT_OVERLAY_ALPHA,
    onSubmitClickListener: OnSubmitClickListener? = null,
    onItemClickListener: OnItemClickListener? = null,
) {
    if (requireActivity() !is AppCompatActivity) {
        throw IllegalAccessException("Fragment host must be extend AppCompatActivity")
    }
    (requireActivity() as AppCompatActivity).showFilePicker(
        title = title,
        titleTextColor = titleTextColor,
        submitText = submitText,
        submitTextColor = submitTextColor,
        accentColor = accentColor,
        fileType = fileType,
        listDirection = listDirection,
        cancellable = cancellable,
        gridSpanCount = gridSpanCount,
        limitItemSelection = limitItemSelection,
        selectedFiles = selectedFiles,
        overlayAlpha = overlayAlpha,
        onSubmitClickListener = onSubmitClickListener,
        onItemClickListener = onItemClickListener,
    )
}