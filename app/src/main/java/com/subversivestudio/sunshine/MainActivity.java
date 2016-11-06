/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.subversivestudio.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private String ApiKey = "3f0e31aca20d1de39f8f1cd15c8963f3";
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    GPSTracker gps;
    double latitude;
    double longitude;
    String postCode;
    public static String PC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setHasOptionsMenu(true);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        //List<Address> address = null;
        List<Address> address = null;

        gps = new GPSTracker(this);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        //Toast.makeText(this, "WE HAVE GOT YOUR LOCATION: LATITUDE = " + latitude + "LONGITUDE = " + longitude, Toast.LENGTH_LONG).show();


			if (geoCoder != null) {
				try {
					address = geoCoder.getFromLocation(latitude, longitude, 1);
					//Toast.makeText(this, "Address has been found" + address, Toast.LENGTH_LONG).show();

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//if (address.size() > 0) {
				if (address != null) {
					try{
						postCode = address.get(0).getPostalCode();
                        Toast.makeText(this, "Sunshine is using: " + postCode + " as your location", Toast.LENGTH_LONG).show();
                        PC = postCode;

					}
					catch(IndexOutOfBoundsException e){
						e.printStackTrace();
					}
				}
				else{
					Toast.makeText(this, "Address was null, maybe no GPS reception?", Toast.LENGTH_LONG).show();
				}
			}



        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
//
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(this);
        String location = sharedPrefs.getString(
                getString(R.string.pref_location_key),
                PC);

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }



}

