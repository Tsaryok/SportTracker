package by.bsuir.sporttracker.view.newtrack

import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import by.bsuir.sporttracker.R
import by.bsuir.sporttracker.databinding.FragmentNewTrackBinding
import by.bsuir.sporttracker.module.TrackBuilder
import com.google.gson.Gson
import org.koin.android.ext.android.inject

class NewTrackFragment: Fragment(){

    companion object {
        const val REQUEST_KEY = "NewTrackFragment_REQUEST_KEY"
    }

    private lateinit var mBinding: FragmentNewTrackBinding

    private val trackBuilder: TrackBuilder by inject()

    private var state = BuildingState.NONE

    private lateinit var startButton: Button
    private lateinit var finishButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentNewTrackBinding.inflate(layoutInflater, container, false)
        startButton = mBinding.startButton
        finishButton = mBinding.finishButton

        setUp()

        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        trackBuilder.onStart()
    }

    override fun onStop() {
        trackBuilder.onStop()
        super.onStop()
    }

    private fun setUp() {
        startButton.setOnClickListener {
            when(state){
                BuildingState.NONE -> {
                    startButton.text = getString(R.string.stop)
                    state = BuildingState.STARTED
                    trackBuilder.startTrackBuilding()
                }
                BuildingState.STARTED -> {
                    startButton.text = getString(R.string.restart)
                    finishButton.visibility = View.VISIBLE
                    state = BuildingState.PAUSED
                    trackBuilder.pauseTrackBuilding()
                }
                BuildingState.PAUSED -> {
                    startButton.text = getString(R.string.stop)
                    finishButton.visibility = View.INVISIBLE
                    state = BuildingState.STARTED
                    trackBuilder.resumeTrackBuilding()
                }
            }
        }

        finishButton.setOnClickListener {
            val bundle = Bundle()
            //bundle.putParcelableArrayList("Locations", trackBuilder.endTrackBuilding())
            bundle.putString("Locations", Gson().toJson(trackBuilder.endTrackBuilding()))
            bundle.putBoolean("isLooped", mBinding.checkBox.isChecked)
            bundle.putString("TrackName", mBinding.trackNameEditText.text.toString())
            setFragmentResult(REQUEST_KEY, bundle)
            findNavController().navigateUp()
        }
    }
}