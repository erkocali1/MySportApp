package com.muzo.mysportapp.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.muzo.mysportapp.databinding.FragmentRunBinding
import com.muzo.mysportapp.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.muzo.mysportapp.other.TrackingUtility
import com.muzo.mysportapp.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentRunBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRunBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requestPermissions()
        super.onViewCreated(view, savedInstanceState)

        binding.fab.setOnClickListener {
            val action =RunFragmentDirections.actionRunFragmentToTrackingFragment()
            findNavController().navigate(action)
        }
    }

    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "Bu uygulamayı kullanmak icin konum izinine ihtiyaç vardır",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "Bu uygulamayı kullanmak icin konum izinine ihtiyaç vardır",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()

        } else {
            requestPermissions()
        }
    }

   override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

   override fun onRequestPermissionsResult(
       requestCode: Int, permissions: Array<out String>, grantResults: IntArray
   ) {
       super.onRequestPermissionsResult(requestCode, permissions, grantResults)
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
   }
}