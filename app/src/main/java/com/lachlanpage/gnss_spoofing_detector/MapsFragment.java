package com.lachlanpage.gnss_spoofing_detector;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback, SpoofingListener{

    MapView mMapView;
    private GoogleMap mMap;

    private RelativeLayout mSpoofingOverlay;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // inflate and return the layout
        View v = inflater.inflate(R.layout.activity_maps, null, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSpoofingOverlay = getView().findViewById(R.id.spoofingOverlay);
        mSpoofingOverlay.setVisibility(View.INVISIBLE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        //mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //mMapView.onLowMemory();
    }

    @Override
    public void onSpoofingDetected(boolean value) {
        final boolean detected = value;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(detected) mSpoofingOverlay.setVisibility(View.VISIBLE);
                else mSpoofingOverlay.setVisibility(View.INVISIBLE);
            }
        });

    }
}
