package com.mohammadhadisormeyli.taskmanagement.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.ActivitySplashScreenBinding;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseActivity;
import com.mohammadhadisormeyli.taskmanagement.ui.main.MainActivity;
import com.mohammadhadisormeyli.taskmanagement.ui.sign.StartActivity;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.SignViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends BaseActivity<ActivitySplashScreenBinding, SignViewModel> {
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash_screen;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(this).get(SignViewModel.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (viewModel.dataManager.checkUser()) {
            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashScreenActivity.this, StartActivity.class));
        }
        finish();
    }
}
