package com.chronic.poc.image;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.TextureView;

import com.chronic.poc.R;
import com.chronic.poc.service.CameraService;
import com.chronic.poc.service.StorageService;
import com.chronic.poc.viewmodels.ViewModelProviderFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Image Activity, currently this is the only activity that tha application is consist of.
 * */
public class ImageActivity extends DaggerAppCompatActivity {

    private static final String TAG = "ImageActivity";

    private ImageViewModel viewModel;

    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private TextureView textureView;
    private File galleryFolder;

    private CameraManager cameraManager;
    private SurfaceTexture surfaceTexture;
    private CameraService.CameraProperties cameraProperties;

    // @Inject tell Dagger to inject object which where configured in
    @Inject
    CameraService cameraService;

    @Inject
    StorageService storageService;

    @Inject
    ViewModelProviderFactory providerFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);

        setContentView(R.layout.activity_image);
        viewModel = ViewModelProviders.of(this, providerFactory).get(ImageViewModel.class);
        galleryFolder = storageService.createImageGallery( getResources().getString( R.string.app_name ) );
        textureView = findViewById( R.id.camera);
        surfaceTexture = textureView.getSurfaceTexture();
        findViewById(R.id.snapshot).setOnClickListener( listener -> onTakePhoneButtonClicked() );
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        surfaceTextureListener = new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                showCamareImagaOnUserInterface(surfaceTexture);
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        };

    }

    private void showCamareImagaOnUserInterface(SurfaceTexture surfaceTexture) {
        cameraProperties = cameraService.setUpCamera(cameraManager, CameraCharacteristics.LENS_FACING_BACK);
        surfaceTexture.setDefaultBufferSize(cameraProperties.getPreviewSize().getWidth(), cameraProperties.getPreviewSize().getHeight());
        cameraService.openCamera(cameraProperties.getCameraId(), backgroundHandler, surfaceTexture);
    }

    @Override
    protected void onResume() {
        super.onResume();
        openBackgroundThread();
        if (textureView.isAvailable()) {
            surfaceTexture = textureView.getSurfaceTexture();
            showCamareImagaOnUserInterface(surfaceTexture);
        } else {
            textureView.setSurfaceTextureListener(surfaceTextureListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeBackgroundThread();
    }


    private void closeBackgroundThread() {
        if (backgroundHandler != null) {
            backgroundThread.quitSafely();
            backgroundThread = null;
            backgroundHandler = null;
        }
    }

    private void openBackgroundThread() {
        backgroundThread = new HandlerThread("camera_background_thread");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    public void onTakePhoneButtonClicked() {
        FileOutputStream outputPhoto = null;
        try {
            outputPhoto = new FileOutputStream( storageService.createImageFile( galleryFolder ) );
            textureView.getBitmap()
                    .compress(Bitmap.CompressFormat.PNG, 100, outputPhoto);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputPhoto != null) {
                    outputPhoto.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
