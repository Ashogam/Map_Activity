package com.ashok.android.cric_grap;

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
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public double latitude = 0.0, longitude = 0.0;
    public double latA = 0.0, lngA = 0.0, latB = 0.0, lngB = 0.0;
    public String[] areas = {"Chennai", "Madurai", "Germany", "Russia", "Bangalore"};
    public String[] lanLog = {"13.0827, 80.2707", "9.9000, 78.1000", "52.5167, 13.3833", "60.0000, 90.0000", "12.9667, 77.5667"};
    EditText btnShowLocation;
    GPSTracker gps;
    String result1 = "";
    private GoogleMap mMap;
    Button btnShowLocationbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        btnShowLocation = (EditText) findViewById(R.id.btnShowLocation);
        btnShowLocationbtn= (Button) findViewById(R.id.btnShowLocationbtn);
        btnShowLocationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnShowLocation.getText().toString().length()>0) {

                    String result = btnShowLocation.getText().toString();
                    for (int i = 0; i < areas.length; i++) {
                        if (result.equalsIgnoreCase(areas[i])) {
                            result1 = lanLog[i];
                            String[] parts = result1.split(",");
                            latB =  Double.parseDouble(parts[0]);
                            lngB =  Double.parseDouble(parts[1]);

                            Log.d("String result","Result after split "+latB+"___"+lngB);

                            Location locationA = new Location("point A");

                            locationA.setLatitude(latA);
                            locationA.setLongitude(lngA);
                            Log.d("String result", "Result after split " + latA + "___" + lngA);
                            Location locationB = new Location("point B");

                            locationB.setLatitude(latB);
                            locationB.setLongitude(lngB);

                            float distance = locationA.distanceTo(locationB);

                            Toast.makeText(MapsActivity.this, "Distance is   "+(distance/1000), Toast.LENGTH_SHORT).show();

                        }
                    }
                }

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);




    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        gps = new GPSTracker(MapsActivity.this);

        // Check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            latA = gps.getLatitude();
            longitude = gps.getLongitude();
            lngA = gps.getLongitude();

            LatLng sydney = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title("My Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));


        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            Toast.makeText(getApplicationContext(), "Can't get location.", Toast.LENGTH_LONG).show();
            gps.showSettingsAlert();
        }


        // Add a marker in Sydney and move the camera

    }


    public class GPSTracker extends Service implements LocationListener {

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60; // 1 minute
        private final Context mContext;
        // Declaring a Location Manager
        protected LocationManager locationManager;
        // Flag for GPS status
        boolean isGPSEnabled = false;
        // Flag for network status
        boolean isNetworkEnabled = false;
        // Flag for GPS status
        boolean canGetLocation = false;
        Location location; // Location
        double latitude; // Latitude
        double longitude; // Longitude

        public GPSTracker(Context context) {
            this.mContext = context;
            getLocation();
        }

        public Location getLocation() {
            try {
                locationManager = (LocationManager) mContext
                        .getSystemService(LOCATION_SERVICE);

                // Getting GPS status
                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // Getting network status
                isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // No network provider is enabled
                } else {
                    this.canGetLocation = true;
                    if (isNetworkEnabled) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for Activity#requestPermissions for more details.
                                locationManager.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                                Log.d("Network", "Network");
                                if (locationManager != null) {
                                    location = locationManager
                                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    Log.d("Network ", "Network" + location);
                                    if (location != null) {
                                        Log.d("Network ", "Network" + location.getLongitude() + "  " + location.getLatitude());
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                                return null;
                            } else {
                                locationManager.requestLocationUpdates(
                                        LocationManager.NETWORK_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                                Log.d("Network else ", "Network");
                                if (locationManager != null) {

                                    location = locationManager
                                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    Log.d("Network else", "Network" + location);
                                    if (location != null) {
                                        Log.d("Network else", "Network" + location.getLongitude() + "  " + location.getLatitude());
                                        latitude = location.getLatitude();
                                        longitude = location.getLongitude();
                                    }
                                }
                            }
                        } else {
                            locationManager.requestLocationUpdates(
                                    LocationManager.NETWORK_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("Network else else ", "Network");
                            Log.d("Network", "Network");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                Log.d("Network else else", "Network" + location);
                                if (location != null) {
                                    Log.d("Network else else", "Network" + location.getLongitude() + "  " + location.getLatitude());
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }

                    }
                    // If GPS enabled, get latitude/longitude using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {

                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                Log.d("GPS Enabled", "GPS Enabled" + location);
                                if (location != null) {
                                    Log.d("GPS Enabled", "GPS Enabled" + location.getLatitude() + "  " + location.getLongitude());
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return location;
        }


        /**
         * Stop using GPS listener
         * Calling this function will stop using GPS in your app.
         */
        public void stopUsingGPS() {
            if (locationManager != null)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        locationManager.removeUpdates(this);
                        Log.d("Stop Using GPS", "GPS ");
                        return;
                    } else {
                        locationManager.removeUpdates(this);
                        Log.d("Stop Using GPS else", "GPS ");
                    }
                } else {
                    locationManager.removeUpdates(this);
                    Log.d("Stop GPS else else", "GPS ");
                }
        }


        /**
         * Function to get latitude
         */
        public double getLatitude() {
            if (location != null) {
                latitude = location.getLatitude();
            }

            // return latitude
            return latitude;
        }


        /**
         * Function to get longitude
         */
        public double getLongitude() {
            if (location != null) {
                longitude = location.getLongitude();
            }

            // return longitude
            return longitude;
        }

        /**
         * Function to check GPS/Wi-Fi enabled
         *
         * @return boolean
         */
        public boolean canGetLocation() {
            return this.canGetLocation;
        }


        /**
         * Function to show settings alert dialog.
         * On pressing the Settings button it will launch Settings Options.
         */
        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing the Settings button.
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });

            // On pressing the cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }


        @Override
        public void onLocationChanged(Location location) {
        }


        @Override
        public void onProviderDisabled(String provider) {
        }


        @Override
        public void onProviderEnabled(String provider) {
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }


        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }
    }


}
