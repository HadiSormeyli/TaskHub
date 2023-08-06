package com.mohammadhadisormeyli.taskmanagement.ui.main.task;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FileItemBinding;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseViewHolder;
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback.onAddFileCallBack;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class FileAttachmentsAdapter extends RecyclerView.Adapter<FileAttachmentsAdapter.FileViewHolder> {


    private final onAddFileCallBack addFileCallBack;
    private final List<String> items;

    public FileAttachmentsAdapter(List<String> items, onAddFileCallBack addFileCallBack) {
        this.items = new ArrayList<>(items);
        this.addFileCallBack = addFileCallBack;
        if (addFileCallBack != null) {
            this.items.add(this.items.size(), "/");
        }
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final FileItemBinding fileItemBinding = FileItemBinding.inflate(
                LayoutInflater.from(parent.getContext())
                , parent
                , false);
        return new FileViewHolder(fileItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.onBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addFile(String file) {
        items.add(0, file);
        notifyDataSetChanged();
    }

    public class FileViewHolder extends BaseViewHolder<String, FileItemBinding> {

        public FileViewHolder(FileItemBinding itemBinding) {
            super(itemBinding);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBind(String file) {
            if (!file.equals("/")) {
                switch (file.substring(file.lastIndexOf("."))) {
                    case ".mp4":
                    case ".mkv":
                    case ".gif":
                    case ".jpg":
                    case ".jpeg":
                    case ".png":
                    case ".tiff":
                    case ".avi":
                    case ".mov":
                        Glide.with(itemView.getContext())
                                .load(file)
                                .error(R.drawable.ic_file_not_found)
                                .centerCrop()
                                .into(itemBinding.fileImageView);
                        break;

                    case ".mp3":
                    case ".pcm":
                    case ".wav":
                    case ".aiff":
                    case ".aac":
                    case ".ogg":
                    case ".wma":
                    case ".flac":
                    case ".alac":
                        Glide.with(itemView)
                                .load(R.drawable.ic_play)
                                .error(R.drawable.ic_file_not_found)
                                .centerCrop()
                                .into(itemBinding.fileImageView);

                    case ".pdf":
                    case ".doc":
                    case ".docx":
                    case ".htm":
                    case ".html":
                    case ".txt":
                    case ".ppt":
                    case ".pptx":
                    case ".odp":
                    default:
                        Glide.with(itemView)
                                .load(R.drawable.ic_audiotrack)
                                .error(R.drawable.ic_file_not_found)
                                .centerCrop()
                                .into(itemBinding.fileImageView);
                        break;
                }
            }

            if (FileUtils.fileExists(file)) {
                itemBinding.getRoot().setOnClickListener(view -> {
                    view.getContext().startActivity(Intent.createChooser(CommonUtils.openIntentBaseFile(file), file));
                });
            }

            if (addFileCallBack != null) {
                if (getAbsoluteAdapterPosition() == items.size() - 1) {
                    itemBinding.deleteFileIv.setVisibility(View.GONE);
                    itemBinding.fileImageView.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.ic_add));
                    itemBinding.getRoot().setOnClickListener(view -> addFileCallBack.addFile(items.size() - 1));
                } else {
                    itemBinding.deleteFileIv.setVisibility(View.VISIBLE);
                    itemBinding.deleteFileIv.setOnClickListener(view -> {
                        items.remove(getAbsoluteAdapterPosition());
                        notifyDataSetChanged();
                    });
                }
            }

            itemBinding.executePendingBindings();
        }
    }
}
