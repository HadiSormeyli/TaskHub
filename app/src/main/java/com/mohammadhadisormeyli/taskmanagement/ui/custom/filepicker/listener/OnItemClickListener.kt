import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.adapter.FilePickerAdapter
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.data.model.Media

interface OnItemClickListener {
    fun onClick(media: Media, position: Int, adapter: FilePickerAdapter)
}