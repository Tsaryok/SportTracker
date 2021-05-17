package by.bsuir.sporttracker.view.tracklist

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.bsuir.sporttracker.databinding.FragmentTrackListBinding
import by.bsuir.sporttracker.model.Track
import by.bsuir.sporttracker.module.TrackSelector
import by.bsuir.sporttracker.view.newtrack.NewTrackFragment

class TrackListFragment: Fragment(){

    companion object{
        private val TAG: String = TrackListFragment::class.java.simpleName
    }

    private lateinit var mTrackAdapter: TrackAdapter

    private lateinit var mBinding: FragmentTrackListBinding

    private lateinit var mTrackSelector: TrackSelector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentTrackListBinding.inflate(layoutInflater, container, false)

        mBinding.trackRecyclerView.layoutManager = LinearLayoutManager(context)
        mTrackAdapter = TrackAdapter{track -> startTrackFragment(track)}
        mBinding.trackRecyclerView.adapter = mTrackAdapter
        mTrackSelector = TrackSelector()
        mTrackSelector.getAllTracks(this)

        setFragmentResultListener(NewTrackFragment.REQUEST_KEY){ _, bundle ->
            Log.i(TAG, "Returned result")
            val locations: String = bundle.getString("Locations")!!
            val isLooped = bundle.getBoolean("isLooped")
            val trackName = bundle.getString("TrackName")
            mTrackSelector.saveTrack(this, trackName!!, locations, isLooped)
            Log.i(TAG, "Adding track in adapter")
        }
        return mBinding.root
    }

    fun setTracks(tracks: List<Track>){
        if (tracks.isNotEmpty()){
            mTrackAdapter.submitList(tracks as ArrayList<Track>)
            //for (track in tracks)
            //    mTrackSelector.deleteTrack(track)
        }
    }

    fun addTrack(track: Track){
        mTrackAdapter.currentList.add(track)
    }

    fun startTrackFragment(track: Track){
        val direction = TrackListFragmentDirections.actionTrackListFragmentToTrackFragment(track.id, track.name, track.isLooped, track.pointString)
        findNavController().navigate(direction)
    }

}