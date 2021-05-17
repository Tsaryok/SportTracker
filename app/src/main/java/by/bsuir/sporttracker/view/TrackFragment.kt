package by.bsuir.sporttracker.view

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import by.bsuir.sporttracker.databinding.FragmentTrackBinding
import by.bsuir.sporttracker.model.Plan
import by.bsuir.sporttracker.model.Track
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlin.math.max

class TrackFragment: Fragment(), OnMapReadyCallback{

    companion object{
        private const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
    }

    private lateinit var mBinding: FragmentTrackBinding

    private lateinit var track: Track

    private lateinit var mGoogleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentTrackBinding.inflate(layoutInflater, container, false)

        val id = TrackFragmentArgs.fromBundle(requireArguments()).id
        val name = TrackFragmentArgs.fromBundle(requireArguments()).name
        val points = TrackFragmentArgs.fromBundle(requireArguments()).pointString
        val isLooped = TrackFragmentArgs.fromBundle(requireArguments()).isLooped

        track = Track(id, name, points, isLooped)

        var mapViewBundle: Bundle? = null
        if (savedInstanceState == null){
            mapViewBundle = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        }
        mBinding.trackMapView.onCreate(mapViewBundle)
        mBinding.trackMapView.getMapAsync(this)

        val meters = Plan(track).distanceInMeters
        mBinding.trackNameTv.text = track.name
        mBinding.trackDistanceTv.text =
            if (meters < 1000){
                "${meters.toInt()} m"
            }else{
                String.format("%.1f km", meters.toDouble()/1000)
            }

        setUpLineChart()

        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        mBinding.trackMapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mBinding.trackMapView.onResume()
    }

    override fun onPause() {
        mBinding.trackMapView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mBinding.trackMapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mBinding.trackMapView.onDestroy()
        super.onDestroy()
    }

    override fun onMapReady(map: GoogleMap) {
        mGoogleMap = map

        val points = track.getLatLngList()
        mGoogleMap.addPolyline(PolylineOptions().addAll(points).width(5F).color(Color.RED))

        setMapByTrack(points)
    }

    private fun setMapByTrack(points: List<LatLng>){
        var maxLatitude = -90.0
        var minLatitude = 90.0
        var maxLongitude = -180.0
        var minLongitude = 180.0
        for (point in points){
            if (point.latitude > maxLatitude){
                maxLatitude = point.latitude
            }
            if (point.latitude < minLatitude){
                minLatitude = point.latitude
            }
            if (point.longitude > maxLongitude){
                maxLongitude = point.longitude
            }
            if (point.longitude < minLongitude){
                minLongitude = point.longitude
            }
        }
        val center = LatLng((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2)
        val zoom = (0.66 / max(maxLatitude - minLatitude, maxLongitude - minLongitude)).toFloat()
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, zoom))
    }

    private fun setUpLineChart(){
        val entries = ArrayList<Entry>()
        val segments = Plan(track).sections[0].segments
        var xValue = 0F
        for (segment in segments){
            entries.add(Entry(xValue, segment.startLocation.altitude.toFloat()))
            xValue += segment.distanceInMeters
        }
        entries.add(Entry(xValue, segments.last().endLocation.altitude.toFloat()))
        val dataSet = LineDataSet(entries, "Профиль трассы")
        dataSet.color = Color.RED
        dataSet.setDrawCircles(false)
        val lineData = LineData(dataSet)
        //mBinding.trackChart.setDrawGridBackground(false)
        //mBinding.trackChart.setDrawMarkers(false)
        mBinding.trackChart.data = lineData
        mBinding.trackChart.isScaleYEnabled = false
        mBinding.trackChart.invalidate()

    }

}