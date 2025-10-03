import com.example.richculture.Data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("user/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ✅ NEW: Password Reset Endpoints
    @POST("user/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<GenericAuthResponse>

    @POST("user/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<GenericAuthResponse>

    @POST("user/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<GenericAuthResponse>


    @GET("user/profile")
    suspend fun getProfile(@Header("Authorization") token: String): User // Assuming ProfileResponse is an alias for User

    @Multipart
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part profileImage: MultipartBody.Part?,
        @Part("name") name: RequestBody?,
        @Part("bio") bio: RequestBody?,
        @Part("gender") gender: RequestBody?
    ): UpdateProfileResponse
}
