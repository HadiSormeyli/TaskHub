package com.mohammadhadisormeyli.taskmanagement.ui.main.home.adapter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.maltaisn.icondialog.pack.IconDrawableLoader
import com.mohammadhadisormeyli.taskmanagement.R
import com.mohammadhadisormeyli.taskmanagement.databinding.DateHeaderItemBinding
import com.mohammadhadisormeyli.taskmanagement.databinding.TaskItemBinding
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseViewHolder
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback.OnTaskClickCallBack
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils


class TaskAdapter(
    val iconDrawableLoader: IconDrawableLoader?,
    val onTaskClick: OnTaskClickCallBack,
) :
    ListAdapter<Any, BaseViewHolder<Any, ViewDataBinding>>(TaskDiffUtil()) {

    class TaskDiffUtil : DiffUtil.ItemCallback<Any>() {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return if (oldItem is SubTaskRelation && newItem is SubTaskRelation)
                oldItem.task.id == newItem.task.id
            else
                oldItem.hashCode() == newItem.hashCode()

        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }

    private lateinit var layoutInflater: LayoutInflater

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<Any, ViewDataBinding> {
        layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.date_header_item -> {
                val headerItemBinding: DateHeaderItemBinding =
                    DateHeaderItemBinding.inflate(layoutInflater, parent, false)
                HeaderViewHolder(headerItemBinding) as BaseViewHolder<Any, ViewDataBinding>
            }

            else -> {
                val taskItemBinding: TaskItemBinding =
                    TaskItemBinding.inflate(layoutInflater, parent, false)
                TaskViewHolder(taskItemBinding) as BaseViewHolder<Any, ViewDataBinding>
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is String -> R.layout.date_header_item
            else -> R.layout.task_item
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<Any, ViewDataBinding>, position: Int) {
        when (val item = getItem(position)) {
            is String -> {
                (holder as HeaderViewHolder).onBind(item)
            }

            is SubTaskRelation -> {
                (holder as TaskViewHolder).onBind(item)
            }
        }
    }

    private inner class TaskViewHolder(itemBinding: TaskItemBinding?) :
        BaseViewHolder<SubTaskRelation?, TaskItemBinding?>(itemBinding) {

        @SuppressLint("SetTextI18n")
        override fun onBind(subTaskRelation: SubTaskRelation?) {

            itemBinding?.let {
                var height = 180F
                it.subTaskRelation = subTaskRelation
                if (iconDrawableLoader != null && subTaskRelation!!.task.category != null) {
                    it.taskCategoryTv.visibility = View.VISIBLE
                    it.categoryColorView.visibility = View.VISIBLE
                    it.taskDividerView.visibility = View.VISIBLE
                    subTaskRelation.task.category?.let { category ->
                        iconDrawableLoader.loadDrawable(category.icon)
                        it.taskCategoryTv.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            category.icon.drawable,
                            null,
                            null,
                            null
                        )
                    }
                } else {
                    it.categoryColorView.visibility = View.GONE
                    it.taskDividerView.visibility =
                        if (subTaskRelation!!.task.attachments.isEmpty()) {
                            height = 130F
                            View.GONE
                        } else {
                            height = 172F
                            View.VISIBLE
                        }
                    it.taskCategoryTv.visibility = View.GONE
                }
                subTaskRelation.task.category?.let { category ->
                    it.taskCategoryTv.text = category.title
                }
                it.taskAttachmentsTv.visibility =
                    if (subTaskRelation.task.attachments.isEmpty()) View.GONE else {
                        it.taskAttachmentsTv.text =
                            subTaskRelation.task.attachments.size.toString() + " files"
                        View.VISIBLE
                    }

                it.taskProgressTv.visibility =
                    if (subTaskRelation.subTasks.size == 0) View.GONE else {
                        var progress = 0
                        for (subTask in subTaskRelation.subTasks) {
                            if (subTask.isDone) {
                                progress++
                            }
                        }
                        animateProgressTextView(
                            0,
                            ((progress.toFloat() / (subTaskRelation.subTasks.size)) * 100).toInt()
                        )
                        View.VISIBLE
                    }

                it.deleteTaskIv.setOnClickListener {
                    onTaskClick.onTaskDelete(
                        subTaskRelation
                    )
                }

                it.taskFrameLayout.setOnClickListener {
                    onTaskClick.onTaskClick(
                        subTaskRelation
                    )
                }
                val lp = it.swipeLayout.layoutParams
                lp.height = ScreenUtils.toDp(height).toInt()
                it.swipeLayout.layoutParams = lp

                val lp2 = it.deleteFrameLayout.layoutParams
                lp2.height = ScreenUtils.toDp(height).toInt()
                it.deleteFrameLayout.layoutParams = lp2

                it.executePendingBindings()
            }
        }

        @SuppressLint("SetTextI18n")
        fun animateProgressTextView(initialValue: Int, finalValue: Int) {
            val valueAnimator = ValueAnimator.ofInt(initialValue, finalValue)
            valueAnimator.duration = (((100 - finalValue) * 20) + (finalValue - 50) * 10).toLong()
            valueAnimator.addUpdateListener {
                itemBinding!!.taskProgressTv.text = it.animatedValue.toString() + " %"
            }
            valueAnimator.start()
        }
    }

    private inner class HeaderViewHolder(itemBinding: DateHeaderItemBinding) :
        BaseViewHolder<String, DateHeaderItemBinding>(itemBinding) {

        override fun onBind(date: String) {
            itemBinding.date = date
            itemBinding.executePendingBindings()
        }
    }
}