package com.muzo.mysportapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.muzo.mysportapp.R
import com.muzo.mysportapp.databinding.FragmentSetupBinding
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class SetupFragment : Fragment() {
    private lateinit var binding:FragmentSetupBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentSetupBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvContinue.setOnClickListener {

            val action=SetupFragmentDirections.actionSetupFragmentToRunFragment()
            findNavController().navigate(action)
        }
    }
}