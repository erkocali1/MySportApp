package com.muzo.mysportapp.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.muzo.mysportapp.R
import com.muzo.mysportapp.databinding.FragmentSettingsBinding
import com.muzo.mysportapp.other.Constants.KEY_NAME
import com.muzo.mysportapp.other.Constants.KEY_WEIGHT
import com.muzo.mysportapp.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private lateinit var binding:FragmentSettingsBinding
    lateinit var sharedPreferences:SharedPreferences



    private fun loadFieldsFromSharedPref(){
        sharedPreferences=requireActivity().getSharedPreferences("entryInformation", Context.MODE_PRIVATE)
        val name=sharedPreferences.getString(KEY_NAME,"")
        val weight=sharedPreferences.getFloat(KEY_WEIGHT,80f)
        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPref()

        binding.buttonChanges.setOnClickListener {
            val success = applyChangedToSharedPref()
            if (success) {
                Snackbar.make(view, "Saved changes", Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(view, "Please fill out all the fields", Snackbar.LENGTH_LONG).show()
            }
        }
    }



    private fun applyChangedToSharedPref():Boolean{
        val nameText=binding.etName.text.toString()
        val weightText=binding.etWeight.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME,nameText)
            .putFloat(KEY_WEIGHT,weightText.toFloat())
            .apply()

        val toolbarText = "Let's go , $nameText!"
        val mainActivity = requireActivity() as MainActivity
        val toolbar = mainActivity.findViewById<MaterialToolbar>(R.id.toolbar)
        val toolbarTitle = toolbar.findViewById<TextView>(R.id.tvToolbarTitle)
        toolbarTitle.text = toolbarText
        return true
    }
}

