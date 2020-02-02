package com.chronic.poc.service;


import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import java.util.Collections;

import androidx.annotation.NonNull;

public class CameraService {

    private static final String TAG = "CameraService";

    private CameraManager cameraManager;


    public CameraProperties setUpCamera(final CameraManager cameraManager, final int cameraFacing ) {
        CameraProperties cameraProperties = null;
        this.cameraManager = cameraManager;
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics cameraCharacteristics =
                        cameraManager.getCameraCharacteristics(cameraId);
                if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) ==
                        cameraFacing) {
                    StreamConfigurationMap streamConfigurationMap = cameraCharacteristics.get(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    Size previewSize = streamConfigurationMap.getOutputSizes(SurfaceTexture.class)[0];
                    cameraProperties = new CameraProperties( cameraId, previewSize );
                    Log.d(TAG, "setUpCamera: preview size: " + previewSize);
                    Log.d(TAG, "setUpCamera: camera id: " + cameraId);
                }
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, "setUpCamera: Camera not accessable!"+ e.getLocalizedMessage(), e );
        }
        return cameraProperties;
    }

    @SuppressLint("MissingPermission")
    public void openCamera(final String cameraId, final Handler handler, SurfaceTexture surfaceTexture) {
        try {
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    createPreviewSession(cameraDevice, surfaceTexture, handler);
                }
                @Override
                public void onDisconnected(@NonNull CameraDevice camera) { camera.close(); }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) { camera.close();}
            }, handler);
        } catch (CameraAccessException e) {
            Log.e(TAG, "setUpCamera: Camera not accessable!"+ e.getLocalizedMessage(), e );
        }
    }

    private void createPreviewSession(final CameraDevice cameraDevice, final SurfaceTexture surfaceTexture, final Handler handler) {
        try{
            Surface previewSurface = new Surface(surfaceTexture);
            CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);

            cameraDevice.createCaptureSession(Collections.singletonList(previewSurface),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                            if (cameraDevice == null) {
                                return;
                            }
                            try {
                                CaptureRequest captureRequest = captureRequestBuilder.build();
                                cameraCaptureSession.setRepeatingRequest(captureRequest,
                                        null, handler);
                            } catch (CameraAccessException e) {
                                Log.e(TAG, "onConfigured: Cannot configure camera capture session: " + e.getLocalizedMessage(), e);
                            }
                        }

                        @Override
                        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                        }
                    }, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    public class CameraProperties {

        private String cameraId;
        private Size previewSize;

        public CameraProperties (final String cameraId, final Size previewSize){
            this.cameraId = cameraId;
            this.previewSize = previewSize;
        }

        public Size getPreviewSize() {
            return  previewSize;
        }

        public String getCameraId() {
            return cameraId;
        }
    }
}
