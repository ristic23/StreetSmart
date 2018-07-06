package com.jovan_ristic.streetsmart.helpers;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class GPSTracker extends Service implements LocationListener {

	private final Context context;

    public boolean isGPSEnabled() {
        return canGetGPSLoc;
    }

    public boolean isNetworkEnabled() {
        return canGetNetLoc;
    }

    boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetNetLoc = false;
    boolean canGetGPSLoc = false;

	Location locationNet;
    Location locationGPS;

	double latitudeNet;
	double longitudeNet;

    double latitudeGPS;
    double longitudeGPS;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 5000;

	protected LocationManager locationManager;

	public GPSTracker(){
	    context = null;
    }

	public GPSTracker(Context context) {
		this.context = context;
		getLocation();
	}

	public Location getLocation()
    {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled) {
                this.canGetNetLoc = false;
            } else {
                this.canGetNetLoc = true;

                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {

                        locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (locationNet != null) {
                            latitudeNet = locationNet.getLatitude();
                            longitudeNet = locationNet.getLongitude();
//                            Toast.makeText(context, "return NET", Toast.LENGTH_SHORT).show();

                            writeLatLong(latitudeNet, longitudeNet);
                        }
                    }
            }

            if (!isGPSEnabled) {
                this.canGetGPSLoc = false;
            } else
                {

                    this.canGetGPSLoc = true;
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        if (locationGPS != null) {
                            latitudeGPS = locationGPS.getLatitude();
                            longitudeGPS = locationGPS.getLongitude();

//                                Toast.makeText(context, "return GPS", Toast.LENGTH_SHORT).show();
                                writeLatLong(latitudeGPS, longitudeGPS);
                                return locationGPS;
                            }
                        }
                }

        }

        return locationNet;
    }

	
	
	public void stopUsingGPS() {
		if(locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	
	public boolean canGetLocation() {
		return this.canGetNetLoc;
	}
	
	public void showGPSSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
		
		alertDialog.setTitle("GPS is settings");
		
		alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
		
		alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				context.startActivity(intent);
			}
		});
		
		alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

        Toast.makeText(context, "showGPSSettingsAlert",
                Toast.LENGTH_SHORT).show();

		alertDialog.show();
	}

    public void showWifiSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("Wifi is settings");

        alertDialog.setMessage("Wifi is not enabled. Do you want to go to settings menu?");

        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                context.startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        Toast.makeText(context, "ShowWifiSettingAlert",
                Toast.LENGTH_SHORT).show();

        alertDialog.show();
    }
	
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

        writeLatLong(arg0.getLatitude(), arg0.getLongitude());

		Toast.makeText(context, "Latitude="+ AppManager.getInstance().getLatitude()
				+ "\nLongitude=" + AppManager.getInstance().getLongitude()
				+ "\nAccuracy=" + arg0.getAccuracy(),
				Toast.LENGTH_SHORT).show();
		if(AppManager.getInstance().isGoogleMapActive())
			AppManager.getInstance().setMyLocationMarker();


	}

	public void writeLatLong(double lat, double lon)
    {
        AppManager.getInstance().setLatitude(lat);
        AppManager.getInstance().setLongitude(lon);

    }

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
        Toast.makeText(context, "Provider disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
