package com.muzo.mysportapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.muzo.mysportapp.R
import com.muzo.mysportapp.databinding.ActivityMainBinding
import com.muzo.mysportapp.db.RunDao
import com.muzo.mysportapp.other.TrackingUtility
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

}