package com.example.bottlesAR;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity that displays a map showing the place at the device's current location.
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;

    // The entry point to the Places API.

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;
    CoordinatorLayout cl;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 17;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private static final int RADIUS = 30;
    private static final double RANGE = .0002703;
    private String[] likelyPlaceNames;
    private String[] likelyPlaceAddresses;
    private List[] likelyPlaceAttributions;
    private LatLng[] likelyPlaceLatLngs;
    private LocationCallback locationCallback;
    private Circle circle;
    private String m_Text = "";
    private static HashMap<String, Marker> bottleMap = new HashMap<>();
    private static HashMap<Marker, String> markToIdMap = new HashMap<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);

//        // Construct a PlacesClient
//        Places.initialize(getApplicationContext(), getString(R.string.maps_api_key));
//        placesClient = Places.createClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
//                    if (circle != null) {
//                        circle.setCenter(new LatLng(location.getLatitude(), location.getLongitude()));
//                    }
                    db.collection("bottle")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        bottleMap.clear();
                                        markToIdMap.clear();
                                        map.clear();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("Main:", document.getData().toString());
                                            if(!document.getData().isEmpty()) {
                                                double latitude = (double) document.get("Latitude");
                                                double longitude = (double) document.get("Longitude");
                                                String message = (String) document.get("Message");
                                                String docId = document.getId();

                                             //   bottleMap.put(b, b);
                                                //if(bottleMap.get(docId) == null) {
                                                    Marker thisMarker = map.addMarker(new MarkerOptions()
                                                            .position(new LatLng(latitude, longitude))
                                                            .draggable(false)
                                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.bottleicon2)));
                                                    thisMarker.setTag(docId);
                                                    thisMarker.setSnippet(message);
                                                    bottleMap.put(docId, thisMarker);
                                                    markToIdMap.put(thisMarker,docId);
                                                //}

                                            }
                                        }
                                    } else {
                                        Log.w(TAG, "Error getting documents.", task.getException());
                                    }
                                }
                            });
                }
            }
        };
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
//        getLayoutInflater().inflate(R.layout.activity_maps, )
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button fab = findViewById(R.id.drop_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (locationPermissionGranted) {
                        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
                        locationTask.addOnCompleteListener(MapsActivity.this, new OnCompleteListener<Location>() {

                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                if (task.isSuccessful()) {
                                    lastKnownLocation = task.getResult();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this, R.style.BottlesTheme);
                                    builder.setTitle("Write Message");

                                    // Set up the input
                                    final EditText input = new EditText(MapsActivity.this);
                                    input.setTextColor(Color.parseColor("#FF000000")); // accent color


                                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                                    builder.setView(input);


                                    // Inflate and set the layout for the dialog
                                    // Pass null as the parent view because its going in the dialog layout
                                    LayoutInflater inflater = mapFragment.getActivity().getLayoutInflater();

                                    builder
                                            // Add action buttons
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    m_Text = input.getText().toString();
                                                    if (m_Text.length() == 0) {
                                                        Toast.makeText(MapsActivity.this, "Cannot write an empty message", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        Toast.makeText(MapsActivity.this, m_Text, Toast.LENGTH_SHORT).show();

                                                        Map<String, Object> bottle = new HashMap<>();
                                                        bottle.put("Message", m_Text);
                                                        bottle.put("Latitude", lastKnownLocation.getLatitude());
                                                        bottle.put("Longitude", lastKnownLocation.getLongitude());
                                                        db.collection("bottle")
                                                                .add(bottle);
                                                    }
                                                }
                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });

                                    builder.show();
                                } else {
                                    Toast.makeText(MapsActivity.this, "Could not get location", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(MapsActivity.this, "Could not get location", Toast.LENGTH_SHORT).show();

                    }

                }
                catch (SecurityException e) {
                    Log.e("Error-Main:", e.getMessage());
                }

            }
        });


    }


    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

//    /**
//     * Sets up the options menu.
//     * @param menu The options menu.
//     * @return Boolean.
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.current_place_menu, menu);
//        return true;
//    }
//
//    /**
//     * Handles a click on the menu option to get a place.
//     * @param item The menu item to handle.
//     * @return Boolean.
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.option_get_place) {
//            showCurrentPlace();
//        }
//        return true;
//    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setOnMarkerClickListener(this);

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
//        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
//
//            @Override
//            // Return null here, so that getInfoContents() is called next.
//            public View getInfoWindow(Marker arg0) {
//                return null;
//            }
//
//            @Override
//            public View getInfoContents(Marker marker) {
//                // Inflate the layouts for the info window, title and snippet.
//                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
//                        (FrameLayout) findViewById(R.id.map), false);
//
//                TextView title = infoWindow.findViewById(R.id.title);
//                title.setText(marker.getTitle());
//
//                TextView snippet = infoWindow.findViewById(R.id.snippet);
//                snippet.setText(marker.getSnippet());
//
//                return infoWindow;
//            }
//        });

        // Prompt the user for permission.
        getLocationPermission();
        
        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
//        LatLng sydney = new LatLng(-34, 151);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
//                                 circle = map.addCircle(new CircleOptions()
//                                        .center(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
//                                        .radius(RADIUS)
//                                        .strokeColor(Color.BLACK));
                                fusedLocationProviderClient.requestLocationUpdates(LocationRequest.create().setInterval(1000),
                                        locationCallback,
                                        Looper.getMainLooper());

                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }



    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        double mLat = marker.getPosition().latitude;
        double mLongitude = marker.getPosition().longitude;
        double currentLat = lastKnownLocation.getLatitude();
        double currentLong = lastKnownLocation.getLongitude();
        Log.i("Main_ONclick:",Double.toString(Math.abs(mLat - currentLat)));
        Log.i("Main_ONclick:",Double.toString(Math.abs(mLongitude - currentLong)));
        if(Math.abs(mLat - currentLat) < RANGE && Math.abs(mLongitude - currentLong) < RANGE ){
            AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this, R.style.BottlesTheme);
            builder.setTitle("");

            // Set up the input
            final TextView input = new TextView(MapsActivity.this);
            input.setTextColor(Color.parseColor("#FF000000"));
            input.setText(marker.getSnippet());

            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            builder.setView(input);

            String docId = markToIdMap.get(marker);
            db.collection("bottle")
                    .document(docId)
                    .delete();
            bottleMap.remove(docId);
            markToIdMap.remove(marker);
            marker.remove();

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            LayoutInflater inflater = mapFragment.getActivity().getLayoutInflater();

            builder
                    // Add action buttons
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });

            builder.show();
        }
        else{

            Toast.makeText(MapsActivity.this, "Out of range", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
