package by.bsuir.sporttracker.view.planlist

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.bsuir.sporttracker.databinding.ItemPlanBinding
import by.bsuir.sporttracker.model.Plan

class PlanAdapter(private val onClick: (Plan) -> Unit):
    ListAdapter<Plan, PlanAdapter.PlanViewHolder>(PlanDiffCallback) {
    companion object{
        private val TAG: String = PlanAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        Log.i(TAG, "onCreateViewHolder")
        val mBinding = ItemPlanBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return PlanViewHolder(mBinding, onClick)
    }

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        holder.bind(getItem(position))
    }

    class PlanViewHolder(private val mBinding: ItemPlanBinding, val onClick: (Plan) -> Unit):
        RecyclerView.ViewHolder(mBinding.root){
        private var currentPlan: Plan? = null

        init {
            mBinding.root.setOnClickListener{
                currentPlan?.let { it1 -> onClick(it1) }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(plan: Plan){
            currentPlan = plan
            Log.i(TAG, "Binding")
            Log.i(TAG, "Track ${plan.id}")
            mBinding.itemPlanNameTv.text = plan.name
            mBinding.itemPlanTimeTv.text = plan.timeMilliseconds.toString()
        }
    }

    object PlanDiffCallback: DiffUtil.ItemCallback<Plan>() {
        override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
            return oldItem.id == newItem.id
        }
    }
}