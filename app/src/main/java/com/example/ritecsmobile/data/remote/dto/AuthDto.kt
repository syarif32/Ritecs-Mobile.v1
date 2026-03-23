package com.example.ritecsmobile.data.remote.dto

// Cetakan untuk data yang KITA KIRIM ke Laravel
data class LoginRequest(
    val email: String,
    val password: String
)

// Cetakan untuk data yang KITA TERIMA dari Laravel
data class LoginResponse(
    val status: String,
    val message: String,
    val data: LoginData? = null
)

data class LoginData(
    val user: UserDto,
    val token: String
)


data class UserDto(
    val user_id: Int,
    val first_name: String,
    val email: String,
    val membership: MembershipDto? = null
)

// Cetakan baru untuk data Membership dari tabel memberships
data class MembershipDto(
    val id: Int,
    val status: String,
    val package_name: String?,
    val end_date: String?,
    val card_image_path: String?
)
data class BookResponse(
    val status: String,
    val data: List<BookDto>
)


data class MemberResponse(
    val status: String,
    val data: List<MemberDto>
)

data class MemberDto(
    val id: Int,
    val first_name: String?,
    val last_name: String?,
    val guest_first_name: String?,
    val guest_last_name: String?,
    val member_number: String?,
    val status: Int, // 1 = Active, 0 = Expired
    val img_path: String? // Path foto profil
)
data class HomeResponse(
    val status: String,
    val data: HomeDataDto
)

data class HomeDataDto(
    val vision: String,
    val mission: String,
    val stats: StatsDto
)

data class StatsDto(
    val members: Int,
    val books: Int,
    val journals: Int,
    val teams: Int
)
