package by.bsuir.sporttracker.module

import android.os.Handler
import android.os.HandlerThread
import by.bsuir.sporttracker.App
import by.bsuir.sporttracker.model.Plan
import by.bsuir.sporttracker.model.Track
import by.bsuir.sporttracker.view.planlist.PlanListFragment

class PlanSelector() {

    private val handlerThread = HandlerThread("PlanSelector")
    private var requestHandler: Handler

    init{
        handlerThread.start()
        requestHandler = Handler(handlerThread.looper)
    }

    private val planDao = App.appDatabase.planDao()

    fun getPlansByTrackId(fragment: PlanListFragment, trackId: Long){
        requestHandler.post{
            val plans = planDao.getPlansByTrackId(trackId)
            fragment.activity?.runOnUiThread {
                fragment.setPlans(plans)
            }
        }
    }

    private fun updatePlan(plan: Plan){
        requestHandler.post {
            planDao.updatePlan(plan)
        }
    }

    fun savePlan(fragment: PlanListFragment, plan: Plan){
        requestHandler.post {
            plan.id = planDao.insertPlan(plan)
            fragment.activity?.runOnUiThread {
                fragment.addPlan(plan)
                //fragment.startTrackFragment(track)
            }
        }
    }

    fun deletePlan(plan: Plan){
        requestHandler.post {
            planDao.deletePlan(plan)
        }
    }
}