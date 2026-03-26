package com.example.ritecsmobile.data.remote.api

import com.example.ritecsmobile.data.remote.dto.BaseResponse
import com.example.ritecsmobile.data.remote.dto.BenefitResponse
import com.example.ritecsmobile.data.remote.dto.BookResponse
import com.example.ritecsmobile.data.remote.dto.GuidelineResponse
import com.example.ritecsmobile.data.remote.dto.HakiResponse
import com.example.ritecsmobile.data.remote.dto.HomeResponse
import com.example.ritecsmobile.data.remote.dto.JournalServiceResponse
import com.example.ritecsmobile.data.remote.dto.LoginRequest
import com.example.ritecsmobile.data.remote.dto.LoginResponse
import com.example.ritecsmobile.data.remote.dto.MemberResponse
import com.example.ritecsmobile.data.remote.dto.RegisterRequest
import com.example.ritecsmobile.data.remote.dto.RegisterResponse
import com.example.ritecsmobile.data.remote.dto.ResendOtpRequest
import com.example.ritecsmobile.data.remote.dto.TrainingResponse
import com.example.ritecsmobile.data.remote.dto.UserDto
import com.example.ritecsmobile.data.remote.dto.UserProfileResponse
import com.example.ritecsmobile.data.remote.dto.VerifyOtpRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.PUT


interface AuthApi {
    @Headers("Accept: application/json")
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @Headers("Accept: application/json")
    @POST("auth/request-otp")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
    @Headers("Accept: application/json")
    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<LoginResponse>
    @Headers("Accept: application/json")
    @POST("auth/resend-otp")
    suspend fun resendOtp(@Body request: ResendOtpRequest): Response<BaseResponse>
    @Headers("Accept: application/json")
    @POST("auth/request-manual")
    suspend fun requestManualActivation(@Body request: com.example.ritecsmobile.data.remote.dto.ManualActivationRequest): Response<BaseResponse>
    @Headers("Accept: application/json")
    @POST("auth/login/google")
    suspend fun googleLogin(@Body request: com.example.ritecsmobile.data.remote.dto.GoogleLoginRequest): Response<LoginResponse>
    @GET("user")
    suspend fun getUserProfile(
        @Header("Authorization") token: String
    ): com.example.ritecsmobile.data.remote.dto.UserDto

    // 2. ProfileScreen
    @GET("user-profile")
    suspend fun getDashboardProfile(
        @Header("Authorization") token: String
    ): com.example.ritecsmobile.data.remote.dto.UserProfileResponse
    // 1. Simpan Teks dan KTP
    @Multipart
    @POST("update-profile")
    suspend fun updateProfileWithKtp(
        @Header("Authorization") token: String,
        @Part("first_name") firstName: okhttp3.RequestBody,
        @Part("last_name") lastName: okhttp3.RequestBody,
        @Part("nik") nik: okhttp3.RequestBody,
        @Part("birthday") birthday: okhttp3.RequestBody,
        @Part("phone") phone: okhttp3.RequestBody,
        @Part("address") address: okhttp3.RequestBody,
        @Part("city") city: okhttp3.RequestBody,
        @Part("province") province: okhttp3.RequestBody,
        @Part("institution") institution: okhttp3.RequestBody,
        @Part("password") password: okhttp3.RequestBody,
        @Part ktp_path: okhttp3.MultipartBody.Part?
    ): com.example.ritecsmobile.data.remote.dto.SimpleResponse

    // 2. Simpan Foto Profil (Avatar)
    @Multipart
    @POST("update-avatar")
    suspend fun updateAvatar(
        @Header("Authorization") token: String,
        @Part avatar: okhttp3.MultipartBody.Part
    ): com.example.ritecsmobile.data.remote.dto.SimpleResponse
    @GET("books")
    suspend fun getBooks(): BookResponse
    @GET("members")
    suspend fun getMembers(): MemberResponse
    // Mengambil data Beranda
    @GET("home-data")
    suspend fun getHomeData(): HomeResponse
    // Mengambil data Petunjuk Penulis
    @GET("guidelines")
    suspend fun getGuidelines(): GuidelineResponse
    // Mengambil data Layanan Jurnal
    @GET("journal-services")
    suspend fun getJournalServices(): JournalServiceResponse
    // Mengambil data Benefit Membership
    @GET("membership-benefits")
    suspend fun getMembershipBenefits(): BenefitResponse
    // Mengambil data Layanan HAKI
    @GET("haki")
    suspend fun getHakiServices(): HakiResponse
    // Mengambil data Pusat Pelatihan
    @GET("training-center")
    suspend fun getTrainingCenter(): TrainingResponse
    // Mengambil data Tentang Ritecs
    @GET("tentang")
    suspend fun getTentangData(): com.example.ritecsmobile.data.remote.dto.TentangResponse
    // Mengambil info kontak & maps
    @GET("contact-info")
    suspend fun getContactInfo(): com.example.ritecsmobile.data.remote.dto.ContactInfoResponse

    // Mengirim pesan kontak
    @POST("contact-send")
    suspend fun sendContactMessage(@Body request: com.example.ritecsmobile.data.remote.dto.ContactSendRequest): com.example.ritecsmobile.data.remote.dto.SimpleResponse



    @GET("membership/banks")
    suspend fun getBanks(@Header("Authorization") token: String): com.example.ritecsmobile.data.remote.dto.BankResponse


    @Multipart
    @POST("membership/register")
    suspend fun registerMembership(
        @Header("Authorization") token: String,
        @Part("first_name") firstName: RequestBody,
        @Part("last_name") lastName: RequestBody?,
        @Part("email") email: RequestBody,
        @Part("nik") nik: RequestBody?,
        @Part("birthday") birthday: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("city") city: RequestBody?,
        @Part("province") province: RequestBody?,
        @Part("institution") institution: RequestBody?,
        @Part("sender_name") senderName: RequestBody,
        @Part("sender_bank") senderBank: RequestBody,
        @Part("bank_id") bankId: RequestBody,
        @Part("amount") amount: RequestBody,
        @Part("type") type: RequestBody,
        @Part ktp_path: MultipartBody.Part,
        @Part proof: MultipartBody.Part
    ): com.example.ritecsmobile.data.remote.dto.SimpleResponse
    @POST("books/{id}/visit")
    suspend fun incrementBookVisit(@Path("id") bookId: Int): retrofit2.Response<Any>
    @POST("books/{id}/download")
    suspend fun incrementBookDownload(@Path("id") bookId: Int): retrofit2.Response<Any>
    @GET("journals")
    suspend fun getJournals(): com.example.ritecsmobile.data.remote.dto.JournalResponse





//    admin
// Mengambil list permintaan aktivasi (OTP Manual) - Sesuai rute Laravel: activation.index
@GET("admin/activation-requests")
suspend fun getActivationRequests(
    @Header("Authorization") token: String
): Response<com.example.ritecsmobile.data.remote.dto.ActivationResponse>

    // Approve Aktivasi - Sesuai rute Laravel: activation.approve
    @PUT("admin/activation-requests/{id}/approve")
    suspend fun approveActivation(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse>

    // Mengambil data User untuk manajemen - Sesuai rute Laravel: users.index
    @GET("admin/users")
    suspend fun getAllUsers(
        @Header("Authorization") token: String
    ): Response<com.example.ritecsmobile.data.remote.dto.UserManageResponse>
}
