package com.mohammadhadisormeyli.taskmanagement.ui.sign;

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
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentSignBinding;
import com.mohammadhadisormeyli.taskmanagement.model.User;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.main.MainActivity;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.FileUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.SignViewModel;
import com.permissionx.guolindev.PermissionX;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

public class SignFragment extends BaseFragment<FragmentSignBinding, SignViewModel> implements AvatarClickCallBack {


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

    @Override
    public int getLayoutId() {
        return R.layout.fragment_sign;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(SignViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.signUserBu.setOnClickListener(v -> validate());


        List<String> avatars = new ArrayList<>();
        viewModel.setUserAvatar("android.resource://" + activity.getPackageName() + "/" + R.drawable.avatar_1);
        for (int id : Arrays.asList(R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3, R.drawable.avatar_4, R.drawable.avatar_5)) {
            String resourcePath = "android.resource://" + activity.getPackageName() + "/" + id;
            avatars.add(resourcePath);
        }
        avatarAdapter = new AvatarAdapter(this, avatars);
        binding.avatarListRv.setAdapter(avatarAdapter);

        Glide.with(activity)
                .load(viewModel.getUserAvatar())
                .fitCenter()
                .into(binding.profileImageView);
    }

    private void validate() {
        binding.signProgressbar.setVisibility(View.VISIBLE);
        if (!validateFirstName() | !validateLastName()) {
            viewModel.dataManager.insertUser(new User(
                                    binding.profileFirstnameEt.getText().toString(),
                                    binding.profileLastnameEt.getText().toString(),
                                    viewModel.getUserAvatar()
                            )
                    )
                    .subscribeOn(viewModel.getSchedulerProvider().io())
                    .observeOn(viewModel.getSchedulerProvider().ui())
                    .subscribe(new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Intent intent = new Intent(activity, MainActivity.class);
                            viewModel.dataManager.insertUser();
                            startActivity(intent);
                            activity.finish();
                        }

                        @Override
                        public void onComplete() {
                            binding.signProgressbar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError(Throwable e) {
                            binding.signProgressbar.setVisibility(View.GONE);
                        }
                    });
        } else binding.signProgressbar.setVisibility(View.GONE);
    }

    private boolean validateFirstName() {
        String name = binding.profileFirstnameEt.getText().toString();
        if (!TextUtils.isEmpty(name)) {
            binding.profileFirstnameEt.setError(null);
            return false;
        }
        binding.profileFirstnameEt.setError("please enter firstname.");
        return true;
    }

    private boolean validateLastName() {
        String name = binding.profileLastnameEt.getText().toString();
        if (!TextUtils.isEmpty(name)) {
            binding.profileLastnameEt.setError(null);
            return false;
        }
        binding.profileLastnameEt.setError("please enter lastname.");
        return true;
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