package by.bsuir.sporttracker.view.planlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.bsuir.sporttracker.databinding.FragmentPlanListBinding
import by.bsuir.sporttracker.model.Plan
import by.bsuir.sporttracker.module.PlanSelector

class PlanListFragment: Fragment() {
    companion object{
        private val TAG: String = PlanListFragment::class.java.simpleName
    }

    private lateinit var mPlanAdapter: PlanAdapter

    private lateinit var mBinding: FragmentPlanListBinding

    private lateinit var mPlanSelector: PlanSelector

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentPlanListBinding.inflate(layoutInflater, container, false)

        mBinding.planRecyclerView.layoutManager = LinearLayoutManager(context)
        mPlanAdapter = PlanAdapter{ plan -> onClick(plan)}
        mBinding.planRecyclerView.adapter = mPlanAdapter
        val trackId = PlanListFragmentArgs.fromBundle(requireArguments()).id
        mPlanSelector = PlanSelector()
        mPlanSelector.getPlansByTrackId(this, trackId)

        /*setFragmentResultListener(NewTrackFragment.REQUEST_KEY){ _, bundle ->
            Log.i(TAG, "Returned result")
            val locations: String = bundle.getString("Locations")!!
            val isLooped = bundle.getBoolean("isLooped")
            val trackName = bundle.getString("TrackName")
            mPlanSelector.saveTrack(this, trackName!!, locations, isLooped)
            Log.i(TAG, "Adding track in adapter")
        }*/
        return mBinding.root
    }

    fun setPlans(plans: List<Plan>){
        if (plans.isNotEmpty()){
            mPlanAdapter.submitList(plans as ArrayList<Plan>)
            //for (track in tracks)
            //    mTrackSelector.deleteTrack(track)
        }
    }

    fun addPlan(plan: Plan){
        mPlanAdapter.currentList.add(plan)
    }

    /*fun startPlanFragment(plan: Plan){
        val direction = TrackListFragmentDirections.actionTrackListFragmentToTrackFragment(plan.id, plan.name, plan.isLooped, plan.pointString)
        findNavController().navigate(direction)
    }*/

    private fun onClick(plan: Plan){

    }
}