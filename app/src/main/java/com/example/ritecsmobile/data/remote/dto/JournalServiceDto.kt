package com.example.ritecsmobile.data.remote.dto

data class JournalServiceResponse(
    val status: String,
    val data: JournalServiceDataDto
)

data class JournalServiceDataDto(
    val aim_scope: String,
    val scopes: List<ScopeDto>,
    val services: List<ServiceDto>
)

data class ScopeDto(
    val id: Int,
    val title: String?,
    val description: String?
)

data class ServiceDto(
    val id: Int,
    val title: String?,
    val description: String?
)