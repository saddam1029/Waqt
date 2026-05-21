package com.example.waqt.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.waqt.databinding.FragmentSettingsBinding
import com.example.waqt.prefs.PrayerPrefs

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: PrayerPrefs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PrayerPrefs(requireContext())

        setupSwitches()
    }

    private fun setupSwitches() {
        // Prayer Alarms
        binding.switchFajr.isChecked = prefs.fajrEnabled
        binding.switchFajr.setOnCheckedChangeListener { _, isChecked ->
            prefs.fajrEnabled = isChecked
        }

        binding.switchDhuhr.isChecked = prefs.dhuhrEnabled
        binding.switchDhuhr.setOnCheckedChangeListener { _, isChecked ->
            prefs.dhuhrEnabled = isChecked
        }

        binding.switchAsr.isChecked = prefs.asrEnabled
        binding.switchAsr.setOnCheckedChangeListener { _, isChecked ->
            prefs.asrEnabled = isChecked
        }

        binding.switchMaghrib.isChecked = prefs.maghribEnabled
        binding.switchMaghrib.setOnCheckedChangeListener { _, isChecked ->
            prefs.maghribEnabled = isChecked
        }

        binding.switchIsha.isChecked = prefs.ishaEnabled
        binding.switchIsha.setOnCheckedChangeListener { _, isChecked ->
            prefs.ishaEnabled = isChecked
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
