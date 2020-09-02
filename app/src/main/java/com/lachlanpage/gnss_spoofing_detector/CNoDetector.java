package com.lachlanpage.gnss_spoofing_detector;

import android.location.GnssMeasurement;
import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.Location;
import android.util.Log;

public class CNoDetector implements GNSSListener, SpoofingListener {

    private static final String TAG = "C N/O DETECTOR";

    private GNSSDetector mContext;

    public CNoDetector(GNSSDetector ctxt)
    {
        mContext = ctxt;
    }

    @Override
    public void onLocationChanged(Location location)
    {
    }

    @Override
    public void onGnssMeasurementsReceived(GnssMeasurementsEvent event)
    {
        boolean isSpoofing = false;
        for(GnssMeasurement measurement : event.getMeasurements()) {
            Log.d(TAG, " " + measurement.getCn0DbHz());
            if (measurement.getCn0DbHz() >= 47.5)
            {
                onSpoofingDetected(true);
                isSpoofing = true;
            }
        }

        if(!isSpoofing) onSpoofingDetected(false);
    }

    @Override
    public void onGnssNavigationMessageReceived(GnssNavigationMessage event)
    {

    }

    @Override
    public void onNmeaReceived(long l, String s)
    {

    }

    @Override
    public void onSpoofingDetected(boolean value)
    {
        mContext.onSpoofingDetected(value);
    }
}
