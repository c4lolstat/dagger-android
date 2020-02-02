package com.chronic.poc.di.image;

import com.chronic.poc.service.CameraService;
import com.chronic.poc.service.StorageService;

import dagger.Module;
import dagger.Provides;

@Module
public class ImageModule {

    @Provides
    static CameraService provideCameraService(){
        return new CameraService();
    }

    @Provides
    static StorageService providesStorageService(){
        return new StorageService();
    }
}
