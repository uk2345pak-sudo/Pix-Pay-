package com.pixpay.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.NumberFormat

// Data model for a photo post
data class PixPhoto(
    val username: String,
    val imageUrl: String,
    val caption: String,
    val likes: Int,
    val comments: Int,
    val category: String,
    val sponsored: Boolean = false
)

val pixPhotos = listOf(
    PixPhoto(
        "@nature_creator",
        "https://images.unsplash.com/photo-1500534623283-312aade485b7?w=1200",
        "Beautiful natural landscape 🌿",
        12500,
        240,
        "Nature"
    ),

    PixPhoto(
        "@islamic_photos",
        "https://images.unsplash.com/photo-1542816417-098367d7c2a5?w=1200",
        "Peaceful Islamic photography",
        18700,
        390,
        "Islamic"
    ),

    PixPhoto(
        "PixPay Official",
        "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=1200",
        "Clean photography promotion",
        0,
        0,
        "Sponsored",
        true
    ),

    PixPhoto(
        "@landscape",
        "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=1200",
        "Mountain view 🏔️",
        9300,
        180,
        "Landscape"
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixPayTheme {
                PixPayApplication()
            }
        }
    }
}

@Composable
fun PixPayApplication() {
    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.Explore, null) },
                    label = { Text("Explore") }
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = { Icon(Icons.Default.AddCircle, null) },
                    label = { Text("Post") }
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { currentTab = 3 },
                    icon = { Icon(Icons.Default.Notifications, null) },
                    label = { Text("Inbox") }
                )

                NavigationBarItem(
                    selected = currentTab == 4,
                    onClick = { currentTab = 4 },
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentTab) {
                0 -> HomeFeed()
                1 -> ExplorePage()
                2 -> CreatePage()
                3 -> InboxPage()
                4 -> ProfilePage()
            }
        }
    }
}

// Home photo feed (uses experimental pager API)
@OptIn(
    androidx.compose.foundation.ExperimentalFoundationApi::class,
    androidx.compose.foundation.pager.ExperimentalPagerApi::class
)
@Composable
fun HomeFeed() {
    val pagerState = rememberPagerState(pageCount = { pixPhotos.size })

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        PhotoPostView(photo = pixPhotos[page])
    }
}

// Photo post view
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PhotoPostView(photo: PixPhoto) {
    var liked by rememberSaveable { mutableStateOf(false) }
    var saved by rememberSaveable { mutableStateOf(false) }
    var commentsOpen by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AsyncImage(
            model = photo.imageUrl,
            contentDescription = photo.caption,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.18f))
        )

        if (photo.sponsored) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(18.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Text(
                    "Sponsored",
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 90.dp, bottom = 25.dp)
        ) {
            Text(
                photo.username,
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                photo.caption,
                color = Color.White,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(5.dp))

            Text(
                "#${photo.category}",
                color = Color.White.copy(alpha = .85f),
                fontSize = 13.sp
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PhotoAction(
                icon = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                text = if (liked) NumberFormat.getInstance().format(photo.likes + 1) else NumberFormat.getInstance().format(photo.likes)
            ) {
                liked = !liked
            }

            PhotoAction(
                Icons.Default.Comment,
                NumberFormat.getInstance().format(photo.comments)
            ) {
                commentsOpen = true
            }

            PhotoAction(
                Icons.Default.Share,
                "Share"
            ) {}

            PhotoAction(
                if (saved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                "Save"
            ) {
                saved = !saved
            }
        }
    }

    if (commentsOpen) {
        ModalBottomSheet(
            onDismissRequest = { commentsOpen = false }
        ) {
            Column(Modifier.fillMaxWidth().padding(25.dp)) {
                Text("Comments", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(20.dp))
                Text("Clean comments will appear here.")
                Spacer(Modifier.height(50.dp))
            }
        }
    }
}

@Composable
fun PhotoAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    action: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable { action() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = Color.Black.copy(alpha = .45f)
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = Color.White,
                modifier = Modifier
                    .padding(10.dp)
                    .size(28.dp)
            )
        }

        Text(
            text,
            color = Color.White,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ExplorePage() {
    var searchText by rememberSaveable { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, null) },
            placeholder = { Text("Search photos, users, hashtags") },
            singleLine = true
        )

        Spacer(Modifier.height(18.dp))

        Text("Trending", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("#IslamicPhotography", "#Nature", "#Landscape").forEach {
                AssistChip(onClick = {}, label = { Text(it) })
            }
        }

        Spacer(Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = true, onClick = {}, label = { Text("Top Photos") })
            FilterChip(selected = false, onClick = {}, label = { Text("Users") })
            FilterChip(selected = false, onClick = {}, label = { Text("Hashtags") })
        }

        Spacer(Modifier.height(15.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pixPhotos) { photo ->
                AsyncImage(
                    model = photo.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun CreatePage() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(35.dp))
        Text("Create", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(30.dp))

        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.AddPhotoAlternate, null)
            Spacer(Modifier.width(8.dp))
            Text("Upload Clean Photo")
        }

        Spacer(Modifier.height(15.dp))

        OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Default.LiveTv, null)
            Spacer(Modifier.width(8.dp))
            Text("Start Live")
        }

        Spacer(Modifier.height(20.dp))
        Text("Live unlocks automatically at 500 followers.", color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        Text("Only supported clean photo content is allowed.", color = Color.Gray)
    }
}

@Composable
fun InboxPage() {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Inbox", fontSize = 29.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(20.dp))
        NotificationRow("Someone liked your photo")
        NotificationRow("You have a new follower")
        NotificationRow("Your photo reached 100K views")
        NotificationRow("You received a gift")
    }
}

@Composable
fun NotificationRow(text: String) {
    ListItem(
        headlineContent = { Text(text) },
        leadingContent = { Icon(Icons.Default.Notifications, null) }
    )
    HorizontalDivider()
}

// Profile + Monetization UI and logic
@Composable
fun ProfilePage() {
    // Sample user stats - in a real app these would come from your backend or ViewModel
    var followers by rememberSaveable { mutableStateOf(2430) }
    var photoViews by rememberSaveable { mutableStateOf(74200) }

    // In-app wallet balance (creator's share credited monthly)
    var walletBalance by rememberSaveable { mutableStateOf(0.0) }

    // Track if a withdrawal has been requested (simple UI state)
    var withdrawalRequested by rememberSaveable { mutableStateOf(false) }

    val followersRequired = 2000
    val viewsRequired = 100000
    val monetizationUnlocked = followers >= followersRequired && photoViews >= viewsRequired

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(85.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("P", fontSize = 35.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.width(15.dp))

            Column {
                Text("@pixcreator", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Text(NumberFormat.getInstance().format(followers) + " Followers")
                Text(NumberFormat.getInstance().format(photoViews) + " Photo Views")
            }
        }

        Spacer(Modifier.height(25.dp))

        Text("Creator Monetization", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        Text("Requirements")
        Text("• $followersRequired Followers")
        val followersProgress = (followers.toFloat() / followersRequired.toFloat()).coerceAtMost(1f)
        LinearProgressIndicator(
            progress = followersProgress,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Text("• $viewsRequired Photo Views")
        val viewsProgress = (photoViews.toFloat() / viewsRequired.toFloat()).coerceAtMost(1f)
        LinearProgressIndicator(
            progress = viewsProgress,
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        Spacer(Modifier.height(20.dp))

        if (!monetizationUnlocked) {
            Text("Monetization is locked until you reach $followersRequired followers and $viewsRequired photo views.")
        } else {
            Text("Monetization is unlocked!", color = Color(0xFF00AA00))
        }

        Spacer(Modifier.height(12.dp))

        // Revenue split information
        Text("Revenue Split: 50% platform / 50% creator")

        Spacer(Modifier.height(12.dp))

        // Wallet UI
        Text("In-App Wallet", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Balance: " + NumberFormat.getCurrencyInstance().format(walletBalance))

        Spacer(Modifier.height(10.dp))

        // Monthly credit simulation button - in a real app this would run on a server monthly cron
        Button(
            onClick = {
                if (monetizationUnlocked) {
                    // Simulate platform earnings for the month (placeholder value).
                    // In a real implementation the platform would calculate actual monthly earnings per creator.
                    val simulatedMonthlyEarnings = 100.0 // placeholder earnings for demonstration
                    val creatorShare = simulatedMonthlyEarnings * 0.5
                    walletBalance += creatorShare
                    withdrawalRequested = false
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Credit Monthly Earnings to Wallet (Simulate)")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                if (walletBalance > 0.0) {
                    // Mark a withdrawal request. Actual transfer to bank/JazzCash must be implemented server-side.
                    withdrawalRequested = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Request Withdrawal")
        }

        if (withdrawalRequested) {
            Spacer(Modifier.height(8.dp))
            Text("Withdrawal requested. Platform will process payout to your chosen method.")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = { /* AI Profile Picture */ }, modifier = Modifier.fillMaxWidth()) {
            Text("AI Profile Picture")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(onClick = { /* Coins & Gifts */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Coins & Gifts")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(onClick = { /* Earnings Dashboard */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Earnings Dashboard")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(onClick = { /* Withdrawal Methods */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Withdrawal Methods")
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(onClick = { /* Settings */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Settings")
        }
    }
}

@Composable
fun PixPayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFFF2D55),
            secondary = Color(0xFF25F4EE),
            background = Color.White,
            surface = Color.White
        ),
        content = content
    )
}
