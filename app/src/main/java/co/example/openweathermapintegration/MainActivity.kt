package co.example.openweathermapintegration

import MainWeather
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


/**
 * Created by Ussama Iftikhar on 07-Apr-2021.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */

class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var mLocationManager: LocationManager? = null
    private val REQUEST_LOCATION = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION
            )

        } else {
            locationEnabled();
        }
    }

    override fun onResume() {
        super.onResume()
        getWeatherForCurrentLocation()
    }


    private fun getData(lat: Double, lon: Double) {
        val layout = findViewById<RelativeLayout>(R.id.lay)
        val textView = findViewById<TextView>(R.id.temp)
        val call1: Call<MainWeather> =
            WeatherService.weatherInstance.getWeather(lat = lat, lon = lon)
        call1.enqueue(
            object : Callback<MainWeather> {
                @SuppressLint("CommitPrefEdits")
                override fun onResponse(call: Call<MainWeather>, response: Response<MainWeather>) {
                    if (response.isSuccessful) {
                        val weather: MainWeather? = response.body()

                        Log.d("clima", "" + weather?.main?.temp)
                        layout.visibility = View.VISIBLE
                        val df = DecimalFormat("####0")
                        textView.text = df.format(weather?.main?.temp).toString()

                    } else {
                        Toast.makeText(this@MainActivity, "Failed to load data", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<MainWeather>?, t: Throwable?) {
                    Log.d("clims", "failure--" + t.toString())
                }

            })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION) {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Location Permission allowed", Toast.LENGTH_SHORT).show()
                locationEnabled();
            }
        }
    }

    private fun locationEnabled() {
        mLocationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS()
        } else {
            getWeatherForCurrentLocation()
        }
    }


    private fun getWeatherForCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.

                if (location != null) {
                    Log.d("clima", location.latitude.toString())
                    getData(lat = location.latitude, lon = location.longitude)
                } else {

                    val request = LocationRequest().setInterval(1000).setFastestInterval(1000)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setNumUpdates(1)

                    val callback: LocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            super.onLocationResult(locationResult)
                            Log.d("clima22", locationResult.lastLocation.latitude.toString())
                            getData(
                                lat = locationResult.lastLocation.latitude,
                                lon = locationResult.lastLocation.longitude
                            )
                        }

                        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                            super.onLocationAvailability(locationAvailability)
                        }
                    }

                    fusedLocationClient.requestLocationUpdates(request, callback, Looper.myLooper())

                }
            }

    }

    private fun OnGPS() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Enable GPS").setCancelable(false)
            .setPositiveButton("Yes") { dialog, which -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
            .setNegativeButton("No") { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun isLocationPermission(): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }
}


