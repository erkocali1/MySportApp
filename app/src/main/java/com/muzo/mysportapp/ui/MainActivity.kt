package com.muzo.mysportapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.muzo.mysportapp.R
import com.muzo.mysportapp.databinding.ActivityMainBinding
import com.muzo.mysportapp.db.RunDao
import com.muzo.mysportapp.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.muzo.mysportapp.other.TrackingUtility
import com.muzo.mysportapp.ui.fragments.TrackingFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navigateToTrackingFragmentIfNeeded(intent)

        setSupportActionBar(binding.toolbar)
        navController=Navigation.findNavController(this,R.id.navHostFragment)
        NavigationUI.setupWithNavController(binding.bottomNavigationView,navController)

       navController.addOnDestinationChangedListener{_, destination, _ ->

           when(destination.id){
               R.id.settingsFragment,R.id.runFragment,R.id.statisticsFragment ->

                   binding.bottomNavigationView.visibility= View.VISIBLE
               else ->binding.bottomNavigationView.visibility=View.GONE
           }
       }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent!!)
    }

    //////BUrayabakkkkkkkkkkkkkkkkkkkkkkkkkkkkk
    private fun navigateToTrackingFragmentIfNeeded(intent: Intent){
        if (intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){
            val navController = findNavController(R.id.navHostFragment)
            navController.navigate(R.id.action_global_trackingFragment)


        }


    }

}