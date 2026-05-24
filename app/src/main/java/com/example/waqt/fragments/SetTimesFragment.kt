package com.example.waqt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.waqt.R
import com.example.waqt.alarm.AlarmScheduler
import com.example.waqt.databinding.FragmentSetTimesBinding
import com.example.waqt.prefs.PrayerPrefs
import com.example.waqt.utils.TimeUtils.formatTo12Hour
import com.example.waqt.utils.TimeUtils.parseTime
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetTimesFragment : Fragment() {

    private var _binding: FragmentSetTimesBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var prefs: PrayerPrefs

    private val times = mutableMapOf(
        "fajr" to "04:32",
        "dhuhr" to "12:15",
        "asr" to "16:15",
        "maghrib" to "18:48",
        "isha" to "20:30",
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSetTimesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadSavedTimes()
        updateTimeViews()

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        setupPrayerCardClicks()

        binding.btnSave.setOnClickListener {
            saveAndSchedule()
        }
    }

    private fun loadSavedTimes() {
        times["fajr"] = prefs.fajr
        times["dhuhr"] = prefs.dhuhr
        times["asr"] = prefs.asr
        times["maghrib"] = prefs.maghrib
        times["isha"] = prefs.isha
    }

    private fun updateTimeViews() {
        binding.tvFajrTime.text = formatTo12Hour(times["fajr"]!!)
        binding.tvDhuhrTime.text = formatTo12Hour(times["dhuhr"]!!)
        binding.tvAsrTime.text = formatTo12Hour(times["asr"]!!)
        binding.tvMaghribTime.text = formatTo12Hour(times["maghrib"]!!)
        binding.tvIshaTime.text = formatTo12Hour(times["isha"]!!)
    }

    private fun setupPrayerCardClicks() {
        val cards = listOf(
            Triple(binding.cardFajr, "fajr", R.drawable.iv_fajr_icon),
            Triple(binding.cardDhuhr, "dhuhr", R.drawable.iv_dhuhr_icon),
            Triple(binding.cardAsr, "asr", R.drawable.iv_dhuhr_icon),
            Triple(binding.cardMaghrib, "maghrib", R.drawable.iv_maghrib_icon),
            Triple(binding.cardIsha, "isha", R.drawable.iv_isha_icon),
        )

        for ((card, key, icon) in cards) {
            card.setOnClickListener {
                val (h, m) = parseTime(times[key]!!)
                BottomSheetTimePickerFragment.newInstance(
                    prayerName = key.replaceFirstChar { it.uppercase() },
                    prayerIcon = icon,
                    initialHour = h,
                    initialMinute = m,
                ) { hour, minute ->
                    val formatted = "%02d:%02d".format(hour, minute)
                    times[key] = formatted
                    updateTimeViews()
                }.show(childFragmentManager, "timePicker_$key")
            }
        }
    }

    private fun saveAndSchedule() {
        prefs.saveAll(
            fajr = times["fajr"]!!,
            dhuhr = times["dhuhr"]!!,
            asr = times["asr"]!!,
            maghrib = times["maghrib"]!!,
            isha = times["isha"]!!,
        )

        AlarmScheduler.scheduleAll(requireContext(), times)

        Toast.makeText(requireContext(), getString(R.string.alarms_scheduled), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
