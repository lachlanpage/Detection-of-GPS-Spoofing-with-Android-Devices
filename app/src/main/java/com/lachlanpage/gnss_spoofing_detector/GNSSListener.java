package com.lachlanpage.gnss_spoofing_detector;

import android.location.GnssMeasurementsEvent;
import android.location.GnssNavigationMessage;
import android.location.GnssStatus;
import android.location.Location;
import android.os.Bundle;

public interface GNSSListener {

    void onLocationChanged(Location location);
    void onGnssMeasurementsReceived(GnssMeasurementsEvent event);
    void onGnssNavigationMessageReceived(GnssNavigationMessage event);
    void onNmeaReceived(long l, String s);

}
