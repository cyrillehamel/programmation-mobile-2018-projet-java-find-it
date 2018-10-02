package ca.qc.cgmatane.informatique.findit.vue;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import ca.qc.cgmatane.informatique.findit.R;

public class Jeu extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Marker marqueurJoueur = null;
    LocationRequest mLocationRequest = new LocationRequest();
    private Marker marqueurDestination = null;

    static final public int ACTIVITE_SCORE = 1;
    protected Intent intentionNaviguerScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vue_jeu);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    double latitudeJoueur = location.getLatitude();
                    double longitudeJoueur = location.getLongitude();
                    LatLng possitionJoueur = new LatLng(latitudeJoueur, longitudeJoueur);
                    Toast.makeText(Jeu.this, "latitude" + latitudeJoueur + " longitude" + longitudeJoueur, Toast.LENGTH_LONG).show();

                    if (marqueurJoueur == null) {
                        MarkerOptions options = new MarkerOptions().position(possitionJoueur).title("position joueur").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        marqueurJoueur = mMap.addMarker(options);
                    } else {
                        marqueurJoueur.setPosition(possitionJoueur);
                    }
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(possitionJoueur));
                    // mMap.animateCamera(CameraUpdateFactory.zoomTo(16f));
                }
            }

            ;
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.drawer_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_score) {
            intentionNaviguerScore = new Intent(this, Score.class);
            startActivityForResult(intentionNaviguerScore, ACTIVITE_SCORE);
            return  true;
        }
        return super.onOptionsItemSelected(item);
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
        recupererPossitionJoueur();
        recupererPossitionDestination();
        createLocationRequest();
        startLocationUpdates();
        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    public void recupererPossitionJoueur() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mFusedLocationClient.getLastLocation().addOnSuccessListener(
                this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        double latitudeJoueur = location.getLatitude();
                        double longitudeJoueur = location.getLongitude();
                        LatLng possitionJoueur = new LatLng(latitudeJoueur, longitudeJoueur);
                        Toast.makeText(Jeu.this, "latitude" + latitudeJoueur + " longitude" + longitudeJoueur, Toast.LENGTH_LONG).show();

                        if (marqueurJoueur == null) {
                            MarkerOptions options = new MarkerOptions().position(possitionJoueur).title("position joueur").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            marqueurJoueur = mMap.addMarker(options);
                        } else {
                            marqueurJoueur.setPosition(possitionJoueur);
                        }
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(possitionJoueur));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(7f));

                    }
                });
    }

    protected void createLocationRequest() {
        mLocationRequest.setInterval(2500);
        mLocationRequest.setFastestInterval(1250);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }



    public void recupererPossitionDestination() {

        double latitudeDestination =37.422509;
        double longitudeDestination =-122.082111;
        LatLng possitionDestination = new LatLng(latitudeDestination, longitudeDestination);
        //Toast.makeText(Jeu.this, "latitude" + latitudeDestination + " longitude" + longitudeDestination, Toast.LENGTH_LONG).show();

        if (marqueurDestination == null) {
            MarkerOptions options = new MarkerOptions().position(possitionDestination).title("Destination");
            marqueurDestination = mMap.addMarker(options);
            } else {
            marqueurDestination.setPosition(possitionDestination);
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(possitionDestination));
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(14f));
    }
}




