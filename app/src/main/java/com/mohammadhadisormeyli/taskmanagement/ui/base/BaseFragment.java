package com.mohammadhadisormeyli.taskmanagement.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public abstract class BaseFragment<T extends ViewDataBinding, V extends BaseViewModel> extends Fragment {

    protected V viewModel;
    protected BaseActivity<?, ?> activity;
    protected T binding;

    public abstract
    @LayoutRes
    int getLayoutId();

    public abstract void preformViewModel();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            activity = (BaseActivity<?, ?>) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        preformViewModel();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onDetach() {
        activity = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.executePendingBindings();
    }

    public void navigate(int id) {
        Navigation.findNavController(binding.getRoot()).navigate(id);
    }

    public void navigate(int id, Bundle bundle) {
        Navigation.findNavController(binding.getRoot()).navigate(id, bundle);
    }


    public void showKeyboard(View view) {
        if (activity != null)
            activity.showKeyboard(view);
    }

    public void hideKeyboard() {
        if (activity != null)
            activity.hideKeyboard();
    }
}
