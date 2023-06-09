package com.muzo.mysportapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationRequest

import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.muzo.mysportapp.R
import com.muzo.mysportapp.other.Constants.ACTION_PAUSE_SERVICE
import com.muzo.mysportapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.muzo.mysportapp.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.muzo.mysportapp.other.Constants.ACTION_STOP_SERVICE
import com.muzo.mysportapp.other.Constants.FASTEST_LOCATION_INTERVAL
import com.muzo.mysportapp.other.Constants.LOCATION_UPDATE_INTERVAL
import com.muzo.mysportapp.other.Constants.NOTIFICATION_CHANNEL_ID
import com.muzo.mysportapp.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.muzo.mysportapp.other.Constants.NOTIFICATION_ID
import com.muzo.mysportapp.other.TrackingUtility
import com.muzo.mysportapp.ui.MainActivity
import timber.log.Timber

typealias Polyline=MutableList<LatLng>
typealias Polylines=MutableList<Polyline>
class TrackingService : LifecycleService() {

    var isFirstRun=true

    lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    companion object{
        val isTracking=MutableLiveData<Boolean>()
        val pathPoints=MutableLiveData<Polylines>()
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {

                ACTION_START_OR_RESUME_SERVICE -> {

                    if (isFirstRun){
                        startForegroundService()
                        isFirstRun=false
                    }else{
                        Timber.d("Resuming Service")
                        startForegroundService()
                    }
                }

                ACTION_PAUSE_SERVICE -> {
                    Timber.d("Paused service")
                    pauseService()
                }

                ACTION_STOP_SERVICE -> {
                    Timber.d("Stop service")
                }

            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
    private fun pauseService(){
        isTracking.postValue(false)
    }

    @SuppressLint("MissingPermission")
    private fun  updateLocationTracking(isTracking:Boolean){
        if (isTracking){
            if (TrackingUtility.hasLocationPermissions(this)){
                val request=com.google.android.gms.location.LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,FASTEST_LOCATION_INTERVAL)
                    .setMinUpdateDistanceMeters(1F)
                    .setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                    .setWaitForAccurateLocation(true)
                    .build()

                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallBack,
                    Looper.getMainLooper()
                )
            }else{
                fusedLocationProviderClient.removeLocationUpdates(
                    locationCallBack
                )
            }

        }

    }

    val locationCallBack=object :LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value!!){
                result?.locations.let {locations ->
                    for (location in locations!!){
                        addPathPoint(location)
                        Timber.d("NEW LOCATION ${location.latitude},${location.longitude}")
                    }
                }
            }
        }
    }
    private fun addPathPoint(location:Location?){
        location?.let {
            val pos =LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }

    }
    private fun addEmptyPolyline()= pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    }?: pathPoints.postValue(mutableListOf(mutableListOf()))
private fun startForegroundService(){

    addEmptyPolyline()
    isTracking.postValue(true)

    val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE)
    as NotificationManager

    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        createNotificationChannel(notificationManager)
    }
    val notificationBuilder=NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
        .setContentTitle("Running App")
        .setContentText("00:00:00")
        .setContentIntent(getMainActivityPendingIntent())

    startForeground(NOTIFICATION_ID,notificationBuilder.build())
}
    private fun getMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java).also {
            it.action = ACTION_SHOW_TRACKING_FRAGMENT
        }
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}