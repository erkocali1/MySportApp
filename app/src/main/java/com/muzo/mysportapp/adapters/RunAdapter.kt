package com.muzo.mysportapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.muzo.mysportapp.databinding.ItemRunBinding
import com.muzo.mysportapp.db.Run
import com.muzo.mysportapp.other.TrackingUtility
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter(private val context: Context) : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Run>) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding = ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.bind(run)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class RunViewHolder(private val binding: ItemRunBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(run: Run) {

            binding.apply {
                Glide.with(context).load(run.img).into(ivRunImage)

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = run.timeStamp
                }
                val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
                tvDate.text = dateFormat.format(calendar.time)
                val avgSpeed = "${run.avgSpeedInKm}km/h"
                tvAvgSpeed.text = avgSpeed
                val distanceInKm = "${run.distanceInMeters / 1000f}km"
                tvDistance.text = distanceInKm
                tvTime.text = TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)
                val caloriesBurned = "${run.caloriesBurned}kcal"
                tvCalories.text = caloriesBurned
            }
        }
    }
}
