package com.xyz.mohit.hikersapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback , GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Location l;
    AutoCompleteTextView SearchText;
    Button button;
    LocationManager locationManager;
    LocationListener locationListener;
    BottomSheetBehavior bottomSheetBehavior;
    Double Longg, latii;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout);
        bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        bottomSheetBehavior.setPeekHeight(120);
        bottomSheetBehavior.setHideable(false);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        button=(Button)findViewById(R.id.button);
        SearchText=(AutoCompleteTextView) findViewById(R.id.Search);
        // On Search Initiated
        GeoDataClient mGoogleApiClient;
        mGoogleApiClient = Places.getGeoDataClient(this,null);

        LatLngBounds latLng=new LatLngBounds(new LatLng(23.63936, 68.14712), new LatLng(28.20453, 97.34466));

        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        PlaceAutocompleteAdapter adapter=new PlaceAutocompleteAdapter(MapsActivity.this,mGoogleApiClient,latLng,null);
        SearchText.setAdapter(adapter);

        SearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == keyEvent.ACTION_DOWN
                        || keyEvent.getAction() == keyEvent.KEYCODE_ENTER)
                {

                    View v=MapsActivity.this.getCurrentFocus();
                    if(v != null) {


                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        geoLocate();
                        return true;
                    }

                }

                return false;
            }
        });

       // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //      checkPlayServices();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Longg = location.getLongitude();
                latii = location.getLatitude();
                mMap.clear();
                LatLng sydney = new LatLng(latii, Longg);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20));

                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> listAdress = geocoder.getFromLocation(latii, Longg, 1);

                    if (listAdress != null && listAdress.size() > 0) {
                        String address = "";

                        if (listAdress.get(0).getSubThoroughfare() != null) {
                            address += listAdress.get(0).getSubThoroughfare()+" , ";                      }

                        if (listAdress.get(0).getThoroughfare() != null) {
                            address += listAdress.get(0).getThoroughfare()+" , ";                        }
                        if (listAdress.get(0).getLocality() != null) {
                            address += listAdress.get(0).getLocality()+" , ";
                        }
                        if (listAdress.get(0).getCountryName() != null) {
                            address += listAdress.get(0).getCountryName();
                        }

                        updateui(address, latii, Longg);


                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        Toast.makeText(MapsActivity.this,"Please Wait till Location Updates",Toast.LENGTH_LONG).show();

    }


    private void geoLocate() {

        mMap.clear();


        String SearchT=SearchText.getText().toString();


        Geocoder geocoder=new Geocoder(getApplicationContext());

        Address address = null;

        List<Address> list= new ArrayList<Address>();

        try {
            list =geocoder.getFromLocationName(SearchT,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (list != null && list.size() > 0) {

            address=list.get(0);
            String add="";
            if (list.get(0).getSubThoroughfare() != null) {
                add += list.get(0).getSubThoroughfare()+" , ";                      }

            if (list.get(0).getThoroughfare() != null) {
                add += list.get(0).getThoroughfare()+" , ";                        }
            if (list.get(0).getLocality() != null) {
                add += list.get(0).getLocality()+" , ";
            }
            if (list.get(0).getCountryName() != null) {
                add += list.get(0).getCountryName();
            }

            Toast.makeText(MapsActivity.this,add.toString(),Toast.LENGTH_LONG);
            LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("New Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
            button.setVisibility(View.VISIBLE);
            updateui(add.toString(),address.getLatitude(),address.getLongitude());

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
    public void onMapReady(GoogleMap googleMap) {

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {

            Criteria criteria=new Criteria();
            criteria.setPowerRequirement(criteria.ACCURACY_MEDIUM);
            criteria.setSpeedAccuracy(criteria.ACCURACY_HIGH);

            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Add a marker in Sydney and move the camera
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                return;
            } else {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,20 , locationListener);
                l= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(l==null)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20, locationListener);

            }
            else {
                mMap.clear();
                LatLng sydney = new LatLng(l.getLatitude(), l.getLongitude());
                mMap.addMarker(new MarkerOptions().position(sydney).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20));
                updateui("Your Current Address", l.getLatitude(), l.getLongitude());
            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    button.setVisibility(View.VISIBLE);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(latLng).title("New Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));

                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                    try {
                        List<Address> listAdress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

                        if (listAdress != null && listAdress.size() > 0) {
                            String address = "";
                            if (listAdress.get(0).getSubThoroughfare() != null) {
                                address += listAdress.get(0).getSubThoroughfare()+" , ";                      }

                            if (listAdress.get(0).getThoroughfare() != null) {
                                address += listAdress.get(0).getThoroughfare()+" , ";                        }
                            if (listAdress.get(0).getLocality() != null) {
                                address += listAdress.get(0).getLocality()+" , ";
                            }
                            if (listAdress.get(0).getCountryName() != null) {
                                address += listAdress.get(0).getCountryName();
                            }

                            updateui(address, latLng.latitude,latLng.longitude);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private void updateui(String address, double l1, double l2) {

        TextView t1 = (TextView) findViewById(R.id.Address);
        t1.setText(address);
        TextView t2 = (TextView) findViewById(R.id.Longitute);
        t2.setText("Longitude: " + String.valueOf(l2));
        TextView t3 = (TextView) findViewById(R.id.Latitude);
        t3.setText("Latitude: " + String.valueOf(l1));


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 20, locationListener);
                    l = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                }
            }
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void RecenterClicked(View v)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 20, locationListener);
                button.setVisibility(View.GONE);
            }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}