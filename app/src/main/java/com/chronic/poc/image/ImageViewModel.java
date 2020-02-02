package com.chronic.poc.image;

import android.util.Log;

import javax.inject.Inject;

import androidx.lifecycle.ViewModel;

public class ImageViewModel extends ViewModel {

    private static final String TAG = "ImageViewModel";


    @Inject
    public ImageViewModel() {
        Log.d(TAG, "ImageViewModel: viewmodel is working...");
    }

}