package by.bsuir.sporttracker.view.tracklist

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.bsuir.sporttracker.databinding.ItemTimecutBinding
import by.bsuir.sporttracker.databinding.ItemTrackBinding
import by.bsuir.sporttracker.model.Plan
import by.bsuir.sporttracker.model.Track
import by.bsuir.sporttracker.service.RequestLocationService

class TrackAdapter(private val onClick: (Track) -> Unit):
    ListAdapter<Track, TrackAdapter.TrackViewHolder>(TrackDiffCallback) {

    companion object{
        private val TAG: String = TrackAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        Log.i(TAG, "onCreateViewHolder")
        val mBinding = ItemTrackBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(mBinding, onClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        holder.bind(getItem(position))
    }

    class TrackViewHolder(private val mBinding: ItemTrackBinding, val onClick: (Track) -> Unit):
        RecyclerView.ViewHolder(mBinding.root){
        private var currentTrack: Track? = null

        init {
            mBinding.root.setOnClickListener{
                currentTrack?.let { it1 -> onClick(it1) }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(track: Track){
            currentTrack = track
            Log.i(TAG, "Binding")
            Log.i(TAG, "Track ${track.id}")
            mBinding.itemTrackNameTv.text = track.name
            val meters = Plan(track).distanceInMeters
            mBinding.itemTrackDistanceTv.text =
                if (meters < 1000){
                    "${meters.toInt()} m"
                }else {
                    String.format("%.1f km", meters.toDouble() / 1000)
                }
        }
    }

    object TrackDiffCallback: DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

    }
}