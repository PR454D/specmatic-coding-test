package com.store.products

import com.store.products.db.ProductRepo
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/products")
class ProductsController(
    private val db: ProductRepo,
) {
    @Valid
    @GetMapping
    fun getAllProducts(@RequestParam(required = false) type: ProductType?): ResponseEntity<List<ProductDetails>> =
        ResponseEntity.ok(db.findAll(type))

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun createProduct(
        @Valid @RequestBody product: ProductDetails,
    ): ResponseEntity<Map<String, Int>> {
        val id = db.save(product)
        return ResponseEntity(mapOf("id" to id), HttpStatusCode.valueOf(201))
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
                "error" to "Validation failed for ${ex.localizedMessage}",
                "path" to "/products",
            ),
        )
    }
}

