package com.muzo.mysportapp.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.muzo.mysportapp.R
import com.muzo.mysportapp.databinding.FragmentSetupBinding
import com.muzo.mysportapp.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.muzo.mysportapp.other.Constants.KEY_NAME
import com.muzo.mysportapp.other.Constants.KEY_WEIGHT
import com.muzo.mysportapp.other.Constants.PREFS_NAME
import com.muzo.mysportapp.other.Constants.PREF_FIRST_APP_OPEN
import com.muzo.mysportapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject



@AndroidEntryPoint
class SetupFragment : Fragment() {
    @Inject
    lateinit var sharedPref: SharedPreferences
    private lateinit var binding: FragmentSetupBinding





    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSetupBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isFirstAppOpen = isFirstAppOpen()
        if (!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                navOptions
            )
        }

        binding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                val action = SetupFragmentDirections.actionSetupFragmentToRunFragment()
                findNavController().navigate(action)

            } else {
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun isFirstAppOpen(): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(PREF_FIRST_APP_OPEN, true)
    }

    private fun setFirstAppOpen(isFirstAppOpen: Boolean) {
        val sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(PREF_FIRST_APP_OPEN, isFirstAppOpen).apply()
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPref = requireActivity().getSharedPreferences("entryInformation", MODE_PRIVATE)
        sharedPref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
        val toolbarText = "Let's go, $name!"

        val mainActivity = requireActivity() as MainActivity
        val toolbar = mainActivity.findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarTitle = toolbar.findViewById<TextView>(R.id.tvToolbarTitle)
        toolbarTitle.text = toolbarText

        setFirstAppOpen(false) // isFirstAppOpen değerini kaydediyoruz

        return true
    }

}