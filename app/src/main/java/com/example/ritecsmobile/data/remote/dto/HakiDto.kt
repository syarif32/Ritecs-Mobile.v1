package com.example.ritecsmobile.data.remote.dto

data class HakiResponse(
    val status: String,
    val data: HakiDataDto
)

data class HakiDataDto(
    val intro_title: String?,
    val intro_description: String?,
    val types: List<HakiTypeDto>,
    val packages: List<HakiPackageDto>
)

data class HakiTypeDto(
    val id: Int,
    val name: String?,
    val description: String?
)

data class HakiPackageDto(
    val id: Int?,
    val title: String?,
    val old_price: String?,
    val new_price: String?,
    val description: String?,
    val whatsapp_message: String?,
    val whatsapp_number: String?
)