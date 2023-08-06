package com.mohammadhadisormeyli.taskmanagement.ui.main.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentEditProfileBinding;
import com.mohammadhadisormeyli.taskmanagement.model.User;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseBottomSheetFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.sign.AvatarAdapter;
import com.mohammadhadisormeyli.taskmanagement.ui.sign.AvatarClickCallBack;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.FileUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.ProfileViewModel;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.Status;
import com.permissionx.guolindev.PermissionX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditProfileBottomSheetFragment extends BaseBottomSheetFragment<FragmentEditProfileBinding, ProfileViewModel> implements AvatarClickCallBack {

    private AvatarAdapter avatarAdapter;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                        String path = FileUtils.getPath(activity, uri);
                        if (path != null) {
                            if (avatarAdapter != null) {
                                avatarAdapter.addItem(path);
                                binding.avatarListRv.smoothScrollToPosition(avatarAdapter.getItemCount() - 1);
                                viewModel.setUserAvatar(path);
                            }

                            Glide.with(activity)
                                    .load(path)
                                    .fitCenter()
                                    .into(binding.profileImageView);
                        }
                    }
                }
            });


    public EditProfileBottomSheetFragment() {
    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_edit_profile;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(ProfileViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        observeUser();

        List<String> avatars = new ArrayList<>();
        viewModel.setUserAvatar("android.resource://" + activity.getPackageName() + "/" + R.drawable.avatar_1);
        for (int id : Arrays.asList(R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3, R.drawable.avatar_4, R.drawable.avatar_5)) {
            String resourcePath = "android.resource://" + activity.getPackageName() + "/" + id;
            avatars.add(resourcePath);
        }
        avatarAdapter = new AvatarAdapter(this, avatars);
        binding.avatarListRv.setAdapter(avatarAdapter);

        binding.editProfileBackIb.setOnClickListener(v -> dismiss());

        binding.editProfileCheckIb.setOnClickListener(v -> editUserInfo());

        binding.editUserBu.setOnClickListener(v -> editUserInfo());
    }

    private void editUserInfo() {
        if (validateFirstname() | validateLastname()) {
            viewModel.updateUser(
                    new User(
                            binding.profileFirstnameEt.getText().toString(),
                            binding.profileLastnameEt.getText().toString(),
                            viewModel.getUserAvatar()
                    )
            );
            dismiss();
        }
    }

    private boolean validateLastname() {
        String lastname = binding.profileLastnameEt.getText().toString();

        if (TextUtils.isEmpty(lastname)) {
            binding.profileLastnameEt.setError("please enter lastname.");
            return false;
        }
        binding.profileLastnameEt.setError(null);
        return true;
    }

    private boolean validateFirstname() {
        String firstname = binding.profileFirstnameEt.getText().toString();

        if (TextUtils.isEmpty(firstname)) {
            binding.profileFirstnameEt.setError("please enter firstname.");
            return false;
        }
        binding.profileFirstnameEt.setError(null);
        return true;
    }


    private void observeUser() {
        viewModel.observeUser().observe(getViewLifecycleOwner(), user -> {
            if (user.status == Status.SUCCESS) {
                binding.profileFirstnameEt.setText(user.data.getFirstName());
                binding.profileLastnameEt.setText(user.data.getLastName());
                viewModel.setUserAvatar(user.data.getImage());
                Glide.with(activity)
                        .load(user.data.getImage())
                        .fitCenter()
                        .into(binding.profileImageView);
            }
        });
    }

    @Override
    public void onAvatarClick(String avatar) {
        if (!avatar.equals("")) {
            viewModel.setUserAvatar(avatar);
            Glide.with(activity)
                    .load(avatar)
                    .fitCenter()
                    .into(binding.profileImageView);
        } else {
            PermissionX.init(activity)
                    .permissions(CommonUtils.getPermissionList())
                    .request((allGranted, grantedList, deniedList) -> {
                                if (allGranted) {
                                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    intent.setType("image/*");
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    activityResultLauncher.launch(Intent.createChooser(intent, "Select a Image for your profile"));
                                }
                            }
                    );
        }
    }
}