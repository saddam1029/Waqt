package com.example.waqt.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.waqt.R
import com.example.waqt.databinding.FragmentTasbeehBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasbeehFragment : Fragment() {

    private var _binding: FragmentTasbeehBinding? = null
    private val binding get() = _binding!!

    private var count = 0
    private val target = 33
    private var currentDhikrIndex = 0

    private val dhikrNames = listOf(
        R.string.subhanallah,
        R.string.alhamdulillah,
        R.string.allahuakbar,
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTasbeehBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChoices()
        updateUI()

        binding.cvTapArea.setOnClickListener {
            onTap()
        }

        binding.clCounter.setOnClickListener {
            onTap()
        }

        binding.ivRefresh.setOnClickListener { resetAll() }
        binding.btnReset.setOnClickListener { resetAll() }
    }

    private fun setupChoices() {
        val choices = listOf(binding.choiceSubhan, binding.choiceAlhamd, binding.choiceAllahu)
        choices.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                currentDhikrIndex = index
                count = 0
                updateUI()
            }
        }
    }

    private fun updateUI() {
        updateDhikrLabel()
        updateProgress(animate = false)
        updateChoiceSelectionUI()
        updateMilestonePills()
        binding.tvCount.text = count.toString()
    }

    private fun onTap() {
        if (count < target) {
            count++
            binding.tvCount.text = count.toString()

            animateTapButton()
            animateCountText()
            vibrate()
            updateProgress(animate = true)

            if (count == target) {
                onMilestoneReached()
            }
        }
    }

    private fun animateTapButton() {
        val scaleDownX = ObjectAnimator.ofFloat(binding.cvTapArea, "scaleX", 1f, 0.95f)
        val scaleDownY = ObjectAnimator.ofFloat(binding.cvTapArea, "scaleY", 1f, 0.95f)
        val scaleUpX = ObjectAnimator.ofFloat(binding.cvTapArea, "scaleX", 0.95f, 1f)
        val scaleUpY = ObjectAnimator.ofFloat(binding.cvTapArea, "scaleY", 0.95f, 1f)

        scaleDownX.duration = 60
        scaleDownY.duration = 60
        scaleUpX.duration = 150
        scaleUpY.duration = 150
        scaleUpX.interpolator = OvershootInterpolator(2f)
        scaleUpY.interpolator = OvershootInterpolator(2f)

        val down = AnimatorSet().apply { playTogether(scaleDownX, scaleDownY) }
        val up = AnimatorSet().apply { playTogether(scaleUpX, scaleUpY) }

        AnimatorSet().apply {
            playSequentially(down, up)
            start()
        }
    }

    private fun animateCountText() {
        val scaleUp = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.tvCount, "scaleX", 1f, 1.2f),
                ObjectAnimator.ofFloat(binding.tvCount, "scaleY", 1f, 1.2f)
            )
            duration = 60
        }
        val scaleDown = AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(binding.tvCount, "scaleX", 1.2f, 1f),
                ObjectAnimator.ofFloat(binding.tvCount, "scaleY", 1.2f, 1f)
            )
            duration = 120
            interpolator = DecelerateInterpolator()
        }
        AnimatorSet().apply {
            playSequentially(scaleUp, scaleDown)
            start()
        }
    }

    private fun updateProgress(animate: Boolean) {
        val progress = ((count.toFloat() / target) * 100).toInt().coerceIn(0, 100)
        if (animate) {
            val animator = ValueAnimator.ofInt(binding.progressRing.progress, progress)
            animator.duration = 200
            animator.interpolator = DecelerateInterpolator()
            animator.addUpdateListener { binding.progressRing.progress = it.animatedValue as Int }
            animator.start()
        } else {
            binding.progressRing.progress = progress
        }
    }

    private fun onMilestoneReached() {
        vibrateMilestone()
        
        // Brief delay before moving to next dhikr
        binding.root.postDelayed(
            {
                if (currentDhikrIndex < (dhikrNames.size - 1)) {
                    currentDhikrIndex++
                    count = 0
                    updateUI()
                } else {
                    // All finished? optionally reset or stay at 33
                }
            },
            500,
        )
    }

    private fun updateChoiceSelectionUI() {
        val activeBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tasbeeh_chip_active)
        val inactiveBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tasbeeh_chip_inactive)
        val activeColor = ContextCompat.getColor(requireContext(), R.color.white)
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.text_primary)

        val choices = listOf(binding.choiceSubhan, binding.choiceAlhamd, binding.choiceAllahu)
        choices.forEachIndexed { i, textView ->
            val isActive = i == currentDhikrIndex
            textView.background = if (isActive) activeBg else inactiveBg
            textView.setTextColor(if (isActive) activeColor else inactiveColor)
        }
    }

    private fun updateMilestonePills() {
        val activeBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tasbeeh_pill_active)
        val inactiveBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_tasbeeh_pill_inactive)
        val activeColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        val inactiveColor = ContextCompat.getColor(requireContext(), R.color.text_secondary)

        val pills = listOf(binding.pillSubhan, binding.pillAlhamd, binding.pillAllahu)
        pills.forEachIndexed { i, textView ->
            val isActive = i == currentDhikrIndex
            textView.background = if (isActive) activeBg else inactiveBg
            textView.setTextColor(if (isActive) activeColor else inactiveColor)
        }
    }

    private fun updateDhikrLabel() {
        val name = getString(dhikrNames[currentDhikrIndex])
        binding.tvDhikrLabel.text = getString(R.string.dhikr_format, target, name)
    }

    private fun resetAll() {
        count = 0
        currentDhikrIndex = 0
        updateUI()
    }

    private fun vibrate() {
        val v = requireContext().getSystemService(Vibrator::class.java) ?: return
        v.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    private fun vibrateMilestone() {
        val v = requireContext().getSystemService(Vibrator::class.java) ?: return
        v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 80, 80, 150), -1))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
