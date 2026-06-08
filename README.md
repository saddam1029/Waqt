<div align="center">

<p align="center">
  <img src="app/src/main/res/drawable/waqt.png" width="150"/>
</p>
<img src="assets/app_icon.png" alt="Waqt App Icon" width="100dp"/>

# وقت — Waqt
### Prayer Time Reminder App

A clean, modern, and minimal Android app for Muslims to set and get notified for all 5 daily prayers — built with Kotlin, MVVM, and Material Design 3.

<br/>

[![Platform](https://img.shields.io/badge/Platform-Android-3D6B60?style=for-the-badge&logo=android&logoColor=white)](https://android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-C4A882?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-26-3D6B60?style=for-the-badge)](https://developer.android.com)
[![Material](https://img.shields.io/badge/Material%20Design-3-C4A882?style=for-the-badge)](https://m3.material.io)
[![License](https://img.shields.io/badge/License-MIT-3D6B60?style=for-the-badge)](LICENSE)

<br/>

[Download APK](#download) · [Features](#features) · [Screenshots](#screenshots) · [Tech Stack](#tech-stack) · [Architecture](#architecture)

</div>

---

## Screenshots

### Light Theme

| Home | Prayer Times | Tasbeeh |
|:----:|:------------:|:-------:|
| <img src="app/src/main/res/drawable/light_home.png" width="200"/> | <img src="app/src/main/res/drawable/light_prayer.png" width="200"/> | <img src="app/src/main/res/drawable/light_tasbeeh.png" width="200"/> |

| Daily Dhikr | Settings |
|:-----------:|:--------:|
| <img src="app/src/main/res/drawable/light_dhiker.png" width="200"/> | <img src="app/src/main/res/drawable/light_setting.png" width="200"/> |

### Dark Theme

| Home | Prayer Times | Tasbeeh |
|:----:|:------------:|:-------:|
| <img src="app/src/main/res/drawable/dark_home.png" width="200"/> | <img src="app/src/main/res/drawable/dark_prayer.png" width="200"/> | <img src="app/src/main/res/drawable/dark_tasbeeh.png" width="200"/> |

| Daily Dhikr | Settings |
|:-----------:|:--------:|
| <img src="app/src/main/res/drawable/dark_dhiker.png" width="200"/> | <img src="app/src/main/res/drawable/dark_setting.png" width="200"/> |
---

## Features

- 🕌 **5 Daily Prayer Alarms** — Set Fajr, Dhuhr, Asr, Maghrib & Isha manually with a beautiful time picker
- 🔔 **Precise Notifications** — Exact alarms via `AlarmManager.setAlarmClock()` — works even in Doze mode
- ⏱️ **Live Countdown** — Home screen shows real-time countdown to the next prayer
- 📿 **Tasbeeh Counter** — Count SubhanAllah, Alhamdulillah & Allahu Akbar with smooth animations and haptic feedback
- 🤲 **Daily Dhikr** — A beautiful daily reminder card with Arabic text, transliteration, and meaning
- 🌙 **Dark Mode** — Full dark theme support, clean and easy on the eyes
- 💾 **Offline First** — All data saved locally using SharedPreferences — no internet needed

---

## Architecture

Waqt follows clean **MVVM architecture** with a single-responsibility folder structure.

```
com.example.waqt/
├── fragments/          # UI Fragments (Home, SetTimes, Tasbeeh, Settings)
├── alarm/              # AlarmScheduler + AlarmReceiver
├── prefs/              # PrayerPrefs (SharedPreferences wrapper)
├── notification/       # NotificationHelper
└── util/               # Extensions, TimeUtils, Constants
```

```
┌─────────────────────────────────────┐
│           UI (Fragments)            │
│  HomeFragment  │  SetTimesFragment  │
└────────────────┬────────────────────┘
                 │ observes
┌────────────────▼────────────────────┐
│            ViewModel                │
│     LiveData / StateFlow            │
└────────────────┬────────────────────┘
                 │
┌────────────────▼────────────────────┐
│    PrayerPrefs (SharedPreferences)  │
│    AlarmScheduler (AlarmManager)    │
└─────────────────────────────────────┘
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | XML + ViewBinding |
| Architecture | MVVM |
| Navigation | Navigation Component |
| Local Storage | SharedPreferences |
| Alarms | AlarmManager (`setAlarmClock`) |
| Async | Kotlin Coroutines |
| Animations | ObjectAnimator + ValueAnimator |
| UI Components | Material Design 3 |

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog or newer
- Min SDK 26 (Android 8.0)
- Kotlin 1.9+

### Run Locally

```bash
# 1. Clone the repo
git clone https://github.com/YOUR_USERNAME/waqt.git

# 2. Open in Android Studio
# File → Open → select the waqt folder

# 3. Build & Run
# Click Run or press Shift + F10
```

### Permissions Required

```xml
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.VIBRATE" />
```

---

## Download

> Coming soon on Google Play Store.

In the meantime, download the latest debug APK from [Releases](https://github.com/YOUR_USERNAME/waqt/releases).

---

## Roadmap

- [x] Manual prayer time setting
- [x] Exact alarm notifications
- [x] Live countdown to next prayer
- [x] Dark mode
- [x] Tasbeeh counter with animations
- [x] Daily Dhikr card
- [ ] Home screen widget
- [ ] Adhan sound selection
- [ ] Play Store release

---

## Contributing

Contributions, issues, and feature requests are welcome.
Feel free to open an [issue](https://github.com/YOUR_USERNAME/waqt/issues) or submit a pull request.

---

## License

```
MIT License

Copyright (c) 2025 YOUR_NAME

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software.
```

---

<div align="center">

Made with ❤️ for the Muslim community

**If this project helped you, please consider giving it a ⭐**

</div>
