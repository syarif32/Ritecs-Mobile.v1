package com.example.ritecsmobile.data.remote.dto

data class BenefitResponse(
    val status: String,
    val data: BenefitDataDto
)

data class BenefitDataDto(
    val price: String?,
    val price_description: String?,
    val benefits: List<BenefitItemDto>,
    val faqs: List<FaqDto>
)

data class BenefitItemDto(
    val id: Int,
    val title: String?,
    val description: String?
)

data class FaqDto(
    val id: Int,
    val question: String?,
    val answer: String?
)