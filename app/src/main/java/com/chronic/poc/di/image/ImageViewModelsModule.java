package com.chronic.poc.di.image;

import com.chronic.poc.di.ViewModelKey;
import com.chronic.poc.image.ImageViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ImageViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(ImageViewModel.class)
    public abstract ViewModel bindImageViewModel(ImageViewModel viewModel);
}
