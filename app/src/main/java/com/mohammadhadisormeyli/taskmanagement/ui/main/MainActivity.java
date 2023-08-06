package com.mohammadhadisormeyli.taskmanagement.ui.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.RelativeCornerSize;
import com.google.android.material.shape.RoundedCornerTreatment;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.ActivityMainBinding;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseActivity;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.MainViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends BaseActivity<ActivityMainBinding, MainViewModel> {

    private NavHostFragment navHostFragment;
    private NavController navController;

    @Override
    public int getBindingVariable() {
        return -1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MaterialShapeDrawable bottomBarBackground = (MaterialShapeDrawable) binding.bottomAppbar.getBackground();
        bottomBarBackground.setShapeAppearanceModel(
                bottomBarBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCorners(new RoundedCornerTreatment())
                        .setAllCornerSizes(new RelativeCornerSize(0.25f))
                        .build());

        navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);


        navController = navHostFragment.getNavController();

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            switch (navDestination.getId()) {
                case R.id.addCategoryFragment:
                case R.id.categoryTasksFragment:
                case R.id.taskDetailsFragment:
                case R.id.addTaskFragment:
                case R.id.searchFragment:
                    hideBottomAppbar();
                    break;
            }
        });

        if (navHostFragment != null) {
            NavigationUI.setupWithNavController(binding.bottomNavigationView, navHostFragment.getNavController());
        }

        binding.menuActionButton.setClosedOnTouchOutside(true);
        binding.categoryFloatingButton.setOnClickListener(view -> navigate(R.id.action_to_addCategoryFragment));
        binding.taskFloatingButton.setOnClickListener(view -> {
            viewModel.resetAddingTask();
            navigate(R.id.action_to_addTaskFragment);
        });
    }

    public void hideBottomAppbar() {
        if (binding.bottomAppbar.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    binding.menuActionButton.close(false);
                    binding.fakeFabButton.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.menuActionButton.setVisibility(View.GONE);
                    binding.bottomAppbar.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            binding.bottomAppbar.startAnimation(animation);
        }
    }

    public void showBottomAppbar() {
        binding.menuActionButton.setVisibility(View.VISIBLE);
        binding.bottomAppbar.setVisibility(View.VISIBLE);
        binding.fakeFabButton.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        binding.menuActionButton.startAnimation(animation);
        binding.bottomAppbar.startAnimation(animation);
        binding.fakeFabButton.startAnimation(animation);
    }

    public void scrollBottomAppBarToTop() {
        if (binding.bottomAppbar.getTranslationY() > 0) {
            binding.bottomAppbar.animate().setDuration(400).translationY(0);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onBackPressed() {
        if (binding.menuActionButton.isOpened()) {
            binding.menuActionButton.toggle(true);
            return;
        }
        super.onBackPressed();

        int backStackEntryCount = navHostFragment.getChildFragmentManager().getBackStackEntryCount();

        if (backStackEntryCount > 0) {
            int id = navController.getCurrentDestination().getId();

            switch (id) {
                case R.id.homeFragment:
                case R.id.categoryFragment:
                case R.id.profileFragment:
                    showBottomAppbar();
                    viewModel.getSearchFilter().reset();
                    break;
            }
        }
    }
}