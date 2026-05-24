package com.example.waqt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.waqt.R
import com.example.waqt.databinding.FragmentHomeBinding
import com.example.waqt.model.NextPrayerInfo
import com.example.waqt.model.PrayerTimes
import com.example.waqt.utils.TimeUtils.formatTo12Hour
import com.example.waqt.utils.TimeUtils.parseTime
import com.example.waqt.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private var isUpdatingUi = false

    @javax.inject.Inject
    lateinit var prefs: com.example.waqt.prefs.PrayerPrefs

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setupSwitchListeners()
        setupThemeToggle()
        updateThemeIcon()
    }

    private fun setupThemeToggle() {
        binding.ivTheme.setOnClickListener {
            prefs.isDarkMode = !prefs.isDarkMode
            updateTheme()
        }
    }

    private fun updateThemeIcon() {
        if (prefs.isDarkMode) {
            binding.ivTheme.setImageResource(R.drawable.iv_dhuhr_icon)
        } else {
            binding.ivTheme.setImageResource(R.drawable.iv_theme_night)
        }
    }

    private fun updateTheme() {
        val mode = if (prefs.isDarkMode) {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
        } else {
            androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        }
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(mode)
    }

    private fun setupSwitchListeners() {
        binding.switchFajr.setOnCheckedChangeListener { _, isChecked -> 
            if (!isUpdatingUi) prefs.fajrEnabled = isChecked 
        }
        binding.switchDhuhr.setOnCheckedChangeListener { _, isChecked -> 
            if (!isUpdatingUi) prefs.dhuhrEnabled = isChecked 
        }
        binding.switchAsr.setOnCheckedChangeListener { _, isChecked -> 
            if (!isUpdatingUi) prefs.asrEnabled = isChecked 
        }
        binding.switchMaghrib.setOnCheckedChangeListener { _, isChecked -> 
            if (!isUpdatingUi) prefs.maghribEnabled = isChecked 
        }
        binding.switchIsha.setOnCheckedChangeListener { _, isChecked -> 
            if (!isUpdatingUi) prefs.ishaEnabled = isChecked 
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.prayerTimes.collect { times ->
                        updatePrayerTimesUI(times)
                    }
                }
                launch {
                    viewModel.nextPrayer.collect { nextInfo ->
                        nextInfo?.let { updateNextPrayerUI(it) }
                    }
                }
                launch {
                    prefs.changes.collect {
                        viewModel.refreshTimes()
                        updatePrayerListStatus(viewModel.prayerTimes.value)
                        updateThemeIcon()
                    }
                }
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            viewModel.refreshTimes()
            updatePrayerListStatus(viewModel.prayerTimes.value)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshTimes()
    }

    private fun updatePrayerTimesUI(times: PrayerTimes) {
        binding.tvFajrTime.text = formatTo12Hour(times.fajr)
        binding.tvDhuhrTime.text = formatTo12Hour(times.dhuhr)
        binding.tvAsrTime.text = formatTo12Hour(times.asr)
        binding.tvMaghribTime.text = formatTo12Hour(times.maghrib)
        binding.tvIshaTime.text = formatTo12Hour(times.isha)
        
        updatePrayerListStatus(times)
    }

    private fun updateNextPrayerUI(info: NextPrayerInfo) {
        binding.tvNextPrayerName.text = info.name
        binding.tvNextPrayerTime.text = formatTo12Hour(info.time)
        binding.tvTimeRemaining.text = info.timeRemaining
        
        highlightNextPrayer(info.name)
    }

    private fun updatePrayerListStatus(times: PrayerTimes) {
        isUpdatingUi = true
        val now = Calendar.getInstance()
        val prayerItems = listOf(
            Triple("Fajr", times.fajr, Pair(binding.switchFajr, binding.ivFajrComplete)),
            Triple("Dhuhr", times.dhuhr, Pair(binding.switchDhuhr, binding.ivDhuhrComplete)),
            Triple("Asr", times.asr, Pair(binding.switchAsr, binding.ivAsrComplete)),
            Triple("Maghrib", times.maghrib, Pair(binding.switchMaghrib, binding.ivMaghribComplete)),
            Triple("Isha", times.isha, Pair(binding.switchIsha, binding.ivIshaComplete))
        )

        for (item in prayerItems) {
            val (h, m) = parseTime(item.second)
            val prayerCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, h)
                set(Calendar.MINUTE, m)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val switch = item.third.first
            val completeIcon = item.third.second

            // Set switch state from prefs
            switch.isChecked = when(item.first) {
                "Fajr" -> prefs.fajrEnabled
                "Dhuhr" -> prefs.dhuhrEnabled
                "Asr" -> prefs.asrEnabled
                "Maghrib" -> prefs.maghribEnabled
                "Isha" -> prefs.ishaEnabled
                else -> true
            }

            if (now.after(prayerCal)) {
                switch.visibility = View.GONE
                completeIcon.visibility = View.VISIBLE
            } else {
                switch.visibility = View.VISIBLE
                completeIcon.visibility = View.GONE
            }
        }
        isUpdatingUi = false
    }

    private fun highlightNextPrayer(nextName: String) {
        val goldColor = ContextCompat.getColor(requireContext(), R.color.gold)
        val strokeWidth = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._1sdp)

        val cards = mapOf(
            "Fajr" to binding.cardFajr,
            "Dhuhr" to binding.cardDhuhr,
            "Asr" to binding.cardAsr,
            "Maghrib" to binding.cardMaghrib,
            "Isha" to binding.cardIsha
        )

        for ((name, card) in cards) {
            if (name == nextName) {
                card.strokeColor = goldColor
                card.strokeWidth = strokeWidth
            } else {
                card.strokeWidth = 0
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
