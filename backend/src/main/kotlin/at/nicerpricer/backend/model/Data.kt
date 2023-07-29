package at.nicerpricer.backend.model

import java.math.BigDecimal
import java.time.LocalDate

data class Data(
    val store: String?,
    val id: String?,
    val name: String?,
    val description: String?,
    val price: BigDecimal?,
    val priceHistory: List<DataHistory>,
    val isWeighted: Boolean?,
    val unit: String?,
    val quantity: Int?,
    val bio: Boolean?,
    val url: String?,
    val category: String?,
    val unavailable: Boolean?,
    val isCanonical: Boolean?,
)

data class DataHistory(
    val date: LocalDate?,
    val price: BigDecimal?,
)
