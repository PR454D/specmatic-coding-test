package com.store.controllers

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.annotation.Nullable
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Repository
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

data class ProductDetails(
    @JsonProperty("id") val id: Int,
    @JsonProperty("name") @field:Pattern(
        regexp = "(?!true|false|null)[a-zA-Z]+", message = "should be a string"
    ) @field:NotEmpty(message = "Name cannot be empty") val name: String,
    @JsonProperty("type") val type: ProductType,
    @JsonProperty("inventory") @field:NotNull @field:Min(1) @field:Max(9999) val inventory: Int,
    @JsonProperty("cost") @field:Nullable val cost: Double?
)

enum class ProductType(
    val value: String,
) {
    book("book"), food("food"), gadget("gadget"), other("other"),
}

@RestController
@RequestMapping("/products")
class Products(
    private val db: ProductDb,
) {
    @Valid
    @GetMapping
    fun getAllProducts(@RequestParam(required = false) type: ProductType?): ResponseEntity<List<ProductDetails>> =
        ResponseEntity.ok(db.findAll(type).toList())

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createProduct(
        @Valid @RequestBody product: ProductDetails,
    ): ResponseEntity<Map<String, Int>> {
        val id = db.save(product)
        return ResponseEntity(mapOf("id" to id), HttpStatusCode.valueOf(201))
    }
}

interface ProductRepo {
    fun findAll(type: ProductType?): List<ProductDetails>

    fun save(product: ProductDetails): Int
}

@Repository
class ProductDb : ProductRepo {
    private var PRODUCTS = mutableMapOf(
        1 to ProductDetails(1, "Game of Thrones", ProductType.book, 50, 200.0),
        2 to ProductDetails(2, "Milk", ProductType.food, 100, 45.0),
        3 to ProductDetails(3, "Camera", ProductType.gadget, 10, 25000.0),
        4 to ProductDetails(4, "iPhone", ProductType.gadget, 2, 50000.0),
        5 to ProductDetails(5, "Binoculars", ProductType.other, 10, 12000.0),
    )

    override fun findAll(type: ProductType?): List<ProductDetails> = PRODUCTS.values.filter { it.type == type }.toList()

    override fun save(product: ProductDetails): Int {
        PRODUCTS.putIfAbsent(PRODUCTS.size + 1, product)
        return PRODUCTS.size
    }
}

@ControllerAdvice
class ValidationExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            mapOf(
                "timestamp" to LocalDateTime.now().toString(),
                "status" to HttpStatus.BAD_REQUEST.value(),
                "error" to "Validation failed",
                "path" to "/products",
            )
        )
    }
}

