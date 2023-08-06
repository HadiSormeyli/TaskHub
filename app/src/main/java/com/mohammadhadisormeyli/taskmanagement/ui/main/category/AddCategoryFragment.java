package com.mohammadhadisormeyli.taskmanagement.ui.main.category;

import android.database.sqlite.SQLiteConstraintException;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maltaisn.icondialog.data.Icon;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentAddCategoryBinding;
import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.ColorPickerDialogFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes.ColorFactory;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes.FactoryPalette;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.colorpicker.palettes.Palette;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.iconpicker.IconPickerView;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;


public class AddCategoryFragment extends BaseFragment<FragmentAddCategoryBinding, MainViewModel>
        implements ColorPickerDialogFragment.ColorDialogResultListener
        , View.OnClickListener, IconPickerView.Callback {


    private ColorPickerDialogFragment colorPickerDialogFragment;
    private ColorAdapter colorAdapter;

    public AddCategoryFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_add_category;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.addCategoryToolbar.setBackground(CommonUtils.getGradientDrawable(
                GradientDrawable.RECTANGLE
                , GradientDrawable.Orientation.TOP_BOTTOM
                , ContextCompat.getColor(activity, R.color.indigo_a_700)
                , ContextCompat.getColor(activity, R.color.indigo_a_400)
                , ContextCompat.getColor(activity, R.color.indigo_a_200)
        ));

        binding.addCategoryBackIb.setOnClickListener(v -> activity.onBackPressed());

        binding.addCategoryIb.setOnClickListener(v -> insertCategory());
        binding.addCategoryBu.setOnClickListener(v -> insertCategory());

        setUpColorsList();
    }

    private void insertCategory() {
        if (validateTitle() && validateIcon())
            viewModel.dataManager.insertCategory(new Category(
                            binding.categoryTitleEt.getText().toString()
                            , colorAdapter.getSelectedColor()
                            , binding.iconPickerView.getSelectedIcon()))
                    .subscribeOn(viewModel.getSchedulerProvider().io())
                    .observeOn(viewModel.getSchedulerProvider().ui())
                    .subscribe(
                            new CompletableObserver() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onComplete() {
                                    binding.categoryTitleEt.setError(null);
                                    activity.onBackPressed();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    if (e instanceof SQLiteConstraintException) {
                                        binding.categoryTitleEt.setError(getResources().getString(R.string.new_category));
                                    }
                                }
                            }
                    );
    }


    private boolean validateIcon() {
        if (binding.iconPickerView.getSelectedIcon() == null) {
            Toast.makeText(activity, "choose icon please", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateTitle() {
        final String title = binding.categoryTitleEt.getText().toString();
        if (TextUtils.isEmpty(title)) {
            binding.categoryTitleEt.setError("Enter title");
            return false;
        }
        return true;
    }

    private void setUpColorsList() {
        final List<Integer> colors = new ArrayList<>();
        final int[] values = new FactoryPalette("rainbow", "Rainbow", ColorFactory.RAINBOW, 16).getValues();
        for (int color : values) colors.add(color);
        binding.colorRecyclerView.setLayoutManager(new LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false));
        colorAdapter = new ColorAdapter(colors, this);
        binding.colorRecyclerView.setAdapter(colorAdapter);
    }

    @Override
    public void onClick(View view) {

        if (colorPickerDialogFragment == null) {
            colorPickerDialogFragment = new ColorPickerDialogFragment();
            ArrayList<Palette> palettes = new ArrayList<>();

            palettes.add(new FactoryPalette("rainbow", "Rainbow", ColorFactory.RAINBOW, 16));
            palettes.add(new FactoryPalette("red", "Red", ColorFactory.RED, 16));
            palettes.add(new FactoryPalette("orange", "Orange", ColorFactory.ORANGE, 16));
            palettes.add(new FactoryPalette("yellow", "Yellow", ColorFactory.YELLOW, 16));
            palettes.add(new FactoryPalette("green", "Green", ColorFactory.GREEN, 16));
            palettes.add(new FactoryPalette("cyan", "Cyan", ColorFactory.CYAN, 16));
            palettes.add(new FactoryPalette("blue", "Blue", ColorFactory.BLUE, 16));
            palettes.add(new FactoryPalette("purple", "Purple", ColorFactory.PURPLE, 16));
            palettes.add(new FactoryPalette("pink", "Pink", ColorFactory.PINK, 16));

            colorPickerDialogFragment.setPalettes(palettes.toArray(new Palette[0]));
            colorPickerDialogFragment.selectPaletteId("rainbow");
        }

        colorPickerDialogFragment.show(getChildFragmentManager(), "");
    }

    @Override
    public void onColorChanged(int color, String paletteId, String colorName, String paletteName) {
        colorPickerDialogFragment.selectPaletteId(paletteId);
        binding.colorRecyclerView.scrollToPosition(colorAdapter.addColor(color));
    }

    @Override
    public void onColorDialogCancelled() {

    }

    @Override
    public void onIconDialogIconsSelected(@NonNull Icon icon) {

    }
}