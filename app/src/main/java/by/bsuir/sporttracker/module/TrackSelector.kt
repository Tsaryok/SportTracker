package by.bsuir.sporttracker.module

import android.os.Handler
import android.os.HandlerThread
import by.bsuir.sporttracker.App
import by.bsuir.sporttracker.model.LocationDB
import by.bsuir.sporttracker.model.Track
import by.bsuir.sporttracker.view.tracklist.TrackListFragment

class TrackSelector() {

    private val handlerThread = HandlerThread("TrackSelector")
    private var requestHandler: Handler

    init{
        handlerThread.start()
        requestHandler = Handler(handlerThread.looper)
    }

    private val trackDao = App.appDatabase.trackDao()

    fun getAllTracks(fragment: TrackListFragment){
        requestHandler.post{
            val tracks = trackDao.getAll()
            fragment.activity?.runOnUiThread {
                fragment.setTracks(tracks)
            }
        }
    }

    private fun updateTrack(track: Track){
        requestHandler.post {
            trackDao.updateTrack(track)
        }
    }

    fun saveTrack(fragment: TrackListFragment, name: String, locations: List<LocationDB>, isLooped: Boolean){
        val track = Track(0, name, locations, isLooped)
        requestHandler.post {
            track.id = trackDao.insertTrack(track)
            fragment.activity?.runOnUiThread {
                fragment.addTrack(track)
                fragment.startTrackFragment(track)
            }
        }
    }

    fun saveTrack(fragment: TrackListFragment, name: String, locations: String, isLooped: Boolean){
        val track = Track(0, name, locations, isLooped)
        requestHandler.post {
            track.id = trackDao.insertTrack(track)
            fragment.activity?.runOnUiThread {
                fragment.startTrackFragment(track)
            }
        }
    }

    fun deleteTrack(track: Track){
        requestHandler.post {
            trackDao.deleteTrack(track)
        }
    }
}