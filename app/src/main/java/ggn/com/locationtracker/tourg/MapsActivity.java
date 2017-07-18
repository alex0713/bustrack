package ggn.com.locationtracker.tourg;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Property;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.*;
import com.google.maps.android.ui.IconGenerator;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import ggn.com.locationtracker.R;
import ggn.com.locationtracker.models.Agency;
import ggn.com.locationtracker.models.LocationDetails;
import ggn.com.locationtracker.models.Route;
import ggn.com.locationtracker.models.WebServices;
import ggn.com.locationtracker.utills.RealmHelperG;
import ggn.com.locationtracker.utills.SharedPrefHelper;
import ggn.com.locationtracker.utills.UtillG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Context context;
    private Bitmap smallMarker;
    private boolean flagsOpen = false;
    private Polyline Polylinetracker;
    private List<Agency> listAgency;
    private List<Route> listActiveRoutes;

    private Route tracingRoute = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_maps);

        context = this;
        //check permission
        checkPermisstion();

        locationStarted = false;

        BitmapDrawable bitmapdraw = (BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.icon);
        Bitmap b = bitmapdraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, 10, 10, false);

        if (checkPlayServices()) {
            createLocationRequest();
            buildGoogleApiClient();
        }


        (findViewById(R.id.btnfab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtillG.transitionToActivity(MapsActivity.this, RouteListActivity.class, v, "toolbar");
            }
        });


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        } else {
            Toast.makeText(getApplicationContext(), "please allow the permission", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MapsActivity.this, "Location access not allowed..!", Toast.LENGTH_LONG).show();
            return;
        }

//        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        //create mapstype
        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(this.context, R.raw.mapstyle);
        mMap.setMapStyle(style);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                tracingRoute = null;
                if (marker.getTag() != null && marker.getTag() instanceof Route) {
                    tracingRoute = (Route) marker.getTag();
                } else if (marker.getTitle() != null && marker.getTitle().equals(getResources().getString(R.string.current_location))) {
                    showDailogConfirmLocationTracking();
                }
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.getTitle() != null && marker.getTitle().equals(getResources().getString(R.string.current_location))) {
                    showDailogConfirmLocationTracking();
                }
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                tracingRoute = null;
            }
        });


        try {
            KmlLayer layer = new KmlLayer(mMap, R.raw.test, getApplicationContext());
            layer.addLayerToMap();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        try {
            KmlLayer layerPlace = new KmlLayer(mMap, R.raw.communitygarden, getApplicationContext());
            layerPlace.addLayerToMap();
//            for (KmlContainer containers : layerPlace.getContainers()) {
//                for (KmlContainer c : containers.getContainers()) {
//                    for (KmlPlacemark p : c.getPlacemarks()) {
//                        Log.d(TAG, "p: " + p.toString());
//                        Log.d(TAG, "p: " + p.getGeometry().getGeometryObject().toString());
//                        Log.d(TAG, "p: " + p.getProperty("name"));
//                    }
//                }
//            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        loadAgency();
        loadRoutes();
    }

    @SuppressWarnings("unchecked")
    void loadAgency() {
        WebServices.fetchAllAgency(this, new WebServices.APICallback() {
            @Override
            public void onResponse(Object result, boolean success, String message) {

                if (success && result != null) {
                    listAgency = (List<Agency>) result;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Agency agency : listAgency) {
                                mMap.addMarker(new MarkerOptions()
                                        .position(agency.getLatLng())
                                        .title(agency.getTitle())
                                        .icon(BitmapDescriptorFactory.fromBitmap(agency.getIcon())));
                            }
                        }
                    });

                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    void loadRoutes() {
        WebServices.fetchAllRoutes(this, new WebServices.APICallback() {
            @Override
            public void onResponse(Object result, boolean success, String message) {
                if (success && result != null) {
                    Route.allRoutes = (List<Route>) result;

                    listActiveRoutes = Route.getActiveRoutes(new Date());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Route route : listActiveRoutes) {
                                for (final Route.BusStop bs : route.getBusstops()) {
                                    mMap.addMarker(new MarkerOptions()
                                            .position(bs.getLatlon())
                                            .title(bs.getTitle())
                                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                                }

                                mMap.addPolyline(new PolylineOptions()
                                        .addAll(route.getFullPath())
                                        .width(4)
                                        .color(route.getColor()));

                                Timer timer = new Timer();
                                timer.schedule(new MovingMarker(route), 0, 1000);
                            }
//                            Timer timer = new Timer();
//                            timer.schedule(new trackingRoutes(), 0, 1000);

                        }
                    });
                }
            }
        });
    }


    private boolean locationStarted = false;

    private void showDailogConfirmLocationTracking() {
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(MapsActivity.this);

        alertDialog2.setTitle(locationStarted ? "Stop" : "Start" + " location tracking")
                .setMessage(locationStarted ? "Stop" : "Start" + " location tracking and save route ?")
                .setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!locationStarted) {
                                    SharedPrefHelper.getInstance(MapsActivity.this).setRouteNumber(SharedPrefHelper.getInstance(MapsActivity.this).getrouteNumber() + 1);
                                }
                                if (Polylinetracker != null) {
                                    Polylinetracker.remove();
                                }
//                                markerG.remove();
//                                markerG = null;
                                locationStarted = !locationStarted;
                                if (locationStarted) {
                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(lastLocation)
                                            .zoom(18)
                                            .bearing(1.0f)
                                            .tilt(0)
                                            .build();
                                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                }
                            }
                        }
                )
                .setNegativeButton("NO", null)
                .show();
    }

//    private void redrawLine()
//    {
//        mMap.clear();  //clears all Markers and Polylines
//
//        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//        for (int i = 0; i < points.size(); i++)
//        {
//            LatLng point = points.get(i);
//            options.add(point);
//        }
//        line = mMap.addPolyline(options); //add Polyline
//    }


    //    LOCATION CHANGES UPDATES start

//    double g=0.0100;

    LatLng lastLocation;
    private Marker markerG;

    @Override
    public void onLocationChanged(Location location) {
        if (locationStarted) {
            if (lastLocation != null) {
                PolylineOptions options = new PolylineOptions().width(5).color(Color.GREEN).geodesic(true);
                options.add(lastLocation, new LatLng(location.getLatitude(), location.getLongitude()));
                Polylinetracker = mMap.addPolyline(options);
            }
            lastLocation = new LatLng(location.getLatitude(), location.getLongitude());


            LocationDetails locationDetails1 = new LocationDetails();
            locationDetails1.setLattitude(lastLocation.latitude);
            locationDetails1.setLongitude(lastLocation.longitude);
            locationDetails1.setTime_ms(System.currentTimeMillis());
            locationDetails1.setRouteid(SharedPrefHelper.getInstance(MapsActivity.this).getrouteNumber());
            RealmHelperG.getInstance(MapsActivity.this).SAVE_LOCATION(locationDetails1);
        }

        lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
        if (!flagsOpen || locationStarted) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(lastLocation)
                    .zoom(flagsOpen ? mMap.getCameraPosition().zoom : 18)
                    .bearing(1.0f)
                    .tilt(0)
                    .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            flagsOpen = true;
        }

        if (markerG != null) {
            markerG.setPosition(lastLocation);
            markerG.setSnippet(locationStarted ? "Tap to stop tracking" : "Tap to start tracking");
        } else {
            markerG = mMap.addMarker(new MarkerOptions().position(lastLocation).title("Current location"));
            markerG.setSnippet(locationStarted ? "Tap to stop tracking" : "Tap to start tracking");
            markerG.showInfoWindow();
        }

        Log.d(TAG, "new location: " + lastLocation.longitude + " " + lastLocation.latitude);
//        redrawLine(); //added
    }


//    LOCATION CHANGES UPDATES END


//    LOCATION UPDATES STUFF


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 60000;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    int UPDATE_INTERVAL = 15; // 1 sec
    int FATEST_INTERVAL = 15; // 5 sec
    int DISPLACEMENT = 1; // 2 meters

    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    /**
     * Creating location request object
     */
    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    /**
     * Method to verify google play services on the device
     */

    public boolean checkPlayServices() {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GoogleApiAvailability.getInstance().isUserResolvableError(resultCode)) {
                GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            return false;
        }
        return true;

//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)getApplicationContext(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            }
//            return false;
//        }
//        return true;
    }


    /**
     * Method to display the location on main UI
     */

  /*  public Location displayLocation()
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return null;
        }
        Location loca = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        return loca;
    }*/
    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates() {
        if (mGoogleApiClient != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onPause() {
        try {
            stopLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }


    @Override
    protected void onResume() {
        try {
            startLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        try {
            stopLocationUpdates();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    class trackingRoutes extends TimerTask {

        @Override
        public void run() {

        }
    }


    class MovingMarker extends TimerTask {
        private Route route;
        private int position = 0;
        private Marker marker;
        private int _direction = 1;

        MovingMarker(Route route) {
            this.route = route;
            Log.d(TAG, "test run: " + route.getRouteTag());
        }

        public void run() {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    final List<LatLng> list = route.getFullPath();

                    position += _direction;
                    if (position == list.size() - 1) {
                        _direction = -1;
                    } else if (position == 0) {
                        _direction = 1;
                    }
                    position = position % list.size();

                    SimpleDateFormat formatter= new SimpleDateFormat("HH:mm");
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    formatter.setTimeZone(TimeZone.getDefault());
                    String strDate = formatter.format(new Date());

                    if (marker != null) {

                        if (!route.getTmpTimeStrng().equals(strDate)) {
                            route.setTmpTimeStrng(strDate);
                            IconGenerator iconFactory = new IconGenerator(context);
                            iconFactory.setStyle((int)(Math.random() * 5 + 2));
                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(route.getRouteTag() + "\n" + route.getTmpTimeStrng())));
                        }

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                            TypeEvaluator<LatLng> typeEvaluator = new TypeEvaluator<LatLng>() {
                                @Override
                                public LatLng evaluate(float fraction, LatLng a, LatLng b) {
                                    return new LatLng((b.latitude - a.latitude) * fraction + a.latitude, (b.longitude - a.longitude) * fraction + a.longitude);
                                }
                            };
                            Property<Marker, LatLng> property = Property.of(Marker.class, LatLng.class, "position");
                            ObjectAnimator animator = ObjectAnimator.ofObject(marker, property, typeEvaluator, list.get(position));
                            animator.setDuration(1000).start();

                        } else {
                            final LatLng a = marker.getPosition();
                            ValueAnimator valueAnimator = new ValueAnimator();
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    float fraction = animation.getAnimatedFraction();
                                    LatLng b = list.get(position);
                                    LatLng newPosition = new LatLng((b.latitude - a.latitude) * fraction + a.latitude, (b.longitude - a.longitude) * fraction + a.longitude);
                                    marker.setPosition(newPosition);
                                }
                            });
                            valueAnimator.setFloatValues(0, 1);
                            valueAnimator.setDuration(1000);
                            valueAnimator.start();
                        }

                        if (tracingRoute != null && marker.getTag() == tracingRoute) {
                            CameraUpdate center= CameraUpdateFactory.newLatLng(list.get(position));
                            mMap.animateCamera(center);
                        }

                    } else {
                        IconGenerator iconFactory = new IconGenerator(context);
                        iconFactory.setStyle((int)(Math.random() * 5 + 2));
                        route.setTmpTimeStrng(strDate);
                        marker = mMap.addMarker(new MarkerOptions()
                                .position(list.get(position))
                                .title(route.getTitle())
                                .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(route.getRouteTag() + "\n" + route.getTmpTimeStrng()))));
                        marker.setTag(route);
                    }
                }
            });

        }
    }





    private void checkPermisstion() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }


}
