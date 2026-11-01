package com.example.week7vp.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.week7vp.R
import com.example.week7vp.model.Community
import com.example.week7vp.model.Project

class Soal1Viewmodel : ViewModel() {

    val communities = mutableStateListOf(
        Community(
            "gdsc", "GDSC", "Google Developer Student Club", 142,
            "Fokus pada ekosistem Google: Android Compose, Flutter, Firebase, dan Google Cloud Platform.",
            R.drawable.code, Color(0xFF2563EB), true
        ),
        Community(
            "cyber", "CyberSecurity Guild", "Security & Pentesting", 88,
            "Eksplorasi ethical hacking, CTF challenges, reverse engineering, dan pertahanan jaringan kampus.",
            R.drawable.security, Color(0xFFDC2626), true
        ),
        Community(
            "ai", "AI Society", "Machine Learning & LLM", 115,
            "Riset terapan deep learning, computer vision, agen cerdas, dan fine-tuning large language models.",
            R.drawable.ai, Color(0xFF7C3AED)
        ),
        Community(
            "mobile", "Mobile Dev Community", "Jetpack Compose & KMP", 95,
            "Pengembangan aplikasi mobile native dengan Jetpack Compose dan Kotlin Multiplatform.",
            R.drawable.phone, Color(0xFF0284C7), true
        ),
        Community(
            "cloud", "Cloud Computing Club", "Cloud & DevOps", 76,
            "Belajar deployment, container, CI/CD, dan arsitektur cloud modern.",
            R.drawable.cloud, Color(0xFF0D9488)
        ),
        Community(
            "data", "Data Science Club", "Data Science & Analytics", 64,
            "Analisis data, visualisasi, dan machine learning untuk menyelesaikan masalah nyata.",
            R.drawable.chart, Color(0xFFEA580C)
        )
    )

    val projects = mutableStateListOf(
        Project(
            "proj-gdsc-01", "gdsc", "Smart Campus Navigation",
            "Navigasi peta gedung perkuliahan, lab komputer, dan fasilitas kampus real-time.",
            listOf("Kotlin", "Jetpack Compose", "Room", "Google Maps SDK")
        ),
        Project(
            "proj-gdsc-02", "gdsc", "Student Attendance QR Scanner",
            "Pemindai barcode & QR presensi kehadiran workshop berbasis CameraX & Firebase.",
            listOf("Jetpack Compose", "Firebase"), true
        ),
        Project(
            "proj-gdsc-03", "gdsc", "Developer RSVP Portal",
            "Portal pendaftaran webinar dan bootcamp teknologi sinkronisasi Ktor API server.",
            listOf("Kotlin", "Ktor", "Jetpack Compose")
        ),
        Project(
            "proj-mobile-01", "mobile", "Habit Tracker",
            "Aplikasi pencatat kebiasaan harian dengan penyimpanan lokal.",
            listOf("Kotlin", "Jetpack Compose", "Room")
        )
    )

    val joinedCommunities: List<Community>
        get() = communities.filter { it.joined }

    fun community(id: String) = communities.find { it.id == id }

    fun project(id: String) = projects.find { it.id == id }

    fun projectsOf(communityId: String) = projects.filter { it.communityId == communityId }

    fun toggleJoin(id: String) {
        val i = communities.indexOfFirst { it.id == id }
        communities[i] = communities[i].copy(joined = !communities[i].joined)
    }

    fun addProject(communityId: String, title: String, description: String, techStack: List<String>) {
        val id = "proj-$communityId-${System.currentTimeMillis() % 100000}"
        projects.add(Project(id, communityId, title.trim(), description.trim(), techStack))
    }

    fun deleteProject(id: String) {
        projects.removeAll { it.id == id }
    }

    fun toggleStatus(id: String) {
        val i = projects.indexOfFirst { it.id == id }
        projects[i] = projects[i].copy(completed = !projects[i].completed)
    }
}