package com.store.products.db

import com.store.products.ProductDetails
import com.store.products.ProductType
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

interface ProductRepo {
    fun findAll(type: ProductType?): List<ProductDetails>

    fun save(product: ProductDetails): Int
}

@Repository
class InMemoryProductDb : ProductRepo {
    private var products = ConcurrentHashMap<Int, ProductDetails>()
    val idGenerator = AtomicInteger(1)
    override fun findAll(type: ProductType?): List<ProductDetails> =
        if (type == null) products.values.toList() else products.values.filter { it.type == type }.toList()

    override fun save(product: ProductDetails): Int {
        val id = idGenerator.getAndIncrement()
        product.id = id
        products[id] = product
        return id
    }
}
