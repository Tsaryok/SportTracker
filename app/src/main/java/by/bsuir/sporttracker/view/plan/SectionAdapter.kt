package by.bsuir.sporttracker.view.plan

import android.annotation.SuppressLint
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.bsuir.sporttracker.databinding.ItemSectionBinding
import by.bsuir.sporttracker.model.Section

class SectionAdapter (private val onChangeSpeed: () -> Unit):
    ListAdapter<Section, SectionAdapter.SectionViewHolder>(SectionDiffCallback) {
    companion object{
        private val TAG: String = SectionAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        Log.i(TAG, "onCreateViewHolder")
        val mBinding = ItemSectionBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return SectionViewHolder(mBinding, onChangeSpeed)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder")
        holder.bind(getItem(position))
    }

    class SectionViewHolder(private val mBinding: ItemSectionBinding, val onChangeSpeed: () -> Unit):
        RecyclerView.ViewHolder(mBinding.root){
        private var currentSection: Section? = null

        init {
            mBinding.itemSectionSpeedEt.setOnKeyListener { v, keyCode, event ->
                Log.i(TAG, "Pressed key: keyCode - $keyCode, event - ${event.displayLabel}")
                if (keyCode == KeyEvent.KEYCODE_ENTER){
                    Log.i(TAG, "Pressed check mark")
                    currentSection?.speedKilometersPerHour = mBinding.itemSectionSpeedEt.text.toString().toFloatOrNull()?:0F
                    mBinding.itemSectionSpeedEt.clearFocus()
                    mBinding.itemSectionSpeedEt.isCursorVisible = false
                    onChangeSpeed()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(section: Section){
            currentSection = section
            Log.i(TAG, "Binding")
            mBinding.itemSectionNameTv.text = "S$layoutPosition"
            val meters = section.distanceInMeters
            mBinding.itemSectionDistanceTv.text =
                if (meters < 1000){
                    "${meters.toInt()} m"
                }else {
                    String.format("%.1f km", meters.toDouble() / 1000)
                }
            val time = section.timeMilliseconds
            mBinding.itemSectionTimeTv.text =
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

    object SectionDiffCallback: DiffUtil.ItemCallback<Section>() {
        override fun areItemsTheSame(oldItem: Section, newItem: Section): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Section, newItem: Section): Boolean {
            return oldItem.segments == newItem.segments
        }
    }
}