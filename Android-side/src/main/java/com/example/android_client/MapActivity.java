package com.example.android_client;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {
  private GoogleMap mMap;
  LocationManager locationManager;
  LocationListener locationListener;
  LatLng userLatLng;
  Network net;
  protected String userId;
  protected Button main_menu;
  protected ProgressDialog progress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map);
    net = new Network();
    main_menu = findViewById(R.id.main_menu_btn_map);
    main_menu.setVisibility(View.INVISIBLE);

    /* get user_id data from signIn activity */
    /* Get user_id coming from sign_in */
    SharedPreferences prefs = getSharedPreferences("user_sign_in", MODE_PRIVATE);
    userId = prefs.getString("user_id", "No user_id defined");

    final Button checkInButton = (Button) findViewById(R.id.checkInButton);
    checkInButton.setVisibility(View.INVISIBLE);

    new CountDownTimer(13000, 1000) {
      public void onTick(long millisUntilFinished) {

      }

      public void onFinish() {
        checkInButton.setVisibility(View.VISIBLE);
      }
    }.start();

    SupportMapFragment mf = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mf.getMapAsync(this);

    checkInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try {
          JSONObject jsonObject = getAddress(userLatLng);
          postCheckIn(jsonObject);
        } catch (IOException e) {
          e.printStackTrace();
        }
        checkInButton.setVisibility(View.GONE);
        main_menu.setVisibility(View.VISIBLE);
      }
    });

    main_menu.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent returnMainMenu = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(returnMainMenu);
      }
    });

  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    /* type location updates and required interface methods here */
    locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    locationListener = new LocationListener() {
      @Override
      public void onLocationChanged(Location location) {
        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear(); // clear the older location coordinates
        mMap.addMarker(new MarkerOptions().position(userLatLng).title("Your check-in"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 20), 3000, null);
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
    };

    askLocationPermission();

  }

  /* Dexter location and contacts permission listener library */
  private void askLocationPermission() {
    Dexter.withActivity(this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
      @Override
      public void onPermissionGranted(PermissionGrantedResponse response) {
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          // TODO: Consider calling
          //    ActivityCompat#requestPermissions
          // here to request the missing permissions, and then overriding
          //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
          //                                          int[] grantResults)
          // to handle the case where the user grants the permission. See the documentation
          // for ActivityCompat#requestPermissions for more details.
          return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        /* get user last location to set the default location marker in the map */
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        userLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        mMap.clear(); // clear the older location coordinates
        mMap.addMarker(new MarkerOptions().position(userLatLng).title("Your check-in"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLng));

      }

      @Override
      public void onPermissionDenied(PermissionDeniedResponse response) {

      }

      @Override
      public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
        token.cancelPermissionRequest();
      }
    }).check();
  }

  /* Get the location info from userLatLng object using GeoCoder */
  public JSONObject getAddress(LatLng userCoordinates) throws IOException {
    userCoordinates = userLatLng;
    Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());
    List<Address> addressList;
    addressList = geocoder.getFromLocation(userCoordinates.latitude, userCoordinates.longitude, 1);
    Log.w("Location Info", addressList.get(0).toString());
    JSONObject locationData = new JSONObject();
    try {
      locationData.put("address", addressList.get(0).getAddressLine(0));
      locationData.put("countryCode", addressList.get(0).getCountryCode());
      locationData.put("country", addressList.get(0).getCountryName());
      locationData.put("date", Calendar.getInstance().getTime());
      //locationData.put("personId:", userId);
    } catch (JSONException e
    ) {
      e.printStackTrace();
    }
    return locationData;
  }

  /* POST the address values to the related page on REST Server */
  public void postCheckIn(JSONObject jsonObject) throws IOException {
    String requestURl = "http://" + net.localIP + ":3000/api/People/" + userId + "/notifications";

    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, requestURl, jsonObject, new Response.Listener<JSONObject>() {
      @Override
      public void onResponse(JSONObject response) {
        // TODO: handle success
        Toast toast = Toast.makeText(getApplicationContext(), "Check-in successful!", Toast.LENGTH_SHORT);
        toast.show();
        Log.e("REST response: ", response.toString());
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Toast toast = Toast.makeText(getApplicationContext(), "Check-In unsuccessful!", Toast.LENGTH_SHORT);
        toast.show();
        error.printStackTrace();
        Log.e("REST response: ", error.toString());
      }
    });
    requestQueue.add(jsonRequest);
  }

}
