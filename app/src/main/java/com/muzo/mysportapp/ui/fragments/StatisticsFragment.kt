package com.muzo.mysportapp.ui.fragments

import CustomMarkerView
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.muzo.mysportapp.R
import com.muzo.mysportapp.databinding.FragmentStatisticsBinding
import com.muzo.mysportapp.other.TrackingUtility
import com.muzo.mysportapp.ui.viewmodels.StatisticsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private lateinit var binding: FragmentStatisticsBinding
    private val viewModel: StatisticsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentStatisticsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        setupBarChart()
    }
    private fun setupBarChart(){
        binding.barChart.xAxis.apply {
            position=XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor= Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.axisLeft.apply {
            axisLineColor= Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply {
            axisLineColor= Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.apply {
            description.text="Avg Speed Over Time"
            legend.isEnabled=false
        }

    }

    private fun subscribeToObservers(){

        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun=TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text=totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km=it/1000f
                val totalDistance= round(km*10f)/10f
                val  totalDistanceString="${totalDistance}km"
                binding.tvTotalDistance.text=totalDistanceString
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed= round(it*10f)/10f
                val avgSpeedString="${avgSpeed}km/h"
                binding.tvAverageSpeed.text=avgSpeedString
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalCalories="${it}kcal"
                binding.tvTotalCalories.text=totalCalories
            }
        })

        viewModel.runSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds=it.indices.map { i->BarEntry(i.toFloat(),it[i].avgSpeedInKm!!) }
                val bardataSet=BarDataSet(allAvgSpeeds,"Avg Speed OverTime").apply {

                    valueTextColor=Color.WHITE
                    color=ContextCompat.getColor((requireContext()), R.color.colorAccent)

                }
                binding.barChart.data= BarData(bardataSet)
                binding.barChart.marker=CustomMarkerView(it.reversed(),requireContext(),R.layout.marker_view)
                binding.barChart.invalidate()
            }
        })
    }

}