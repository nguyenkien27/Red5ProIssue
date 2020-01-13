package com.example.red5proissue.PublishRecordedTest;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.example.red5proissue.BasePublishTestFragment;
import com.example.red5proissue.R;
import com.example.red5proissue.Red5PropertiesContent;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;
import com.red5pro.streaming.view.R5VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PublishRecordedTestFragment extends BasePublishTestFragment {

    private static final String TAG = PublishRecordedTestFragment.class.getSimpleName();

    @BindView(R.id.videoPreview)
    R5VideoView preview;

    private String streamName;
    private int currentCameraFacing;

    public PublishRecordedTestFragment() {
        // Required empty public constructor
    }

    public static PublishRecordedTestFragment newInstance() {
        PublishRecordedTestFragment fragment = new PublishRecordedTestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.red5_publish_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        publish();
    }

    private void publish() {
        String b = getActivity().getPackageName();

        //Create the configuration from the values.xml
        R5Configuration config = new R5Configuration(R5StreamProtocol.RTSP,
                Red5PropertiesContent.GetPropertyString("host"),
                Red5PropertiesContent.GetPropertyInt("port"),
                Red5PropertiesContent.GetPropertyString("context"),
                Red5PropertiesContent.GetPropertyFloat("publish_buffer_time"));
        config.setLicenseKey(Red5PropertiesContent.GetPropertyString("license_key"));
        config.setBundleID(b);

        R5Connection connection = new R5Connection(config);

        //setup a new stream using the connection
        publish = new R5Stream(connection);

        publish.audioController.sampleRate = Red5PropertiesContent.GetPropertyInt("sample_rate");

        //show all logging
        publish.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);

        //set Scale Mode
        publish.setScaleMode(0);

        if (Red5PropertiesContent.GetPropertyBool("audio_on")) {
            //attach a microphone
            attachMic();
        }

        preview.attachStream(publish);

        if (Red5PropertiesContent.GetPropertyBool("video_on")) {
            attachCamera();
        }

        preview.showDebugView(Red5PropertiesContent.GetPropertyBool("debug_view"));

        publish.setListener(this);
        if (streamName == null || streamName.equals("")) {
            publish.publish(Red5PropertiesContent.GetPropertyString("stream5"), R5Stream.RecordType.Live);
        } else {
            publish.publish(streamName, R5Stream.RecordType.Record);
        }

        if (Red5PropertiesContent.GetPropertyBool("video_on")) {
            cam.startPreview();
        }
    }

    protected void attachCamera() {

        int rotate = (currentCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) ? 180 : 0;

        cam = (currentCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) ?
                openFrontFacingCameraGingerbread() : openBackFacingCameraGingerbread();

        cam.setDisplayOrientation((camOrientation + rotate) % 360);

        camera = new R5Camera(cam, Red5PropertiesContent.GetPropertyInt("camera_width"), Red5PropertiesContent.GetPropertyInt("camera_height"));
        camera.setBitrate(Red5PropertiesContent.GetPropertyInt("bitrate"));
        camera.setOrientation(camOrientation);
        camera.setFramerate(Red5PropertiesContent.GetPropertyInt("fps"));
        publish.attachCamera(camera);
    }

    protected void attachMic() {
        R5Microphone mic = new R5Microphone();
        publish.attachMic(mic);
    }

    public void setCameraFacing() {
        R5Camera publishCam = (R5Camera) publish.getVideoSource();

        Camera newCam = null;

        //NOTE: Some devices will throw errors if you have a camera open when you attempt to open another
        publishCam.getCamera().stopPreview();
        publishCam.getCamera().release();

        //NOTE: The front facing camera needs to be 180 degrees further rotated than the back facing camera
        int rotate = 0;
        if (currentCameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            newCam = openBackFacingCameraGingerbread();
            if (newCam != null) {
                currentCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        } else {
            newCam = openFrontFacingCameraGingerbread();
            rotate = 180;
            if (newCam != null)
                currentCameraFacing = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }

        if (newCam != null) {

            newCam.setDisplayOrientation((camOrientation + rotate) % 360);

            publishCam.setCamera(newCam);
            publishCam.setOrientation(camOrientation);

            newCam.startPreview();
        }
    }
}
