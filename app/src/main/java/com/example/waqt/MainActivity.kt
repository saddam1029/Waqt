package com.example.waqt

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.waqt.fragments.HomeFragment
import com.example.waqt.fragments.SetTimesFragment
import com.example.waqt.fragments.SettingsFragment
import com.example.waqt.fragments.TasbeehFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val setTimesFragment = SetTimesFragment()
    private val tasbeehFragment = TasbeehFragment()
    private val settingsFragment = SettingsFragment()
    private var activeFragment: Fragment = homeFragment

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission result handled by system
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        setupNavigation()
        checkAndRequestPermissions()
    }

    private fun setupNavigation() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        supportFragmentManager.beginTransaction().apply {
            add(R.id.nav_host_fragment, settingsFragment, "settings").hide(settingsFragment)
            add(R.id.nav_host_fragment, tasbeehFragment, "tasbeeh").hide(tasbeehFragment)
            add(R.id.nav_host_fragment, setTimesFragment, "setTimes").hide(setTimesFragment)
            add(R.id.nav_host_fragment, homeFragment, "home")
            commit()
        }

        bottomNav.setOnItemSelectedListener { item ->
            val target = when (item.itemId) {
                R.id.navigation_home -> homeFragment
                R.id.navigation_set_times -> setTimesFragment
                R.id.navigation_tasbeeh -> tasbeehFragment
                R.id.navigation_settings -> settingsFragment
                else -> homeFragment
            }

            if (target != activeFragment) {
                animateBottomNavItem(item.itemId)
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                    .hide(activeFragment)
                    .show(target)
                    .commit()
                activeFragment = target
                true
            } else {
                false
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun animateBottomNavItem(itemId: Int) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val menuView = bottomNav.getChildAt(0) as ViewGroup

        for (i in 0 until menuView.childCount) {
            val itemView = menuView.getChildAt(i)
            if (itemView.id == itemId) {
                // Try to find the icon view, fallback to the item view itself
                val icon = itemView.findViewById<View>(com.google.android.material.R.id.navigation_bar_item_icon_view)
                    ?: itemView

                icon.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(150)
                    .setInterpolator(OvershootInterpolator(3f))
                    .withEndAction {
                        icon.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(150)
                            .start()
                    }
                    .start()
                break
            }
        }
    }
}
