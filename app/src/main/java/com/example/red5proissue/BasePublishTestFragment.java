package com.example.red5proissue;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;

import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Camera;

public abstract class BasePublishTestFragment extends Red5LiveDetailFragment implements R5ConnectionListener {

    private static final String TAG = BasePublishTestFragment.class.getSimpleName();

    protected R5Stream publish;
    protected Camera cam;
    protected R5Camera camera;
    protected int camOrientation;

    private PublishTestListener publishTestListener;

    public BasePublishTestFragment() {

    }

    @Override
    public void onConnectionEvent(R5ConnectionEvent event) {
        Log.d(TAG, ":onConnectionEvent " + event.name());
        if (event.name() == R5ConnectionEvent.LICENSE_ERROR.name()) {
            Handler h = new Handler(Looper.getMainLooper());
            h.post(() -> showDialog("License is Invalid"));
        }
        if (event.name() == R5ConnectionEvent.START_STREAMING.name()) {
        } else if (event.name() == R5ConnectionEvent.BUFFER_FLUSH_START.name()) {
            if (publishTestListener != null) {
                publishTestListener.onPublishFlushBufferStart();
            }
        } else if (event.name() == R5ConnectionEvent.BUFFER_FLUSH_EMPTY.name() ||
                event.name() == R5ConnectionEvent.DISCONNECTED.name()) {
            if (publishTestListener != null) {
                publishTestListener.onPublishFlushBufferComplete();
                publishTestListener = null;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }

    protected Camera openFrontFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                    camOrientation = cameraInfo.orientation;
                    applyDeviceRotation();
                    break;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

        return cam;
    }

    protected Camera openBackFacingCameraGingerbread() {
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        System.out.println("Number of cameras: " + cameraCount);
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    cam = Camera.open(camIdx);
                    camOrientation = cameraInfo.orientation;
                    applyInverseDeviceRotation();
                    break;
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

        return cam;
    }

    protected void applyDeviceRotation() {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 270;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 90;
                break;
        }

        Rect screenSize = new Rect();
        getActivity().getWindowManager().getDefaultDisplay().getRectSize(screenSize);
        float screenAR = (screenSize.width() * 1.0f) / (screenSize.height() * 1.0f);
        if ((screenAR > 1 && degrees % 180 == 0) || (screenAR < 1 && degrees % 180 > 0))
            degrees += 180;

        System.out.println("Apply Device Rotation: " + rotation + ", degrees: " + degrees);

        camOrientation += degrees;

        camOrientation = camOrientation % 360;
    }

    protected void applyInverseDeviceRotation() {
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 270;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 90;
                break;
        }

        camOrientation += degrees;

        camOrientation = camOrientation % 360;
    }

    public void stopPublish(PublishTestListener listener) {
        publishTestListener = listener;
        if (publish != null) {
            publish.stop();

            if (publish.getVideoSource() != null) {
                Camera c = ((R5Camera) publish.getVideoSource()).getCamera();
                c.stopPreview();
                c.release();
            }
            publish = null;
        }

    }

    @Override
    public Boolean isPublisherTest() {
        return true;
    }

}
