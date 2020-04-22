package com.example.mylocationproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Location extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {

    private Toolbar toolbar;
    Intent intent;

    GoogleMap googleMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    public android.location.Location firstLocation;

    private LocationManager locationManager;

    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;

    List<Marker> markers = new ArrayList<Marker>();
    Marker myMarker = null;

    double firstLatitude;
    double firstLongitude;
    double lastLatitude;
    double lastLongitude;
    LatLng firstLatLng;
    LatLng lastLatLng;

    String photoUrl;
    Bitmap btm;

    Dialog dialog;
    Dialog distanceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        toolbar = (Toolbar) findViewById(R.id.tbLocation);
        toolbar.setTitle("Location Tracking");
        setSupportActionBar(toolbar);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
        handleResult();

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        checkProviderEnabled(locationManager);

    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {


                        dialog.dismiss();
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(callGPSSettingIntent,111);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

            final AlertDialog alert = builder.create();
        if (!isFinishing()) {
            alert.show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (resultCode == 111) {
            switch (requestCode) {
                case 1:
                    break;
            }
        }
    }


    private void checkProviderEnabled(LocationManager locationManager) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000,0,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(android.location.Location location) {

                            if(firstLatitude==0.0 && firstLongitude==0.0){
                                firstLatitude = location.getLatitude();
                                firstLongitude = location.getLongitude();
                                firstLatLng = new LatLng(firstLatitude,firstLongitude);
                                 getMyLocation(googleMap,firstLatitude,firstLongitude,firstLatLng);
                            }else {
                                lastLatitude = location.getLatitude();
                                lastLongitude = location.getLongitude();
                                lastLatLng = new LatLng(lastLatitude, lastLongitude);
                                getMyLocation (googleMap,lastLatitude, lastLongitude,lastLatLng);
                            }

                            if(firstLatLng!=null&&lastLatLng!=null){
                                distanceBetween(firstLatLng,lastLatLng);
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
                    });
        } else if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
                @Override
                public void onLocationChanged(android.location.Location location) {

                    if(firstLatitude==0.0 && firstLongitude==0.0){
                        firstLatitude = location.getLatitude();
                        firstLongitude = location.getLongitude();
                        firstLatLng = new LatLng(firstLatitude,firstLongitude);
                        getMyLocation(googleMap,firstLatitude,firstLongitude,firstLatLng);
                    }else {
                        lastLatitude = location.getLatitude();
                        lastLongitude = location.getLongitude();
                        lastLatLng = new LatLng(lastLatitude, lastLongitude);
                        getMyLocation (googleMap,lastLatitude, lastLongitude,lastLatLng);
                    }

                    if(firstLatLng!=null&&lastLatLng!=null){
                        distanceBetween(firstLatLng,lastLatLng);
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
            });
        }
    }

    private void distanceBetween(LatLng firstLatLng, LatLng lastLatLng) {

        Settings s = new Settings();

        android.location.Location startPoint= new android.location.Location("start");
        startPoint.setLatitude(firstLatLng.latitude);
        startPoint.setLongitude(firstLatLng.longitude);
        android.location.Location lastPoint= new android.location.Location("last");
        lastPoint.setLatitude(lastLatLng.latitude);
        lastPoint.setLongitude(lastLatLng.longitude);

        double distance = startPoint.distanceTo(lastPoint);
        double changedDistance;
        double warningDistance = 5.0;

        if(getIntent().getStringExtra("key")!=null) {
            String strDistance = getIntent().getStringExtra("key");
            if(strDistance!=null){
                changedDistance = Double.parseDouble(strDistance);
                if (changedDistance!=0.0) {
                    if (distance >= changedDistance) {
                        String str = "warning";
                        openDialog(str);
                    }
                }
            }
        }else if (distance >= warningDistance) {
            String str = "warning";
            openDialog(str);
        }
    }


    private void getMyLocation(GoogleMap googleMap, double latitude, double longitude, LatLng latLng) {

        Geocoder geocoder = new Geocoder(getApplicationContext());

        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            String coordinates = addressList.get(0).getCountryCode() + " : " + latLng.toString();



            if (latLng != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                if (myMarker != null) myMarker.remove();
                myAddMarker(googleMap,coordinates,latLng);

                String dialog = "You are here:\n" + coordinates;

                openDialog(dialog);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void myAddMarker(GoogleMap googleMap, String coordinates, LatLng latLng){
        if (btm != null) {
          myMarker=  googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here: " + coordinates).icon(BitmapDescriptorFactory.fromBitmap(btm)));
        }else {
            myMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here: " + coordinates));
        }
    }


    public void openDialog(String coordinates){
        if(coordinates.matches("warning")){
            if(dialog==null){
                Context context = this;
                Activity activity = (Activity) context;
                if(!activity.isFinishing()) {
                    dialog = new Dialog().newInstance(coordinates);
                    dialog.show(getSupportFragmentManager(), "Dialog");
                }
            }else {
                Context context = this;
                Activity activity = (Activity) context;
                if(!activity.isFinishing()) {
                    dialog.dismiss();
                    dialog = new Dialog().newInstance(coordinates);
                    dialog.show(getSupportFragmentManager(), "Dialog");
                }
            }
        }else {
            if(distanceDialog==null){
                Context context = this;
                Activity activity = (Activity) context;
                if(!activity.isFinishing()) {
                    distanceDialog = new Dialog().newInstance(coordinates);
                    distanceDialog.show(getSupportFragmentManager(), "Dialog");
                }
            }else {
                Context context = this;
                Activity activity = (Activity) context;
                if(!activity.isFinishing()) {
                    distanceDialog.dismiss();
                    distanceDialog = new Dialog().newInstance(coordinates);
                    distanceDialog.show(getSupportFragmentManager(), "Dialog");
                }
            }
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            String str = account.toString();

            if (account.getPhotoUrl() != null) {
                photoUrl = account.getPhotoUrl().toString();
                Picasso.get().load(account.getPhotoUrl()).resize(150,150).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                       btm = bitmap;

                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }

        } else {
            startActivity(new Intent(Location.this, Login.class));
            finish();
        }
    }


    public void handleResult() {


        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    public void fetchLastLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);

        }

        Task<android.location.Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                if (location != null) {

                    //Toast.makeText(getApplicationContext(), location.getLatitude() + " / " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(Location.this);

                    firstLocation = location;

                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

    /*    if(firstLocation!=null){
            double latitude = firstLocation.getLatitude();
            double longitude = firstLocation.getLongitude();
            LatLng latLng = new LatLng(latitude,longitude);

            //.icon(BitmapDescriptorFactory.fromBitmap(bitmap)
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            if (uri!=null){
             try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here: " + latLng.toString()).icon(BitmapDescriptorFactory.fromPath(uri.toString())));
              } catch (IOException e) {
                 e.printStackTrace();
                 googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here: " + latLng.toString()));
             }
            }else  googleMap.addMarker(new MarkerOptions().position(latLng).title("You are here: " + latLng.toString()));

        }*/
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (REQUEST_CODE) {

            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_profile) {

            intent = new Intent(this, Profile.class);
            this.startActivity(intent);
            finish();


        } else if (item.getItemId() == R.id.menu_settings) {

            intent = new Intent(this, Settings.class);
            this.startActivity(intent);
            finish();
        }
        //if (item.getItemId() == android.R.id.home) super.onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item3 = menu.findItem(R.id.menu_logout);
        item3.setVisible(false);
        return true;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

}
