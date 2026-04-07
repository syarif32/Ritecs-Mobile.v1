package com.example.ritecsmobile.data.remote.api

import com.example.ritecsmobile.data.remote.dto.AdminBankResponse
import com.example.ritecsmobile.data.remote.dto.AdminBookResponse
import com.example.ritecsmobile.data.remote.dto.AdminJournalResponse
import com.example.ritecsmobile.data.remote.dto.AdminUserManageResponse
import com.example.ritecsmobile.data.remote.dto.BankRequest
import com.example.ritecsmobile.data.remote.dto.BaseResponse
import com.example.ritecsmobile.data.remote.dto.BenefitResponse
import com.example.ritecsmobile.data.remote.dto.BookFormDataResponse
import com.example.ritecsmobile.data.remote.dto.BookResponse
import com.example.ritecsmobile.data.remote.dto.GuidelineResponse
import com.example.ritecsmobile.data.remote.dto.HakiResponse
import com.example.ritecsmobile.data.remote.dto.HomeResponse
import com.example.ritecsmobile.data.remote.dto.IdNameDto
import com.example.ritecsmobile.data.remote.dto.JournalFormDataResponse
import com.example.ritecsmobile.data.remote.dto.JournalServiceResponse
import com.example.ritecsmobile.data.remote.dto.LoginRequest
import com.example.ritecsmobile.data.remote.dto.LoginResponse
import com.example.ritecsmobile.data.remote.dto.MakeMemberRequest
import com.example.ritecsmobile.data.remote.dto.MemberResponse
import com.example.ritecsmobile.data.remote.dto.MembershipTransactionResponse
import com.example.ritecsmobile.data.remote.dto.PasswordConfirmRequest
import com.example.ritecsmobile.data.remote.dto.RegisterRequest
import com.example.ritecsmobile.data.remote.dto.RegisterResponse
import com.example.ritecsmobile.data.remote.dto.ResendOtpRequest
import com.example.ritecsmobile.data.remote.dto.RoleManageResponse
import com.example.ritecsmobile.data.remote.dto.TrainingResponse
import com.example.ritecsmobile.data.remote.dto.UpdateTransactionStatusRequest
import com.example.ritecsmobile.data.remote.dto.VerifyOtpRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path


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
// Mengambil list aktivasi manual
@Headers("Accept: application/json")
@GET("admin/activation-requests")
suspend fun getActivationRequests(
    @Header("Authorization") token: String
): Response<com.example.ritecsmobile.data.remote.dto.ActivationResponse>

    // Menyetujui aktivasi manual (dengan password)
    @Headers("Accept: application/json") // <- TAMBAHKAN INI JUGA
    @PUT("admin/activation-requests/{id}/approve")
    suspend fun approveActivation(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: PasswordConfirmRequest
    ): Response<com.example.ritecsmobile.data.remote.dto.BaseResponse>

    // Mengambil data User untuk manajemen
    @Headers("Accept: application/json") // <- TAMBAHKAN INI JUGA
    @GET("admin/users")
    suspend fun getAllUsers(
        @Header("Authorization") token: String
    ): Response<com.example.ritecsmobile.data.remote.dto.UserManageResponse>

    // Mengambil statistik dashboard
    @Headers("Accept: application/json") // <- TAMBAHKAN INI JUGA
    @GET("admin/dashboard-stats")
    suspend fun getAdminDashboardStats(
        @Header("Authorization") token: String
    ): Response<com.example.ritecsmobile.data.remote.dto.AdminDashboardResponse>

//    manage role
@Headers("Accept: application/json")
@GET("admin/access-management")
suspend fun getRoleManagementUsers(@Header("Authorization") token: String): Response<RoleManageResponse>

    @Headers("Accept: application/json")
    @PATCH("admin/access-management/{id}/promote")
    suspend fun promoteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: PasswordConfirmRequest
    ): Response<BaseResponse>

    @Headers("Accept: application/json")
    @PATCH("admin/access-management/{id}/demote")
    suspend fun demoteUser(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: PasswordConfirmRequest
    ): Response<BaseResponse>
//    payment
// (Existing) Get List Transaksi Aktif
@Headers("Accept: application/json")
@GET("admin/memberships")
suspend fun getMembershipTransactions(@Header("Authorization") token: String): Response<MembershipTransactionResponse>

    // (BARU) Get List Transaksi Trashed
    @Headers("Accept: application/json")
    @GET("admin/memberships/trashed")
    suspend fun getTrashedMemberships(@Header("Authorization") token: String): Response<MembershipTransactionResponse>

    // (Existing) Update Status Transaksi
    @Headers("Accept: application/json")
    @POST("admin/memberships/{id}/update")
    suspend fun updateMembershipTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateTransactionStatusRequest
    ): Response<BaseResponse>

    // (BARU) Restore Transaksi Trashed
    @Headers("Accept: application/json")
    @POST("admin/memberships/{id}/restore")
    suspend fun restoreMembershipTransaction(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse>

    // --- ADMIN MANAGE BANKS ---
    @Headers("Accept: application/json")
    @GET("admin/banks")
    suspend fun getAdminBanks(@Header("Authorization") token: String): Response<AdminBankResponse>

    @Headers("Accept: application/json")
    @POST("admin/banks")
    suspend fun createAdminBank(
        @Header("Authorization") token: String,
        @Body request: BankRequest
    ): Response<BaseResponse>

    @Headers("Accept: application/json")
    @PUT("admin/banks/{id}") // Jika error method not allowed, coba ganti ke @PATCH
    suspend fun updateAdminBank(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: BankRequest
    ): Response<BaseResponse>

    @Headers("Accept: application/json")
    @DELETE("admin/banks/{id}")
    suspend fun deleteAdminBank(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<BaseResponse>
    @Headers("Accept: application/json")
    @GET("admin/users")
    suspend fun getActiveUsers(@Header("Authorization") token: String): Response<AdminUserManageResponse>

    @Headers("Accept: application/json")
    @GET("admin/users/nonactive")
    suspend fun getNonActiveUsers(@Header("Authorization") token: String): Response<AdminUserManageResponse>

    @Headers("Accept: application/json")
    @DELETE("admin/users/{id}")
    suspend fun deactivateUser(@Header("Authorization") token: String, @Path("id") id: Int): Response<BaseResponse>

    @Headers("Accept: application/json")
    @POST("admin/users/{id}/restore")
    suspend fun restoreUser(@Header("Authorization") token: String, @Path("id") id: Int): Response<BaseResponse>

    @Headers("Accept: application/json")
    @POST("admin/users/{id}/make-member")
    suspend fun makeUserMember(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: MakeMemberRequest
    ): Response<BaseResponse>
//    buku
@Headers("Accept: application/json")
@GET("admin/published-books")
suspend fun getAdminBooks(@Header("Authorization") token: String): Response<AdminBookResponse>

    @Headers("Accept: application/json")
    @GET("admin/published-books/create")
    suspend fun getBookFormData(@Header("Authorization") token: String): Response<BookFormDataResponse>

    @Headers("Accept: application/json")
    @DELETE("admin/published-books/{id}/delete")
    suspend fun deleteAdminBook(@Header("Authorization") token: String, @Path("id") id: Int): Response<BaseResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST("admin/published-books/store")
    suspend fun storeAdminBook(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("Synopsis") synopsis: RequestBody?,
        @Part("publisher") publisher: RequestBody?,
        @Part("isbn") isbn: RequestBody?,
        @Part("publish_date") publishDate: RequestBody?,
        @Part("ebook_path") ebookPath: RequestBody?,      // BARU
        @Part("pages") pages: RequestBody?,               // BARU
        @Part("width") width: RequestBody?,               // BARU
        @Part("length") length: RequestBody?,             // BARU
        @Part("thickness") thickness: RequestBody?,       // BARU
        @Part("print_price") printPrice: RequestBody?,    // BARU
        @Part("ebook_price") ebookPrice: RequestBody?,    // BARU
        @Part category: List<MultipartBody.Part>,
        @Part writter: List<MultipartBody.Part>,
        @Part coverImage: MultipartBody.Part?
    ): retrofit2.Response<com.example.ritecsmobile.data.remote.dto.BaseResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST("admin/published-books/{id}/update")
    suspend fun updateAdminBook(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("Synopsis") synopsis: RequestBody?,
        @Part("publisher") publisher: RequestBody?,
        @Part("isbn") isbn: RequestBody?,
        @Part("publish_date") publishDate: RequestBody?,
        @Part("ebook_path") ebookPath: RequestBody?,
        @Part("pages") pages: RequestBody?,
        @Part("width") width: RequestBody?,
        @Part("length") length: RequestBody?,
        @Part("thickness") thickness: RequestBody?,
        @Part("print_price") printPrice: RequestBody?,
        @Part("ebook_price") ebookPrice: RequestBody?,
        @Part category: List<MultipartBody.Part>,
        @Part writter: List<MultipartBody.Part>,
        @Part coverImage: MultipartBody.Part?
    ): retrofit2.Response<com.example.ritecsmobile.data.remote.dto.BaseResponse>
//    jurnal
@Headers("Accept: application/json")
@GET("admin/published-journals")
suspend fun getAdminJournals(@Header("Authorization") token: String): Response<AdminJournalResponse>

    @Headers("Accept: application/json")
    @GET("admin/published-journals/create")
    suspend fun getJournalFormData(@Header("Authorization") token: String): Response<JournalFormDataResponse>

    @Headers("Accept: application/json")
    @DELETE("admin/delete-journal/{id}")
    suspend fun deleteAdminJournal(@Header("Authorization") token: String, @Path("id") id: Int): Response<BaseResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST("admin/store-journal")
    suspend fun storeAdminJournal(
        @Header("Authorization") token: String,
        @Part("title") title: RequestBody,
        @Part("url_path") urlPath: RequestBody?,
        @Part keywords: List<MultipartBody.Part>,
        @Part coverImage: MultipartBody.Part?
    ): Response<BaseResponse>

    @Multipart
    @Headers("Accept: application/json")
    @POST("admin/update-journal/{id}")
    suspend fun updateAdminJournal(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Part("title") title: RequestBody,
        @Part("url_path") urlPath: RequestBody?,
        @Part keywords: List<MultipartBody.Part>,
        @Part coverImage: MultipartBody.Part?
    ): Response<BaseResponse>
    // Tambahkan di dalam interface AuthApi

    @FormUrlEncoded
    @POST("admin/add-writer-ajax")
    suspend fun addWriterAjax(
        @Header("Authorization") token: String,
        @Field("name") name: String
    ): retrofit2.Response<IdNameDto>

    // ==========================================
    // API UNTUK RIWAYAT TRANSAKSI USER BIASA
    // ==========================================
    @Headers("Accept: application/json")
    @GET("user/transactions")
    suspend fun getUserTransactions(
        @Header("Authorization") token: String
    ): retrofit2.Response<com.example.ritecsmobile.data.remote.dto.UserTransactionResponse>

    @FormUrlEncoded
    @POST("admin/add-keyword-ajax") // Sesuaikan dengan URL di routes/api.php Bos ya
    suspend fun addKeywordAjax(
        @Header("Authorization") token: String,
        @Field("name") name: String
    ): Response<IdNameDto>

}
