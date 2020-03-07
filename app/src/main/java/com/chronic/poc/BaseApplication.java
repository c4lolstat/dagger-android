package com.chronic.poc;

import com.chronic.poc.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

/**
 * Make application a Dagger application so Dependency Injecten could be used
 * over the application, without boilerplate code.
 * Tutorial this code based on:
 * https://www.youtube.com/watch?v=3qZh6Fyrz-k&list=PLgCYzUzKIBE8AOAspC3DHoBNZIBHbIOsC
 * */
public class BaseApplication extends DaggerApplication {


    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}






