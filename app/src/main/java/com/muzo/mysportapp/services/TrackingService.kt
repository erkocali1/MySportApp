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
import com.muzo.mysportapp.other.Constants.TIMER_UPDATE_INTERVAL
import com.muzo.mysportapp.other.TrackingUtility
import com.muzo.mysportapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline=MutableList<LatLng>
typealias Polylines=MutableList<Polyline>
@AndroidEntryPoint
class TrackingService : LifecycleService() {

    var isFirstRun=true


@Inject
    lateinit var fusedLocationProviderClient:FusedLocationProviderClient

    private val timeRunInSeconds=MutableLiveData<Long>()

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder
    companion object{
        val timeRunInMilis=MutableLiveData<Long>()
        val isTracking=MutableLiveData<Boolean>()
        val pathPoints=MutableLiveData<Polylines>()
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSeconds.postValue(0L)
        timeRunInMilis.postValue(0L)
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
                        startTimer()
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
    private var isTimerEnabled=false
    private var lapTime=0L
    private var timeRun=0L
    private var timeStarted=0L
    private var lastSecondTimestamp=0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted=System.currentTimeMillis()
        isTimerEnabled=true
        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!){
                //time diffrence betwenn now and timestarted
                lapTime=System.currentTimeMillis()-timeStarted

                timeRunInMilis.postValue(timeRun+lapTime)

                if (timeRunInMilis.value!!>= lastSecondTimestamp+1000L){
                    timeRunInSeconds.postValue(timeRunInSeconds.value!!+1)
                    lastSecondTimestamp+=1000L
                }
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun+=lapTime
        }

    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled=false
    }
//burayabakkk
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

    startTimer()

    isTracking.postValue(true)

    val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE)
    as NotificationManager

    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
        createNotificationChannel(notificationManager)
    }

    startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())
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