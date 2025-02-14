package com.store.products.db

import com.store.products.ProductDetails
import com.store.products.ProductType
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ProductDbTest {
    private val db = InMemoryProductDb()

    @Test
    fun `saved product id and generated id should be same`() = runTest {
        coroutineScope {
            val jobs = (1..100).map { i ->
                async {
                    var savedId = db.save(ProductDetails(i, "Product $i", ProductType.gadget, i * 25, i * 25.0))
                    assertEquals(i, savedId, "Generated id and the saved id should be same")
                }
            }
            jobs.awaitAll()
        }
        assertEquals(100, db.findAll(null).size, "All products size should be 100")
    }

    @Test
    fun `fetch products size async`() = runTest {
        assertEquals(0, db.findAll(null).size, "Initial all products size should be 0")
        var savedId = async {
            db.save(ProductDetails(1, "Product 1", ProductType.gadget, 25, 25.0))
        }.await()

        assertEquals(2, db.idGenerator.get(), "generated id is incremented")
        assertEquals(1, savedId, "saved id should be 1")
        assertEquals(1, db.findAll(null).size, "products size after async saving is 1")
    }

}