package com.mohammadhadisormeyli.taskmanagement.ui.main.profile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentProfileBinding;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseFragment;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.ProfileViewModel;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.Status;

/*TODO
     5.finish and release
*/


public class ProfileFragment extends BaseFragment<FragmentProfileBinding, ProfileViewModel> {


    public ProfileFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_profile;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(ProfileViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.nightModeSc.setTrackTintList(CommonUtils.getSwitchTrackColorStateList(activity, R.color.blue_a_200));
//        binding.reminderSwitchCompact.setTrackTintList(CommonUtils.getSwitchTrackColorStateList(activity, R.color.orange_a_200));

        binding.editLinearLayout.setOnClickListener(v -> {
            new EditProfileBottomSheetFragment().show(getChildFragmentManager(), "");
        });


        binding.nightModeSc.setChecked(viewModel.dataManager.getNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
//        binding.reminderSwitchCompact.setChecked(viewModel.dataManager.getReminder());

        binding.nightModeSc.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            binding.nightModeSc.setEnabled(false);
            activity.overridePendingTransition(0, 0);
            viewModel.dataManager.setNightMode((isChecked) ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            activity.recreate();
            binding.nightModeSc.setEnabled(true);
        });

        observeUser();
    }

    private void observeUser() {
        viewModel.observeUser().observe(getViewLifecycleOwner(), user -> {
            if (user.status == Status.SUCCESS) {
                if (user.data != null) {
                    binding.userNameTextView.setText(user.data.getFirstName());
                    binding.userLastnameTextView.setText(user.data.getLastName());
                    Glide.with(activity)
                            .load(user.data.getImage())
                            .fitCenter()
                            .into(binding.profileImageView);
                }
            }
        });
    }
}