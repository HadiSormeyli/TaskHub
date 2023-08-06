package com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.adapter

import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.FileType
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.data.model.Media
import android.graphics.drawable.Drawable
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.mohammadhadisormeyli.taskmanagement.R
import com.mohammadhadisormeyli.taskmanagement.databinding.ItemLayoutBinding
import getMusicCoverArt
import lastPathTitle
import size

internal class ItemVH(
    private val listener: ((Int) -> Unit)?,
    private val binding: ItemLayoutBinding,
    private val accentColor: Int,
    private val overlayAlpha: Float,
    private val limitSelectionCount: Int
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.apply {
            frameChecked.setBackgroundColor(accentColor)
            frameChecked.alpha = overlayAlpha
            card.setOnClickListener {
                listener?.invoke(bindingAdapterPosition)
            }
        }
    }

    fun bind(item: Media) = binding.apply {
        cardErrorState.isVisible = false
        frameChecked.isVisible = item.isSelected
        cardOrder.isVisible = item.isSelected && limitSelectionCount > 1
        ivChecked.isVisible = item.isSelected && limitSelectionCount == 1

        tvOrder.text = "${item.order}"
        tvPath.text = item.file.name
        tvFileSize.text = item.file.size()

        val previewImage: Any? = when (item.type) {
            FileType.AUDIO -> {
                ivMediaIcon.setImageResource(R.drawable.ic_audiotrack)
                item.file.getMusicCoverArt()
            }

            FileType.IMAGE -> {
                ivMediaIcon.setImageResource(R.drawable.ic_image)
                item.file
            }

            FileType.VIDEO -> {
                ivMediaIcon.setImageResource(R.drawable.ic_play)
                item.file
            }

            FileType.ALL -> {
                ivMediaIcon.setImageResource(R.drawable.ic_play)
                item.file
            }
        }

        Glide.with(ivImage)
            .load(previewImage)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    setErrorState(item.type)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean = false
            })
            .into(ivImage)
    }

    private fun setErrorState(type: FileType) = binding.apply {
        cardErrorState.isVisible = true
        when (type) {
            FileType.VIDEO -> Glide.with(ivErrorIcon).load(R.drawable.ic_play).into(ivErrorIcon)
            FileType.IMAGE -> Glide.with(ivErrorIcon).load(R.drawable.ic_image).into(ivErrorIcon)
            FileType.AUDIO -> Glide.with(ivErrorIcon).load(R.drawable.ic_audiotrack)
                .into(ivErrorIcon)
            FileType.ALL -> Glide.with(ivErrorIcon).load(R.drawable.ic_audiotrack)
                .into(ivErrorIcon)
        }
    }

}