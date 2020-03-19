package com.mehul.hikerswatch;

/*
    Title: Hiker's Watch
    Author: Mehul Patel
    Date: March 18, 2020
    Description: This app will display location coordinates and address as if it were a hiker's watch
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //create a location manager
    LocationManager locMan;

    //create a location listener
    LocationListener locList;

    //process when the user gives us permission if we don't already have it
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //check if permission is granted
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            startListening();
        }
    }

    //method to start location manager if permission is explicitly granted
    public void startListening() {
        //check explicitly for the permission granted
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //start the location manager since permission is granted
            locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    //method to update the location
    public void updateLocationInfo (Location location){
        //log the location as string
        Log.i("LocationInfo:", location.toString());

        TextView latTextView = (TextView) findViewById(R.id.latitudeTextView);
        TextView longTextView = (TextView) findViewById(R.id.longitudeTextView);
        TextView altTextView = (TextView) findViewById(R.id.altitudeTextView);
        TextView accTextView = (TextView) findViewById(R.id.accuracyTextView);

        latTextView.setText("Latitude: " + location.getLatitude());
        longTextView.setText("Longitude: " + location.getLongitude());
        altTextView.setText("Altitude: " + location.getAltitude());
        accTextView.setText("Accuracy: " + location.getAccuracy());

        //get location of user and assign to geocoder variable
        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            //create string to hold the address. set initial value as could not find address if there
            //is no address that can be appended to the string
            String address = "Could not find address";

            //create list of addresses and get the address information of a location from the geocoder geo
            List<Address> addressList = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            //check if there is an address found for us to use
            if(addressList != null && addressList.size() > 0){
                Log.i("PlaceInfo:", addressList.get(0).toString());

                address = "Address: \n";

                //check each item in the address list and append the contents to the address string
                //check for road/building/house number and append
                if(addressList.get(0).getSubThoroughfare() != null){
                    address += addressList.get(0).getSubThoroughfare() + " ";
                }

                //check for road name and append
                if(addressList.get(0).getThoroughfare() != null){
                    address += addressList.get(0).getThoroughfare() + "\n";
                }

                //check for town name and append
                if(addressList.get(0).getLocality() != null){
                    address += addressList.get(0).getLocality() + "\n";
                }

                //check for postal code/zip name and append
                if(addressList.get(0).getPostalCode() != null){
                    address += addressList.get(0).getPostalCode() + "\n";
                }

                //check for country name and append
                if(addressList.get(0).getCountryName() != null){
                    address += addressList.get(0).getCountryName() + "\n";
                }

                TextView addressTextView = (TextView) findViewById(R.id.addressTextView);

                //set address text view to display the address
                addressTextView.setText(address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up the location manager and cast it to a location manager type
        locMan = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //set up the location listener
        locList = new LocationListener() {

            //do something with the location when location is changed in this method
            @Override
            public void onLocationChanged(Location location) {
                //calls the update LocationInfo method when current location is changed
                updateLocationInfo(location);
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

        //set up listener by asking for permission and check if version is less than 23
        if (Build.VERSION.SDK_INT < 23) {
            //call startListening method which sets up the location manager after performing an explicit permission check
            startListening();

        //if version is 23 or above
        } else {
            //check if we do not have permission to ACCESS_FINE_LOCATION. Permission request for ACCESS_FINE_LOCATION is set in the AndroidManifext.xml
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                //ask for permission for 'this' activity and send a request code of 1. request code can be whatever number of your choosing
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            //if we do have permission
            }else {
                //start listening for location
                locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locList);

                //get last known location from GPS_PROVIDER and assign it to loc
                Location loc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                //check to see if there is a last known location
                if(loc != null){
                    //call the updateLocationInfo method to update the location information
                    updateLocationInfo(loc);
                }


            }
        }


    }
}
