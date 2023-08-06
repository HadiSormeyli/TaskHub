package com.mohammadhadisormeyli.taskmanagement.ui.main.category;

import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentCategoryBinding;
import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.main.MainActivity;
import com.mohammadhadisormeyli.taskmanagement.ui.main.category.callback.OnCategoryClick;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.MainViewModel;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryFragment extends BaseFragment<FragmentCategoryBinding, MainViewModel> implements OnCategoryClick {

    @Inject
    protected IconDrawableLoader iconDrawableLoader;

    private CategoryAdapter categoryAdapter;

    public CategoryFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_category;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.categorySearchIb.setOnClickListener(v -> {
            viewModel.getSearchFilter().setContainsTasks(false);
            navigate(R.id.action_to_searchFragment);
        });


        setUpBgView();
        setUpCategories();
    }

    private void setUpBgView() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int size = displayMetrics.widthPixels;
        LayerDrawable drawable = (LayerDrawable) ContextCompat.getDrawable(activity, R.drawable.diamond_bg);
        if (drawable != null) {
            ((GradientDrawable) ((RotateDrawable) drawable.getDrawable(0)).getDrawable()).setSize(size, size);
        }

        binding.view.getLayoutParams().width = size;
        binding.view.getLayoutParams().height = size;
        binding.view.setBackground(drawable);

        binding.categoryRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ((MainActivity) activity).scrollBottomAppBarToTop();
                }
            }
        });
    }

    private void setUpCategories() {
        categoryAdapter = new CategoryAdapter(CategoryFragment.this);
        binding.categoryRecycleView.setAdapter(categoryAdapter);
        viewModel.observeAllCategories().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.categoryProgressbar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.categoryProgressbar.setVisibility(View.GONE);
                    final ArrayList<Category> data = new ArrayList<>(resource.data);

                    final ArrayList<String> tags = new ArrayList<>();
                    tags.add("f");
                    tags.add("s");
                    Icon icon = new Icon(62485, 1, tags
                            , "M19 13H13V19H11V13H5V11H11V5H13V11H19V13Z"
                            , 24, 24, null);

                    data.add(0, new Category(activity.getResources().getString(R.string.new_category), "#FF000000", icon));

                    for (Category category : data) {
                        iconDrawableLoader.loadDrawable(category.getIcon());
                    }

                    categoryAdapter.submitList(data);
                    break;
            }
        });
    }

    @Override
    public void onCategoryClick(int position, Category category) {
        if (position == 0) {
            navigate(R.id.action_to_addCategoryFragment);
        } else {
            viewModel.setSelectedCategory(category);
            navigate(R.id.action_to_categoryTasksFragment);
        }
    }
}