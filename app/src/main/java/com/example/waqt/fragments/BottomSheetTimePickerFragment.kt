package com.example.waqt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import com.example.waqt.R
import com.example.waqt.databinding.FragmentBottomSheetTimePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetTimePickerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetTimePickerBinding? = null
    private val binding get() = _binding!!

    private var prayerName: String = ""
    private var prayerIcon: Int = R.drawable.iv_fajr_icon
    private var initialHour: Int = 0
    private var initialMinute: Int = 0
    private var onTimeSet: ((hour: Int, minute: Int) -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBottomSheetTimePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvPrayerName.text = prayerName
        binding.ivPrayerIcon.setImageResource(prayerIcon)

        binding.timePicker.setIs24HourView(false)
        binding.timePicker.hour = initialHour
        binding.timePicker.minute = initialMinute

        binding.ivClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSetTime.setOnClickListener {
            onTimeSet?.invoke(binding.timePicker.hour, binding.timePicker.minute)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            prayerName: String,
            prayerIcon: Int,
            initialHour: Int,
            initialMinute: Int,
            onTimeSet: (hour: Int, minute: Int) -> Unit
        ): BottomSheetTimePickerFragment {
            return BottomSheetTimePickerFragment().apply {
                this.prayerName = prayerName
                this.prayerIcon = prayerIcon
                this.initialHour = initialHour
                this.initialMinute = initialMinute
                this.onTimeSet = onTimeSet
            }
        }
    }
}