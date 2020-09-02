package com.lachlanpage.gnss_spoofing_detector;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssClock;
import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/* Manager Class for Various Detection Algorithms */
public class GNSSDetector implements SpoofingListener{

    private static final String TAG = "GNSSDetector";

    private Context mContext;
    private OverviewFragment mOVerviewFragment;
    private MapsFragment mMapsFragment;
    private DebugFragment mDebugFragment;

    private boolean mDetectionEnabled = false;

    private static final long LOCATION_RATE_GPS_MS = TimeUnit.SECONDS.toMillis(1L);

    private boolean mCNODetectorEnabled = false;

    private CNoDetector mCNoDetector;

    public GNSSDetector(Context context, OverviewFragment frag1, MapsFragment frag2, DebugFragment frag3)
    {
        mContext = context;
        mOVerviewFragment = frag1;
        mMapsFragment = frag2;
        mDebugFragment = frag3;

        mCNoDetector = new CNoDetector(this);

        /* Create all detectors at startup but only call the listeners for ones which are active */


        // setup listeners and associate with timer interval
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        String providerInfo = "";

        if(isGPSEnabled) {
            providerInfo = LocationManager.GPS_PROVIDER;
        }

        if(!providerInfo.isEmpty()) {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(providerInfo, LOCATION_RATE_GPS_MS, 0, mLocationListener);
                locationManager.addNmeaListener(nmeaMessageListener);
                locationManager.registerGnssMeasurementsCallback(GNSSMeasurementsEventListener);
                locationManager.registerGnssStatusCallback(GNSSStatusListener);
                locationManager.registerGnssNavigationMessageCallback(GNSSNavigationMessageListener);
            }
        }
    }


    /* START GNSS LISTENERS */
    // Location Listener
    private final LocationListener mLocationListener =
            new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    // update the text of the overview fragment
                    mOVerviewFragment.updateLatLon(location.getLatitude(), location.getLongitude());

                    if(location.getProvider().equals(LocationManager.GPS_PROVIDER))
                    {
                        String locationStream = String.format(
                                Locale.US,
                                "Fix,%s,%f,%f,%f,%f,%f,%d",
                                location.getProvider(),
                                location.getLatitude(),
                                location.getLongitude(),
                                location.getAltitude(),
                                location.getSpeed(),
                                location.getAccuracy(),
                                location.getTime());

                        Log.d(TAG, "Location Stream: " + locationStream);
                    }

                    else
                    {
                        Log.d(TAG, "New Provider: " + location.getProvider());
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

    // GNSS Measurements Listener
    private final GnssMeasurementsEvent.Callback GNSSMeasurementsEventListener =
            new GnssMeasurementsEvent.Callback() {
                @Override
                public void onGnssMeasurementsReceived(GnssMeasurementsEvent eventArgs) {
                    super.onGnssMeasurementsReceived(eventArgs);

                    if(mDetectionEnabled)
                    {
                        if(mCNODetectorEnabled) mCNoDetector.onGnssMeasurementsReceived(eventArgs);
                    }

                }

                @Override
                public void onStatusChanged(int status) {
                    super.onStatusChanged(status);
                }
            };


    // GNSS Navigation Messages
    private final GnssNavigationMessage.Callback GNSSNavigationMessageListener =
            new GnssNavigationMessage.Callback() {
                @Override
                public void onGnssNavigationMessageReceived(GnssNavigationMessage event) {
                    super.onGnssNavigationMessageReceived(event);


                    StringBuilder builder = new StringBuilder("Nav");
                    builder.append(",");
                    builder.append(event.getSvid());
                    builder.append(",");
                    builder.append(event.getType());
                    builder.append(",");

                    int status = event.getStatus();
                    builder.append(status);
                    builder.append(",");
                    builder.append(event.getMessageId());
                    builder.append(",");
                    builder.append(event.getSubmessageId());
                    byte[] data = event.getData();
                    for (byte word : data) {
                        builder.append(",");
                        builder.append(word);
                    }

                    Log.d(TAG, "GNSSNAV: " + builder);
                }

                @Override
                public void onStatusChanged(int status) {
                    super.onStatusChanged(status);
                }
            };

    //GNSS STATuS CALLBACK
    private final GnssStatus.Callback GNSSStatusListener = new GnssStatus.Callback() {
        @Override
        public void onStarted() {
            super.onStarted();
        }

        @Override
        public void onStopped() {
            super.onStopped();
        }

        @Override
        public void onFirstFix(int ttffMillis) {
            super.onFirstFix(ttffMillis);
        }

        @Override
        public void onSatelliteStatusChanged(GnssStatus status) {
            super.onSatelliteStatusChanged(status);
        }
    };

    // NMEA MESSAGE LISTENER
    private final OnNmeaMessageListener nmeaMessageListener = new OnNmeaMessageListener() {
        @Override
        public void onNmeaMessage(String message, long timestamp) {
            String nmeaStream = String.format(Locale.US, "NMEA,%s,%d", message.trim(), timestamp);
            Log.d(TAG, "NMEASTREAM: " + nmeaStream);
        }
    };


    public void stopDetection()
    {
        mDetectionEnabled = false;
    }

    public void startDetection()
    {
        mDetectionEnabled = true;
    }


    public void setCNODetection(boolean value)
    {
        mCNODetectorEnabled = value;
    }

    @Override
    public void onSpoofingDetected(boolean value) {
        // update the User Interface to alert user
        mMapsFragment.onSpoofingDetected(value);
        mOVerviewFragment.onSpoofingDetected(value);
    }
}
