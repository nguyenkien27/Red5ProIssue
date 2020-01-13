package com.example.red5proissue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.red5proissue.PublishRecordedTest.PublishRecordedTestFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Load XML TESTS
        Red5PropertiesContent.LoadTests(getResources().openRawResource(R.raw.tests));

        fragment = PublishRecordedTestFragment.newInstance();

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameView, fragment)
                    .commit();
        }
    }

    @OnClick(R.id.tvSwitchCamera)
    void changeCamera() {
        if (fragment != null && fragment instanceof PublishRecordedTestFragment) {
            ((PublishRecordedTestFragment) fragment).setCameraFacing();
        }
    }
}
