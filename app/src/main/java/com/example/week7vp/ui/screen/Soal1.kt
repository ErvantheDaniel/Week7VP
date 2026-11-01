@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.example.week7vp.ui.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.week7vp.R
import com.example.week7vp.model.ClubProjectsRoute
import com.example.week7vp.model.CommunitiesRoute
import com.example.week7vp.model.Community
import com.example.week7vp.model.DashboardRoute
import com.example.week7vp.model.Project
import com.example.week7vp.model.ProjectDetailRoute
import com.example.week7vp.model.techOptions
import com.example.week7vp.viewmodel.Soal1Viewmodel
import kotlinx.coroutines.launch

private val Blue = Color(0xFF2563EB)
private val Red = Color(0xFFDC2626)
private val Gray = Color(0xFF64748B)

@Composable
private fun Soal1Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(primary = Blue, background = Color(0xFFF1F6FA)),
        content = content
    )
}

@Composable
fun Soal1(viewModel: Soal1Viewmodel = viewModel()) {
    val nav = rememberNavController()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val tabs = listOf<Triple<Any, String, Int>>(
        Triple(DashboardRoute, "Dashboard", R.drawable.home),
        Triple(CommunitiesRoute, "Communities", R.drawable.communities)
    )
    val showBottomBar = tabs.any { destination?.hasRoute(it.first::class) == true }

    Soal1Theme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbar) },
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        tabs.forEach { (route, label, icon) ->
                            NavigationBarItem(
                                selected = destination?.hierarchy?.any { it.hasRoute(route::class) } == true,
                                onClick = {
                                    nav.navigate(route) {
                                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Image(painter = painterResource(id = icon), contentDescription = label, modifier = Modifier.size(24.dp)) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = DashboardRoute,
                modifier = Modifier.padding(padding).consumeWindowInsets(padding)
            ) {
                composable<DashboardRoute> {
                    DashboardScreen(
                        joined = viewModel.joinedCommunities,
                        projects = viewModel.projects,
                        communityName = { viewModel.community(it)?.name ?: "" },
                        projectCount = { viewModel.projectsOf(it).size },
                        onProjectClick = { nav.navigate(ProjectDetailRoute(it)) }
                    )
                }

                composable<CommunitiesRoute> {
                    CommunitiesScreen(
                        communities = viewModel.communities,
                        onToggle = viewModel::toggleJoin,
                        onOpen = { community ->
                            if (community.joined) {
                                nav.navigate(ClubProjectsRoute(community.id))
                            } else {
                                scope.launch {
                                    snackbar.currentSnackbarData?.dismiss()
                                    snackbar.showSnackbar("Harap bergabung ke komunitas terlebih dahulu!", actionLabel = "OK")
                                }
                            }
                        }
                    )
                }

                composable<ClubProjectsRoute> { entry ->
                    val route = entry.toRoute<ClubProjectsRoute>()
                    val community = viewModel.community(route.communityId) ?: return@composable
                    ClubProjectsScreen(
                        community = community,
                        projects = viewModel.projectsOf(community.id),
                        onBack = { nav.popBackStack() },
                        onProjectClick = { nav.navigate(ProjectDetailRoute(it)) },
                        onDelete = viewModel::deleteProject,
                        onAdd = { title, desc, stack -> viewModel.addProject(community.id, title, desc, stack) }
                    )
                }

                composable<ProjectDetailRoute> { entry ->
                    val route = entry.toRoute<ProjectDetailRoute>()
                    val project = viewModel.project(route.projectId) ?: return@composable
                    ProjectDetailScreen(
                        project = project,
                        communityName = viewModel.community(project.communityId)?.name ?: "",
                        onBack = { nav.popBackStack() },
                        onToggle = { viewModel.toggleStatus(project.id) }
                    )
                }
            }
        }
    }
}

// Komponen umum
@Composable
private fun Header(title: String, subtitle: String, trailing: @Composable () -> Unit) {
    Row(
        Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 12.sp, color = Gray)
        }
        trailing()
    }
}

@Composable
private fun AppCard(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    val colors = CardDefaults.cardColors(containerColor = Color.White)
    val elevation = CardDefaults.cardElevation(2.dp)
    if (onClick == null) {
        Card(modifier = modifier, shape = shape, colors = colors, elevation = elevation) {
            Column(Modifier.padding(14.dp), content = content)
        }
    } else {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors, elevation = elevation) {
            Column(Modifier.padding(14.dp), content = content)
        }
    }
}

@Composable
private fun Pill(text: String, bg: Color, fg: Color) {
    Text(
        text, color = fg, fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
        modifier = Modifier.background(bg, RoundedCornerShape(50)).padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

@Composable
private fun StatusPill(completed: Boolean) {
    if (completed) Pill("● Completed", Color(0xFFDCFCE7), Color(0xFF15803D))
    else Pill("● In Progress", Color(0xFFFEF3C7), Color(0xFFB45309))
}

@Composable
private fun TechTags(tags: List<String>) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        tags.forEach { Pill(it, Color(0xFFF1F5F9), Color(0xFF334155)) }
    }
}

@Composable
private fun IconBox(@DrawableRes icon: Int, color: Color) {
    Box(
        Modifier.size(40.dp).background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(id = icon), contentDescription = null, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun Avatar(size: Int, bg: Color, fg: Color) {
    Box(Modifier.size(size.dp).background(bg, CircleShape), contentAlignment = Alignment.Center) {
        Text("AP", color = fg, fontWeight = FontWeight.Bold, fontSize = (size / 3).sp)
    }
}

@Composable
private fun SectionTitle(text: String, trailing: String = "") {
    Row(Modifier.fillMaxWidth().padding(top = 6.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(trailing, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Blue)
    }
}

@Composable
private fun EmptyText(text: String) {
    Text(text, color = Gray, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun ProjectCard(project: Project, communityName: String?, onClick: () -> Unit, onDelete: (() -> Unit)?) {
    val compact = communityName != null
    AppCard(onClick = onClick) {
        Row(verticalAlignment = Alignment.Top) {
            IconBox(R.drawable.folder, Blue)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                if (communityName != null) {
                    Text(communityName, color = Blue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Text(project.title, fontWeight = FontWeight.Bold)
                if (!compact) {
                    Text(project.description, fontSize = 12.sp, color = Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            if (onDelete != null) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp),
                    colors = IconButtonDefaults.iconButtonColors(containerColor = Red.copy(alpha = 0.12f), contentColor = Red)
                ) {
                    Image(painter = painterResource(id = R.drawable.delete), contentDescription = "Delete", modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        if (compact) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(project.techStack.joinToString(" • "), fontSize = 11.sp, color = Gray, modifier = Modifier.weight(1f))
                StatusPill(project.completed)
            }
        } else {
            TechTags(project.techStack)
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text("ID: ${project.id}", fontSize = 10.sp, color = Gray)
                StatusPill(project.completed)
            }
        }
    }
}

@Composable
private fun StatCard(@DrawableRes icon: Int, value: Int, label: String, sub: String, modifier: Modifier) {
    AppCard(modifier) {
        IconBox(icon, Blue)
        Spacer(Modifier.height(8.dp))
        Text(value.toString(), fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(sub, fontSize = 10.sp, color = Gray)
    }
}

@Composable
private fun JoinButton(joined: Boolean, onClick: () -> Unit) {
    if (joined) {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.height(36.dp),
            border = BorderStroke(1.dp, Red),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Red.copy(alpha = 0.08f), contentColor = Red),
            contentPadding = PaddingValues(horizontal = 14.dp)
        ) {
            Image(painter = painterResource(id = R.drawable.check), contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Joined", fontSize = 12.sp)
        }
    } else {
        Button(onClick = onClick, modifier = Modifier.height(36.dp), contentPadding = PaddingValues(horizontal = 14.dp)) {
            Image(painter = painterResource(id = R.drawable.add), contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("Join", fontSize = 12.sp)
        }
    }
}

// Layar 1: Dashboard
@Composable
private fun DashboardScreen(
    joined: List<Community>,
    projects: List<Project>,
    communityName: (String) -> String,
    projectCount: (String) -> Int,
    onProjectClick: (String) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Header("CampusTech Hub", "Student Profile & Portfolio Dashboard") { Avatar(32, Blue, Color.White) }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Box(
                    Modifier.fillMaxWidth()
                        .background(Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(56, Color.White, Blue)
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Pill("MAHASISWA AKTIF", Color.White.copy(alpha = 0.2f), Color.White)
                            Spacer(Modifier.height(4.dp))
                            Text("Aditya Pratama", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Teknik Informatika • Angkatan 2022", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            Text("NIM: 22001045", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        }
                    }
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatCard(R.drawable.communities, joined.size, "Joined Clubs", "Komunitas yang Diikuti", Modifier.weight(1f))
                    StatCard(R.drawable.folder, projects.size, "Total Projects", "Seluruh Proyek Dibuat", Modifier.weight(1f))
                }
            }
            item { SectionTitle("★ Semua Komunitas yang Diikuti (${joined.size})", "Status: Joined") }
            if (joined.isEmpty()) item { EmptyText("Belum bergabung dengan komunitas") }
            items(joined, key = { it.id }) { c ->
                AppCard {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBox(c.icon, c.color)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(c.name, fontWeight = FontWeight.Bold)
                            Text("${c.category} • ${projectCount(c.id)} Proyek", fontSize = 11.sp, color = Gray)
                        }
                        Pill("Joined ✓", Red.copy(alpha = 0.1f), Red)
                    }
                }
            }
            item { SectionTitle("Semua Proyek Mahasiswa (${projects.size})", "Total: ${projects.size} Proyek") }
            if (projects.isEmpty()) item { EmptyText("Belum ada proyek yang dicatat") }
            items(projects, key = { it.id }) { p ->
                ProjectCard(p, communityName(p.communityId), { onProjectClick(p.id) }, null)
            }
        }
    }
}

// Layar 2: Communities
@Composable
private fun CommunitiesScreen(communities: List<Community>, onToggle: (String) -> Unit, onOpen: (Community) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Header("Communities Directory", "Pilih komunitas untuk membuka Club Projects") {
            Image(painter = painterResource(id = R.drawable.search), contentDescription = "Search", modifier = Modifier.size(24.dp))
        }
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(communities, key = { it.id }) { c ->
                AppCard(onClick = { onOpen(c) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconBox(c.icon, c.color)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(c.name, fontWeight = FontWeight.Bold)
                            Text("${c.category} • ${c.members} Mahasiswa", fontSize = 11.sp, color = Gray)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(c.description, fontSize = 12.sp, color = Gray)
                    HorizontalDivider(Modifier.padding(vertical = 10.dp))
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val tint = if (c.joined) Blue else Color.Gray
                            Image(
                                painter = painterResource(id = if (c.joined) R.drawable.unlock else R.drawable.lock),
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                if (c.joined) "Unlocked • Buka Proyek →" else "Terkunci (Gate Akses)",
                                fontSize = 11.sp, color = tint, fontWeight = FontWeight.SemiBold
                            )
                        }
                        JoinButton(c.joined) { onToggle(c.id) }
                    }
                }
            }
        }
    }
}

// Layar 3: Club Projects
@Composable
private fun AddProjectDialog(communityName: String, onCancel: () -> Unit, onSave: (String, String, List<String>) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val selected = remember { mutableStateListOf<String>() }

    AlertDialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        title = { Text("Add Project", fontWeight = FontWeight.Bold) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Komunitas: $communityName", fontSize = 12.sp, color = Gray)
                Text("Judul Proyek *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    singleLine = true, modifier = Modifier.fillMaxWidth()
                )
                Text("Deskripsi Proyek (Minimal 3 Baris) *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = desc, onValueChange = { desc = it },
                    minLines = 3, modifier = Modifier.fillMaxWidth()
                )
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("Tech Stack (Pilihan Multi-Selection) *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("${selected.size} Dipilih", fontSize = 12.sp, color = Blue)
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    techOptions.forEach { tech ->
                        val on = tech in selected
                        FilterChip(
                            selected = on,
                            onClick = { if (on) selected.remove(tech) else selected.add(tech) },
                            label = { Text(tech) },
                            leadingIcon = if (on) {
                                { Image(painter = painterResource(id = R.drawable.check), contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, desc, selected.toList()) },
                enabled = title.isNotBlank() && desc.isNotBlank() && selected.isNotEmpty()
            ) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancel") } }
    )
}

@Composable
private fun ClubProjectsScreen(
    community: Community,
    projects: List<Project>,
    onBack: () -> Unit,
    onProjectClick: (String) -> Unit,
    onDelete: (String) -> Unit,
    onAdd: (String, String, List<String>) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("${community.name} Projects", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(community.category, fontSize = 11.sp, color = Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Image(painter = painterResource(id = R.drawable.back), contentDescription = "Back", modifier = Modifier.size(24.dp))
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Image(painter = painterResource(id = R.drawable.add), contentDescription = "Add", modifier = Modifier.size(24.dp)) },
                text = { Text("Add Project") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Row(
                    Modifier.fillMaxWidth().background(Blue.copy(alpha = 0.1f), RoundedCornerShape(16.dp)).padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(community.category, fontWeight = FontWeight.Bold, color = Blue)
                        Text("${community.members} Anggota • ${projects.size} Proyek Aktif", fontSize = 11.sp, color = Blue)
                    }
                    Pill("Joined ✓", Red.copy(alpha = 0.1f), Red)
                }
            }
            item { SectionTitle("Daftar Proyek Mahasiswa (${projects.size})") }
            if (projects.isEmpty()) item { EmptyText("Belum ada proyek yang dicatat") }
            items(projects, key = { it.id }) { p ->
                ProjectCard(p, null, { onProjectClick(p.id) }, { onDelete(p.id) })
            }
        }
    }

    if (showDialog) {
        AddProjectDialog(
            communityName = "${community.name} (${community.category})",
            onCancel = { showDialog = false },
            onSave = { title, desc, stack ->
                onAdd(title, desc, stack)
                showDialog = false
            }
        )
    }
}

// Layar 4: Project Detail
@Composable
private fun ProjectDetailScreen(project: Project, communityName: String, onBack: () -> Unit, onToggle: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Project Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Image(painter = painterResource(id = R.drawable.back), contentDescription = "Back", modifier = Modifier.size(24.dp))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                Modifier.fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B))), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Pill(communityName, Blue.copy(alpha = 0.4f), Color.White)
                Spacer(Modifier.height(8.dp))
                Text(project.title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }
            AppCard(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text("Mark as Done", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        StatusPill(project.completed)
                    }
                    Switch(checked = project.completed, onCheckedChange = { onToggle() })
                }
            }
            AppCard(Modifier.fillMaxWidth()) {
                Text("Deskripsi Lengkap Proyek", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(project.description, fontSize = 13.sp, color = Gray)
            }
            AppCard(Modifier.fillMaxWidth()) {
                Text("Tech Stack Terpilih", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                TechTags(project.techStack)
            }
        }
    }
}

// Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Soal1Preview() {
    Soal1()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun DashboardPreview() {
    val vm = viewModel<Soal1Viewmodel>()
    Soal1Theme {
        DashboardScreen(
            joined = vm.joinedCommunities,
            projects = vm.projects,
            communityName = { vm.community(it)?.name ?: "" },
            projectCount = { vm.projectsOf(it).size },
            onProjectClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CommunitiesPreview() {
    val vm = viewModel<Soal1Viewmodel>()
    Soal1Theme {
        CommunitiesScreen(communities = vm.communities, onToggle = {}, onOpen = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ClubProjectsPreview() {
    val vm = viewModel<Soal1Viewmodel>()
    Soal1Theme {
        ClubProjectsScreen(
            community = vm.communities[0],
            projects = vm.projectsOf("gdsc"),
            onBack = {},
            onProjectClick = {},
            onDelete = {},
            onAdd = { _, _, _ -> }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddProjectDialogPreview() {
    Soal1Theme {
        AddProjectDialog(communityName = "GDSC (Google Developer Student Club)", onCancel = {}, onSave = { _, _, _ -> })
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProjectDetailPreview() {
    val vm = viewModel<Soal1Viewmodel>()
    Soal1Theme {
        ProjectDetailScreen(project = vm.projects[0], communityName = "GDSC", onBack = {}, onToggle = {})
    }
}