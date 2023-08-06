package com.mohammadhadisormeyli.taskmanagement.ui.sign;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.AvatarItemBinding;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseViewHolder;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder> {

    private final AvatarClickCallBack onAvatarClickCallBack;
    private final List<String> avatars;

    public AvatarAdapter(AvatarClickCallBack onAvatarClickCallBack, List<String> avatars) {
        this.onAvatarClickCallBack = onAvatarClickCallBack;
        this.avatars = avatars;

        this.avatars.add("");
    }

    @NonNull
    @Override
    public AvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AvatarItemBinding avatarItemBinding = AvatarItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AvatarViewHolder(avatarItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AvatarViewHolder holder, int position) {
        holder.onBind(avatars.get(position));
    }

    @Override
    public int getItemCount() {
        return avatars.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(String o) {
        avatars.add(avatars.size() - 1, o);
        notifyDataSetChanged();
    }

    protected class AvatarViewHolder extends BaseViewHolder<String, AvatarItemBinding> {

        public AvatarViewHolder(AvatarItemBinding itemBinding) {
            super(itemBinding);
        }

        @Override
        public void onBind(String avatar) {
            if (!avatar.equals(""))
                Glide.with(itemBinding.avatarImageView)
                        .load(avatar)
                        .into(itemBinding.avatarImageView);

            else itemBinding.avatarImageView.setImageResource(R.drawable.ic_add);


            itemBinding.avatarRootView.setOnClickListener(v -> {
                onAvatarClickCallBack.onAvatarClick(avatar);
            });
        }
    }
}
