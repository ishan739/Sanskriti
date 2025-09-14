import com.example.richculture.Data.LoginRequest
import com.example.richculture.Data.LoginResponse
import com.example.richculture.Data.PostResponse
import com.example.richculture.Data.SignupRequest
import com.example.richculture.Data.SignupResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface CommunityApi {

    // ✅ Upload Post
    @Multipart
    @POST("post/upload")
    suspend fun uploadPost(
        @Part image: MultipartBody.Part,
        @Part("caption") caption: RequestBody,
        @Part("location") location: RequestBody,
        @Header("Authorization") token: String
    ): Response<PostResponse>

    // ✅ Get all posts
    @GET("post")
    suspend fun getPosts(): Response<List<PostResponse>>

    // ✅ Like/Unlike post
    @PUT("post/{postId}/like")
    suspend fun likePost(
        @Path("postId") postId: String,
        @Header("Authorization") token: String
    ): Response<PostResponse>

    // ✅ Add comment
    @POST("post/{postId}/comment")
    suspend fun addComment(
        @Path("postId") postId: String,
        @Header("Authorization") token: String,
        @Body comment: Map<String, String>
    ): Response<PostResponse>

    // ✅ Delete comment
    @DELETE("post/{postId}/comment/{commentId}")
    suspend fun deleteComment(
        @Path("postId") postId: String,
        @Path("commentId") commentId: String,
        @Header("Authorization") token: String
    ): Response<PostResponse>
}
