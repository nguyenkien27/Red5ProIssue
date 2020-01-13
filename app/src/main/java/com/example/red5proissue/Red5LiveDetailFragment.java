package com.example.red5proissue;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

// import com.red5pro.streaming.event.R5StreamEvent;
// import com.red5pro.streaming.event.R5StreamListener;

public class Red5LiveDetailFragment extends Fragment {

    public Context context;

    public Red5LiveDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.red5_fragment_test_detail, container, false);
        return rootView;
    }

    public Boolean isPublisherTest() {
        return false;
    }

    public Boolean shouldClean() {
        return true;
    }

    public void showDialog(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
