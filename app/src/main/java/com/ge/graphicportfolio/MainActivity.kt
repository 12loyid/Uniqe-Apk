package com.ge.graphicportfolio

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val Purple = Color(0xFF6D28D9)
private val Indigo = Color(0xFF312E81)
private val Navy = Color(0xFF070A1B)
private val CardDark = Color(0xFF11162B)
private val SoftPurple = Color(0xFFB88CFF)
private val Green = Color(0xFF22C55E)

private data class Project(val id: Int, val title: String, val category: String, val description: String, val views: Int)
private data class Service(val id: Int, val title: String, val description: String)

data class AppState(
    val dark: Boolean = true,
    val quality: String = "High Resolution",
    val colorProfile: String = "sRGB",
    val layout: String = "Clean Grid",
    val category: String = "All",
    val sort: String = "Most Recent",
    val bio: String = "Creative graphic designer specializing in brand identity, UI/UX, advertising and visual systems.",
    val skills: List<String> = listOf("Photoshop", "Illustrator", "Figma", "InDesign", "After Effects"),
    val availability: String = "Open to Work",
    val watermark: Boolean = true,
    val protectDownloads: Boolean = true,
    val language: String = "English",
    val uiStyle: String = "Modern",
    val brandName: String = "Uniqe Design",
    val slogan: String = "Showcase your creativity. • Build your brand.",
    val projects: List<Project> = listOf(
        Project(1, "Modern Brand Identity", "Branding", "Complete identity system, logo, stationery and social media kit.", 1259),
        Project(2, "Mobile App Experience", "UI/UX", "Mobile interface, dashboard and interaction design.", 940),
        Project(3, "Editorial Illustration", "Illustration", "Digital illustration and campaign artwork.", 720),
        Project(4, "Motion Campaign", "Motion Graphics", "Animated social campaign and promo visuals.", 615),
        Project(5, "Premium Packaging", "Packaging", "Retail packaging and product presentation system.", 532)
    ),
    val services: List<Service> = listOf(
        Service(1, "Brand Identity", "Logo, brand system, stationery and guidelines."),
        Service(2, "UI/UX Design", "Websites, mobile apps and dashboards."),
        Service(3, "Illustration", "Digital illustration, vector art and campaigns."),
        Service(4, "Motion Graphics", "Animation, promo videos and social content."),
        Service(5, "Packaging Design", "Product packaging, labels and print-ready files."),
        Service(6, "Print Design", "Flyers, brochures, posters and marketing materials.")
    )
)

class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()
    fun update(block: (AppState) -> AppState) = _state.update(block)
    fun addProject(title: String, category: String, description: String) = update { it.copy(projects = it.projects + Project((it.projects.maxOfOrNull { p -> p.id } ?: 0) + 1, title, category, description, 0)) }
    fun removeProject(id: Int) = update { it.copy(projects = it.projects.filterNot { p -> p.id == id }) }
    fun addService(title: String, description: String) = update { it.copy(services = it.services + Service((it.services.maxOfOrNull { s -> s.id } ?: 0) + 1, title, description)) }
    fun removeService(id: Int) = update { it.copy(services = it.services.filterNot { s -> s.id == id }) }
}

@Composable
fun UniqeTheme(dark: Boolean, style: String, content: @Composable () -> Unit) {
    val colors = if (dark) darkColorScheme(
        primary = if (style == "Classic") Color(0xFF8B5CF6) else SoftPurple,
        secondary = if (style == "Classic") Color(0xFF4338CA) else Purple,
        tertiary = Color(0xFF60A5FA),
        background = Navy,
        surface = CardDark,
        onBackground = Color.White,
        onSurface = Color.White
    ) else lightColorScheme(primary = Purple, secondary = Indigo, tertiary = Color(0xFF2563EB))
    MaterialTheme(colorScheme = colors, typography = Typography(), content = content)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { UniqeApp() }
    }
}

@Composable
fun UniqeApp(vm: AppViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    var screen by rememberSaveable { mutableStateOf("Home") }
    UniqeTheme(state.dark, state.uiStyle) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = { BottomBar(screen) { screen = it } }
        ) { padding ->
            when (screen) {
                "Home" -> HomeScreen(state, vm, Modifier.padding(padding))
                "Portfolio" -> PortfolioScreen(state, vm, Modifier.padding(padding))
                "Services" -> ServicesScreen(state, vm, Modifier.padding(padding))
                "Profile" -> ProfileScreen(state, Modifier.padding(padding))
                "Settings" -> SettingsScreen(state, vm, Modifier.padding(padding))
                "Contact" -> ContactScreen(state, Modifier.padding(padding))
            }
        }
    }
}

@Composable
fun BottomBar(current: String, onChange: (String) -> Unit) {
    val items = listOf("Home" to Icons.Default.Home, "Portfolio" to Icons.Default.GridView, "Services" to Icons.Default.DesignServices, "Profile" to Icons.Default.Person, "Settings" to Icons.Default.Settings)
    NavigationBar {
        items.forEach { (name, icon) ->
            NavigationBarItem(selected = current == name, onClick = { onChange(name) }, icon = { Icon(icon, null) }, label = { Text(name) })
        }
    }
}

@Composable
fun ScreenHeader(title: String, subtitle: String? = null, onBack: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            subtitle?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp) }
        }
        Image(painterResource(R.drawable.app_logo), "Uniqe Design", Modifier.size(44.dp).clip(RoundedCornerShape(13.dp)))
    }
}

@Composable
fun HomeScreen(state: AppState, vm: AppViewModel, modifier: Modifier) {
    val context = LocalContext.current
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 28.dp)) {
        item {
            Box(Modifier.fillMaxWidth().height(300.dp).background(Brush.linearGradient(listOf(Navy, Indigo, Purple)))) {
                Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.app_logo), null, Modifier.size(74.dp).clip(RoundedCornerShape(22.dp)))
                        Spacer(Modifier.width(16.dp))
                        Column { Text("Uniqe Design", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold); Text("Graphic Design Portfolio", color = SoftPurple) }
                    }
                    Spacer(Modifier.height(22.dp))
                    Text("Showcase your creativity.\nBuild your brand.", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(onClick = { }) { Text("View Portfolio") }
                        OutlinedButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=Adama,Ethiopia"))) }) { Text("Location") }
                    }
                }
            }
        }
        item { SectionTitle("Welcome", "A professional workspace for your creative portfolio") }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard("Projects", state.projects.size.toString(), Icons.Default.Work)
                StatCard("Services", state.services.size.toString(), Icons.Default.DesignServices)
                StatCard("Availability", "Open", Icons.Default.CheckCircle)
            }
        }
        item { SectionTitle("Featured Projects", "Recent work and selected case studies") }
        items(state.projects.take(3)) { project -> ProjectTile(project, state, vm, true) }
        item {
            Card(Modifier.padding(18.dp).fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Ready to work together?", fontWeight = FontWeight.Bold, fontSize = 19.sp)
                    Text("Contact Uniqe Design for branding, UI/UX, illustration, motion and print design.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:gutemab@gmail.com"))) }) { Text("Email us") }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String) {
    Column(Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
        Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(Modifier.width(145.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp)) { Icon(icon, null, tint = SoftPurple); Spacer(Modifier.height(7.dp)); Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold); Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }
    }
}

@Composable
fun PortfolioScreen(state: AppState, vm: AppViewModel, modifier: Modifier) {
    var search by remember { mutableStateOf("") }
    var showAdd by remember { mutableStateOf(false) }
    val categories = listOf("All", "Branding", "UI/UX", "Illustration", "Motion Graphics", "Packaging")
    val filtered = state.projects.filter { state.category == "All" || it.category == state.category }.filter { it.title.contains(search, true) || it.category.contains(search, true) }.let {
        when (state.sort) { "Alphabetical" -> it.sortedBy(Project::title); "Most Viewed" -> it.sortedByDescending(Project::views); else -> it }
    }
    Column(modifier.fillMaxSize()) {
        ScreenHeader("Portfolio", "Showcase your best creative work")
        Row(Modifier.padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(search, { search = it }, Modifier.weight(1f), placeholder = { Text("Search projects") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true)
            Spacer(Modifier.width(8.dp)); FilledTonalButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, null); Text("Add") }
        }
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(18.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) { categories.forEach { c -> FilterChip(selected = state.category == c, onClick = { vm.update { it.copy(category = c) } }, label = { Text(c) }) } }
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { items(filtered) { ProjectTile(it, state, vm, false) } }
    }
    if (showAdd) AddProjectDialog({ showAdd = false }) { title, category, description -> vm.addProject(title, category, description); showAdd = false }
}

@Composable
fun ProjectTile(project: Project, state: AppState, vm: AppViewModel, compact: Boolean) {
    val colors = when (project.category) { "Branding" -> listOf(Color(0xFF7C3AED), Color(0xFFEC4899)); "UI/UX" -> listOf(Color(0xFF2563EB), Color(0xFF06B6D4)); "Illustration" -> listOf(Color(0xFFF59E0B), Color(0xFFEF4444)); else -> listOf(Indigo, Purple) }
    Card(Modifier.padding(horizontal = 18.dp).fillMaxWidth(), shape = RoundedCornerShape(20.dp)) {
        Column {
            Box(Modifier.fillMaxWidth().height(if (compact) 150.dp else 185.dp).background(Brush.linearGradient(colors))) {
                Column(Modifier.align(Alignment.Center).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) { Image(painterResource(R.drawable.app_logo), null, Modifier.size(62.dp).clip(RoundedCornerShape(17.dp))); Text(project.category, color = Color.White, fontWeight = FontWeight.Bold); Text("Uniqe Design", color = Color.White.copy(alpha = .75f), fontSize = 12.sp) }
                if (state.watermark) Text("UNIQE DESIGN", color = Color.White.copy(alpha = .35f), modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp), fontSize = 10.sp)
            }
            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text(project.title, fontWeight = FontWeight.Bold, fontSize = 17.sp); Text(project.description, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp); Text("${project.views} views", color = SoftPurple, fontSize = 12.sp) }
                IconButton(onClick = { vm.removeProject(project.id) }) { Icon(Icons.Default.DeleteOutline, "Delete") }
            }
        }
    }
}

@Composable
fun ServicesScreen(state: AppState, vm: AppViewModel, modifier: Modifier) {
    var add by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize()) {
        ScreenHeader("Services", "Professional creative services")
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.services) { service ->
                Card(Modifier.padding(horizontal = 18.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Purple.copy(.18f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Brush, null, tint = SoftPurple) }
                        Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(service.title, fontWeight = FontWeight.Bold); Text(service.description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp) }
                        IconButton(onClick = { vm.removeService(service.id) }) { Icon(Icons.Default.DeleteOutline, "Remove") }
                    }
                }
            }
            item { OutlinedButton(onClick = { add = true }, Modifier.padding(horizontal = 18.dp).fillMaxWidth()) { Icon(Icons.Default.Add, null); Text("Add Service") } }
        }
    }
    if (add) AddServiceDialog({ add = false }) { title, desc -> vm.addService(title, desc); add = false }
}

@Composable
fun ProfileScreen(state: AppState, modifier: Modifier) {
    val context = LocalContext.current
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 28.dp)) {
        item {
            Column(Modifier.fillMaxWidth().padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painterResource(R.drawable.app_logo), null, Modifier.size(100.dp).clip(CircleShape))
                Spacer(Modifier.height(12.dp)); Text("Uniqe Design", fontSize = 27.sp, fontWeight = FontWeight.Bold); Text("Graphic Design Studio", color = SoftPurple)
                Spacer(Modifier.height(10.dp)); AssistChip(onClick = {}, label = { Text(state.availability) }, leadingIcon = { Icon(Icons.Default.CheckCircle, null, tint = Green) })
            }
        }
        item { ProfileCard("About Me", state.bio) }
        item { ProfileCard("Core Skills", state.skills.joinToString(" • ")) }
        item {
            ProfileCard("Contact", "Adama, Ethiopia\n+251 912 746 028\n+251 997 787 917\ngutemab@gmail.com\nTelegram: @geadvert\nInstagram: @geadvertt\nTikTok: @geprintingadvertizingstudio")
        }
        item { Button(onClick = { context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:gutemab@gmail.com"))) }, Modifier.padding(horizontal = 18.dp).fillMaxWidth()) { Icon(Icons.Default.Email, null); Spacer(Modifier.width(8.dp)); Text("Contact Uniqe Design") } }
    }
}

@Composable
fun ProfileCard(title: String, body: String) {
    Card(Modifier.padding(horizontal = 18.dp, vertical = 6.dp).fillMaxWidth(), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(17.dp)) { Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp); Spacer(Modifier.height(6.dp)); Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp) } }
}

@Composable
fun ContactScreen(state: AppState, modifier: Modifier) {
    val context = LocalContext.current
    Column(modifier.fillMaxSize()) {
        ScreenHeader("Contact", "Start your next project")
        Card(Modifier.padding(18.dp).fillMaxWidth(), shape = RoundedCornerShape(22.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text("Uniqe Design", fontSize = 23.sp, fontWeight = FontWeight.Bold); Text("We turn ideas into strong visual identities.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(18.dp))
                ContactRow(Icons.Default.Email, "Email", "gutemab@gmail.com") { context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:gutemab@gmail.com"))) }
                ContactRow(Icons.Default.Phone, "Phone", "+251 912 746 028") { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:+251912746028"))) }
                ContactRow(Icons.Default.LocationOn, "Location", "Adama, Ethiopia") { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/search/?api=1&query=Adama,Ethiopia"))) }
                ContactRow(Icons.Default.Send, "Telegram", "@geadvert") { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/geadvert"))) }
            }
        }
    }
}

@Composable
fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = SoftPurple); Spacer(Modifier.width(14.dp)); Column { Text(title, fontWeight = FontWeight.SemiBold); Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp) } }
}

@Composable
fun SettingsScreen(state: AppState, vm: AppViewModel, modifier: Modifier) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { }
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 28.dp)) {
        item { ScreenHeader("Settings", "Control the portfolio experience") }
        item { SettingsGroup("Display & Visual") {
            SettingChoice("App Style", state.uiStyle, Icons.Default.AutoAwesome, listOf("Modern", "Classic")) { vm.update { it.copy(uiStyle = it.uiStyle.toggle("Modern", "Classic")) } }
            SettingSwitch("Dark Mode", "Make vibrant work stand out", state.dark, Icons.Default.DarkMode) { vm.update { it.copy(dark = !it.dark) } }
            SettingChoice("Image Quality", state.quality, Icons.Default.HighQuality, listOf("High Resolution", "Compressed Mode")) { vm.update { it.copy(quality = it.quality.toggle("High Resolution", "Compressed Mode")) } }
            SettingChoice("Color Profile", state.colorProfile, Icons.Default.Palette, listOf("sRGB", "Adobe RGB")) { vm.update { it.copy(colorProfile = it.colorProfile.toggle("sRGB", "Adobe RGB")) } }
        } }
        item { SettingsGroup("Layout & Content") {
            SettingChoice("Layout Style", state.layout, Icons.Default.GridView, listOf("Masonry", "Clean Grid", "Large Cards", "List View")) { vm.update { it.copy(layout = nextOf(it.layout, listOf("Masonry", "Clean Grid", "Large Cards", "List View"))) } }
            SettingChoice("Category", state.category, Icons.Default.FilterList, listOf("All", "Branding", "UI/UX", "Illustration", "Motion Graphics", "Packaging")) { vm.update { it.copy(category = nextOf(it.category, listOf("All", "Branding", "UI/UX", "Illustration", "Motion Graphics", "Packaging"))) } }
            SettingChoice("Sort Projects", state.sort, Icons.Default.Sort, listOf("Most Recent", "Most Viewed", "Alphabetical")) { vm.update { it.copy(sort = nextOf(it.sort, listOf("Most Recent", "Most Viewed", "Alphabetical"))) } }
            SettingSwitch("Watermark", "Auto-apply Uniqe Design mark", state.watermark, Icons.Default.Watermark) { vm.update { it.copy(watermark = !it.watermark) } }
            SettingSwitch("Download Protection", "Protect portfolio files", state.protectDownloads, Icons.Default.Security) { vm.update { it.copy(protectDownloads = !it.protectDownloads) } }
        } }
        item { SettingsGroup("Profile & Account") {
            SettingChoice("Availability", state.availability, Icons.Default.Work, listOf("Open to Work", "Freelance/Contract", "Not Available")) { vm.update { it.copy(availability = nextOf(it.availability, listOf("Open to Work", "Freelance/Contract", "Not Available"))) } }
            SettingChoice("Language", state.language, Icons.Default.Language, listOf("English", "አማርኛ")) { vm.update { it.copy(language = if (it.language == "English") "አማርኛ" else "English") } }
            SettingAction("Upload Resume / Images", "Choose files from your phone", Icons.Default.UploadFile) { launcher.launch("*/*") }
        } }
        item { SettingsGroup("Management") {
            SettingAction("Manage Projects", "Add or remove portfolio projects", Icons.Default.Work) { }
            SettingAction("Manage Services", "Add or remove services", Icons.Default.DesignServices) { }
            SettingAction("Social & Portfolio Links", "Behance, Dribbble, Adobe Portfolio, LinkedIn, Instagram, X", Icons.Default.Link) { }
            SettingAction("Dashboard & Customer Messages", "Manage conversations and client requests", Icons.Default.Chat) { }
            SettingAction("Location & Map", "Adama, Ethiopia", Icons.Default.Map) { }
        } }
        item { SettingsGroup("Brand") {
            SettingAction("Logo & App Name", "Uniqe Design • UD logo", Icons.Default.BrandingWatermark) { }
            SettingAction("Brand Colors", "Purple / Blue visual system", Icons.Default.ColorLens) { }
            SettingAction("Slogan", "Showcase your creativity • Build your brand", Icons.Default.FormatQuote) { }
            SettingAction("Modern / Classic UI", "Switch the visual presentation style", Icons.Default.AutoAwesome) { vm.update { it.copy(uiStyle = it.uiStyle.toggle("Modern", "Classic")) } }
        } }
    }
}

private fun String.toggle(a: String, b: String) = if (this == a) b else a
private fun nextOf(current: String, values: List<String>): String = values[(values.indexOf(current) + 1).mod(values.size)]

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.padding(horizontal = 18.dp, vertical = 6.dp)) { Text(title, fontWeight = FontWeight.Bold, color = SoftPurple, modifier = Modifier.padding(8.dp)); Card(shape = RoundedCornerShape(18.dp)) { Column(content = content) } }
}

@Composable
fun SettingSwitch(title: String, subtitle: String, checked: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = SoftPurple); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }; Switch(checked, { onClick() }) }
}

@Composable
fun SettingChoice(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, values: List<String>, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = SoftPurple); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(value, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }; Text("›", fontSize = 28.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
}

@Composable
fun SettingAction(title: String, subtitle: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = SoftPurple); Spacer(Modifier.width(13.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp) }; Icon(Icons.Default.ChevronRight, null) }
}

@Composable
fun AddProjectDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }; var category by remember { mutableStateOf("Branding") }; var description by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add Project") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(title, { title = it }, label = { Text("Project title") }); OutlinedTextField(category, { category = it }, label = { Text("Category") }); OutlinedTextField(description, { description = it }, label = { Text("Description") }) } }, confirmButton = { Button(onClick = { if (title.isNotBlank()) onAdd(title, category, description.ifBlank { "New Uniqe Design project." }) }) { Text("Add") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}

@Composable
fun AddServiceDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }; var description by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add Service") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(title, { title = it }, label = { Text("Service title") }); OutlinedTextField(description, { description = it }, label = { Text("Description") }) } }, confirmButton = { Button(onClick = { if (title.isNotBlank()) onAdd(title, description.ifBlank { "Creative service by Uniqe Design." }) }) { Text("Add") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
