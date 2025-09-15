package com.example.richculture.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.asImageBitmap
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
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.richculture.Data.Author
import com.example.richculture.Data.Comment
import com.example.richculture.Data.PostResponse
import com.example.richculture.R
import com.example.richculture.ViewModels.CommunityViewModel
import com.example.richculture.ViewModels.UserViewModel
import com.example.richculture.navigate.Screen
import com.example.richculture.utility.TokenManager
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

// --- Data Models for UI ---
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommunityWallScreen(
    navController: NavController,
    communityViewModel: CommunityViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }
    val token = tokenManager.getToken()
    val currentUserFromVM by userViewModel.currentUser.collectAsState()
    var postsState by remember { mutableStateOf<List<PostResponse>?>(null) }
    val initialPosts by communityViewModel.posts.observeAsState()
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

    val pullRefreshState = rememberPullRefreshState(refreshing = isRefreshing, onRefresh = {
        isRefreshing = true
        communityViewModel.getPosts()
    })

    if (selectedPostForDetail != null) {
        PostDetailDialog(
            post = postsState?.find { it._id == selectedPostForDetail!!._id } ?: selectedPostForDetail!!,
            onDismiss = { selectedPostForDetail = null },
            onLikeClicked = { postId ->
                if (currentUserFromVM != null && token != null) {
                    postsState = postsState?.map { p -> if (p._id == postId) p.copy(likes = if (p.likes.contains(currentUserFromVM!!._id)) p.likes - currentUserFromVM!!._id else p.likes + currentUserFromVM!!._id) else p }
                    communityViewModel.likePost(postId, token)
                }
            },
            onAddComment = { postId, text ->
                if (currentUserFromVM != null && token != null) {
                    postsState = postsState?.map { p -> if (p._id == postId) p.copy(comments = p.comments + Comment(UUID.randomUUID().toString(), text, Author(currentUserFromVM!!._id, currentUserFromVM!!.name, currentUserFromVM!!.profileImage), SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(Date()))) else p }
                    communityViewModel.addComment(postId, text, token)
                }
            },
            onDeleteComment = { postId, commentId ->
                if (currentUserFromVM != null && token != null) {
                    postsState = postsState?.map { p -> if (p._id == postId) p.copy(comments = p.comments.filterNot { it._id == commentId }) else p }
                    communityViewModel.deleteComment(postId, commentId, token)
                }
            },
            currentUserId = currentUserFromVM?._id ?: ""
        )
    }

    Scaffold(topBar = { CommunityTopAppBar(navController) }, containerColor = Color(0xFFF0F2F5)) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).pullRefresh(pullRefreshState)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    MemoriesContent(
                        navController = navController,
                        posts = postsState,
                        isUserLoggedIn = currentUserFromVM != null,
                        onLike = { postId ->
                            if (currentUserFromVM != null && token != null) {
                                postsState = postsState?.map { post -> if (post._id == postId) post.copy(likes = if (post.likes.contains(currentUserFromVM!!._id)) post.likes - currentUserFromVM!!._id else post.likes + currentUserFromVM!!._id) else post }
                                communityViewModel.likePost(postId, token)
                            } else { Toast.makeText(context, "Please log in to like posts.", Toast.LENGTH_SHORT).show() }
                        },
                        onPostClicked = { post -> selectedPostForDetail = post },
                        currentUserId = currentUserFromVM?._id ?: ""
                    )
                }
            }
            PullRefreshIndicator(refreshing = isRefreshing, state = pullRefreshState, modifier = Modifier.align(Alignment.TopCenter), contentColor = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun MemoriesContent(
    navController: NavController,
    posts: List<PostResponse>?,
    isUserLoggedIn: Boolean,
    onLike: (String) -> Unit,
    onPostClicked: (PostResponse) -> Unit,
    currentUserId: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (isUserLoggedIn) {
            Button(
                onClick = { navController.navigate(Screen.CreatePost.route) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Share", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Your Memory", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        when {
            posts == null -> {}
            posts.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No memories shared yet. Be the first!", color = Color.Gray)
                }
            }
            else -> {
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
}

@Composable
fun MemoryPostCard(
    post: PostResponse,
    currentUserId: String,
    onLikeClicked: () -> Unit,
    onCardClicked: () -> Unit,
) {
    val isLiked = post.likes.contains(currentUserId)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.clickable(onClick = onCardClicked),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = post.author?.profileImage, contentDescription = "Profile", modifier = Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop, error = painterResource(id = R.drawable.ic_profile))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(post.author?.name ?: "Unknown User", fontWeight = FontWeight.Bold)
                    Text(post.location ?: "", fontSize = 12.sp, color = Color.Gray)
                }
            }
            AsyncImage(model = post.media, contentDescription = post.caption, modifier = Modifier.fillMaxWidth().height(400.dp), contentScale = ContentScale.Crop)
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(post.caption ?: "", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                    PostActionButton(icon = if (isLiked) painterResource(id = R.drawable.ic_like_filled) else painterResource(id = R.drawable.ic_like), text = "${post.likes.size}", onClick = onLikeClicked, tint = if (isLiked) Color.Red else Color.Gray)
                    PostActionButton(icon = painterResource(id = R.drawable.ic_comment), text = "${post.comments.size}", onClick = onCardClicked)
                    PostActionButton(icon = painterResource(id = R.drawable.ic_share), text = "Share", onClick = {
                        scope.launch {
                            val bitmap = context.getImageBitmapFromUrl(post.media)
                            if (bitmap != null) {
                                context.shareImageAndText(bitmap, "${post.caption}\n\nShared from Sanskriti App")
                            } else {
                                Toast.makeText(context, "Could not share image.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
                if (post.comments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CommentPreview(post.comments.first())
                }
            }
        }
    }
}

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
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f))) {
            Scaffold(
                modifier = Modifier.padding(top = 60.dp).clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                topBar = {
                    TopAppBar(
                        title = { Text(post.author?.name ?: "Post", fontWeight = FontWeight.Bold) },
                        navigationIcon = { IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = "Close") } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = Color.Black)
                    )
                },
                containerColor = Color.White
            ) { padding ->
                LazyColumn(modifier = Modifier.padding(padding)) {
                    item {
                        AsyncImage(model = post.media, contentDescription = post.caption, modifier = Modifier.fillMaxWidth().height(400.dp), contentScale = ContentScale.Crop)
                    }
                    item {
                        val isLiked = post.likes.contains(currentUserId)
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(post.caption ?: "", lineHeight = 22.sp, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                PostActionButton(icon = if (isLiked) painterResource(id = R.drawable.ic_like_filled) else painterResource(id = R.drawable.ic_like), text = "${post.likes.size}", onClick = { onLikeClicked(post._id) }, tint = if (isLiked) Color.Red else Color.Gray)
                                PostActionButton(icon = painterResource(id = R.drawable.ic_comment), text = "${post.comments.size}")
                                PostActionButton(icon = painterResource(id = R.drawable.ic_share), text = "Share")
                            }
                        }
                    }
                    item { Divider(color = Color.LightGray.copy(alpha = 0.5f)) }
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
        Spacer(modifier = Modifier.height(16.dp))
        if (comments.isEmpty()) {
            Text("No comments yet.", color = Color.Gray, modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 24.dp))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                comments.forEach { comment ->
                    CommentItem(comment = comment, onDelete = { onDeleteComment(comment._id) })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        CommentInputField(onCommentPosted = onAddComment)
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(comment.author?.name ?: "Unknown User", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(comment.createdAt?.take(10) ?: "", fontSize = 12.sp, color = Color.Gray)
            }
            Text(comment.message ?: "", fontSize = 14.sp, color = Color.DarkGray)
        }
        if (comment.author?._id == currentUser?._id) {
            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Comment", tint = Color.Gray)
            }
        }
    }
}

// --- SHARE FUNCTIONALITY HELPERS ---
suspend fun Context.getImageBitmapFromUrl(url: String?): Bitmap? {
    if (url == null) return null
    return try {
        val loader = ImageLoader(this)
        val request = ImageRequest.Builder(this).data(url).allowHardware(false).build()
        val result = (loader.execute(request) as SuccessResult).drawable
        (result as BitmapDrawable).bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.shareImageAndText(bitmap: Bitmap, text: String) {
    val uri = bitmap.saveToCache(this) ?: return
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, text)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, "Share Post Via"))
}

fun Bitmap.saveToCache(context: Context): Uri? {
    return try {
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val stream = FileOutputStream("$cachePath/image.png")
        this.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        val imageFile = File(cachePath, "image.png")
        FileProvider.getUriForFile(context, "${context.packageName}.provider", imageFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
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
fun PostActionButton(icon: Painter, text: String, onClick: (() -> Unit)? = null, tint: Color = Color.Unspecified) {
    val modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
        Icon(icon, contentDescription = text, tint = tint, modifier = Modifier.size(20.dp))
        Text(text, color = Color.Gray, fontSize = 14.sp)
    }
}
