package com.example

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Tool
import com.example.ui.theme.*
import com.example.viewmodel.ChatMessage
import com.example.viewmodel.FunoViewModel
import kotlinx.coroutines.launch

// Premium light palette matching the screenshot
val ScreenBg = Color(0xFFF7F7FA)
val SidebarBg = Color(0xFFEBEAEF)
val PurpleActive = Color(0xFF6236FF)
val TextDark = Color(0xFF15141A)
val TextGray = Color(0xFF5E6578)
val BorderLightColor = Color(0xFFE3E2E8)
val MascotColor = Color(0xFF9881FF)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FunoApp()
            }
        }
    }
}

// Side bar category mapping for the screenshot layout
enum class SidebarCategory(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    PROMPT("Prompt", Icons.Default.TextSnippet),
    CODE("Code", Icons.Default.Code),
    BUILD_APP("Build app", Icons.Default.Layers),
    BUILD_WEBS("Build webs", Icons.Default.Language),
    EDIT_VIDEO("Edit video", Icons.Default.VideoCall),
    EDIT_PHOTO("Edit photo", Icons.Default.Image),
    MODELS_SPECIFIC("Models specific", Icons.Default.Psychology),
    ALL_MODELS("1000+ tools", Icons.Default.AutoAwesome)
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FunoApp() {
    val viewModel: FunoViewModel = viewModel()
    var selectedSidebar by remember { mutableStateOf(SidebarCategory.ALL_MODELS) }
    var showChatDialog by remember { mutableStateOf(false) }
    val selectedTool by viewModel.selectedTool.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    
    // Connect search view to category selection on change
    LaunchedEffect(selectedSidebar) {
        viewModel.selectPricing(null) // Reset pricing filter on category swap
        when (selectedSidebar) {
            SidebarCategory.PROMPT -> {
                viewModel.selectCategory("Prompt")
                viewModel.setSearchQuery("")
            }
            SidebarCategory.CODE -> {
                viewModel.selectCategory("Code")
                viewModel.setSearchQuery("")
            }
            SidebarCategory.BUILD_APP -> {
                viewModel.selectCategory("Build App")
                viewModel.setSearchQuery("")
            }
            SidebarCategory.BUILD_WEBS -> {
                viewModel.selectCategory("Website")
                viewModel.setSearchQuery("")
            }
            SidebarCategory.EDIT_VIDEO -> {
                viewModel.selectCategory("Video")
                viewModel.setSearchQuery("")
            }
            SidebarCategory.EDIT_PHOTO -> {
                viewModel.selectCategory("Photo")
                viewModel.setSearchQuery("")
            }
            SidebarCategory.MODELS_SPECIFIC -> {
                viewModel.selectCategory(null)
                viewModel.setSearchQuery("copilot")
            }
            SidebarCategory.ALL_MODELS -> {
                viewModel.selectCategory(null)
                viewModel.setSearchQuery("")
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ScreenBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // 1. Top Header Card: "Just do it today" with persistent search history chips
                TopBannerHeader(
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Glassmorphic Horizontal Carousel for 25 Featured Tools (OpusClip, Runway, etc.)
                // Only show this prominent carousel when "1000+ tools" is selected for maximum visual impact!
                AnimatedVisibility(
                    visible = selectedSidebar == SidebarCategory.ALL_MODELS && searchQuery.isEmpty(),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Featured Picks",
                                color = TextDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PurpleActive.copy(alpha = 0.1f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Top 25 Models",
                                    color = PurpleActive,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        FeaturedGlassmorphicCarousel(
                            viewModel = viewModel,
                            onToolClick = { viewModel.selectTool(it) }
                        )
                    }
                }

                // 3. Main Content Split Panel (Sidebar + Grid)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    // Left Column Side Panel (30% horizontal space)
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(116.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(SidebarBg)
                            .padding(vertical = 12.dp, horizontal = 4.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        SidebarCategory.values().forEach { category ->
                            SidebarItemRow(
                                category = category,
                                isSelected = selectedSidebar == category,
                                onClick = {
                                    selectedSidebar = category
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Right Column Models Grid
                    val filteredTools by viewModel.filteredTools.collectAsState()
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                    ) {
                        if (selectedSidebar == SidebarCategory.BUILD_APP) {
                            BuildAppView(
                                viewModel = viewModel,
                                onToolClick = { viewModel.selectTool(it) }
                            )
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (selectedSidebar == SidebarCategory.ALL_MODELS) "AI Models" else selectedSidebar.label,
                                        color = TextDark,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Explore ${filteredTools.size} tools • Popular now",
                                        color = TextGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (filteredTools.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    contentAlignment = Alignment.Center
                                ) {
                                    EmptySearchResultView()
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(filteredTools) { tool ->
                                        ModelGridCard(
                                            tool = tool,
                                            viewModel = viewModel,
                                            onClick = { viewModel.selectTool(tool) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Floating Cute Mascot Helper: "AI for help" bubble popping up
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 12.dp)
            ) {
                FloatingMascotBubble(
                    onClick = { showChatDialog = true }
                )
            }

            // 5. Immersive Chat Assistant BottomSheet / Overlay Screen
            if (showChatDialog) {
                ChatAssistantDialog(
                    viewModel = viewModel,
                    onDismiss = { showChatDialog = false }
                )
            }

            // 6. Global Tool Detail Sheet Dialog
            selectedTool?.let { tool ->
                ToolDetailDialog(
                    tool = tool,
                    viewModel = viewModel,
                    onDismiss = { viewModel.selectTool(null) }
                )
            }
        }
    }
}

@Composable
fun TopBannerHeader(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    viewModel: FunoViewModel
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val recentHistory by viewModel.recentSearchHistory.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(24.dp), clip = false)
            .testTag("top_banner_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Sparkling custom logo inspired by the official glassy F5 logo
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF00F0FF), Color(0xFFFF007F))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "5",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Just do it today",
                        color = TextDark,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "FUNO5 — Five Tools. One Flow.",
                        color = TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Persistent Search History clickable chips
            AnimatedVisibility(visible = recentHistory.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent Searches",
                            color = TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Clear All",
                            color = PurpleActive,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { viewModel.clearAllSearchHistory() }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(recentHistory) { item ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F0F5))
                                    .border(1.dp, BorderLightColor, RoundedCornerShape(12.dp))
                                    .clickable {
                                        onSearchChange(item.query)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = item.query,
                                        color = TextDark,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete",
                                        tint = TextGray,
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clickable { viewModel.deleteSearchQuery(item.query) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Inline search box inside the header card
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("top_search_input"),
                placeholder = { Text("Search 1000+ models instantly...", color = TextGray, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TextGray, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchChange("") },
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextGray, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextDark,
                    unfocusedTextColor = TextDark,
                    focusedBorderColor = PurpleActive,
                    unfocusedBorderColor = BorderLightColor,
                    focusedContainerColor = Color(0xFFF7F7FA),
                    unfocusedContainerColor = Color(0xFFF7F7FA)
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.saveSearchQuery(searchQuery)
                    }
                    keyboardController?.hide()
                })
            )
        }
    }
}

@Composable
fun SidebarItemRow(
    category: SidebarCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColors = if (isSelected) PurpleActive else Color.Transparent
    val labelColor = if (isSelected) Color.White else TextDark
    val iconColor = if (isSelected) Color.White else TextGray

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColors)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .testTag("sidebar_${category.label}"),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.label,
                tint = iconColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.label,
                color = labelColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// A beautiful, glassmorphic horizontal carousel for the 25 Featured Best Picks
@Composable
fun FeaturedGlassmorphicCarousel(
    viewModel: FunoViewModel,
    onToolClick: (Tool) -> Unit
) {
    val featuredTools = remember { viewModel.allTools.filter { it.isFeatured } }
    val lazyListState = rememberLazyListState()

    LazyRow(
        state = lazyListState,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("featured_carousel"),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(featuredTools) { tool ->
            var isHovered by remember { mutableStateOf(false) }
            val scale by animateFloatAsState(
                targetValue = if (isHovered) 1.05f else 1.0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
            )

            Box(
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                colors = listOf(Color.White.copy(alpha = 0.6f), Color.White.copy(alpha = 0.1f))
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .background(
                        Color.White.copy(alpha = 0.4f) // Glassmorphism backdrop translucency
                    )
                    .clickable { onToolClick(tool) }
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MascotModelLogo(name = tool.name, logoColorHex = tool.logoHex)
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = Color(0xFFFFC500),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = tool.rating.toString(),
                                color = TextDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = tool.name,
                        color = TextDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = tool.category.uppercase(),
                        color = PurpleActive,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = tool.bestFor,
                        color = TextGray,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.height(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModelGridCard(
    tool: Tool,
    viewModel: FunoViewModel,
    onClick: () -> Unit
) {
    val favoriteState by viewModel.favorites.collectAsState()
    val isFav = favoriteState.any { it.toolId == tool.id }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp), clip = false)
            .clickable { onClick() }
            .testTag("model_card_${tool.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Heart favorite indicator directly on top right of the card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                IconButton(
                    onClick = { viewModel.toggleFavorite(tool.id) },
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.TopEnd)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFav) Color.Red else TextGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Custom logo rendering matching the gorgeous icons in the uploaded screenshot
            MascotModelLogo(name = tool.name, logoColorHex = tool.logoHex)

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tool.name,
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = tool.tags.take(2).joinToString(" • ") { it.replaceFirstChar { c -> c.uppercase() } },
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Draw high fidelity dynamic vector logo badges inspired by the GPT-5, Claude, Stable Diffusion screenshots!
@Composable
fun MascotModelLogo(name: String, logoColorHex: String) {
    val primaryColor = Color(android.graphics.Color.parseColor(logoColorHex))
    
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F0F5)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(32.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            when {
                name.startsWith("G") || name.contains("Opus") || name.contains("Firefly") -> {
                    val path = Path().apply {
                        moveTo(cx, cy - h/2.2f)
                        quadraticTo(cx, cy, cx + w/2.2f, cy)
                        quadraticTo(cx, cy, cx, cy + h/2.2f)
                        quadraticTo(cx, cy, cx - w/2.2f, cy)
                        quadraticTo(cx, cy, cx, cy - h/2.2f)
                    }
                    drawPath(path = path, color = primaryColor)
                    drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = Offset(cx, cy))
                }
                name.startsWith("C") || name.contains("Relume") || name.contains("Framer") -> {
                    drawArc(
                        color = primaryColor,
                        startAngle = 45f,
                        sweepAngle = 270f,
                        useCenter = false,
                        topLeft = Offset(w*0.15f, h*0.15f),
                        size = Size(w*0.7f, h*0.7f),
                        style = Stroke(width = 6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    )
                }
                name.startsWith("L") || name.contains("v0") || name.contains("Remove") -> {
                    for (i in 0 until 4) {
                        val angle = i * 90f
                        val rad = Math.toRadians(angle.toDouble())
                        val ox = cx + (w * 0.2f * Math.cos(rad)).toFloat()
                        val oy = cy + (h * 0.2f * Math.sin(rad)).toFloat()
                        drawCircle(color = primaryColor.copy(alpha = 0.7f), radius = w * 0.22f, center = Offset(ox, oy))
                    }
                    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = Offset(cx, cy))
                }
                name.contains("Stable") || name.contains("Midjourney") || name.contains("Photoroom") -> {
                    drawCircle(color = primaryColor, radius = w * 0.35f, center = Offset(cx, cy))
                    drawCircle(color = Color(0xFFFF85A1), radius = w * 0.2f, center = Offset(cx - 3f, cy - 3f))
                    drawCircle(color = Color(0xFF00FFB2), radius = w * 0.15f, center = Offset(cx + 4f, cy + 4f))
                }
                name.contains("Whisper") || name.contains("Durable") || name.contains("Pictory") -> {
                    drawCircle(
                        color = primaryColor,
                        radius = w * 0.35f,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.6f),
                        radius = w * 0.2f,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    drawCircle(color = primaryColor, radius = 3.dp.toPx(), center = Offset(cx, cy))
                }
                else -> {
                    val path = Path().apply {
                        moveTo(w*0.2f, h*0.2f)
                        lineTo(w*0.8f, h*0.2f)
                        lineTo(w*0.8f, h*0.7f)
                        lineTo(w*0.4f, h*0.7f)
                        lineTo(w*0.2f, h*0.9f)
                        close()
                    }
                    drawPath(path = path, color = primaryColor)
                    drawLine(color = Color.White, start = Offset(w*0.4f, h*0.4f), end = Offset(w*0.35f, h*0.45f), strokeWidth = 2.dp.toPx())
                    drawLine(color = Color.White, start = Offset(w*0.35f, h*0.45f), end = Offset(w*0.4f, h*0.5f), strokeWidth = 2.dp.toPx())
                    drawLine(color = Color.White, start = Offset(w*0.6f, h*0.4f), end = Offset(w*0.65f, h*0.45f), strokeWidth = 2.dp.toPx())
                    drawLine(color = Color.White, start = Offset(w*0.65f, h*0.45f), end = Offset(w*0.6f, h*0.5f), strokeWidth = 2.dp.toPx())
                }
            }
        }
    }
}

// Mascot popping up at bottom right "AI for help" bubble
@Composable
fun FloatingMascotBubble(
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val bobbingOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bobbing"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .offset(y = bobbingOffset.dp)
            .clickable { onClick() }
            .testTag("floating_mascot_bubble")
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp),
            colors = CardDefaults.cardColors(containerColor = PurpleActive),
            modifier = Modifier.shadow(4.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp))
        ) {
            Text(
                text = "AI for help",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(MascotColor),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f

                drawCircle(color = Color.White, radius = 3.5.dp.toPx(), center = Offset(cx - 6.dp.toPx(), cy - 2.dp.toPx()))
                drawCircle(color = Color.White, radius = 3.5.dp.toPx(), center = Offset(cx + 6.dp.toPx(), cy - 2.dp.toPx()))
                drawCircle(color = Color.Black, radius = 1.5.dp.toPx(), center = Offset(cx - 6.dp.toPx(), cy - 2.dp.toPx()))
                drawCircle(color = Color.Black, radius = 1.5.dp.toPx(), center = Offset(cx + 6.dp.toPx(), cy - 2.dp.toPx()))

                drawCircle(color = Color(0xFFFF94B8), radius = 2.5.dp.toPx(), center = Offset(cx - 11.dp.toPx(), cy + 2.dp.toPx()))
                drawCircle(color = Color(0xFFFF94B8), radius = 2.5.dp.toPx(), center = Offset(cx + 11.dp.toPx(), cy + 2.dp.toPx()))

                drawArc(
                    color = Color.Black,
                    startAngle = 0f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(cx - 3.dp.toPx(), cy + 1.dp.toPx()),
                    size = Size(6.dp.toPx(), 4.dp.toPx()),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }
        }
    }
}

// Immersive overlay dialog for Funo AI chatbot
@Composable
fun ChatAssistantDialog(
    viewModel: FunoViewModel,
    onDismiss: () -> Unit
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    var inputQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isTyping) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE615141A))
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = ScreenBg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurpleActive),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = "Bot",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Funo Bot Concierge",
                                    color = TextDark,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Online • Ask anything",
                                    color = PurpleActive,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close chat", tint = TextDark)
                        }
                    }

                    Divider(color = BorderLightColor, modifier = Modifier.padding(vertical = 12.dp))

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages) { msg ->
                            DialogChatBubble(msg = msg)
                        }

                        if (isTyping) {
                            item {
                                DialogTypingIndicatorBubble()
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputQuery,
                            onValueChange = { inputQuery = it },
                            placeholder = { Text("Ask Funo bot...", color = TextGray, fontSize = 13.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("dialog_chat_input"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark,
                                focusedBorderColor = PurpleActive,
                                unfocusedBorderColor = BorderLightColor,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (inputQuery.isNotBlank()) {
                                    viewModel.sendMessage(inputQuery)
                                    inputQuery = ""
                                    keyboardController?.hide()
                                }
                            })
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                if (inputQuery.isNotBlank()) {
                                    viewModel.sendMessage(inputQuery)
                                    inputQuery = ""
                                    keyboardController?.hide()
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(PurpleActive)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogChatBubble(msg: ChatMessage) {
    val alignment = if (msg.isUser) Alignment.End else Alignment.Start
    val bg = if (msg.isUser) PurpleActive else Color.White
    val textColor = if (msg.isUser) Color.White else TextDark
    val borderCol = if (msg.isUser) Color.Transparent else BorderLightColor

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Card(
            shape = if (msg.isUser) {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 2.dp)
            } else {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp)
            },
            colors = CardDefaults.cardColors(containerColor = bg),
            border = if (!msg.isUser) BorderStroke(1.dp, borderCol) else null,
            modifier = Modifier.widthIn(max = 260.dp)
        ) {
            Text(
                text = msg.text,
                color = textColor,
                fontSize = 13.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun DialogTypingIndicatorBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 2.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderLightColor),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PurpleActive))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PurpleActive.copy(alpha = 0.6f)))
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(PurpleActive.copy(alpha = 0.3f)))
            }
        }
    }
}

@Composable
fun EmptySearchResultView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = "No results",
            tint = TextGray,
            modifier = Modifier.size(44.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "No Tools Found",
            color = TextDark,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Try searching something else or switch category.",
            color = TextGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ToolDetailDialog(
    tool: Tool,
    viewModel: FunoViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val favoriteState by viewModel.favorites.collectAsState()
    val isFav = favoriteState.any { it.toolId == tool.id }
    val clipboardManager = LocalClipboardManager.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x0F000000)),
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(text = "Close", color = TextDark)
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MascotModelLogo(name = tool.name, logoColorHex = tool.logoHex)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = tool.name,
                            color = TextDark,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = tool.category.uppercase(),
                            color = PurpleActive,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.toggleFavorite(tool.id) },
                    modifier = Modifier
                        .size(36.dp)
                        .minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite toggle",
                        tint = if (isFav) Color.Red else TextGray
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC500),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${tool.rating} / 5.0 Rating",
                            color = TextDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "${tool.monthlyUsers} active users",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "BEST FOR:",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tool.bestFor,
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1EDE9)),
                    border = BorderStroke(1.dp, Color(0xFFE5DCD6))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🏆 PERFECT PROMPT FOR ${tool.name.uppercase()}",
                                color = Color(0xFF9E5911),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(tool.perfectPrompt))
                                    Toast.makeText(context, "Copied Prompt to Clipboard!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(28.dp)
                                    .minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy prompt",
                                    tint = Color(0xFF9E5911),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "\"${tool.perfectPrompt}\"",
                            color = TextDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tool.websiteUrl))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleActive),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = "Website", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Website", color = Color.White, fontSize = 13.sp)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(tool.appUrl))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEBEAEF)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                    ) {
                        Icon(Icons.Default.Launch, contentDescription = "Launch", tint = TextDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Launch App", color = TextDark, fontSize = 13.sp)
                    }
                }
            }
        },
        containerColor = Color.White,
        textContentColor = TextDark,
        titleContentColor = TextDark
    )
}

@Composable
fun BuildAppView(
    viewModel: FunoViewModel,
    onToolClick: (Tool) -> Unit
) {
    val filteredTools by viewModel.filteredTools.collectAsState()
    val selectedPricing by viewModel.selectedPricing.collectAsState()
    
    val featuredAppTools = remember { viewModel.allTools.filter { it.category == "Build App" && it.isFeatured } }
    val otherAppTools = filteredTools.filter { it.category == "Build App" && !it.isFeatured }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Category Header with Badge "100 tools"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Build App",
                        color = TextDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(PurpleActive)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "100 tools",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = "Top-tier custom AI application creators",
                    color = TextGray,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Pricing Filters (All / Free / Freemium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val filters = listOf(null to "All", "Free" to "Free", "Freemium" to "Freemium")
            filters.forEach { (type, label) ->
                val isSelected = selectedPricing == type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) PurpleActive else Color.White)
                        .border(1.dp, if (isSelected) Color.Transparent else BorderLightColor, RoundedCornerShape(12.dp))
                        .clickable { viewModel.selectPricing(type) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else TextDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LazyColumn scrollable layout
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section 1: BIG Cards of Top 10 Featured (horizontal row)
            if (featuredAppTools.isNotEmpty() && selectedPricing == null) {
                item {
                    Column {
                        Text(
                            text = "Top 10 Featured",
                            color = TextDark,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(featuredAppTools) { tool ->
                                BigFeaturedAppCard(
                                    tool = tool,
                                    onToolClick = onToolClick
                                )
                            }
                        }
                    }
                }
            }

            // Section 2: Other 90 small cards
            item {
                Text(
                    text = "Explore App Creators (${otherAppTools.size})",
                    color = TextDark,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            if (otherAppTools.isEmpty()) {
                item {
                    EmptySearchResultView()
                }
            } else {
                // Chunk other tools into pairs to display them in a neat 2-column list
                val chunked = otherAppTools.chunked(2)
                items(chunked) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ModelGridCard(
                                tool = pair[0],
                                viewModel = viewModel,
                                onClick = { onToolClick(pair[0]) }
                            )
                        }
                        if (pair.size > 1) {
                            Box(modifier = Modifier.weight(1f)) {
                                ModelGridCard(
                                    tool = pair[1],
                                    viewModel = viewModel,
                                    onClick = { onToolClick(pair[1]) }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BigFeaturedAppCard(
    tool: Tool,
    onToolClick: (Tool) -> Unit
) {
    Card(
        modifier = Modifier
            .width(230.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp), clip = false)
            .clickable { onToolClick(tool) }
            .testTag("big_featured_${tool.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                MascotModelLogo(name = tool.name, logoColorHex = tool.logoHex)
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFC500).copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFC500),
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = tool.rating.toString(),
                            color = TextDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tool.name,
                color = TextDark,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "BEST FOR: ${tool.bestFor}",
                color = PurpleActive,
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Monthly Active: ${tool.monthlyUsers}",
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF7F7FA))
                    .padding(8.dp)
            ) {
                Text(
                    text = "\"${tool.perfectPrompt}\"",
                    color = TextGray,
                    fontSize = 10.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    lineHeight = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

