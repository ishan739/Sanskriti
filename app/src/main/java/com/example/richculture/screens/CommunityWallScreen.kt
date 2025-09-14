package com.example.richculture.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.richculture.Data.Author
import com.example.richculture.Data.Comment
import com.example.richculture.Data.PostResponse
import com.example.richculture.R
import com.example.richculture.ViewModels.CommunityViewModel
import com.example.richculture.ViewModels.UserViewModel
import com.example.richculture.navigate.Screen
import com.example.richculture.utility.TokenManager
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

// --- Data Models for UI ---
enum class CommunityTab { Memories, Recipes }
data class Recipe(
    val id: Int, val name: String, val cuisine: String, val imageResId: Int,
    val timeMin: Int, val difficulty: String, val rating: Float, val likes: Int
)

// --- Dummy Data ---
val recipes = listOf(
    Recipe(1, "Masala Chai", "North Indian", R.drawable.ic_pattachitra, 15, "Easy", 4.8f, 89),
    Recipe(2, "Butter Chicken", "Punjabi", R.drawable.ic_blue_pottery, 45, "Medium", 4.9f, 156)
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommunityWallScreen(
    navController: NavController,
    communityViewModel: CommunityViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    var selectedTab by remember { mutableStateOf(CommunityTab.Memories) }
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token = tokenManager.getToken()

    val currentUserFromVM by userViewModel.currentUser.collectAsState()

    var postsState by remember { mutableStateOf<List<PostResponse>?>(null) }
    val initialPosts by communityViewModel.posts.observeAsState()

    // ✅ NEW: State to manage the post detail dialog
    var selectedPostForDetail by remember { mutableStateOf<PostResponse?>(null) }

    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(initialPosts) {
        if (initialPosts != null) {
            postsState = initialPosts
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        if (postsState == null) {
            isRefreshing = true
            communityViewModel.getPosts()
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            communityViewModel.getPosts()
        }
    )

    // ✅ NEW: Show the Post Detail Dialog when a post is selected
    if (selectedPostForDetail != null) {
        PostDetailDialog(
            post = selectedPostForDetail!!,
            onDismiss = { selectedPostForDetail = null },
            onLikeClicked = { postId ->
                if (currentUserFromVM != null && token != null) {
                    postsState = postsState?.map { p ->
                        if (p._id == postId) p.copy(likes = if (p.likes.contains(currentUserFromVM!!._id)) p.likes - currentUserFromVM!!._id else p.likes + currentUserFromVM!!._id) else p
                    }
                    communityViewModel.likePost(postId, token)
                }
            },
            onAddComment = { postId, text ->
                if (currentUserFromVM != null && token != null) {
                    postsState = postsState?.map { p ->
                        if (p._id == postId) p.copy(comments = p.comments + Comment(UUID.randomUUID().toString(), text, Author(currentUserFromVM!!._id, currentUserFromVM!!.name, currentUserFromVM!!.profileImage), "")) else p
                    }
                    communityViewModel.addComment(postId, text, token)
                }
            },
            onDeleteComment = { postId, commentId ->
                if (currentUserFromVM != null && token != null) {
                    postsState = postsState?.map { p ->
                        if (p._id == postId) p.copy(comments = p.comments.filterNot { it._id == commentId }) else p
                    }
                    communityViewModel.deleteComment(postId, commentId, token)
                }
            },
            currentUserId = currentUserFromVM?._id ?: ""
        )
    }

    Scaffold(
        topBar = { CommunityTopAppBar(navController) },
        containerColor = Color(0xFFF4F7FA)
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).pullRefresh(pullRefreshState)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item { MainTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it }) }
                item {
                    AnimatedContent(targetState = selectedTab, label = "Tab Animation") { tab ->
                        when (tab) {
                            CommunityTab.Memories -> {
                                when {
                                    postsState == null -> { /* Covered by refresh indicator */ }
                                    else -> {
                                        MemoriesContent(
                                            navController = navController,
                                            posts = postsState!!,
                                            isUserLoggedIn = currentUserFromVM != null,
                                            onLike = { postId ->
                                                if (currentUserFromVM != null && token != null) {
                                                    postsState = postsState?.map { post ->
                                                        if (post._id == postId) post.copy(likes = if (post.likes.contains(currentUserFromVM!!._id)) post.likes - currentUserFromVM!!._id else post.likes + currentUserFromVM!!._id) else post
                                                    }
                                                    communityViewModel.likePost(postId, token)
                                                } else { Toast.makeText(context, "Please log in to like posts.", Toast.LENGTH_SHORT).show() }
                                            },
                                            onPostClicked = { post -> selectedPostForDetail = post },
                                            currentUserId = currentUserFromVM?._id ?: ""
                                        )
                                    }
                                }
                            }
                            CommunityTab.Recipes -> RecipesContent()
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}


@Composable
fun MemoriesContent(
    navController: NavController,
    posts: List<PostResponse>,
    isUserLoggedIn: Boolean,
    onLike: (String) -> Unit,
    onPostClicked: (PostResponse) -> Unit,
    currentUserId: String
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isUserLoggedIn) {
            Button(
                onClick = { navController.navigate(Screen.CreatePost.route) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(Color(0xFFAB47BC), Color(0xFF7E57C2)))), contentAlignment = Alignment.Center) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = "Share", tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Share Your Memory", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        if (posts.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No memories shared yet. Be the first!", color = Color.Gray)
            }
        } else {
            posts.forEach { post ->
                MemoryPostCard(
                    post = post,
                    currentUserId = currentUserId,
                    onLikeClicked = { onLike(post._id) },
                    onCardClicked = { onPostClicked(post) }
                )
            }
        }
    }
}


@Composable
fun MemoryPostCard(
    post: PostResponse,
    currentUserId: String,
    onLikeClicked: () -> Unit,
    onCardClicked: () -> Unit,
) {
    val isLiked = post.likes.contains(currentUserId)

    Card(
        modifier = Modifier.clickable(onClick = onCardClicked),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = post.author?.profileImage, contentDescription = "Profile Image", modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentScale = ContentScale.Crop, error = painterResource(id = R.drawable.ic_profile))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(post.author?.name ?: "Unknown User", fontWeight = FontWeight.Bold)
                    Text(post.location ?: "", fontSize = 12.sp, color = Color.Gray)
                }
            }
            AsyncImage(model = post.media, contentDescription = post.caption, modifier = Modifier.fillMaxWidth().height(350.dp), contentScale = ContentScale.Crop)
            Column(modifier = Modifier.padding(16.dp)) {
                Text(post.caption ?: "", lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    PostActionButton(icon = painterResource(id = if (isLiked) R.drawable.ic_like_filled else R.drawable.ic_like), text = "${post.likes.size}", onClick = onLikeClicked, tint = if (isLiked) Color.Red else Color.Gray)
                    PostActionButton(icon = painterResource(id = R.drawable.ic_comment), text = "${post.comments.size}", onClick = onCardClicked)
                    PostActionButton(icon = painterResource(id = R.drawable.ic_share), text = "Share")
                }
                // ✅ NEW: Show a preview of the first comment if it exists
                if (post.comments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CommentPreview(post.comments.first())
                    if (post.comments.size > 1) {
                        Text(
                            text = "View all ${post.comments.size} comments",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 8.dp).clickable(onClick = onCardClicked)
                        )
                    }
                }
            }
        }
    }
}

// --- ✅ NEW: POST DETAIL DIALOG COMPOSABLE ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailDialog(
    post: PostResponse,
    onDismiss: () -> Unit,
    onLikeClicked: (String) -> Unit,
    onAddComment: (String, String) -> Unit,
    onDeleteComment: (String, String) -> Unit,
    currentUserId: String
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Card(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(0.dp)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(post.author?.name ?: "Post") },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }
                    )
                }
            ) { padding ->
                LazyColumn(modifier = Modifier.padding(padding)) {
                    item {
                        // Display the main post content again
                        MemoryPostCard(
                            post = post,
                            currentUserId = currentUserId,
                            onLikeClicked = { onLikeClicked(post._id) },
                            onCardClicked = {} // Card is not clickable inside the detail view
                        )
                    }
                    // Display the full, interactive comment section
                    item {
                        CommentSection(
                            comments = post.comments,
                            onAddComment = { text -> onAddComment(post._id, text) },
                            onDeleteComment = { commentId -> onDeleteComment(post._id, commentId) }
                        )
                    }
                }
            }
        }
    }
}


// --- ✅ NEW: Comment Preview Composable ---
@Composable
fun CommentPreview(comment: Comment) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(comment.author?.name ?: "User", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(comment.message ?: "", fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}


@Composable
fun CommentSection(
    comments: List<Comment>,
    onAddComment: (String) -> Unit,
    onDeleteComment: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Comments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        CommentInputField(onCommentPosted = onAddComment)
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            comments.forEach { comment ->
                CommentItem(comment = comment, onDelete = { onDeleteComment(comment._id) })
            }
        }
    }
}

@Composable
fun CommentItem(comment: Comment, onDelete: () -> Unit) {
    val userViewModel: UserViewModel = koinViewModel()
    val currentUser by userViewModel.currentUser.collectAsState()

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        AsyncImage(model = comment.author?.profileImage, contentDescription = "Author", modifier = Modifier.size(32.dp).clip(CircleShape), error = painterResource(id = R.drawable.ic_profile))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            // ✅ CRITICAL BUG FIX: Displaying the actual comment text now
            Row {
                Text(comment.author?.name ?: "Unknown User", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(comment.message ?: "", fontSize = 14.sp, color = Color.DarkGray)
            }
        }
        if (comment.author?._id == currentUser?._id) {
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Comment", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun CommunityTopAppBar(navController: NavController) {
    Box {
        Spacer(modifier = Modifier.fillMaxWidth().height(110.dp).background(Brush.verticalGradient(listOf(Color(0xFF81C784), Color(0xFF2E7D32))), shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Community Hub 🌏", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Share memories & discover recipes", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun MainTabs(selectedTab: CommunityTab, onTabSelected: (CommunityTab) -> Unit) {
    Card(shape = CircleShape, colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), elevation = CardDefaults.cardElevation(4.dp)) {
        Row(modifier = Modifier.padding(6.dp)) {
            TabButton(text = "Memories", icon = painterResource(id = R.drawable.ic_story), isSelected = selectedTab == CommunityTab.Memories, activeColor = Color(0xFF7E57C2), modifier = Modifier.weight(1f)) { onTabSelected(CommunityTab.Memories) }
            TabButton(text = "Recipes", icon = painterResource(id = R.drawable.ic_recipe), isSelected = selectedTab == CommunityTab.Recipes, activeColor = Color(0xFF388E3C), modifier = Modifier.weight(1f)) { onTabSelected(CommunityTab.Recipes) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentInputField(onCommentPosted: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Add a comment...") },
        trailingIcon = {
            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onCommentPosted(text)
                        text = ""
                    }
                }
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Post Comment")
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun RecipeCard(recipe: Recipe) {
    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = recipe.imageResId), contentDescription = recipe.name, modifier = Modifier.size(100.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                    Text(recipe.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = Color.Gray)
                }
                Text(recipe.cuisine, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    MetaInfos(painterResource(id = R.drawable.ic_time), text = "${recipe.timeMin} mins")
                    MetaInfos(painterResource(id = R.drawable.ic_chart), text = recipe.difficulty)
                    MetaInfos(painterResource(id = R.drawable.ic_ratinh), text = "${recipe.rating}")
                }
                Text("${recipe.likes} likes", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* View Recipe */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)), shape = CircleShape) { Text("View Recipe") }
            }
        }
    }
}

@Composable
fun RecipeFilterChips() {
    val filters = listOf("All", "Breakfast", "Main Course", "Snacks")
    var selectedFilter by remember { mutableStateOf("All") }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(filters) { filter ->
            FilterChip(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, label = { Text(filter) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF388E3C), selectedLabelColor = Color.White))
        }
    }
}

@Composable
fun TabButton(text: String, icon: Painter, isSelected: Boolean, activeColor: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = modifier.height(50.dp), shape = CircleShape, colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) activeColor else Color.Transparent, contentColor = if (isSelected) Color.White else Color.Gray), elevation = if (isSelected) ButtonDefaults.buttonElevation(4.dp) else null) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}

@Composable
fun PostActionButton(icon: Painter, text: String, onClick: (() -> Unit)? = null, tint: Color = Color.Unspecified) {
    val modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
        Icon(icon, contentDescription = text, tint = tint, modifier = Modifier.size(20.dp))
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun MetaInfos(icon: Painter, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun RecipesContent() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RecipeFilterChips()
        recipes.forEach { recipe -> RecipeCard(recipe = recipe) }
    }
}

