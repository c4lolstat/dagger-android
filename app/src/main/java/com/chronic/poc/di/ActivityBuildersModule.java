package com.chronic.poc.di;

import com.chronic.poc.di.image.ImageModule;
import com.chronic.poc.di.image.ImageViewModelsModule;
import com.chronic.poc.image.ImageActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector(
            modules = {ImageViewModelsModule.class, ImageModule.class})
    abstract ImageActivity contributeAuthActivity();


}
