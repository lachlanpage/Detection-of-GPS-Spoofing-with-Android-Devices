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

public class OverviewFragment extends Fragment implements SpoofingListener{

    private boolean detectionStatus = false;
    private Button detectionButton;

    private TextView mLat;
    private TextView mLon;
    private GNSSDetector mDetector;

    private CheckBox mCNOCheckbox;

    private TextView mSpoofingStatusTextview;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLat = getView().findViewById(R.id.latTextView);
        mLon = getView().findViewById(R.id.longTextView);

        mSpoofingStatusTextview = getView().findViewById(R.id.spoofingStatusTextView);

        detectionButton = getView().findViewById(R.id.detection_status_button);
        detectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // flip between START and STOP states of detector
                if(detectionStatus)
                {
                    // stop the detector
                    Toast.makeText(getContext(), "Detection Stopped", Toast.LENGTH_LONG).show();
                    mDetector.stopDetection();
                    detectionStatus = false;
                    detectionButton.setText(R.string.start_detection);
                }

                else
                {
                    Toast.makeText(getContext(), "Detection Started", Toast.LENGTH_LONG).show();
                    mDetector.startDetection();
                    detectionStatus = true;
                    detectionButton.setText(R.string.stop_detection);
                }
            }
        });


        mCNOCheckbox = getView().findViewById(R.id.CnoDetectorCheckbox);
        mCNOCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCNOCheckbox.isChecked()) mDetector.setCNODetection(true);
                else mDetector.setCNODetection(false);
            }
        });
    }

    public void setDetector(GNSSDetector detector)
    {
        mDetector = detector;
    }

    public void updateLatLon(double lat, double lon)
    {
        mLat.setText( String.valueOf(lat));
        mLon.setText( String.valueOf(lon));
    }


    @Override
    public void onSpoofingDetected(boolean value) {

        final boolean detected = value;

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(detected)
                {
                    mSpoofingStatusTextview.setText("Spoofing Status: DETECTED");
                    mSpoofingStatusTextview.setTextColor(getResources().getColor(R.color.spoofingDetected));
                }

                else
                {
                    mSpoofingStatusTextview.setText("Spoofing Status: SAFE");
                    mSpoofingStatusTextview.setTextColor(getResources().getColor(R.color.spoofingSafe));
                }
            }
        });
    }
}
