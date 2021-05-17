package by.bsuir.sporttracker.view.plan

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import by.bsuir.sporttracker.databinding.FragmentPlanBinding
import by.bsuir.sporttracker.model.Plan
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

class PlanFragment: Fragment(){

    companion object{
        private val TAG: String = PlanFragment::class.java.simpleName
    }

    private lateinit var mBinding: FragmentPlanBinding
    private lateinit var mSectionAdapter: SectionAdapter
    private lateinit var plan: Plan


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentPlanBinding.inflate(layoutInflater, container, false)

        mBinding.planRecyclerview.layoutManager = LinearLayoutManager(context)
        mSectionAdapter = SectionAdapter{ setTime()}
        mBinding.planRecyclerview.adapter = mSectionAdapter

        val id = PlanFragmentArgs.fromBundle(requireArguments()).id
        val name = PlanFragmentArgs.fromBundle(requireArguments()).name
        val trackId = PlanFragmentArgs.fromBundle(requireArguments()).trackId
        val sectionString = PlanFragmentArgs.fromBundle(requireArguments()).sectionString
        plan = Plan(id, name, trackId, sectionString)

        mSectionAdapter.submitList(plan.sections)

        mBinding.planNameTv.text = plan.name

        setTime()

        setUpLineChart()

        return mBinding.root
    }

    private fun setUpLineChart(){
        val entries = ArrayList<Entry>()
        var xValue = 0F
        for (section in plan.sections) {
            val segments = section.segments
            for (segment in segments) {
                entries.add(Entry(xValue, segment.startLocation.altitude.toFloat()))
                xValue += segment.distanceInMeters
            }
            entries.add(Entry(xValue, segments.last().endLocation.altitude.toFloat()))
        }
        val dataSet = LineDataSet(entries, "Профиль трассы")
        dataSet.color = Color.RED
        dataSet.setDrawCircles(false)
        val lineData = LineData(dataSet)
        mBinding.planChart.setDrawGridBackground(false)
        mBinding.planChart.data = lineData
        mBinding.planChart.isScaleYEnabled = false
        mBinding.planChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener
        {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                Log.i(TAG, "Value selected: ${e.toString()}")
                Log.i(TAG, "Selected index: ${entries.indexOf(e)}")
                Log.i(TAG, "Highlight selected: ${h.toString()}")
                plan.addCheckpoint(entries.indexOf(e))
                mSectionAdapter.notifyDataSetChanged()
            }
            override fun onNothingSelected() {
                Log.i(TAG, "Nothing selected.")
            }

        })
        mBinding.planChart.invalidate()
    }

    private fun setTime(){
        val time = plan.timeMilliseconds
        mBinding.planTimeTv.text =
            when {
                time > 3600000 -> {
                    "${time/3600000}:${(time%3600000)/60000}:${(time%60000)/1000}.${time%1000/100}"
                }
                time > 60000 -> {
                    "${(time%3600000)/60000}:${(time%60000)/1000}.${time%1000/100}"
                }
                time > 0 -> {
                    "${(time%60000)/1000}.${time%1000/100}"
                }
                else -> {
                    "-:-"
                }
            }
    }
}