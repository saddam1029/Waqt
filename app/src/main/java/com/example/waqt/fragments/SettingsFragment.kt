package com.example.waqt.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.waqt.R
import com.example.waqt.databinding.FragmentSettingsBinding
import com.example.waqt.prefs.PrayerPrefs
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: PrayerPrefs
    private var isUpdatingUi = false

    private val soundPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }
            uri?.let {
                prefs.adhanSound = it.toString()
                val ringtone = RingtoneManager.getRingtone(requireContext(), it)
                val name = ringtone.getTitle(requireContext())
                prefs.adhanSoundName = name
                binding.tvAdhanSoundName.text = name
            }
        }
    }

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
        updatePermissionStatus()
        setupSoundSelection()
        observeSettingsChanges()
    }

    private fun setupSoundSelection() {
        binding.tvAdhanSoundName.text = prefs.adhanSoundName
        binding.clSelectSound.setOnClickListener {
            val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION)
                putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.adhan_sound))
                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, prefs.adhanSound?.let { Uri.parse(it) })
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
            }
            soundPickerLauncher.launch(intent)
        }
    }

    private fun updatePermissionStatus() {
        val isGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permissions not required before Android 13
        }

        if (isGranted) {
            binding.tvPermissions.text = getString(R.string.granted)
            binding.tvPermissions.setTextColor(ContextCompat.getColor(requireContext(), R.color.badge_text))
            binding.tvPermissions.setBackgroundResource(R.drawable.bg_badge)
        } else {
            binding.tvPermissions.text = getString(R.string.not_granted)
            binding.tvPermissions.setTextColor(ContextCompat.getColor(requireContext(), R.color.badge_text_error))
            binding.tvPermissions.setBackgroundResource(R.drawable.bg_badge_error)
        }
    }

    private fun setupSwitches() {
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
        
        updateSwitchesFromPrefs()
    }

    private fun observeSettingsChanges() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                prefs.changes.collect {
                    updateSwitchesFromPrefs()
                }
            }
        }
    }

    private fun updateSwitchesFromPrefs() {
        isUpdatingUi = true
        binding.switchFajr.isChecked = prefs.fajrEnabled
        binding.switchDhuhr.isChecked = prefs.dhuhrEnabled
        binding.switchAsr.isChecked = prefs.asrEnabled
        binding.switchMaghrib.isChecked = prefs.maghribEnabled
        binding.switchIsha.isChecked = prefs.ishaEnabled
        isUpdatingUi = false
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            updateSwitchesFromPrefs()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
