package com.example.week7vp.model

import androidx.compose.ui.graphics.Color
import androidx.annotation.DrawableRes
import kotlinx.serialization.Serializable

@Serializable
object DashboardRoute

@Serializable
object CommunitiesRoute

@Serializable
data class ClubProjectsRoute(val communityId: String)

@Serializable
data class ProjectDetailRoute(val projectId: String)

data class Community(
    val id: String,
    val name: String,
    val category: String,
    val members: Int,
    val description: String,
    @DrawableRes val icon: Int,
    val color: Color,
    val joined: Boolean = false
)

data class Project(
    val id: String,
    val communityId: String,
    val title: String,
    val description: String,
    val techStack: List<String>,
    val completed: Boolean = false
)

val techOptions = listOf("Kotlin", "Jetpack Compose", "Room", "Firebase", "Ktor", "Python", "Google Maps SDK")