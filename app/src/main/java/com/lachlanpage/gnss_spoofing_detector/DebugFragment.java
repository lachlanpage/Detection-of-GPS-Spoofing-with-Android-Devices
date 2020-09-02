package com.lachlanpage.gnss_spoofing_detector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.w3c.dom.Text;

public class DebugFragment extends Fragment{

    private boolean detectionStatus = false;
    private Button detectionButton;

    private TextView mLat;
    private TextView mLon;
    private GNSSDetector mDetector;

    private CheckBox mCNOCheckbox;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debug, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {    }

}
