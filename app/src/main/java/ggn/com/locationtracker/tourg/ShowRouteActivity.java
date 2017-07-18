package ggn.com.locationtracker.tourg;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import ggn.com.locationtracker.R;
import ggn.com.locationtracker.models.LocationDetails;
import ggn.com.locationtracker.utills.RealmHelperG;

public class ShowRouteActivity extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;


    int routeNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_route);


        routeNumber = getIntent().getIntExtra("route", 0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void redrawLine(List<LocationDetails> points)
    {
        if (points.size() > 0)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(points.get(0).getLattitude(), points.get(0).getLongitude()), 17.0f));
            mMap.clear();  //clears all Markers and Polylines

            mMap.addMarker(new MarkerOptions().position(new LatLng(points.get(0).getLattitude(), points.get(0).getLongitude())).title("Start location")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));


            PolylineOptions options = new PolylineOptions().width(5).color(Color.GREEN).geodesic(true);
            for (int i = 0; i < points.size(); i++)
            {
                LatLng point = new LatLng(points.get(i).getLattitude(), points.get(i).getLongitude());
                options.add(point);
            }


            mMap.addMarker(new MarkerOptions().position(new LatLng(points.get(points.size()-1).getLattitude(), points.get(points.size()-1).getLongitude())).title("End location")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));


            mMap.addPolyline(options); //add Polyline
        }
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
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
//        mMap.setMyLocationEnabled(true);

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(googleMap.getMyLocation().getLatitude(),googleMap.getMyLocation().getLongitude())));

        redrawLine(RealmHelperG.getInstance(ShowRouteActivity.this).GET_LOCATIONDETAILS(routeNumber));
    }
}
