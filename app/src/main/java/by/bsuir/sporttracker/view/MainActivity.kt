package by.bsuir.sporttracker.view

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import by.bsuir.sporttracker.R
import by.bsuir.sporttracker.databinding.ActivityMainBinding
import by.bsuir.sporttracker.model.Plan
import by.bsuir.sporttracker.model.Track
import by.bsuir.sporttracker.view.planlist.PlanAdapter
import by.bsuir.sporttracker.view.planlist.PlanListFragment
import by.bsuir.sporttracker.view.planlist.PlanListFragmentDirections
import by.bsuir.sporttracker.view.tracklist.TrackListFragmentDirections

class MainActivity : AppCompatActivity() {

    companion object{
        private val TAG: String = MainActivity::class.java.simpleName
    }

    private lateinit var mBinding: ActivityMainBinding
    private var currentTrack: Track? = null
    private var currentPlan: Plan? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)

        mBinding.fab.setOnClickListener {
            when(findNavController(R.id.nav_host_fragment).currentDestination!!.id){
                R.id.trackListFragment -> {
                    val directions = TrackListFragmentDirections.actionTrackListFragmentToNewTrackFragment()
                    findNavController(R.id.nav_host_fragment).navigate(directions)
                }
                R.id.trackFragment -> {
                    val directions = TrackFragmentDirections
                        .actionTrackFragmentToPlanListFragment(currentTrack!!.id)
                    findNavController(R.id.nav_host_fragment).navigate(directions)
                }
                R.id.planListFragment -> {
                    val plan = Plan(currentTrack!!)
                    Log.i(TAG, "Action to PlanFragment with $plan.")
                    val directions = PlanListFragmentDirections
                        .actionPlanListFragmentToPlanFragment(
                            plan.id,
                            plan.name,
                            plan.trackId,
                            plan.sectionString
                        )
                    findNavController(R.id.nav_host_fragment).navigate(directions)
                }
            }
        }
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHostFragment.navController
            .addOnDestinationChangedListener { controller, destination, arguments ->
                Log.i(TAG, "On change destination.")
                when(destination.id) {
                    R.id.newTrackFragment -> {
                        mBinding.fab.hide()
                    }
                    R.id.trackListFragment -> {
                        mBinding.fab.show()
                        mBinding.fab.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_add_24, theme))
                    }
                    R.id.trackFragment -> {
                        val id = arguments?.getLong("id") ?:0
                        val name = arguments?.getString("name")!!
                        val points = arguments.getString("pointString")!!
                        val isLooped = arguments.getBoolean("isLooped")
                        currentTrack = Track(id, name, points, isLooped)
                        Log.i(TAG, "Saving track $name in MainActivity: $points")
                        mBinding.fab.show()
                        mBinding.fab.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_plan_24, theme))
                    }
                    R.id.planListFragment -> {
                        mBinding.fab.show()
                        mBinding.fab.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_add_24, theme))
                    }
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bottom_app_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }
}