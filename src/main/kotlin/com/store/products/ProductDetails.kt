package com.store.products

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.annotation.Nullable
import jakarta.validation.constraints.*

data class ProductDetails(
    @JsonProperty("id") var id: Int,
    @JsonProperty("name") @field:Pattern(
        regexp = "(?!true|false|null)[a-zA-Z]+",
        message = "should be a string",
    ) @field:NotEmpty(message = "Name cannot be empty") val name: String,
    @JsonProperty("type") val type: ProductType,
    @JsonProperty("inventory") @field:NotNull @field:Min(1) @field:Max(9999) val inventory: Int,
    @JsonProperty("cost") @field:Nullable val cost: Double?,
)

enum class ProductType {
    book, food, gadget, other,
}
