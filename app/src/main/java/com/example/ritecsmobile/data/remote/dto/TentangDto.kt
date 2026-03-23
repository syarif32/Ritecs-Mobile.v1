package com.example.ritecsmobile.data.remote.dto

data class TentangResponse(
    val status: String,
    val data: TentangDataDto
)

data class TentangDataDto(
    val pre_title: String?,
    val subtitle: String?,
    val vision: String?,
    val mision: String?
)